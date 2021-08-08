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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.angelmariages.rodalieswidget.WidgetManager;
import org.angelmariages.rodalieswidget.timetables.schedules.Schedule;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.TimeUtils;
import org.angelmariages.rodalieswidget.utils.U;

public class GetSchedule extends AsyncTask<Object, Void, Void> {
	private String origin = "-1";
	private String destination = "-1";
	private int core;

	private ArrayList<TrainTime> get(Context context, String origin, String destination, int core, int deltaDays) {
		this.origin = origin;
		this.destination = destination;
		this.core = core;
		return deltaDays != 0 ? getJSONFromDelta(context, deltaDays) : getCorrectJSON(context);
	}

	private ArrayList<TrainTime> getJSONFromDelta(Context context, int deltaDays) {
		String jsonLines = readJSONFile(context, deltaDays);

		/*ScheduleProviderOld scheduleProviderOld;
		if (core == 50) {
			scheduleProviderOld = new RodaliesScheduleOld(origin, destination);
		} else {
			scheduleProviderOld = new RenfeScheduleOld(origin, destination, core);
		}*/

		if (jsonLines.isEmpty()) {
			U.log("Getting json from internet...");
			List<TrainTime> schedule;

			//schedule = scheduleProviderOld.getSchedule(deltaDays);
			schedule = new Schedule(origin, destination, core).get();

			ArrayList<TrainTime> hourSchedule = null;
			if (schedule != null) {
				hourSchedule = new ArrayList<>();
				addHoursToSchedule(context, schedule, hourSchedule, true);
			}

			return hourSchedule;
		} else {
			U.log("Getting json from file...");

			ArrayList<TrainTime> scheduleFromJSON = new ArrayList<>();//ScheduleFileManager.getScheduleFromJSON(jsonLines, origin, destination);

			ArrayList<TrainTime> hourSchedule = new ArrayList<>();
			if (scheduleFromJSON.size() > 0) {
				addHoursToSchedule(context, scheduleFromJSON, hourSchedule, true);
			}
			return hourSchedule;
		}
	}

	private ArrayList<TrainTime> getCorrectJSON(Context context) {
		String jsonLines = readJSONFile(context, 0);

		ScheduleProviderOld scheduleProviderOld;
		/*if (core == 50) {
			scheduleProviderOld = new RodaliesScheduleOld(origin, destination);
		} else {
			scheduleProviderOld = new RenfeScheduleOld(origin, destination, core);
		}*/

		if (jsonLines.isEmpty()) {
			U.log("Getting json from internet...");
			List<TrainTime> schedule;
			ArrayList<TrainTime> pastSchedule = null;

			//schedule = scheduleProviderOld.getSchedule();

			schedule = new Schedule(origin, destination, core).get();

			ArrayList<TrainTime> hourSchedule = null;
			boolean switched = false;
			if (schedule != null) {
				if (TimeUtils.getCurrentHour() == 0) {
					//pastSchedule = getScheduleFromYesterday(context, origin, destination, schedule);
					if (pastSchedule != null && !TimeUtils.isScheduleExpired(pastSchedule)) {
						schedule = pastSchedule;
						switched = true;
					}
				}

				hourSchedule = new ArrayList<>();
				addHoursToSchedule(context, schedule, hourSchedule, false);
				if (!switched) {
					//jsonLines = ScheduleFileManager.getJSONString(schedule);
					saveJSONFile(context, jsonLines);
					removeOldJSON(context);
				}
			}

			return hourSchedule;
		} else {
			U.log("Getting json from file...");

			ArrayList<TrainTime> scheduleFromJSON = new ArrayList<>();//ScheduleFileManager.getScheduleFromJSON(jsonLines, origin, destination);
			ArrayList<TrainTime> pastSchedule = null;

			if (TimeUtils.getCurrentHour() == 0) {
				//pastSchedule = getScheduleFromYesterday(context, origin, destination, scheduleProviderOld);
				if (pastSchedule != null && !TimeUtils.isScheduleExpired(pastSchedule)) {
					scheduleFromJSON = pastSchedule;
				}
			}

			ArrayList<TrainTime> hourSchedule = new ArrayList<>();
			if (scheduleFromJSON.size() > 0) {
				addHoursToSchedule(context, scheduleFromJSON, hourSchedule, false);
			}
			return hourSchedule;
		}
	}

