/*
 * MIT License
 *
 * Copyright (c) 2022 Àngel Mariages
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

package org.angelmariages.rodalieswidget.timetables.schedules.retriever

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import org.angelmariages.rodalieswidget.R
import org.angelmariages.rodalieswidget.utils.U
import org.angelmariages.rodalieswidget.utils.WorkerManagerUtils
import org.angelmariages.rodalieswidget.utils.WorkerManagerUtils.getWorkerManager

class GetScheduleWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val updateScheduleChannelId = "update_schedule_id"
    private val jobId = 4444


    companion object {
        fun enqueueRefreshWidget(context: Context, widgetID: Int, deltaDays: Int) {
            getWorkerManager(context, onGotWorkerManager = WorkerManagerUtils.OnGotWorkerManager {
                val request = OneTimeWorkRequestBuilder<GetScheduleWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setConstraints(Constraints.NONE)
                    .setInputData(
                        workDataOf(
                            "widgetID" to widgetID,
                            "deltaDays" to deltaDays
                        )
                    )
                    .build()

                it.enqueue(request)
            })
        }
    }

    override suspend fun doWork(): Result {
        val widgetID = inputData.getInt("widgetID", -1)
        val deltaDays = inputData.getInt("deltaDays", 0)

        U.log("Recieved work request for: $widgetID -> deltaDays: $deltaDays")

        run {
            return@run GetSchedule().execute(applicationContext, widgetID, deltaDays)
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannel(applicationContext)
        }

        return ForegroundInfo(
            jobId,
            createForegroundNotification(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChannel(context: Context) {
        val mNotifyMgr = context.getSystemService(NotificationManager::class.java)
        var mChannel = mNotifyMgr.getNotificationChannel(updateScheduleChannelId)
        if (mChannel == null) {
            mChannel = NotificationChannel(
                updateScheduleChannelId,
                "Schedule update notifications",
                NotificationManager.IMPORTANCE_LOW
            )
        }

        mChannel.setShowBadge(false)
        mChannel.enableLights(false)
        mChannel.enableVibration(false)
        mNotifyMgr.createNotificationChannel(mChannel)
    }

    private fun createForegroundNotification(context: Context): Notification {
        val notification = NotificationCompat.Builder(context, updateScheduleChannelId).apply {
            setSmallIcon(R.mipmap.ic_notification_white)
            setContentTitle(context.getString(R.string.app_name))
            setContentText(context.getString(R.string.update_schedules_notification_text))
            setOnlyAlertOnce(true)
            setNotificationSilent()
            setShowWhen(false)
            priority = NotificationCompat.PRIORITY_LOW
        }

        return notification.build()
    }
}