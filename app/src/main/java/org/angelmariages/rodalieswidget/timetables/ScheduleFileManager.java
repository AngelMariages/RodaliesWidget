package org.angelmariages.rodalieswidget.timetables;

import android.util.JsonReader;

import org.angelmariages.rodalieswidget.utils.U;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class ScheduleFileManager {
	private static final String KEY_TRANSFERS = "transfers";
	private static final String KEY_LINE = "line";
	private static final String KEY_TIMES = "times";
	private static final String KEY_TRAVEL_TIME = "travel_time";
	private static final String KEY_LINE_TRANSFER_ONE = "line_transfer_one";
	private static final String KEY_LINE_TRANSFER_TWO = "line_transfer_two";
	private static final String KEY_DEPARTURE = "departure_time";
	private static final String KEY_ARRIVAL = "arrival_time";
	private static final String KEY_DEPARTURE_TRANSFER_ONE = "departure_time_transfer_one";
	private static final String KEY_ARRIVAL_TRANSFER_ONE = "arrival_time_transfer_one";
	private static final String KEY_DEPARTURE_TRANSFER_TWO = "departure_time_transfer_two";
	private static final String KEY_ARRIVAL_TRANSFER_TWO = "arrival_time_transfer_two";


	private ScheduleFileManager() {}

	public static String getJSONString(ArrayList<TrainTime> trainTimes) {
		if(trainTimes.size() < 1) return null;
		JSONObject scheduleObject = new JSONObject();

		int transfers = trainTimes.get(0).getTransfer();

		try {
			scheduleObject.put(KEY_TRANSFERS, transfers);
			scheduleObject.put(KEY_LINE, trainTimes.get(0).getLine());
			if (transfers > 0) {
				scheduleObject.put(KEY_LINE_TRANSFER_ONE, trainTimes.get(0).getLine_transfer_one());
				if (transfers > 1)
					scheduleObject.put(KEY_LINE_TRANSFER_TWO, trainTimes.get(0).getLine_transfer_two());
			}

			JSONArray timesArray = new JSONArray();
			for (TrainTime trainTime : trainTimes) {
				JSONObject timeObject = new JSONObject();
				timeObject.put(KEY_DEPARTURE, trainTime.getDeparture_time());
				timeObject.put(KEY_ARRIVAL, trainTime.getArrival_time());
				timeObject.put(KEY_TRAVEL_TIME, trainTime.getTravel_time());
				if (trainTime.getTransfer() > 0) {
					timeObject.put(KEY_DEPARTURE_TRANSFER_ONE, trainTime.getDeparture_time_transfer_one());
					timeObject.put(KEY_ARRIVAL_TRANSFER_ONE, trainTime.getArrival_time_transfer_one());
					if (trainTime.getTransfer() > 1) {
						timeObject.put(KEY_DEPARTURE_TRANSFER_TWO, trainTime.getDeparture_time_transfer_two());
						timeObject.put(KEY_ARRIVAL_TRANSFER_TWO, trainTime.getArrival_time_transfer_two());
					}
				}
				timesArray.put(timeObject);
			}
			scheduleObject.put(KEY_TIMES, timesArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return scheduleObject.toString();
	}

	public static ArrayList<TrainTime> getScheduleFromJSON(String jsonString, int origen, int desti) {
		ArrayList<TrainTime> schedule = new ArrayList<>();

		try {
			JSONObject scheduleObject = new JSONObject(jsonString);
			int transfers = (int) scheduleObject.get(KEY_TRANSFERS);
			String line = scheduleObject.getString(KEY_LINE);
			String line_transfer_one = null, line_transfer_two = null;
			if(transfers > 0) {
				line_transfer_one = scheduleObject.getString(KEY_LINE_TRANSFER_ONE);
				if(transfers > 1)
					line_transfer_two = scheduleObject.getString(KEY_LINE_TRANSFER_TWO);
			}

			JSONArray timesArray = (JSONArray) scheduleObject.get(KEY_TIMES);
			U.log("TIMESARRAY LENGTH: " + timesArray.length());
			switch (transfers) {
				case 0: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(line,
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								timeObject.optString(KEY_TRAVEL_TIME),
								origen, desti));
					}
				} break;
				case 1: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(1, line,
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								line_transfer_one,
								timeObject.getString(KEY_DEPARTURE_TRANSFER_ONE),
								timeObject.getString(KEY_ARRIVAL_TRANSFER_ONE),
								timeObject.getString(KEY_TRAVEL_TIME),
								origen, desti));
					}
				} break;
				case 2: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(2, line,
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								line_transfer_one,
								timeObject.optString(KEY_DEPARTURE_TRANSFER_ONE),
								timeObject.optString(KEY_ARRIVAL_TRANSFER_ONE),
								line_transfer_two,
								timeObject.getString(KEY_DEPARTURE_TRANSFER_TWO),
								timeObject.getString(KEY_ARRIVAL_TRANSFER_TWO),
								origen, desti));
					}
				} break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return schedule;
	}
}
