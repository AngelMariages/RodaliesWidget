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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies2

import org.angelmariages.rodalieswidget.timetables.TrainTime
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies2.model.RodaliesJSONSchedule
import java.util.ArrayList
import java.util.Calendar

fun parse(
    schedule: RodaliesJSONSchedule,
    origin: String,
    destination: String,
    calendarInstance: Calendar
): List<TrainTime> {
    val transfers = 0;
    val trainTimes = ArrayList<TrainTime>()

    for (timeItem in schedule.result.items) {
        trainTimes.add(
            TrainTime(
                "R1",
                timeItem.departsAtOrigin,
                timeItem.arrivesAtDestination,
                origin,
                destination,
                calendarInstance
            )
        )
    }

    return trainTimes
}