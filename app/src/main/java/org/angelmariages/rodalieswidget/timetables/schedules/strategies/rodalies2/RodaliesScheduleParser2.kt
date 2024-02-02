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
    val transfers = getTransfersNumber(schedule)
    val trainTimes = ArrayList<TrainTime>()

    val scheduleItems = schedule.result.items

    for (timeItem in scheduleItems) {
        when (transfers) {
            0 -> {
                trainTimes.add(
                    TrainTime(
                        timeItem.steps.first().line.name,
                        timeItem.departsAtOrigin,
                        timeItem.arrivesAtDestination,
                        origin,
                        destination,
                        calendarInstance
                    )
                )
            }

            1 -> {
                // timeItem.departsAtOrigin is first train departure time
                // secondStep.arrivesAt is first train arrival time
                // secondStep.departsAt is second train departure time
                // timeItem.arrivesAtDestination is second train arrival time
                // firstStep.line.name is first train line
                // secondStep.line.name is second train line
                // firstStep.station.name is null
                // secondStep.station.name is transfer station
                val firstStep = timeItem.steps[0]
                val secondStep = timeItem.steps.getOrNull(1)

                if (secondStep != null) {
                    trainTimes.add(
                        TrainTime(
                            firstStep.line.name,
                            timeItem.departsAtOrigin,
                            secondStep.arrivesAt,
                            secondStep.line.name,
                            secondStep.station?.id,
                            secondStep.departsAt,
                            timeItem.arrivesAtDestination,
                            origin,
                            destination,
                            false,
                            calendarInstance
                        )
                    )
                } else {
                    trainTimes.add(
                        TrainTime(
                            firstStep.line.name,
                            timeItem.departsAtOrigin,
                            timeItem.arrivesAtDestination,
                            null,
                            null,
                            null,
                            null,
                            origin,
                            destination,
                            true,
                            calendarInstance
                        )
                    )
                }
            }

            else -> {}
        }
    }

    return trainTimes
}

private fun getTransfersNumber(schedule: RodaliesJSONSchedule): Int {
    val items = schedule.result.items

    if (items.any { it.steps.size > 1 }) {
        return 1
    }

    return 0
}