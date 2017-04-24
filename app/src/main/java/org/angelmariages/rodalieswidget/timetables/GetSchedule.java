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

import org.angelmariages.rodalieswidget.WidgetManager;
import org.angelmariages.rodalieswidget.utils.U;

public class GetSchedule extends AsyncTask<Integer, Void, Void> {
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

			ArrayList<TrainTime> hourSchedule = null;
			if (schedule != null && schedule.size() > 0) {
				hourSchedule = new ArrayList<>();
				addHoursToShedule(schedule, hourSchedule);

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
				addHoursToShedule(scheduleFromJSON, hourSchedule);
			}

			return hourSchedule;
		}
	}

	private void addHoursToShedule(ArrayList<TrainTime> scheduleFromJSON, ArrayList<TrainTime> hourSchedule) {
		int currentHour = U.getCurrentHour();
		int currentMinute = U.getCurrentMinute();
		boolean show_all_times = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_all_times", false);

		for (TrainTime trainTime : scheduleFromJSON) {
			String departureTime = trainTime.getDeparture_time();
			String departureTime_one = trainTime.getDeparture_time_transfer_one();
			String departureTime_two = trainTime.getDeparture_time_transfer_two();
			if (!show_all_times) {
				int hour = -1, minute = -1;
				if (departureTime != null) {
					String[] split = departureTime.split(":");
					hour = Integer.parseInt(split[0]);
					minute = Integer.parseInt(split[1]);
				} else if (departureTime_one != null) {
					String[] split1 = departureTime_one.split(":");
					hour = Integer.parseInt(split1[0]);
					minute = Integer.parseInt(split1[1]);
				} else if (departureTime_two != null) {
					String[] split2 = departureTime_two.split(":");
					hour = Integer.parseInt(split2[0]);
					minute = Integer.parseInt(split2[1]);
				}

				if(hour == 0) {
					hourSchedule.add(trainTime);
				} else if (hour == currentHour && minute >= currentMinute) {
					hourSchedule.add(trainTime);
				} else if(hour > currentHour) {
					hourSchedule.add(trainTime);
				}
			} else {
				hourSchedule.add(trainTime);
			}
		}
	}

	private String readJSONFile() {
		String fileName = "horaris_" + origin + "_" + destination + "_" + U.getTodayDateWithoutPath() + ".json";

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

		String fileName = "horaris_" + origin + "_" + destination + "_" + U.getTodayDateWithoutPath() + ".json";

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
		final String endsWith = "_" + U.getTodayDateWithoutPath() + ".json";

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
		final String endsWith = "_" + U.getTodayDateWithoutPath() + ".xml";

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

	@Override
	protected Void doInBackground(Integer... params) {
		if (params.length == 1) {
			int widgetId = params[0];
			int[] stations = U.getStations(context, widgetId);
			if (stations.length == 2 && stations[0] != -1 && stations[1] != -1) {
				if (stations[0] == stations[1]) {
					U.sendNoTimesError(widgetId, context);
				} else {
					ArrayList<TrainTime> trainTimes = get(stations[0], stations[1]);
					get(stations[1], stations[0]);

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
		}
		return null;
	}
}
