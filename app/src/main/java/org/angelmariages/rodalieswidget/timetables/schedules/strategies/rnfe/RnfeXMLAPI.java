/*
 * MIT License
 *
 * Copyright (c) 2021 Àngel Mariages
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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class RnfeXMLAPI {
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new UnzippingInterceptor()).build();
    private final String origin;
    private final String destination;
    private final int division;
    // https://horarios.renfe.com/cer/horarios/horarios.jsp?nucleo=50&o=79600&d=72503&df=20201020&ho=00&hd=26

    public RnfeXMLAPI(String origin, String destination, int division) {
        this.origin = origin;
        this.destination = destination;
        this.division = division;
    }

    public String getPageFromInternet(int deltaDays) {
        URL requestURL = getRequestURL(deltaDays);

        Request request = new Request.Builder()
                .url(requestURL)
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Origin", "https://horarios.renfe.com")
                .addHeader("Accept", "text/xml")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private URL getRequestURL(int deltaDays) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("horarios.renfe.com")
                .addPathSegment("cer")
                .addPathSegment("horarios")
                .addPathSegment("horarios.jsp")
                .addQueryParameter("nucleo", String.valueOf(this.division))
                .addQueryParameter("o", this.origin)
                .addQueryParameter("d", this.destination)
                .addQueryParameter("df", getTodayDate(deltaDays))
                .addQueryParameter("ho", "00")
                .addQueryParameter("hd", "26")
                .build().url();
    }

    private String getTodayDate(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, deltaDays);
        return String.format(Locale.getDefault(), "%d%02d%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    private static class UnzippingInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return unzip(response);
        }


        // copied from okhttp3.internal.http.HttpEngine (because is private)
        private Response unzip(final Response response) {
            if (response.body() == null) {
                return response;
            }

            //check if we have gzip response
            String contentEncoding = response.headers().get("Content-Encoding");

            //this is used to decompress gzipped responses
            if (contentEncoding != null && contentEncoding.equals("gzip")) {
                long contentLength = response.body().contentLength();
                GzipSource responseBody = new GzipSource(response.body().source());
                Headers strippedHeaders = response.headers().newBuilder().build();
                return response.newBuilder().headers(strippedHeaders)
                        .body(new RealResponseBody(response.body().contentType().toString(), contentLength, Okio.buffer(responseBody)))
                        .build();
            } else {
                return response;
            }
        }
    }
}
