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

import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Calendar
import java.util.Locale

fun getRodaliesJSONSchedule(deltaDays: Int, origin: String, destination: String): String? {
    val httpClient = OkHttpClient()

    val url = getRequestURL(deltaDays, origin, destination)
    val request = Request.Builder()
        .url(url)
        .addHeader("Pragma", "no-cache")
        .addHeader("Cache-Control", "no-cache")
        .addHeader("Host", "serveisgrs.rodalies.gencat.cat")
        .addHeader("Accept", "application/json; charset=utf-8".toMediaType().toString())
        .addHeader("Accept-Encoding", "gzip")
        .addHeader("User-Agent", "okhttp/4.9.2")
        .get()
        .build()
    try {
        httpClient.newCall(request).execute().use { response ->
            if (response.body != null) {
                return response.body!!.string()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

private fun getRequestURL(deltaDays: Int, origin: String, destination: String): String {
    return HttpUrl.Builder()
        .scheme("https")
        .host("serveisgrs.rodalies.gencat.cat")
        .addPathSegments("/api/timetables")
        .addQueryParameter("originStationId", origin)
        .addQueryParameter("destinationStationId", destination)
        .addQueryParameter("travelingOn", getTodayDate(deltaDays))
        .addQueryParameter("fromTime", "0")
        .build().toString()
}

private fun getTodayDate(deltaDays: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, deltaDays)
    return String.format(
        Locale.getDefault(), "%d-%02d-%02d",
        cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]
    )
}