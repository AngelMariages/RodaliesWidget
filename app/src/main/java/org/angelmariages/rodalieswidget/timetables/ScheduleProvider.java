/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
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

package org.angelmariages.rodalieswidget.timetables;


import android.content.Context;

import org.angelmariages.rodalieswidget.utils.U;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

abstract class ScheduleProvider {
	private static final int MAX_TIMEOUT_INTENTS = 5;
	private int timeoutIntents = 0;

	abstract ArrayList<TrainTime> getSchedule();

	abstract ArrayList<TrainTime> getSchedule(int deltaDays);

	String doServiceRequest(String url, Context context) {
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuilder html = new StringBuilder();

		try {
			//TODO: REMOVE THIS LOG!
			U.log("URL: " + url);
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestMethod("GET");

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				html.append(line);
			}
		} catch (SocketTimeoutException e) {
			U.log("TIMEOUT doServiceRequest " + e.getMessage());
			if (timeoutIntents < MAX_TIMEOUT_INTENTS) {
				timeoutIntents++;
				return doServiceRequest(url, context);
			}
			U.logEventTimeout(url, context);
		} catch (MalformedURLException e) {
			U.log("ERROR: URL malformada.");
		} catch (IOException e) {
			U.log("No es pot obrir el stream.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					U.log("Error tancant l'stream: " + e.getMessage());
				}
			}
		}

		return html.toString();
	}

	void resetTimeoutIntents() {
		timeoutIntents = 0;
	}
}
