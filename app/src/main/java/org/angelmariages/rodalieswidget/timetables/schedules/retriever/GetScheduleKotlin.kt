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

import android.content.Context
import android.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.angelmariages.rodalieswidget.timetables.TrainTime
import org.angelmariages.rodalieswidget.timetables.schedules.Schedule
import org.angelmariages.rodalieswidget.timetables.schedules.persistence.removeOldFiles
import org.angelmariages.rodalieswidget.timetables.schedules.persistence.retrieveSavedSchedule
import org.angelmariages.rodalieswidget.timetables.schedules.persistence.saveSchedule
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.ServiceDisruptionError
import org.angelmariages.rodalieswidget.utils.TimeUtils
import org.angelmariages.rodalieswidget.utils.U

class GetScheduleKotlin {
    suspend fun execute(
        context: Context,
        widgetId: Int,
        deltaDays: Int
    ) = coroutineScope {
        val core = U.getCore(context, widgetId)
        val stations = U.getStations(context, widgetId)

        if (stations.size != 2 || stations.any { it.equals("-1", true) }) {
            U.log("No stations set!")
            return@coroutineScope U.sendNoStationsSetError(widgetId, context)
        }

        if (stations[0].equals(stations[1])) {
            U.log("Stations are equal")
            return@coroutineScope U.sendNoTimesError(widgetId, context)
        }

        val (origin, destination) = stations

        withContext(Dispatchers.IO) {
            if (deltaDays == 0) {
                getScheduleForToday(context, widgetId, origin, destination, core)
            } else {
                getScheduleForDelta(context, widgetId, origin, destination, core, deltaDays)
            }
        }
    }

    private suspend fun getScheduleForToday(
        context: Context,
        widgetId: Int,
        origin: String,
        destination: String,
        core: Int
    ) = coroutineScope {
        // If it's 00:00 and we can get the yesterday schedule and there are some trains left,
        // we should display that one because the trains past 00 are in the yesterday schedule
        if (TimeUtils.getCurrentHour() == 0) {
            val yesterdaySavedSchedule = retrieveSchedule(context, -1, origin, destination, core)

            // TODO: CHANGE CONDITION!
            if (!TimeUtils.isScheduleExpired(yesterdaySavedSchedule as java.util.ArrayList<TrainTime>?)) {
                U.sendNewTrainTimes(widgetId, origin, destination, yesterdaySavedSchedule, context)

                return@coroutineScope
            }
        }

        try {
            val result = withContext(Dispatchers.IO) {
                U.log("Getting JSON for ${origin}->${destination}")
                val result =
                    retrieveSchedule(context, 0, origin, destination, core)

                // We get also the return schedule
                retrieveSchedule(context, 0, destination, origin, core)

                removeOldFiles(context)

                return@withContext result
            }

            U.sendNewTrainTimes(
                widgetId, origin, destination,
                result as java.util.ArrayList<TrainTime>, context
            )
        } catch (e: ServiceDisruptionError) {
            if (e.errors.any { e -> e.error.contains("disruptions") }) {
                U.sendProgramedDisruptionsError(widgetId, context)
            }
        }
    }

    private suspend fun getScheduleForDelta(
        context: Context,
        widgetId: Int,
        origin: String,
        destination: String,
        core: Int,
        deltaDays: Int
    ) = coroutineScope {
        try {
            val result = withContext(Dispatchers.IO) {
                U.log("Getting JSON for ${origin}->${destination} with delta $deltaDays")

                return@withContext retrieveSchedule(context, deltaDays, origin, destination, core)
            }

            U.sendNewTrainTimes(
                widgetId, origin, destination,
                result as java.util.ArrayList<TrainTime>, context
            )
        } catch (e: ServiceDisruptionError) {
            if (e.errors.any { e -> e.error.contains("disruptions") }) {
                U.sendProgramedDisruptionsError(widgetId, context)
            }
        }
    }

    private fun retrieveSchedule(
        context: Context,
        deltaDays: Int,
        origin: String,
        destination: String,
        core: Int
    ): List<TrainTime> {
        val savedSchedule = retrieveSavedSchedule(context, origin, destination, deltaDays)

        if (savedSchedule != null) {
            return savedSchedule
        }

        U.log("Empty file, getting from Internet...")

        val schedule = Schedule(origin, destination, core, deltaDays).get() ?: null

        if (schedule == null) {
            return emptyList()
        }

        val trainTimes = getTrainTimesFromSchedule(context, schedule as List<TrainTime>, true)

        saveSchedule(context, origin, destination, deltaDays, trainTimes)

        return trainTimes
    }

    private fun getTrainTimesFromSchedule(
        context: Context,
        schedule: List<TrainTime>,
        forceAdd: Boolean
    ): ArrayList<TrainTime> {
        val hourSchedule = ArrayList<TrainTime>()
        val showAllTimes = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("show_all_times", false)

        for (trainTime in schedule) {
            if (forceAdd) {
                hourSchedule.add(trainTime)
            } else if (!showAllTimes) {
                if (TimeUtils.isScheduledTrain(trainTime)) {
                    hourSchedule.add(trainTime)
                }
            } else {
                hourSchedule.add(trainTime)
            }
        }

        //fallback
        if (!forceAdd && !showAllTimes && hourSchedule.size == 0) {
            hourSchedule.addAll(schedule)
        }

        return hourSchedule
    }

}