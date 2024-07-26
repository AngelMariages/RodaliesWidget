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

package org.angelmariages.rodalieswidget.glance.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import kotlinx.coroutines.launch
import org.angelmariages.rodalieswidget.glance.GlanceRodaliesWidget
import org.angelmariages.rodalieswidget.glance.worker.WeatherWorker
import org.angelmariages.rodalieswidget.utils.U

@Composable
private fun AppWidgetBox(
    modifier: GlanceModifier = GlanceModifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = GlanceModifier.appWidgetBackgroundModifier().then(modifier),
    ) {
        content()
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundModifier(): GlanceModifier {
    return this.fillMaxSize()
        .padding(16.dp)
        .appWidgetBackground()
        .background(GlanceTheme.colors.background)
        .appWidgetBackgroundCornerRadius()
}

fun GlanceModifier.appWidgetBackgroundCornerRadius(): GlanceModifier {
    if (Build.VERSION.SDK_INT >= 31) {
        cornerRadius(android.R.dimen.system_app_widget_background_radius)
    }
    return cornerRadius(16.dp)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun WidgetWrapper(glanceId: GlanceId, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)

    AppWidgetBox {
        Header(
            originStationLabel = "Premià de Mar",
            destinationStationLabel = "Barcelona - Clot Aragó",
            onOriginClick = {
                U.log("Premia clicked")
            },
            onDestinationClick = {
                U.log("Clot clicked")
            },
            onUpdate = {
                U.log("Refresh clicked")
                scope.launch {
                    refreshWeather(context, appWidgetId)
                }
            },
            onSwap = {
                U.log("Swap clicked")
            }
        )
        Box(
            modifier = GlanceModifier.fillMaxSize().padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private suspend fun refreshWeather(context: Context, appWidgetId: Int) {
    // Force the widget to refresh
    GlanceRodaliesWidget().updateAll(context)
    WeatherWorker.enqueue(context, true, appWidgetId)
}