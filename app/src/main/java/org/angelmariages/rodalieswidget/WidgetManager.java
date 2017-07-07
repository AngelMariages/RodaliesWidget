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

			appWidgetManager.updateAppWidget(widgetID, reloadWidget(context, widgetID));

			U.log("Updating widget id:" + i + " widgetID? " + widgetID);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String intentAction = intent.getAction();

		U.log("onReceive(); intentAction: " + intentAction);

		Fabric.with(context, new Crashlytics());

		if (intentAction.isEmpty()) return;

		if (intentAction.startsWith(U.ACTION_SEND_SCHEDULE)) {
			int widgetID = U.getIdFromIntent(intent);

			if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
				Bundle bundle = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE);
				ArrayList<TrainTime> schedule = (ArrayList<TrainTime>) bundle.getSerializable(U.EXTRA_SCHEDULE_DATA);

				loadSchedule(context, widgetID, schedule);

				U.logEventUpdate(schedule, context);
			}

		} else if (intentAction.startsWith(U.ACTION_CLICK_UPDATE_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);

			reloadWidget(context, widgetID);

			U.logUpdates(context, widgetID);
		} else if (intentAction.startsWith(U.ACTION_CLICK_SWAP_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);

			swapStations(context, widgetID, widgetState);

			U.logEventSwap(context);
		} else if (intentAction.startsWith(U.ACTION_CLICK_STATIONS_TEXT)) {
			int widgetID = U.getIdFromIntent(intent);

			int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
			if (widgetID != -1 && originOrDestination != -1) {
				Intent dialogActivity = new Intent(context, SelectStation.class);
				dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				dialogActivity.putExtra(U.EXTRA_OREGNorDESTINATION, originOrDestination);
				dialogActivity.putExtra(U.EXTRA_WIDGET_ID, widgetID);

				context.startActivity(dialogActivity);
			}
		} else if (intentAction.startsWith(U.ACTION_UPDATE_STATIONS)) {
			int widgetID = U.getIdFromIntent(intent);

			String newOrigin = intent.getStringExtra(U.EXTRA_ORIGIN);
			String newDestination = intent.getStringExtra(U.EXTRA_DESTINATION);

			U.log("Got update to: " + widgetID);
			U.log("UpdateContains: " + newOrigin + "," + newDestination);

			updateStationTexts(StationUtils.getNameFromID(newOrigin, U.getCore(context, widgetID)), StationUtils.getNameFromID(newDestination, U.getCore(context, widgetID)),
					context, widgetID);
		} else if (intentAction.startsWith(U.ACTION_CLICK_LIST_ITEM)) {
			int widgetID = U.getIdFromIntent(intent);

			String departureTime = intent.getStringExtra(U.EXTRA_ALARM_DEPARTURE_TIME);

			context.startActivity(new Intent(context, SelectAlarmActivity.class)
					.setAction(departureTime)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
					.putExtra(U.EXTRA_WIDGET_ID, widgetID)
			);
		} else if (intentAction.startsWith(U.ACTION_NOTIFY_UPDATE)) {
			int widgetID = U.getIdFromIntent(intent);

			notifyUpdate(context, widgetID);
		} else if (intentAction.startsWith(U.ACTION_WIDGET_NO_DATA)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);

			AppWidgetManager.getInstance(context).updateAppWidget(widgetID,
					new RodaliesWidget(context, widgetID, widgetState, R.layout.widget_layout_no_data, null));
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
			reloadWidget(context, widgetID);
		} else {
			U.log("ERROR: Widget id not found");
		}
	}

	private void swapStations(Context context, int widgetID, int widgetState) {
		Intent swapIntent = new Intent(context, WidgetReceiver.class);
		swapIntent.setAction(U.ACTION_CLICK_SWAP_BUTTON);
		swapIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		swapIntent.putExtra(U.EXTRA_WIDGET_STATE, widgetState);
		context.sendBroadcast(swapIntent);
	}

	private void loadSchedule(final Context context, final int widgetID, final ArrayList<TrainTime> schedule) {
		RodaliesWidget widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout, schedule);
		if (schedule != null && schedule.size() > 0) {
			if (schedule.get(0).getTransfer() == 1)
				widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout_one_transfer, schedule);
			else if (schedule.get(0).getTransfer() == 2)
				widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout_two_transfer, schedule);

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
						int scrollPosition = getScrollPosition(schedule);
						//if(scrollPosition != 0 && schedule.get(0).getTransfer() == 0) scrollPosition--;
						finalWidget.setRelativeScrollPosition(R.id.scheduleListView, scrollPosition);
						//finalWidget.setScrollPosition(R.id.scheduleListView, getScrollPosition(schedule));
						AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(widgetID, finalWidget);
					}
				}, 500);
			}
		}
		AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
	}

	private int getScrollPosition(ArrayList<TrainTime> schedule) {
		int currentHour = U.getCurrentHour();
		int currentMinute = U.getCurrentMinute();
		for (int i = 0; i < schedule.size(); i++) {
			String departureTime = schedule.get(i).getDeparture_time();
			int hour = -1, minute = -1;
			if (departureTime != null) {
				String[] split = departureTime.split(":");
				hour = Integer.parseInt(split[0]);
				minute = Integer.parseInt(split[1]);
			}
			if ((hour == currentHour && minute > currentMinute) || hour > currentHour) return i;
		}
		return 0;
	}

	private RodaliesWidget reloadWidget(Context context, int widgetID) {
		if (widgetID != -1) {
			RodaliesWidget widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_UPDATING_TABLES, R.layout.widget_layout_updating, null);
			AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
			return widget;
		}
		return null;
	}

	private void notifyUpdate(Context context, int widgetID) {
		AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.scheduleListView);
	}
}
