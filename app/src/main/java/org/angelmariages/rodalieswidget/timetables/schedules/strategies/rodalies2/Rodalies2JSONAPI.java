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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies2;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Rodalies2JSONAPI {
    private final String origin;
    private final String destination;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient();

    public Rodalies2JSONAPI(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    /*
       curl -H "Host: serveisgrs.rodalies.gencat.cat" -H "User-Agent: okhttp/4.9.2" -H "Pragma: no-cache" -H "Cache-Control: no-cache" --compressed "https://serveisgrs.rodalies.gencat.cat/api/timetables?originStationId=79409&destinationStationId=79009&travelingOn=2024-02-01&fromTime=0"
     */

    private String getRequestURL(int deltaDays) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("serveisgrs.rodalies.gencat.cat")
                .addPathSegments("/api/timetables")
                .addQueryParameter("originStationId", this.origin)
                .addQueryParameter("destinationStationId", this.destination)
                .addQueryParameter("travelingOn", getTodayDate(deltaDays))
                .addQueryParameter("fromTime", "0")
                .build().toString();
    }

    public String getPageFromInternet(int deltaDays) {
        String url = getRequestURL(deltaDays);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Host", "serveisgrs.rodalies.gencat.cat")
                .addHeader("Accept", JSON.toString())
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("User-Agent", "okhttp/4.9.2")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private String getTodayDate(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, deltaDays);
        return String.format(Locale.getDefault(), "%d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }
}
