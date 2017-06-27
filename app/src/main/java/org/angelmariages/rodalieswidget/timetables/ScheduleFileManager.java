package org.angelmariages.rodalieswidget.timetables;

import org.angelmariages.rodalieswidget.utils.U;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

final class ScheduleFileManager {
	private static final String KEY_TRANSFERS = "transfers";
	private static final String KEY_LINE = "line";
	private static final String KEY_DIRECT_TRAIN = "direct_train";
	private static final String KEY_SAME_ORIGIN_TRAIN = "same_origin_train";
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
	private static final String KEY_STATION_TRANSFER_ONE = "station_transfer_one";
	private static final String KEY_STATION_TRANSFER_TWO = "station_transfer_two";


	private ScheduleFileManager() {
	}

	static String getJSONString(ArrayList<TrainTime> trainTimes) {
		if (trainTimes.size() < 1) return null;
		JSONObject scheduleObject = new JSONObject();

		int transfers = trainTimes.get(0).getTransfer();

		try {
			scheduleObject.put(KEY_TRANSFERS, transfers);
			if (transfers > 0) {
				scheduleObject.put(KEY_STATION_TRANSFER_ONE, trainTimes.get(0).getStation_transfer_one());
				if (transfers > 1) {
					scheduleObject.put(KEY_STATION_TRANSFER_TWO, trainTimes.get(0).getStation_transfer_two());
				}
			}

			JSONArray timesArray = new JSONArray();
			for (TrainTime trainTime : trainTimes) {
				JSONObject timeObject = new JSONObject();
				timeObject.put(KEY_LINE, trainTime.getLine());
				timeObject.put(KEY_DIRECT_TRAIN, trainTime.isDirect_train());
				timeObject.put(KEY_SAME_ORIGIN_TRAIN, trainTime.isSame_origin_train());
				timeObject.put(KEY_DEPARTURE, trainTime.getDeparture_time());
				timeObject.put(KEY_ARRIVAL, trainTime.getArrival_time());
				timeObject.put(KEY_TRAVEL_TIME, trainTime.getTravel_time());
				if (trainTime.getTransfer() > 0) {
					timeObject.put(KEY_LINE_TRANSFER_ONE, trainTime.getLine_transfer_one());
					timeObject.put(KEY_DEPARTURE_TRANSFER_ONE, trainTime.getDeparture_time_transfer_one());
					timeObject.put(KEY_ARRIVAL_TRANSFER_ONE, trainTime.getArrival_time_transfer_one());
					if (trainTime.getTransfer() > 1) {
						timeObject.put(KEY_LINE_TRANSFER_TWO, trainTime.getLine_transfer_two());
						timeObject.put(KEY_DEPARTURE_TRANSFER_TWO, trainTime.getDeparture_time_transfer_two());
						timeObject.put(KEY_ARRIVAL_TRANSFER_TWO, trainTime.getArrival_time_transfer_two());
					}
				}
				timesArray.put(timeObject);
			}
			scheduleObject.put(KEY_TIMES, timesArray);
		} catch (JSONException e) {
			U.log("Error on getJSONString: " + e.getMessage());
		}
		return scheduleObject.toString();
	}

	static ArrayList<TrainTime> getScheduleFromJSON(String jsonString, String origin, String destination) {
		ArrayList<TrainTime> schedule = new ArrayList<>();

		try {
			JSONObject scheduleObject = new JSONObject(jsonString);
			int transfers = (int) scheduleObject.get(KEY_TRANSFERS);
			String station_transfer_one = null, station_transfer_two = null;
			if (transfers > 0) {
				station_transfer_one = scheduleObject.optString(KEY_STATION_TRANSFER_ONE);
				if (transfers > 1) {
					station_transfer_two = scheduleObject.optString(KEY_STATION_TRANSFER_TWO);
				}
			}

			JSONArray timesArray = (JSONArray) scheduleObject.get(KEY_TIMES);
			U.log("TIMESARRAY LENGTH: " + timesArray.length());
			switch (transfers) {
				case 0: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(timeObject.optString(KEY_LINE),
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								timeObject.optString(KEY_TRAVEL_TIME),
								origin, destination));
					}
				}
				break;
				case 1: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(timeObject.optString(KEY_LINE),
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								timeObject.optString(KEY_LINE_TRANSFER_ONE),
								station_transfer_one,
								timeObject.optString(KEY_DEPARTURE_TRANSFER_ONE),
								timeObject.optString(KEY_ARRIVAL_TRANSFER_ONE),
								timeObject.optString(KEY_TRAVEL_TIME),
								origin, destination,
								timeObject.optBoolean(KEY_DIRECT_TRAIN),
								timeObject.optBoolean(KEY_SAME_ORIGIN_TRAIN)));
					}
				}
				break;
				case 2: {
					for (int i = 0; i < timesArray.length(); i++) {
						JSONObject timeObject = timesArray.getJSONObject(i);
						schedule.add(new TrainTime(timeObject.optString(KEY_LINE),
								timeObject.optString(KEY_DEPARTURE),
								timeObject.optString(KEY_ARRIVAL),
								timeObject.optString(KEY_LINE_TRANSFER_ONE),
								station_transfer_one,
								timeObject.optString(KEY_DEPARTURE_TRANSFER_ONE),
								timeObject.optString(KEY_ARRIVAL_TRANSFER_ONE),
								timeObject.optString(KEY_LINE_TRANSFER_TWO),
								station_transfer_two,
								timeObject.optString(KEY_DEPARTURE_TRANSFER_TWO),
								timeObject.optString(KEY_ARRIVAL_TRANSFER_TWO),
								origin, destination,
								timeObject.optBoolean(KEY_SAME_ORIGIN_TRAIN)));
					}
				}
				break;
			}
		} catch (JSONException e) {
			U.log("Error on getJSONString: " + Arrays.toString(e.getStackTrace()));
		}
		return schedule;
	}
}
