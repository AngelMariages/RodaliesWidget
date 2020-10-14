package org.angelmariages.rodalieswidget.timetables.rnfe;

/*
 * MIT License
 *
 * Copyright (c) 2020 Àngel Mariages
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
 */

import com.google.gson.GsonBuilder;

import org.angelmariages.rodalieswidget.timetables.ScheduleProvider;
import org.angelmariages.rodalieswidget.timetables.TrainTime;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RnfeRequest implements ScheduleProvider {

	private final String origin;
	private final String destination;
	private final int nucli;
	private String transferStationOne;
	private String transferStationTwo;

	private int lastRowIndex;

	public RnfeRequest(String origin, String destination, int nucli) {
		this.origin = origin;
		this.destination = destination;
		this.nucli = nucli;
	}

    /*
        curl 'http://horarios.renfe.com/cer/HorariosServlet'
        -H 'Pragma: no-cache'
        -H 'Origin: http://horarios.renfe.com'
        -H 'Accept-Encoding: gzip, deflate'
        -H 'Content-Type: application/json;charset=UTF-8'
        -H 'Accept: application/json, text/plain, *//*'
        -H 'Cache-Control: no-cache'
        --data-binary '{"nucleo":"50",
            "origen":"79600",
            "destino":"79404",
            "fchaViaje":"20181219",
            "validaReglaNegocio":true,
            "tiempoReal":true,
            "servicioHorarios":"VTI",
            "horaViajeOrigen":"00",
            "horaViajeLlegada":"26",
            "accesibilidadTrenes":true
         }' --compressed
     */

	public String getPageFromInternet(int deltaDays) {

		HttpsURLConnection connection = null;
		BufferedReader in = null;
		StringBuilder html = new StringBuilder();
		String query = "nucleo=" + nucli;
		query += "&o=" + origin;
		query += "&d=" + destination;
		query += "&df=" + getTodayDate(deltaDays);
		query += "&ho=00&i=s&cp=NO&TXTInfo=";

		try {
			HashMap<String, String> requestParams = new HashMap<>();

			requestParams.put("nucleo", String.valueOf(nucli));
			requestParams.put("origen", origin);
			requestParams.put("destino", destination);
			requestParams.put("fchaViaje", getTodayDate(deltaDays));
			requestParams.put("validaReglaNegocio", "true");
			requestParams.put("tiempoReal", "true");
			requestParams.put("servicioHorarios", "VTI");
			requestParams.put("horaViajeOrigen", "00");
			requestParams.put("horaViajeLlegada", "26");
			requestParams.put("accesibilidadTrenes", "true");

			System.out.println(new GsonBuilder().create().toJson(requestParams));

			byte[] out = new GsonBuilder().create().toJson(requestParams).getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			String url = "https://horarios.renfe.com/cer/HorariosServlet";
			//TODO: REMOVE THIS LOG!
			System.out.println("URL: " + url + query);
			connection = (HttpsURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setConnectTimeout(2500);
			connection.setReadTimeout(2500);

			connection.setFixedLengthStreamingMode(length);
			connection.setRequestProperty("Origin", "https://horarios.renfe.com");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Pragma", "no-cache");
			connection.connect();
			try(OutputStream os = connection.getOutputStream()) {
				os.write(out);
			}

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				html.append(line);
			}

			//TODO: Check if errors and send to analytics??
            /*BufferedReader err = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String errLine;

            while ((errLine = err.readLine()) != null) {
                System.out.println("errLine = " + errLine);
            }*/
		} catch (MalformedURLException e) {
			System.out.println("ERROR: URL malformada.");
		} catch (IOException e) {
			System.out.println("No es pot obrir el stream.");
		} finally {
			if (connection != null) {
				connection.disconnect();
				System.out.println("Disconnected");
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("Error tancant l'stream: " + e.getMessage());
				}
			}
		}

		return html.toString();
	}


	private String getTodayDate(int deltaDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, deltaDays);
		return String.format(Locale.getDefault(), "%d%02d%02d",
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public ArrayList<TrainTime> getSchedule() {
		return null;
	}

	@Override
	public ArrayList<TrainTime> getSchedule(int deltaDays) {
		return null;
	}
}