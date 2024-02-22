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

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.runComposition
import kotlinx.coroutines.Dispatchers

/**
 * Activity that displays Glance widgets using `GlanceAppWidget.runComposition`
 * to output RemoteViews without having a bound widget.
 */
class RodaliesWidgetViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WidgetView(GlanceRodaliesWidget(), DpSize(180.dp, 180.dp))
        }
    }
}

@OptIn(ExperimentalGlanceApi::class)
@Composable
fun WidgetView(widget: GlanceAppWidget, size: DpSize = DpSize(200.dp, 200.dp)) {
    val context = LocalContext.current
    val remoteViews by
    remember(widget, size) { widget.runComposition(context, sizes = listOf(size)) }
        .collectAsState(null, Dispatchers.Default)
    AndroidView(
        factory = {
            // Using an AWHV is necessary for ListView support, and to properly select a RemoteViews
            // from a multi-size RemoteViews. If the RemoteViews has only one size and does not
            // contain lazy list items, a FrameLayout works fine.
            AppWidgetHostView(context).apply { setFakeAppWidget() }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view -> view.updateAppWidget(remoteViews) },
    )
}

/**
 * The hostView requires an AppWidgetProviderInfo to work in certain OS versions. This method uses
 * reflection to set a fake provider info.
 */
private fun AppWidgetHostView.setFakeAppWidget() {
    val context = context
    val info = AppWidgetProviderInfo().apply {
        initialLayout = androidx.glance.appwidget.R.layout.glance_default_loading_layout
    }
    try {
        val activityInfo = ActivityInfo().apply {
            applicationInfo = context.applicationInfo
            packageName = context.packageName
            labelRes = applicationInfo.labelRes
        }

        info::class.java.getDeclaredField("providerInfo").run {
            isAccessible = true
            set(info, activityInfo)
        }

        this::class.java.getDeclaredField("mInfo").apply {
            isAccessible = true
            set(this@setFakeAppWidget, info)
        }
    } catch (e: Exception) {
        throw IllegalStateException("Couldn't set fake provider", e)
    }
}