	private ArrayList<TrainTime> getScheduleFromYesterday(Context context, String origin, String destination, List<TrainTime> scheduleProviderOld) {
		/*String yesterdayJsonLines = readJSONFile(context, -1);
		if (yesterdayJsonLines.isEmpty()) {
			return scheduleProviderOld.getSchedule(-1);
		}
		return ScheduleFileManager.getScheduleFromJSON(yesterdayJsonLines, origin, destination);*/
		return null;
	}

	private void addHoursToSchedule(Context context, List<TrainTime> scheduleFromJSON, ArrayList<TrainTime> hourSchedule, boolean forceAdd) {
		boolean show_all_times = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_all_times", false);

		for (TrainTime trainTime : scheduleFromJSON) {
			if (forceAdd) {
				hourSchedule.add(trainTime);
			} else {
				if (!show_all_times) {
					if (TimeUtils.isScheduledTrain(trainTime)) hourSchedule.add(trainTime);
				} else {
					hourSchedule.add(trainTime);
				}
			}
		}

		//fallback
		if(!forceAdd && !show_all_times && hourSchedule != null && hourSchedule.size() == 0) {
			hourSchedule.addAll(scheduleFromJSON);
		}
	}

	private String readJSONFile(Context context, int deltaDays) {
		String fileName = "horaris_" + origin + "_" + destination + "_" + TimeUtils.getTodayDateWithoutPath(deltaDays) + ".json";

		StringBuilder allLines = new StringBuilder();

		InputStreamReader inputStreamReader;
		try {
			inputStreamReader = new InputStreamReader(context.openFileInput(fileName));
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				allLines.append(line);
			}
		} catch (IOException e) {
			U.log("File " + fileName + " not found, getting from internet");
		}

		return allLines.toString();
	}

	private void saveJSONFile(Context context, String jsonFile) {
		if (jsonFile == null || jsonFile.isEmpty()) return;

		String fileName = "horaris_" + origin + "_" + destination + "_" + TimeUtils.getTodayDateWithoutPath() + ".json";

		OutputStreamWriter outputStreamWriter;
		try {
			outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
			outputStreamWriter.write(jsonFile);
			outputStreamWriter.close();
		} catch (IOException e) {
			U.log("Error on saveJSONFile: " + Arrays.toString(e.getStackTrace()));
		}
	}

	private void removeOldJSON(Context context) {
		File filesDir = context.getFilesDir();
		final String endsWith = "_" + TimeUtils.getTodayDateWithoutPath() + ".json";

		FilenameFilter filenameFilter = (dir, name) -> {
			if (!name.contains("horaris_")) {
				return false;
			}

			if (name.contains("_")) {
				String[] split = name.split("_");
				if (split.length == 4) {
					int ind = split[3].indexOf(".json");
					if (ind != -1) {
						String fileDate = split[3].substring(0, ind);
						return !TimeUtils.isFuture(fileDate) && !TimeUtils.isYesterday(fileDate);
					}
				}
			}

			return !name.endsWith(endsWith);
		};

		for (File file : Objects.requireNonNull(filesDir.listFiles(filenameFilter))) {
			U.log("Deleting file: " + file.getName() + ";RESULT: " + file.delete());
		}
	}

	@Override
	protected Void doInBackground(Object... params) {
		if (params.length == 3) {
			Context context = (Context) params[0];
			int widgetId = (int) params[1];
			int deltaDays = (int) params[2];
			int core = U.getCore(context, widgetId);
			String[] stations = U.getStations(context, widgetId);

			if (stations.length == 2 && !stations[0].equalsIgnoreCase("-1") && !stations[1].equalsIgnoreCase("-1")) {
				if (stations[0].equals(stations[1])) {
					U.sendNoTimesError(widgetId, context);
				} else {
					ArrayList<TrainTime> trainTimes = get(context, stations[0], stations[1], core, deltaDays);
					get(context, stations[1], stations[0], core, deltaDays);

					if (trainTimes != null) {
						Intent sendScheduleIntent = new Intent(context, WidgetManager.class);
						sendScheduleIntent.setAction(Constants.ACTION_SEND_SCHEDULE + widgetId + stations[0]);
						sendScheduleIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetId);
						Bundle bundle = new Bundle();

						bundle.putSerializable(Constants.EXTRA_SCHEDULE_DATA, trainTimes);
						sendScheduleIntent.putExtra(Constants.EXTRA_SCHEDULE_BUNDLE, bundle);
						context.sendBroadcast(sendScheduleIntent);
					} else {
						U.sendNoInternetError(widgetId, context);
					}
				}
			} else {
				U.sendNoStationsSetError(widgetId, context);
			}
		}
		return null;
	}
}
