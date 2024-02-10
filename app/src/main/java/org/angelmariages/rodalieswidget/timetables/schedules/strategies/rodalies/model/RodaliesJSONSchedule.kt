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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RodaliesJSONSchedule(
    val result: RodaliesJSONResult,
)

@JsonClass(generateAdapter = true)
data class RodaliesJSONResult(
    val items: List<RodaliesJSONItem>
)

@JsonClass(generateAdapter = true)
data class RodaliesJSONItem(
    val departsAtOrigin: String,
    val arrivesAtDestination: String,
    val steps: List<RodaliesJSONStep>
)

@JsonClass(generateAdapter = true)
data class RodaliesJSONStep(
    val departsAt: String?,
    val arrivesAt: String?,
    val line: RodaliesJSONLine,
    val station: RodaliesJSONStation?
)

@JsonClass(generateAdapter = true)
data class RodaliesJSONLine(
    val id: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class RodaliesJSONStation(
    val id: String,
    val name: String,
)