package org.angelmariages.rodalieswidget.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.grandcentrix.tray.AppPreferences;

import org.angelmariages.rodalieswidget.AlarmReceiver;
import org.angelmariages.rodalieswidget.WidgetManager;
import org.angelmariages.rodalieswidget.timetables.TrainTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class U {
	//====================== [ CONSTANTS ] ======================
	public static final int ORIGIN = 100;
	public static final int DESTINATION = 200;

	public static final String ACTION_CLICK_UPDATE_BUTTON = "org.angelmariages.RodaliesWidget.clickUpdateButtonId_";
	public static final String ACTION_CLICK_SWAP_BUTTON = "org.angelmariages.RodaliesWidget.clickSwapButtonId_";
	public static final String ACTION_UPDATE_STATIONS = "org.angelmariages.RodaliesWidget.sendNewSettingsId_";
	public static final String ACTION_SEND_NEW_STATIONS = "org.angelmariages.RodaliesWidget.sendNewStations";
	public static final String ACTION_CLICK_STATIONS_TEXT = "org.angelmariages.RodaliesWidget.clickStationsText_";
	public static final String ACTION_CLICK_LIST_ITEM = "org.angelmariages.RodaliesWidget.clickListItem_";
	public static final String ACTION_WIDGET_NO_DATA = "org.angelmariages.RodaliesWidget.widgetNoData_";
	public static final String ACTION_SEND_SCHEDULE = "org.angelmariages.RodaliesWidget.ACTION_WIDGET_SEND_SCHEDULE_";
	public static final String ACTION_NOTIFY_UPDATE = "org.angelmariages.RodaliesWidget.ACTION_NOTIFY_UPDATE_";

	public static final String EXTRA_OREGNorDESTINATION = "org.angelmariages.RodaliesWidget.originOrDestination";
	public static final String EXTRA_ORIGIN = "org.angelmariages.RodaliesWidget.extraOrigin";
	public static final String EXTRA_DESTINATION = "org.angelmariages.RodaliesWidget.extraDestination";
	public static final String EXTRA_WIDGET_ID = "org.angelmariages.RodaliesWidget.extraWidgetId";
	public static final String EXTRA_ALARM_DEPARTURE_TIME = "org.angelmariages.RodaliesWidget.extraRideLength";
	public static final String EXTRA_CONFIG_STATION = "org.angelmariages.RodaliesWidget.newSettings";
	public static final String EXTRA_WIDGET_STATE = "org.angelmariages.RodaliesWidget.extraWidgetState";
	public static final String EXTRA_SCHEDULE_DATA = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_DATA";
	public static final String EXTRA_SCHEDULE_BUNDLE = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_BUNDLE";

	private static final String PREFERENCE_KEY = "org.angelmariages.RodaliesWidget.PREFERENCE_FILE_KEY_ID_";
	private static final String PREFERENCE_STRING_ORIGIN = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ORIGIN";
	private static final String PREFERENCE_STRING_DESTINATION = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_DESTINATION";
	private static final String PREFERENCE_STRING_ALARM_FOR_ID = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ALARM_FOR_ID_";
	public static final String PREFERENCE_STRING_ALARM_URI = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ALARM_URI";
	private static final String PREFERENCE_STRING_CORE_ID = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_CORE_ID_";

	public static final int WIDGET_STATE_SCHEDULE_LOADED = 0;
	public static final int WIDGET_STATE_NO_INTERNET = 1;
	public static final int WIDGET_STATE_NO_STATIONS = 2;
	public static final int WIDGET_STATE_NO_TIMES = 3;
	public static final int WIDGET_STATE_UPDATING_TABLES = 4;

	//====================== [ END_CONSTANTS ] ======================
	private static final boolean LOGGING = true;

	private static FirebaseDatabase mFirebaseDatabase;

	public static void log(String message) {
		if (LOGGING) {
			Log.d("RodaliesLog", message);
		}
	}

	public static int getIdFromIntent(Intent intent) {
		return intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
	}

	public static int getStateFromIntent(Intent intent) {
		return intent.getIntExtra(U.EXTRA_WIDGET_STATE, -1);
	}

	public static void saveCore(Context context, int widgetID, int coreID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(U.PREFERENCE_STRING_CORE_ID, coreID).apply();
	}

	public static int getCore(Context context, int widgetID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
		int core = sharedPreferences.getInt(U.PREFERENCE_STRING_CORE_ID, -1);

		// TODO: 27/06/2017 Remove for next versions
		String[] stations = U.getStations(context, widgetID);
		if(core == -1 && stations.length == 2) {
			if (StationUtils.nuclis.get(50).containsKey(stations[0]) && StationUtils.nuclis.get(50).containsKey(stations[1])) {
			 	saveCore(context, widgetID, 50);
				return 50;
			}
		}

		return core;
	}

	public static void saveStations(Context context, int widgetID, String origin, String destination) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);

		removeOldPreferences(sharedPreferences);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(U.PREFERENCE_STRING_ORIGIN, origin);
		editor.putString(U.PREFERENCE_STRING_DESTINATION, destination);
		editor.apply();
	}

	public static String[] getStations(Context context, int widgetID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);

		updateOldPreferences(sharedPreferences);

		if (sharedPreferences != null) {
			return new String[]{
					sharedPreferences.getString(U.PREFERENCE_STRING_ORIGIN, "-1"),
					sharedPreferences.getString(U.PREFERENCE_STRING_DESTINATION, "-1")
			};
		}
		return new String[]{"-1", "-1"};
	}

	private static void updateOldPreferences(SharedPreferences sharedPreferences) {
		int origin, destination;
		if(sharedPreferences != null) {
			try {
				if ((origin = sharedPreferences.getInt(U.PREFERENCE_STRING_ORIGIN, -1)) != -1) {
					sharedPreferences.edit().remove(U.PREFERENCE_STRING_ORIGIN).apply();
					sharedPreferences.edit().putString(U.PREFERENCE_STRING_ORIGIN, String.valueOf(origin)).apply();
				}
			} catch (RuntimeException ignored) {}

			try {
				if ((destination = sharedPreferences.getInt(U.PREFERENCE_STRING_DESTINATION, -1)) != -1) {
					sharedPreferences.edit().remove(U.PREFERENCE_STRING_DESTINATION).apply();
					sharedPreferences.edit().putString(U.PREFERENCE_STRING_DESTINATION, String.valueOf(destination)).apply();
				}
			} catch (RuntimeException ignored) {}
		}
	}

	private static void removeOldPreferences(SharedPreferences sharedPreferences) {
		if(sharedPreferences != null) {
			try {
				if (sharedPreferences.getInt(U.PREFERENCE_STRING_ORIGIN, -1) != -1)
					sharedPreferences.edit().remove(U.PREFERENCE_STRING_ORIGIN).apply();
			} catch (RuntimeException ignored) {}
			try {
				if (sharedPreferences.getInt(U.PREFERENCE_STRING_DESTINATION, -1) != -1)
					sharedPreferences.edit().remove(U.PREFERENCE_STRING_DESTINATION).apply();
			} catch (RuntimeException ignored) {}
		}
	}

	public static FirebaseDatabase getFirebaseDatabase() {
		if (mFirebaseDatabase == null) {
			mFirebaseDatabase = FirebaseDatabase.getInstance();
		}
		return mFirebaseDatabase;
	}

	public static void logUpdates(Context context, int widgetID) {
		mFirebaseDatabase = U.getFirebaseDatabase();

		final String[] stations = U.getStations(context, widgetID);

		if (stations.length == 2 && !stations[0].equalsIgnoreCase("-1") && !stations[1].equalsIgnoreCase("-1")) {
			DatabaseReference mRefJourneys = mFirebaseDatabase.getReference("statics/journeys");
			DatabaseReference mRefStations = mFirebaseDatabase.getReference("statics/stations");

			mRefJourneys.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					DataSnapshot originDestination = dataSnapshot.child(stations[0] + "@@" + stations[1]);

					if (originDestination.exists()) {
						int value = Integer.parseInt(String.valueOf(originDestination.getValue()));
						originDestination.getRef().setValue(value + 1);
					} else {
						originDestination.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (mRefJourneys): " + databaseError.getMessage());
				}
			});

			mRefStations.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					DataSnapshot origin = dataSnapshot.child(String.valueOf(stations[0])).child("departures");
					DataSnapshot destination = dataSnapshot.child(String.valueOf(stations[1])).child("arrivals");

					if (origin.exists()) {
						int value = Integer.parseInt(String.valueOf(origin.getValue()));
						origin.getRef().setValue(value + 1);
					} else {
						origin.getRef().setValue(1);
					}

					if (destination.exists()) {
						int value = Integer.parseInt(String.valueOf(destination.getValue()));
						destination.getRef().setValue(value + 1);
					} else {
						destination.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (mRefStations): " + databaseError.getMessage());
				}
			});
		}
	}

	public static void logEventUpdate(ArrayList<TrainTime> trainTimes, Context context) {
		Bundle bundle = null;
		String origin = "-1", destination = "-1";

		if(trainTimes.size() > 0) {
			origin = trainTimes.get(0).getOrigin();
			destination = trainTimes.get(0).getDestination();
		}

		if(!origin.equalsIgnoreCase("-1") && !destination.equalsIgnoreCase("-1")) {
			bundle = new Bundle();
			bundle.putString("origin_station", origin);
			bundle.putString("destination_station", destination);
		}
		FirebaseAnalytics.getInstance(context).logEvent("update_schedules", bundle);
	}

	public static void logEventSwap(Context context) {
		FirebaseAnalytics.getInstance(context).logEvent("swap_schedules", null);
	}

	private static void logEventAlarmSet(Context context, String departure_time, String origin, String destination) {
		Bundle bundle = null;
		if(!origin.equalsIgnoreCase("-1") && !destination.equalsIgnoreCase("-1")) {
			bundle = new Bundle();
			bundle.putString("origin_station", origin);
			bundle.putString("destination_station", destination);
			bundle.putString("departure_time", departure_time);
		}

		FirebaseAnalytics.getInstance(context).logEvent("alarm_set", bundle);
	}

	public static void logEventAlarmFired(Context context) {
		FirebaseAnalytics.getInstance(context).logEvent("alarm_fired", null);
	}

	public static void sendNoInternetError(int widgetId, Context context) {
		Intent noDataIntent = new Intent(context, WidgetManager.class);
		noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_INTERNET);
		context.sendBroadcast(noDataIntent);
	}

	public static void sendNoTimesError(int widgetId, Context context) {
		Intent noDataIntent = new Intent(context, WidgetManager.class);
		noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_TIMES);
		context.sendBroadcast(noDataIntent);
	}

	public static void sendNoStationsSetError(int widgetId, Context context) {
		Intent noStationsIntent = new Intent(context, WidgetManager.class);
		noStationsIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetId);
		noStationsIntent.putExtra(U.EXTRA_WIDGET_ID, widgetId);
		noStationsIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_STATIONS);
		context.sendBroadcast(noStationsIntent);
	}

	public static void sendNotifyUpdate(int widgetID, Context context) {
		Intent notifyUpdateIntent = new Intent(context, WidgetManager.class);
		notifyUpdateIntent.setAction(U.ACTION_NOTIFY_UPDATE + widgetID);
		notifyUpdateIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		context.sendBroadcast(notifyUpdateIntent);
	}

	public static int getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.HOUR_OF_DAY)));
	}

	public static int getCurrentMinute() {
		Calendar cal = Calendar.getInstance();
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.MINUTE)));
	}

	public static String getTodayDateWithoutPath() {
		Calendar cal = Calendar.getInstance();
		return String.format(Locale.getDefault(), "%02d%02d%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}

	public static boolean isDateFuture(String dateWithoutPath) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		try {
			Date date = new Date(format.parse(dateWithoutPath).getTime());
			return DateUtils.isToday(date.getTime()) || date.after(Calendar.getInstance().getTime());
		} catch (ParseException e) {
			U.log("Can't parse date for isDateFuture " + dateWithoutPath);
			return false;
		}
	}

	public static boolean isBeforeCurrentHour(int currentHour, int currentMinute, String time) {
		if (time == null) return false;
		String[] split = time.split(":");
		int hour = Integer.parseInt(split[0]);
		int minute = Integer.parseInt(split[1]);

		return hour != 0 && (hour < currentHour || hour == currentHour && minute <= currentMinute);
	}

	public static boolean setAlarm(Context context, @NonNull String departureTime, @NonNull String alarmTime, int widgetID, String origin, String destination) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		String[] hourMinutes = alarmTime.split(":");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 272829,
				new Intent(context, AlarmReceiver.class).putExtra(EXTRA_WIDGET_ID, widgetID),
				PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);

		if(hourMinutes.length == 2) {
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourMinutes[0]));
			calendar.set(Calendar.MINUTE, Integer.parseInt(hourMinutes[1]));
			calendar.set(Calendar.SECOND, 0);
			alarmManager.cancel(pendingIntent);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			} else {
				alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			}

			new AppPreferences(context).put(PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination, departureTime);

			sendNotifyUpdate(widgetID, context);

			logEventAlarmSet(context, departureTime, origin, destination);

			return true;
		}
		return false;
	}

	public static String getAlarm(Context context, int widgetID, String origin, String destination) {
		return new AppPreferences(context).getString(PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination, null);
	}

	public static void removeAlarm(Context context, int widgetID, String origin, String destination) {
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 272829,
				new Intent(context, AlarmReceiver.class).putExtra(EXTRA_WIDGET_ID, widgetID),
				PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

		new AppPreferences(context).remove(PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination);
		sendNotifyUpdate(widgetID, context);
	}
}
