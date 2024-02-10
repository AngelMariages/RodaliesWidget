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
package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies

import com.squareup.moshi.Moshi
import org.angelmariages.rodalieswidget.timetables.TrainTime
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.Strategy
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesJSONSchedule
import java.io.IOException
import java.util.Calendar

class RodaliesStrategy : Strategy {
    private val moshi = Moshi.Builder().build()
    private val jsonAdapter = moshi.adapter(RodaliesJSONSchedule::class.java)


    override fun getSchedule(
        origin: String,
        destination: String,
        division: Int,
        deltaDays: Int
    ): List<TrainTime>? {
        try {
            val pageFromInternet =
                getRodaliesJSONSchedule(deltaDays, origin, destination) ?: return null

            val fromMoshi = jsonAdapter.fromJson(pageFromInternet)
                ?: return emptyList()

            val calendarInstance = getCalendar(deltaDays)

            return parse(fromMoshi, origin, destination, calendarInstance)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getCalendar(deltaDays: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, deltaDays)
        return cal
    }
}
