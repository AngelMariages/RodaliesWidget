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
import java.util.Calendar;
import java.util.Locale;

import org.angelmariages.rodalieswidget.WidgetManager;
import org.angelmariages.rodalieswidget.utils.U;

public class GetSchedule extends AsyncTask<Integer, Void, Void> {
	private final Calendar cal = Calendar.getInstance();
	private final Context context;
	private int origin = -1;
	private int destination = -1;

	public GetSchedule(Context context) {
		this.context = context;
	}

	private ArrayList<TrainTime> get(int origin, int destination) {
		this.origin = origin;
		this.destination = destination;
		return getJSONFromToday();
	}

	private ArrayList<TrainTime> getJSONFromToday() {
		String jsonFileRead = readJSONFile();

		if (jsonFileRead.isEmpty()) {
			U.log("Getting json from internet...");
			RodaliesSchedule rodaliesSchedule = new RodaliesSchedule(origin, destination);
			ArrayList<TrainTime> schedule = rodaliesSchedule.getSchedule();

			ArrayList<TrainTime> hourSchedule = new ArrayList<>();
			if (schedule != null && schedule.size() > 0) {
				int currentHour = getCurrentHour();
				for (TrainTime trainTime : schedule) {
					String departureTime = trainTime.getDeparture_time();
					String departureTime_one = trainTime.getDeparture_time_transfer_one();
					String departureTime_two = trainTime.getDeparture_time_transfer_two();
					boolean show_all_times = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_all_times", false);
					if(!show_all_times) {
						int hour = 24;
						if (departureTime != null)
							hour = Integer.parseInt(departureTime.split(":")[0]);
						else if (departureTime_one != null)
							hour = Integer.parseInt(departureTime_one.split(":")[0]);
						else if (departureTime_two != null)
							hour = Integer.parseInt(departureTime_two.split(":")[0]);

						if (hour == 0 || hour >= currentHour) {
							hourSchedule.add(trainTime);
						}
					} else {
						hourSchedule.add(trainTime);
					}
				}

				jsonFileRead = ScheduleFileManager.getJSONString(schedule);
				saveJSONFile(jsonFileRead);
				removeOldJSON();
			}

			return hourSchedule;
		} else {
			U.log("Getting json from file...");

			ArrayList<TrainTime> scheduleFromJSON = ScheduleFileManager.getScheduleFromJSON(jsonFileRead, origin, destination);
			ArrayList<TrainTime> hourSchedule = new ArrayList<>();
			if (scheduleFromJSON.size() > 0) {
				int currentHour = getCurrentHour();
				for (TrainTime trainTime : scheduleFromJSON) {
					String departureTime = trainTime.getDeparture_time();
					String departureTime_one = trainTime.getDeparture_time_transfer_one();
					String departureTime_two = trainTime.getDeparture_time_transfer_two();
					boolean show_all_times = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_all_times", false);
					if(!show_all_times) {
						int hour = 24;
						if (departureTime != null)
							hour = Integer.parseInt(departureTime.split(":")[0]);
						else if (departureTime_one != null)
							hour = Integer.parseInt(departureTime_one.split(":")[0]);
						else if (departureTime_two != null)
							hour = Integer.parseInt(departureTime_two.split(":")[0]);

						if (hour == 0 || hour >= currentHour) {
							hourSchedule.add(trainTime);
						}
					} else {
						hourSchedule.add(trainTime);
					}
				}
			}

			return hourSchedule;
		}
	}

	private String readJSONFile() {
		String fileName = "horaris_" + origin + "_" + destination + "_" + getTodayDateWithoutPath() + ".json";

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

	private void saveJSONFile(String jsonFile) {
		if (jsonFile.isEmpty()) return;

		String fileName = "horaris_" + origin + "_" + destination + "_" + getTodayDateWithoutPath() + ".json";

		OutputStreamWriter outputStreamWriter;
		try {
			outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
			outputStreamWriter.write(jsonFile);
			outputStreamWriter.close();
		} catch (IOException e) {
			U.log("Error on saveJSONFile: " + Arrays.toString(e.getStackTrace()));
		}
	}

	private void removeOldJSON() {
		File filesDir = context.getFilesDir();
		final String endsWith = "_" + getTodayDateWithoutPath() + ".json";

		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith(endsWith) && !name.equals("instant-run");
			}
		};

		for (File file : filesDir.listFiles(filenameFilter)) {
			U.log("Deleting file: " + file.getName() + ";RESULT: " + file.delete());
		}
		// TODO: 29/01/17 Clear this in the next release
		removeOldXML();
	}

	private void removeOldXML() {
		File filesDir = context.getFilesDir();
		final String endsWith = "_" + getTodayDateWithoutPath() + ".xml";

		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith(".json") && !name.endsWith(endsWith) && !name.equals("instant-run");
			}
		};

		for (File file : filesDir.listFiles(filenameFilter)) {
			U.log("Deleting old file: " + file.getName() + ";RESULT: " + file.delete());
		}
	}

	private String getTodayDateWithoutPath() {
		return String.format(Locale.getDefault(), "%02d%02d%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}

	private int getCurrentHour() {
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.HOUR_OF_DAY)));
	}

	@Override
	protected Void doInBackground(Integer... params) {
		if (params.length == 1) {
			int widgetId = params[0];
			int[] stations = U.getStations(context, widgetId);
			if (stations.length == 2) {
				if (stations[0] != -1 && stations[1] != -1) {
					if (stations[0] == stations[1]) {
						U.sendNoTimesError(widgetId, context);
					} else {
						ArrayList<TrainTime> trainTimes = get(stations[0], stations[1]);

						if (trainTimes != null) {
							Intent sendScheduleIntent = new Intent(context, WidgetManager.class);
							sendScheduleIntent.setAction(U.ACTION_SEND_SCHEDULE + widgetId + stations[0]);
							sendScheduleIntent.putExtra(U.EXTRA_WIDGET_ID, widgetId);
							Bundle bundle = new Bundle();

							bundle.putSerializable(U.EXTRA_SCHEDULE_DATA, trainTimes);
							sendScheduleIntent.putExtra(U.EXTRA_SCHEDULE_BUNDLE, bundle);
							context.sendBroadcast(sendScheduleIntent);
						} else {
							U.sendNoInternetError(widgetId, context);
						}
					}
				} else {
					U.sendNoStationsSetError(widgetId, context);
				}
			} else {
				U.sendNoStationsSetError(widgetId, context);
			}
		}
		return null;
	}
}
