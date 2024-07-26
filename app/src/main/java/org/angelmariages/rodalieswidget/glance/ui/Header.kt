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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import org.angelmariages.rodalieswidget.R

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun Header(
    originStationLabel: String,
    destinationStationLabel: String,
    onOriginClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onUpdate: () -> Unit,
    onSwap: () -> Unit
) {
    Row(
        modifier = GlanceModifier.wrapContentHeight().fillMaxWidth(),
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight()
        ) {
            Text(
                text = originStationLabel,
                maxLines = 1,
                modifier = GlanceModifier.clickable(block = onOriginClick),
                style = TextStyle(
                    fontSize = 14.sp,
                ),
            )
            Text(
                text = destinationStationLabel,
                maxLines = 1,
                modifier = GlanceModifier.clickable(block = onDestinationClick),
                style = TextStyle(
                    fontSize = 14.sp,
                ),
            )
        }
        Row {
            Box(modifier = GlanceModifier.padding(end = 4.dp)) {
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.ic_update),
                    backgroundColor = GlanceTheme.colors.primary,
                    contentColor = GlanceTheme.colors.onPrimary,
                    modifier = GlanceModifier.size(48.dp),
                    contentDescription = "Refresh",
                    onClick = onUpdate
                )
            }
            Box(modifier = GlanceModifier.padding(start = 4.dp)) {
                CircleIconButton(
                    imageProvider = ImageProvider(R.drawable.ic_swap),
                    backgroundColor = GlanceTheme.colors.primary,
                    contentColor = GlanceTheme.colors.onPrimary,
                    modifier = GlanceModifier.size(48.dp),
                    contentDescription = "Swap",
                    onClick = onSwap
                )
            }
        }
    }
}