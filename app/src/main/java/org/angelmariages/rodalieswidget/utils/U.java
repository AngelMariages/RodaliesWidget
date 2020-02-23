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

package org.angelmariages.rodalieswidget.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.angelmariages.rodalieswidget.BuildConfig;
import org.angelmariages.rodalieswidget.WidgetManager;
import org.angelmariages.rodalieswidget.timetables.TrainTime;

import java.util.ArrayList;
import java.util.Locale;

public final class U {

	private static FirebaseDatabase mFirebaseDatabase;

	public static void log(String message) {
		if (Constants.LOGGING) {
			Log.d("RodaliesLog", message);
		}
	}

	public static int getFirstWidgetId(Context context) {
		try {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int ids[] = appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), WidgetManager.class.getName()));
			if (ids.length != 0) {
				return ids[0];
			}
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}

	public static boolean isFirstTime(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_GLOBAL_KEY, Context.MODE_PRIVATE);
		boolean isFirstTime = sharedPreferences.getBoolean(Constants.PREFERENCE_BOOLEAN_FIRST_TIME, true);
		if (isFirstTime) {
			sharedPreferences.edit().putBoolean(Constants.PREFERENCE_BOOLEAN_FIRST_TIME, false).apply();
		}
		return isFirstTime;
	}

	public static int getIdFromIntent(Intent intent) {
		return intent.getIntExtra(Constants.EXTRA_WIDGET_ID, -1);
	}

	public static int getStateFromIntent(Intent intent) {
		return intent.getIntExtra(Constants.EXTRA_WIDGET_STATE, -1);
	}

	public static void saveCore(Context context, int widgetID, int coreID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(Constants.PREFERENCE_STRING_CORE_ID, coreID).apply();
	}

	public static int getCore(Context context, int widgetID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
		int core = sharedPreferences.getInt(Constants.PREFERENCE_STRING_CORE_ID, -1);

		// TODO: 27/06/2017 Remove for next versions
		String[] stations = U.getStations(context, widgetID);
		if (core == -1 && stations.length == 2) {
			if (StationUtils.nuclis.get(50).containsKey(stations[0]) && StationUtils.nuclis.get(50).containsKey(stations[1])) {
				saveCore(context, widgetID, 50);
				return 50;
			}
		}

		return core;
	}

	public static void saveStations(Context context, int widgetID, String origin, String destination) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);

		removeOldPreferences(sharedPreferences);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Constants.PREFERENCE_STRING_ORIGIN, origin);
		editor.putString(Constants.PREFERENCE_STRING_DESTINATION, destination);
		editor.apply();
	}

	public static String[] getStations(Context context, int widgetID) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);

		updateOldPreferences(sharedPreferences);

		if (sharedPreferences != null) {
			return new String[]{
					sharedPreferences.getString(Constants.PREFERENCE_STRING_ORIGIN, "-1"),
					sharedPreferences.getString(Constants.PREFERENCE_STRING_DESTINATION, "-1")
			};
		}
		return new String[]{"-1", "-1"};
	}

	private static void updateOldPreferences(SharedPreferences sharedPreferences) {
		int origin, destination;
		if (sharedPreferences != null) {
			try {
				if ((origin = sharedPreferences.getInt(Constants.PREFERENCE_STRING_ORIGIN, -1)) != -1) {
					sharedPreferences.edit().remove(Constants.PREFERENCE_STRING_ORIGIN).apply();
					sharedPreferences.edit().putString(Constants.PREFERENCE_STRING_ORIGIN, String.valueOf(origin)).apply();
				}
			} catch (RuntimeException ignored) {
			}

			try {
				if ((destination = sharedPreferences.getInt(Constants.PREFERENCE_STRING_DESTINATION, -1)) != -1) {
					sharedPreferences.edit().remove(Constants.PREFERENCE_STRING_DESTINATION).apply();
					sharedPreferences.edit().putString(Constants.PREFERENCE_STRING_DESTINATION, String.valueOf(destination)).apply();
				}
			} catch (RuntimeException ignored) {
			}
		}
	}

	private static void removeOldPreferences(SharedPreferences sharedPreferences) {
		if (sharedPreferences != null) {
			try {
				if (sharedPreferences.getInt(Constants.PREFERENCE_STRING_ORIGIN, -1) != -1)
					sharedPreferences.edit().remove(Constants.PREFERENCE_STRING_ORIGIN).apply();
			} catch (RuntimeException ignored) {
			}
			try {
				if (sharedPreferences.getInt(Constants.PREFERENCE_STRING_DESTINATION, -1) != -1)
					sharedPreferences.edit().remove(Constants.PREFERENCE_STRING_DESTINATION).apply();
			} catch (RuntimeException ignored) {
			}
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
			DatabaseReference journeyRef = mFirebaseDatabase.getReference("statics/journeys/" + stations[0] + "@@" + stations[1]);
			DatabaseReference departuresRef = mFirebaseDatabase.getReference("statics/stations/" + stations[0] + "/departures");
			DatabaseReference arrivalsRef = mFirebaseDatabase.getReference("statics/stations/" + stations[1] + "/arrivals");

			journeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						int value = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
						dataSnapshot.getRef().setValue(value + 1);
					} else {
						dataSnapshot.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (journeyRef): " + databaseError.getMessage());
				}
			});

			departuresRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						int value = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
						dataSnapshot.getRef().setValue(value + 1);
					} else {
						dataSnapshot.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (departuresRef): " + databaseError.getMessage());
				}
			});

			arrivalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						int value = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
						dataSnapshot.getRef().setValue(value + 1);
					} else {
						dataSnapshot.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (arrivalsRef): " + databaseError.getMessage());
				}
			});
		}
	}

	public static void logEventUpdate(ArrayList<TrainTime> trainTimes, Context context) {
		Bundle bundle = null;
		String origin = "-1", destination = "-1";

		if (trainTimes.size() > 0) {
			origin = trainTimes.get(0).getOrigin();
			destination = trainTimes.get(0).getDestination();
		}

		if (!origin.equalsIgnoreCase("-1") && !destination.equalsIgnoreCase("-1")) {
			bundle = new Bundle();
			bundle.putString("origin_station", origin);
			bundle.putString("destination_station", destination);
		}
		FirebaseAnalytics.getInstance(context).logEvent("update_schedules", bundle);
		setUserProperty(context, "last_time_used", System.currentTimeMillis());
		setUserProperty(context, "last_origin", origin);
		setUserProperty(context, "last_destination", destination);
	}

	public static void logEventSwap(Context context) {
		FirebaseAnalytics.getInstance(context).logEvent("swap_schedules", null);
	}

	public static void setUserProperty(Context context, final String key, final Object data) {
		FirebaseAnalytics.getInstance(context).setUserProperty(key, data.toString());
		FirebaseApp instance = FirebaseApp.getInstance();
		if (instance != null) {
			final FirebaseInstanceId firebaseInstanceId = FirebaseInstanceId.getInstance(instance);
			String uid = firebaseInstanceId.getId();
			Locale locale = ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
			final String language = locale.getLanguage();

			if (uid != null) {
				mFirebaseDatabase = U.getFirebaseDatabase();
				final DatabaseReference userProperties = mFirebaseDatabase.getReference("userProperties/" + uid);
				userProperties.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						userProperties.child("device_model").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								if (!dataSnapshot.exists()) {
									userProperties.child("device_model").setValue(Build.MODEL);
								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});

						userProperties.child("firebase_token").setValue(FirebaseInstanceId.getInstance().getToken());
						userProperties.child("language").setValue(language);

						if (dataSnapshot.exists()) {
							userProperties.child(key).setValue(data);
						} else {
							userProperties.setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									userProperties.child(key).setValue(data);
								}
							});
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});
			}
		}
	}

	public static void noColourFound(Context context, String line, String origin, String destination, String nucli) {
		mFirebaseDatabase = U.getFirebaseDatabase();
		final DatabaseReference colorsNotFound = mFirebaseDatabase.getReference("colorsNotFound");

		DatabaseReference n = colorsNotFound.child(nucli).getRef();
		n.child("origin").setValue(origin);
		n.child("destination").setValue(destination);
		n.child("line").setValue(line);
		n.child(String.valueOf(TimeUtils.getCurrentDateAsTimestamp())).setValue(BuildConfig.VERSION_CODE);
	}

	public static void sendNoInternetError(int widgetId, Context context) {
		Intent noDataIntent = new Intent(context, WidgetManager.class);
		noDataIntent.setAction(Constants.ACTION_WIDGET_NO_DATA + widgetId);
		noDataIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetId);
		noDataIntent.putExtra(Constants.EXTRA_WIDGET_STATE, Constants.WIDGET_STATE_NO_INTERNET);
		context.sendBroadcast(noDataIntent);
	}

	public static void sendNoTimesError(int widgetId, Context context) {
		Intent noDataIntent = new Intent(context, WidgetManager.class);
		noDataIntent.setAction(Constants.ACTION_WIDGET_NO_DATA + widgetId);
		noDataIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetId);
		noDataIntent.putExtra(Constants.EXTRA_WIDGET_STATE, Constants.WIDGET_STATE_NO_TIMES);
		context.sendBroadcast(noDataIntent);
	}

	public static void sendNoStationsSetError(int widgetId, Context context) {
		Intent noStationsIntent = new Intent(context, WidgetManager.class);
		noStationsIntent.setAction(Constants.ACTION_WIDGET_NO_DATA + widgetId);
		noStationsIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetId);
		noStationsIntent.putExtra(Constants.EXTRA_WIDGET_STATE, Constants.WIDGET_STATE_NO_STATIONS);
		context.sendBroadcast(noStationsIntent);
	}

	public static void sendNotifyUpdate(int widgetID, Context context) {
		Intent notifyUpdateIntent = new Intent(context, WidgetManager.class);
		notifyUpdateIntent.setAction(Constants.ACTION_NOTIFY_UPDATE + widgetID);
		notifyUpdateIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		context.sendBroadcast(notifyUpdateIntent);
	}

	public static int getScrollPosition(ArrayList<TrainTime> schedule) {
		for (int i = 0; i < schedule.size(); i++) {
			if (TimeUtils.isScheduledTrain(schedule.get(i))) return i;
		}
		return 0;
	}

	public static int getColor(Context context, int resourceId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return context.getResources().getColor(resourceId, null);
		} else {
			//noinspection deprecation
			return context.getResources().getColor(resourceId);
		}
	}

	public static void logEventTimeout(String url, Context context) {
		Bundle bundle;

		bundle = new Bundle();
		bundle.putString("url_timeout", url);

		FirebaseAnalytics.getInstance(context).logEvent("update_timeout", bundle);
	}
}
