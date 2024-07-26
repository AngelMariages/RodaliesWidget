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

package org.angelmariages.rodalieswidget.glance.state

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory.create
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.angelmariages.rodalieswidget.utils.U
import java.io.InputStream
import java.io.OutputStream


@Serializable
data class WeatherData(
    val degrees: Int,
)

@Serializable
sealed interface WeatherInfo {
    @Serializable
    @SerialName("loading")
    data object Loading : WeatherInfo

    @Serializable
    @SerialName("available")
    data class Available(
        val currentData: WeatherData,
    ) : WeatherInfo

    @Serializable
    @SerialName("unavailable")
    data class Unavailable(val message: String) : WeatherInfo
}

object WeatherInfoStateDefinition : GlanceStateDefinition<WeatherInfo> {

    private const val DATA_STORE_FILENAME_PREFIX = "weatherInfo_"

    override suspend fun getDataStore(context: Context, fileKey: String) =
        create(serializer = WeatherInfoSerializer, produceFile = { getLocation(context, fileKey) })

    override fun getLocation(context: Context, fileKey: String) =
        context.dataStoreFile(DATA_STORE_FILENAME_PREFIX + fileKey.lowercase())

    object WeatherInfoSerializer : Serializer<WeatherInfo> {
        override val defaultValue = WeatherInfo.Unavailable("No data")

        override suspend fun readFrom(input: InputStream): WeatherInfo = try {
            U.log("Reading weather info from input stream")
            Json.decodeFromString(WeatherInfo.serializer(), input.readBytes().decodeToString())
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read data: ${exception.message}")
        }

        override suspend fun writeTo(t: WeatherInfo, output: OutputStream) {
            output.use {
                U.log("Writing weather info $t")
                it.write(Json.encodeToString(WeatherInfo.serializer(), t).toByteArray())
            }
        }

    }
}