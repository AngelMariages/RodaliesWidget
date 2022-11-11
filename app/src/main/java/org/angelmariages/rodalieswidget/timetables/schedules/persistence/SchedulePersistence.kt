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

package org.angelmariages.rodalieswidget.timetables.schedules.persistence

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.angelmariages.rodalieswidget.timetables.TrainTime
import org.angelmariages.rodalieswidget.utils.TimeUtils
import org.angelmariages.rodalieswidget.utils.U
import java.io.IOException


fun saveSchedule(
    context: Context,
    origin: String,
    destination: String,
    deltaDays: Int,
    trainTimes: List<TrainTime>
) {
    if (!isValidTrainTimes(trainTimes)) {
        return
    }

    val fileName = getFileName(origin, destination, deltaDays)
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, TrainTime::class.java)
    val jsonAdapter = moshi.adapter<List<TrainTime>>(type)

    val fileContents = jsonAdapter.toJson(trainTimes)

    saveFile(context, fileName, fileContents.toByteArray())
}

fun isValidTrainTimes(trainTimes: List<TrainTime>): Boolean {
    return trainTimes.any { it.departureTime != null && it.departureTime.isNotEmpty() }
}

fun retrieveSavedSchedule(
    context: Context,
    origin: String,
    destination: String,
    deltaDays: Int
): List<TrainTime>? {
    val fileName = getFileName(origin, destination, deltaDays)
    val content = retrieveFile(context, fileName)

    if (content == null || content.isEmpty()) {
        return null
    }

    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, TrainTime::class.java)
    val jsonAdapter = moshi.adapter<List<TrainTime>>(type)

    try {
        return jsonAdapter.fromJson(content)
    } catch (e: Exception) {
        U.log("Error on retrieveSchedule: " + e.message)
    }

    return null
}

fun removeOldFiles(context: Context) {
    // TODO: implement
}

private fun retrieveFile(
    context: Context,
    fileName: String
): String? {
    try {
        return context.openFileInput(fileName).use { inputStream ->
            inputStream.bufferedReader().use {
                it.readText()
            }
        }
    } catch (e: IOException) {
        U.log("Error on retrieveFile: " + e.message)
    }

    return null
}

private fun saveFile(context: Context, fileName: String, fileContents: ByteArray) {
    try {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStreamWriter ->
            outputStreamWriter.write(fileContents)
        }
    } catch (e: IOException) {
        U.log("Error on saveFile: " + e.message)
    }
}

private fun getFileName(origin: String, destination: String, deltaDays: Int): String {
    return "horaris_${origin}_${destination}_${TimeUtils.getTodayDate(deltaDays)}.json"
}
