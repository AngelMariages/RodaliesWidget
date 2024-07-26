/*
 * MIT License
 *
 * Copyright (c) 2024 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.angelmariages.rodalieswidget.glance.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import org.angelmariages.rodalieswidget.glance.GlanceRodaliesWidget
import org.angelmariages.rodalieswidget.glance.state.WeatherData
import org.angelmariages.rodalieswidget.glance.state.WeatherInfo
import org.angelmariages.rodalieswidget.glance.state.WeatherInfoStateDefinition
import org.angelmariages.rodalieswidget.utils.U
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


private const val uniqueWorkName = "weatherWidgetWorker"
private const val appWidgetIdKey = "appWidgetId"

// Without this declaration, the class reference to WeatherWidgetWorker::class.java below does not
// work because it is defined in that function after it is referenced. This will not show up in the
// sample.
class WeatherWorker(
    private val context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun enqueue(context: Context, force: Boolean = false, glanceId: Int) {
            val manager = WorkManager.getInstance(context)
            val data = Data.Builder()

            if (force) {
                val requestBuilder = OneTimeWorkRequestBuilder<WeatherWorker>().setInputData(
                    data.putInt(appWidgetIdKey, glanceId).build()
                )
                manager.beginUniqueWork(
                    "${uniqueWorkName}_$glanceId",
                    ExistingWorkPolicy.REPLACE,
                    requestBuilder.build()
                ).enqueue()

                U.log("Forcefully updating weather $glanceId")

                return
            }

            val requestBuilder = PeriodicWorkRequestBuilder<WeatherWorker>(
                30.minutes.toJavaDuration()
            ).setInputData(
                data.putInt(appWidgetIdKey, glanceId).build()
            )

            U.log("Enqueueing weather worker $glanceId")
            manager.enqueueUniquePeriodicWork(
                "${uniqueWorkName}_$glanceId",
                ExistingPeriodicWorkPolicy.KEEP,
                requestBuilder.build()
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun cancel(context: Context, glanceId: Int) {
            val manager = WorkManager.getInstance(context)
            manager.cancelUniqueWork("${uniqueWorkName}_$glanceId")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val workerGlanceId = inputData.getInt(appWidgetIdKey, -1)

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(GlanceRodaliesWidget::class.java)

        U.log("GlanceIds: $glanceIds")

        val glanceId = glanceIds.find {
            manager.getAppWidgetId(it) == workerGlanceId
        } ?: return Result.failure().also {
            cancel(context, workerGlanceId)
            U.log("GlanceId not found: $workerGlanceId")
        }

        val randomDelay = Random.nextLong(1000, 5000)
        updateAppWidgetState(context = applicationContext,
            definition = WeatherInfoStateDefinition,
            glanceId = glanceId,
            updateState = {
                WeatherInfo.Loading
            })
        GlanceRodaliesWidget().apply {
            // This is a workaround to force the widget to update. This is not necessary in a real
            update(context, glanceId)
        }

        U.log("Worker glanceId: $workerGlanceId, randomDelay: $randomDelay")

        delay(randomDelay)

        val randomError = Random.nextBoolean()

        if (randomError) {
            updateAppWidgetState(context = applicationContext,
                definition = WeatherInfoStateDefinition,
                glanceId = glanceId,
                updateState = {
                    WeatherInfo.Unavailable("Error")
                })
            GlanceRodaliesWidget().apply {
                // This is a workaround to force the widget to update. This is not necessary in a real
                update(context, glanceId)
            }
            return Result.success()
        }

        U.log("Updating weather for glanceId: $glanceId")

        val newDegrees = Random.nextInt(32, 100)
        U.log("New $newDegrees")

        updateAppWidgetState(context = applicationContext,
            definition = WeatherInfoStateDefinition,
            glanceId = glanceId,
            updateState = {
                WeatherInfo.Available(WeatherData(newDegrees))
            })

        GlanceRodaliesWidget().apply {
            // This is a workaround to force the widget to update. This is not necessary in a real
            update(context, glanceId)
        }

        return Result.success()
    }

}