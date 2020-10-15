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
 *
 */

package org.angelmariages.rodalieswidget;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.angelmariages.rodalieswidget.timetables.GetSchedule;
import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;

import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

class RodaliesWidget extends RemoteViews {
	private final int state;
	private final Context context;
	private final int widgetID;

	RodaliesWidget(Context context, int widgetID, int state, int layout, ArrayList<TrainTime> schedule, int deltaDays) {
		super(context.getPackageName(), layout);
		this.context = context;
		this.widgetID = widgetID;

		final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
		FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
				.setMinimumFetchIntervalInSeconds(HOURS.toSeconds(1))
				.build();
		firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

		firebaseRemoteConfig.setDefaultsAsync(singletonMap("remote_view_in_memory", false));
		firebaseRemoteConfig.fetch(MINUTES.toSeconds(30)).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				firebaseRemoteConfig.activate();
			}
		});

		setStationNames();
		setPendingIntents();
		//@TODO manage web service status !important

		this.state = state;

		if (state == Constants.WIDGET_STATE_UPDATING_TABLES) {
			startForegroundService(context);
			new GetSchedule().execute(context, widgetID, deltaDays);
		} else if (state == Constants.WIDGET_STATE_SCHEDULE_LOADED) {
			Intent adapterIntent = new Intent(context, WidgetService.class);
			adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
			adapterIntent.setData(Uri.fromParts("content", String.valueOf(widgetID) + Math.random(), null));

			Bundle bundle = new Bundle();
			bundle.putSerializable(Constants.EXTRA_SCHEDULE_DATA, schedule);
			adapterIntent.putExtra(Constants.EXTRA_SCHEDULE_BUNDLE, bundle);

			if (schedule != null && schedule.size() > 0) {
				int core = U.getCore(context, widgetID);
				TrainTime trainTime = schedule.get(0);
				switch (trainTime.getTransfer()) {
					case 1: {
						String transferStation = null;
						try {
							transferStation = StationUtils.getNameFromID(trainTime.getStation_transfer_one(), core);
						} catch (NumberFormatException ignored) {
						}
						if (transferStation != null) {
							this.setTextViewText(R.id.transferOneTitleText, transferStation);
							this.setTextViewText(R.id.lineTransferOneText, trainTime.getLine_transfer_one());
							try {
								this.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one() + core).getBColor()));
								this.setTextColor(R.id.lineTransferOneText, StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one() + core).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + trainTime.getLine_transfer_one() + core);
							}
						} else this.setViewVisibility(R.id.transferOneTitleText, View.GONE);
					}
					break;
					case 2: {
						String transferStation = null, transferStationTwo = null;
						try {
							transferStation = StationUtils.getNameFromID(trainTime.getStation_transfer_one(), core);
							transferStationTwo = StationUtils.getNameFromID(trainTime.getStation_transfer_two(), core);
						} catch (NumberFormatException ignored) {
						}
						if (transferStation != null) {
							this.setTextViewText(R.id.transferOneTitleText, transferStation);
							this.setTextViewText(R.id.lineTransferOneText, trainTime.getLine_transfer_one());
							try {
								this.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one() + core).getBColor()));
								this.setTextColor(R.id.lineTransferOneText, StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one() + core).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + trainTime.getLine_transfer_one() + core);
							}
						} else this.setViewVisibility(R.id.transferOneTitleText, View.GONE);
						if (transferStationTwo != null) {
							this.setTextViewText(R.id.transferTwoTitleText, transferStationTwo);
							this.setTextViewText(R.id.lineTransferTwoText, trainTime.getLine_transfer_two());
							try {
								this.setInt(R.id.lineTransferTwoText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_two() + core).getBColor()));
								this.setTextColor(R.id.lineTransferTwoText, StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_two() + core).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + trainTime.getLine_transfer_two() + core);
							}
						} else this.setViewVisibility(R.id.transferTwoTitleText, View.GONE);
					}
					break;
				}
			}
			this.setRemoteAdapter(R.id.scheduleListView, adapterIntent);
		} else if (state == Constants.WIDGET_STATE_NO_INTERNET) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_internet));
		} else if (state == Constants.WIDGET_STATE_NO_STATIONS) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_stations));
		} else if (state == Constants.WIDGET_STATE_NO_TIMES) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_times));
		}

		setStationNames();
		if (state != Constants.WIDGET_STATE_UPDATING_TABLES) {
			setPendingIntents();
			stopForegroundService(context);
		}
	}

	private void startForegroundService(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			context.startForegroundService(new Intent(context, ScheduleUpdateNotificationService.class));
		}
	}

	private void stopForegroundService(Context context) {
		context.stopService(new Intent(context, ScheduleUpdateNotificationService.class));
	}

	private void setStationNames() {
		String[] stations = U.getStations(context, widgetID);
		if (stations.length == 2) {
			int core = U.getCore(context, widgetID);

			/*Crashlytics.setString("origin", stations[0]);
			Crashlytics.setString("destination", stations[1]);
			Crashlytics.setInt("core", core);*/

			updateStationsText(StationUtils.getNameFromID(stations[0], core), StationUtils.getNameFromID(stations[1], core));
		}
	}

	private void setPendingIntents() {
		setListViewClickIntent();
		setUpdateButtonIntent();
		setSwapButtonIntent();
		setConfigStationIntent();
	}

	private void setListViewClickIntent() {
		//It this intent is not set the intent when on click on a row of the list view doesn't work
		Intent listViewClickIntent = new Intent(context, WidgetManager.class);
		listViewClickIntent.setAction(Constants.ACTION_CLICK_LIST_ITEM + getWidgetID());
		listViewClickIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		PendingIntent clickPI = PendingIntent.getBroadcast(context, 0,
				listViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setPendingIntentTemplate(R.id.scheduleListView, clickPI);
	}

	private void setUpdateButtonIntent() {
		Intent updateButtonIntent = new Intent(context, WidgetManager.class);
		updateButtonIntent.setAction(Constants.ACTION_CLICK_UPDATE_BUTTON + getWidgetID());
		updateButtonIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		updateButtonIntent.putExtra(Constants.EXTRA_WIDGET_STATE, state);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				updateButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.updateButton, pendingIntent);
	}

	private void setSwapButtonIntent() {
		Intent swapButtonIntent = new Intent(context, WidgetManager.class);
		swapButtonIntent.setAction(Constants.ACTION_CLICK_SWAP_BUTTON + getWidgetID());
		swapButtonIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		swapButtonIntent.putExtra(Constants.EXTRA_WIDGET_STATE, state);
		PendingIntent swapPI = PendingIntent.getBroadcast(context, 0,
				swapButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.swapButton, swapPI);
	}

	private void setConfigStationIntent() {
		Intent originStationIntent = new Intent(context, WidgetManager.class);
		originStationIntent.setAction(Constants.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_O");
		originStationIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		originStationIntent.putExtra(Constants.EXTRA_ORIGINorDESTINATION, Constants.ORIGIN);
		PendingIntent showDialogPI1 = PendingIntent.getBroadcast(context, 0,
				originStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.originLayout, showDialogPI1);

		Intent destinationStationIntent = new Intent(context, WidgetManager.class);
		destinationStationIntent.setAction(Constants.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_D");
		destinationStationIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		destinationStationIntent.putExtra(Constants.EXTRA_ORIGINorDESTINATION, Constants.DESTINATION);
		PendingIntent showDialogPI2 = PendingIntent.getBroadcast(context, 0,
				destinationStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.destinationLayout, showDialogPI2);
	}

	private void updateStationsText(String originText, String destinationText) {
		String nullOrigin = context.getResources().getString(R.string.no_origin_set);
		String nullDestination = context.getResources().getString(R.string.no_destination_set);
		if (originText == null) originText = nullOrigin;
		if (destinationText == null) destinationText = nullDestination;

		this.setTextViewText(R.id.originTextView, originText);
		this.setTextViewText(R.id.destinationTextView, destinationText);
	}

	private String getWidgetID() {
		return String.valueOf(widgetID);
	}


}

