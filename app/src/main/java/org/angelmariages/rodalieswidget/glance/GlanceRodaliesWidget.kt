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

package org.angelmariages.rodalieswidget.glance

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.angelmariages.rodalieswidget.glance.state.WeatherData
import org.angelmariages.rodalieswidget.glance.state.WeatherInfo
import org.angelmariages.rodalieswidget.glance.state.WeatherInfoStateDefinition
import org.angelmariages.rodalieswidget.glance.ui.WidgetWrapper
import org.angelmariages.rodalieswidget.glance.worker.WeatherWorker
import org.angelmariages.rodalieswidget.utils.U
import kotlin.random.Random


/**
 * Sample AppWidget that showcase scrollable layouts using the LazyColumn
 */
class GlanceRodaliesWidget : GlanceAppWidget() {
    override val stateDefinition = WeatherInfoStateDefinition

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        coroutineScope {
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(id)

            val store = stateDefinition.getDataStore(context, appWidgetId.toString())

            U.log("GlanceId: $id, trying to get weather info from data store")
            val currentData = store.data.map {
                U.log("Current degrees: $it")
                it
            }.stateIn(this@coroutineScope)

            // Load the current weather if there is not a current value present.
            if (currentData.value is WeatherInfo.Unavailable) {
                U.log("Updating weather ${currentData.value}")
                updateAppWidgetState(context = context,
                    definition = WeatherInfoStateDefinition,
                    glanceId = id,
                    updateState = {
                        val newDegrees = Random.nextInt(32, 100)
                        U.log("Updating weather $newDegrees")
                        WeatherInfo.Available(WeatherData(newDegrees))
                    })
            }


            WeatherWorker.enqueue(context, true, appWidgetId)

            // Note: you can also set `android:updatePeriodMillis` to control how often the
            // launcher requests an update, but this does not support periods less than
            // 30 minutes.
            provideContent {
                Content(glanceId = id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalGlancePreviewApi::class)
    @Composable
    @Preview
    fun Content(glanceId: GlanceId) {
        val weatherInfo = currentState<WeatherInfo>()

        GlanceTheme {
            when (weatherInfo) {
                WeatherInfo.Loading -> {
                    WidgetWrapper(glanceId) {
                        CircularProgressIndicator()
                    }
                }

                is WeatherInfo.Unavailable -> {
                    WidgetWrapper(glanceId) {
                        Text(text = "Unavailable: ${weatherInfo.message}")
                    }
                }

                is WeatherInfo.Available -> {
                    val degrees = weatherInfo.currentData.degrees

                    WidgetWrapper(glanceId) {
                        LazyColumn {
                            items(10) { id ->
                                Text(
                                    text = "Item $id, w: $degrees",
                                    modifier = GlanceModifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
