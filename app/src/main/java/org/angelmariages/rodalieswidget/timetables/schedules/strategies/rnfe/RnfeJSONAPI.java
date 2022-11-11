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

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RnfeJSONAPI {
    private final String origin;
    private final String destination;
    private final int division;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient();

    public RnfeJSONAPI(String origin, String destination, int division) {
        this.origin = origin;
        this.destination = destination;
        this.division = division;
    }

    /*
       curl 'http://horarios.renfe.com/cer/HorariosServlet' -H 'Pragma: no-cache' -H 'Origin: http://horarios.renfe.com' -H 'Accept-Encoding: gzip, deflate' -H 'Content-Type: application/json;charset=UTF-8' -H 'Accept: application/json, text/plain, *//*'-H 'Cache-Control: no-cache' --data-binary '{"nucleo":"50", "origen":"79600", "destino":"79404", "fchaViaje":"20181219", "validaReglaNegocio":true, "tiempoReal":true, "servicioHorarios":"VTI", "horaViajeOrigen":"00", "horaViajeLlegada":"26", "accesibilidadTrenes":true}' --compressed
     */

    public String getPageFromInternet(int deltaDays) {
        String url = "https://horarios.renfe.com/cer/HorariosServlet";
        String requestParams = getRequestParams(deltaDays);

        RequestBody body = RequestBody.create(JSON, requestParams);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Origin", "https://horarios.renfe.com")
                .addHeader("Accept", JSON.toString())
                .addHeader("Accept-Encoding", "gzip, deflate")
                .post(body)
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

    private String getRequestParams(int deltaDays) {
        HashMap<String, String> requestParams = new HashMap<>();

        requestParams.put("nucleo", String.valueOf(division));
        requestParams.put("origen", origin);
        requestParams.put("destino", destination);
        requestParams.put("fchaViaje", getTodayDate(deltaDays));
        requestParams.put("validaReglaNegocio", "true");
        requestParams.put("tiempoReal", "true");
        requestParams.put("servicioHorarios", "VTI");
        requestParams.put("horaViajeOrigen", "00");
        requestParams.put("horaViajeLlegada", "26");
        requestParams.put("accesibilidadTrenes", "true");

        return new GsonBuilder().create().toJson(requestParams);
    }


    private String getTodayDate(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, deltaDays);
        return String.format(Locale.getDefault(), "%d%02d%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }
}
