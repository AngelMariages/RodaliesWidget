/*
 * MIT License
 *
 * Copyright (c) 2017 Àngel Mariages
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class WidgetManager extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		U.log("onEnabled");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		// TODO: 07-May-16 Borrar shared preferences
		U.log("onDeleted");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		U.log("onUpdate; num of widgets:" + appWidgetIds.length);

		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetID = appWidgetIds[i];

			appWidgetManager.updateAppWidget(widgetID, reloadWidget(context, widgetID, 0));

			U.log("Updating widget id:" + i + " widgetID? " + widgetID);
		}
		try {
			U.setUserProperty(context, "num_of_widgets", appWidgetIds.length);
		} catch (Exception ignored) {
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String intentAction = intent.getAction();

		U.log("onReceive(); intentAction: " + intentAction);

		Fabric.with(context, new Crashlytics());

		if (intentAction == null || intentAction.isEmpty()) return;

		if (intentAction.startsWith(Constants.ACTION_SEND_SCHEDULE)) {
			int widgetID = U.getIdFromIntent(intent);

			if (intent.hasExtra(Constants.EXTRA_SCHEDULE_BUNDLE)) {
				Bundle bundle = intent.getBundleExtra(Constants.EXTRA_SCHEDULE_BUNDLE);
				ArrayList<TrainTime> schedule = (ArrayList<TrainTime>) bundle.getSerializable(Constants.EXTRA_SCHEDULE_DATA);

				loadSchedule(context, widgetID, schedule);

				U.logEventUpdate(schedule, context);
			}

		} else if (intentAction.startsWith(Constants.ACTION_CLICK_UPDATE_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);

			reloadWidget(context, widgetID, 0);

			U.logUpdates(context, widgetID);
		} else if (intentAction.startsWith(Constants.ACTION_CLICK_SWAP_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);

			swapStations(context, widgetID, widgetState);

			U.logEventSwap(context);
		} else if (intentAction.startsWith(Constants.ACTION_CLICK_STATIONS_TEXT)) {
			int widgetID = U.getIdFromIntent(intent);

			int originOrDestination = intent.getIntExtra(Constants.EXTRA_ORIGINorDESTINATION, -1);
			if (widgetID != -1 && originOrDestination != -1) {
				Intent dialogActivity = new Intent(context, SelectStation.class);
				dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				dialogActivity.putExtra(Constants.EXTRA_ORIGINorDESTINATION, originOrDestination);
				dialogActivity.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);

				context.startActivity(dialogActivity);
			}
		} else if (intentAction.startsWith(Constants.ACTION_UPDATE_STATIONS)) {
			int widgetID = U.getIdFromIntent(intent);

			String newOrigin = intent.getStringExtra(Constants.EXTRA_ORIGIN);
			String newDestination = intent.getStringExtra(Constants.EXTRA_DESTINATION);

			U.log("Got update to: " + widgetID);
			U.log("UpdateContains: " + newOrigin + "," + newDestination);

			updateStationTexts(StationUtils.getNameFromID(newOrigin, U.getCore(context, widgetID)), StationUtils.getNameFromID(newDestination, U.getCore(context, widgetID)),
					context, widgetID);
		} else if (intentAction.startsWith(Constants.ACTION_CLICK_LIST_ITEM)) {
			int widgetID = U.getIdFromIntent(intent);

			boolean promotion_line = intent.getBooleanExtra(Constants.EXTRA_PROMOTION_LINE, false);
			if (promotion_line) {
				context.startActivity(new Intent(context, RateAppActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
				);
			} else {
				int switchTo = intent.getIntExtra(Constants.EXTRA_SWITCH_TO, Integer.MAX_VALUE);
				if (switchTo != Integer.MAX_VALUE) {
					U.setUserProperty(context, "has_switched_days", true);
					reloadWidget(context, widgetID, switchTo);
				} else {
					String departureTime = intent.getStringExtra(Constants.EXTRA_ALARM_DEPARTURE_TIME);

					context.startActivity(new Intent(context, SelectAlarmActivity.class)
							.setAction(departureTime)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
							.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
					);
				}
			}
		} else if (intentAction.startsWith(Constants.ACTION_NOTIFY_UPDATE)) {
			int widgetID = U.getIdFromIntent(intent);

			notifyUpdate(context, widgetID);
		} else if (intentAction.startsWith(Constants.ACTION_WIDGET_NO_DATA)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);

			AppWidgetManager.getInstance(context).updateAppWidget(widgetID,
					new RodaliesWidget(context, widgetID, widgetState, R.layout.widget_layout_no_data, null, 0));
		}
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		U.log("onDisabled()");
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		super.onRestored(context, oldWidgetIds, newWidgetIds);
		U.log("onRestored()");
	}

	private void updateStationTexts(String originText, String destinationText, Context context, int widgetID) {
		if (widgetID != -1) {
			int core = U.getCore(context, widgetID);
			U.saveStations(context, widgetID, StationUtils.getIDFromName(originText, core), StationUtils.getIDFromName(destinationText, core));
			reloadWidget(context, widgetID, 0);
		} else {
			U.log("ERROR: Widget id not found");
		}
	}

	private void swapStations(Context context, int widgetID, int widgetState) {
		Intent swapIntent = new Intent(context, WidgetReceiver.class);
		swapIntent.setAction(Constants.ACTION_CLICK_SWAP_BUTTON);
		swapIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
		swapIntent.putExtra(Constants.EXTRA_WIDGET_STATE, widgetState);
		context.sendBroadcast(swapIntent);
	}

	private void loadSchedule(final Context context, final int widgetID, final ArrayList<TrainTime> schedule) {
		RodaliesWidget widget = new RodaliesWidget(context, widgetID, Constants.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout, schedule, 0);
		if (schedule != null && schedule.size() > 0) {
			if (schedule.get(0).getTransfer() == 1)
				widget = new RodaliesWidget(context, widgetID, Constants.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout_one_transfer, schedule, 0);
			else if (schedule.get(0).getTransfer() == 2)
				widget = new RodaliesWidget(context, widgetID, Constants.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout_two_transfer, schedule, 0);

			boolean scroll_to_time = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("scroll_to_time", false);
			boolean show_more_transfer_trains = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_more_transfer_trains", false);

			if (!show_more_transfer_trains) {
				ArrayList<TrainTime> scheduleTmp = new ArrayList<>(schedule);
				for (TrainTime trainTime : scheduleTmp) {
					if (trainTime.isSame_origin_train()) schedule.remove(trainTime);
				}
			}

			if (scroll_to_time) {
				final RodaliesWidget finalWidget = widget;
				final HandlerThread scrollHandlerThread = new HandlerThread("ScrollHandlerThread");
				scrollHandlerThread.start();
				new Handler(scrollHandlerThread.getLooper()).postDelayed(new Runnable() {
					@Override
					public void run() {
						int scrollPosition = U.getScrollPosition(schedule);
						//if(scrollPosition != 0 && schedule.get(0).getTransfer() == 0) scrollPosition--;
						if (scrollPosition > 0)
							finalWidget.setRelativeScrollPosition(R.id.scheduleListView, scrollPosition);
						//finalWidget.setScrollPosition(R.id.scheduleListView, getScrollPosition(schedule));
						AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(widgetID, finalWidget);
					}
				}, 500);
			}
		}
		AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
	}

	private RodaliesWidget reloadWidget(Context context, int widgetID, int deltaDays) {
		if (widgetID != -1) {
			RodaliesWidget widget = new RodaliesWidget(context, widgetID, Constants.WIDGET_STATE_UPDATING_TABLES, R.layout.widget_layout_updating, null, deltaDays);
			AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
			return widget;
		}
		return null;
	}

	private void notifyUpdate(Context context, int widgetID) {
		AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.scheduleListView);
	}
}
