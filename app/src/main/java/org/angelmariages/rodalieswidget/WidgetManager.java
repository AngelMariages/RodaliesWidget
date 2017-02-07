package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;

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

			U.logUpdates(context, widgetID);

			U.log("Updating widget id:" + i + " widgetID? " + widgetID);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		String intentAction = intent.getAction();

		U.log("onReceive(); intentAction: " + intentAction);

		if (intentAction.isEmpty()) return;

		if (intentAction.startsWith(U.ACTION_SEND_SCHEDULE)) {
			int widgetID = U.getIdFromIntent(intent);

			if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
				Bundle bundle = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE);
				ArrayList<TrainTime> schedule = (ArrayList<TrainTime>) bundle.getSerializable(U.EXTRA_SCHEDULE_DATA);

				loadSchedule(context, widgetID, schedule);
			}

		} else if (intentAction.startsWith(U.ACTION_CLICK_UPDATE_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);

			reloadWidget(context, widgetID);

			U.logUpdates(context, widgetID);
		} else if (intentAction.startsWith(U.ACTION_CLICK_SWAP_BUTTON)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);

			swapStations(context, widgetID, widgetState);
		} else if (intentAction.startsWith(U.ACTION_CLICK_STATIONS_TEXT)) {
			int widgetID = U.getIdFromIntent(intent);

			int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
			if (widgetID != -1 && originOrDestination != -1) {
				Intent dialogActivity = new Intent(context, SelectStation.class);
				dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				dialogActivity.putExtra(U.EXTRA_OREGNorDESTINATION, originOrDestination);
				dialogActivity.putExtra(U.EXTRA_WIDGET_ID, widgetID);

				context.startActivity(dialogActivity);
			}
		} else if (intentAction.startsWith(U.ACTION_UPDATE_STATIONS)) {
			int widgetID = U.getIdFromIntent(intent);

			int newOrigin = intent.getIntExtra(U.EXTRA_ORIGIN, -1);
			int newDestination = intent.getIntExtra(U.EXTRA_DESTINATION, -1);

			U.log("Got update to: " + widgetID);
			U.log("UpdateContains: " + newOrigin + "," + newDestination);

			updateStationTexts(StationUtils.getNameFromID(newOrigin), StationUtils.getNameFromID(newDestination),
					context, widgetID);
		} else if (intentAction.startsWith(U.ACTION_CLICK_LIST_ITEM)) {
			String duracioTrajecte = intent.getStringExtra(U.EXTRA_RIDE_LENGTH);

			Toast.makeText(context, "Duració del trajecte: " + duracioTrajecte, Toast.LENGTH_SHORT).show();
		} else if (intentAction.startsWith(U.ACTION_WIDGET_NO_DATA)) {
			int widgetID = U.getIdFromIntent(intent);
			int widgetState = U.getStateFromIntent(intent);
			int widgetLayout = R.layout.widget_layout;

			if (widgetState == 1 || widgetState == 2 || widgetState == 3)
				widgetLayout = R.layout.widget_layout_no_data;

			AppWidgetManager.getInstance(context).updateAppWidget(widgetID,
					new RodaliesWidget(context, widgetID, widgetState, widgetLayout, null));
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
			U.saveStations(context, widgetID, originText, destinationText);
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

	private void loadSchedule(Context context, int widgetID, ArrayList<TrainTime> schedule) {
		RodaliesWidget widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout, schedule);
		if (schedule != null && schedule.size() > 0) {
			if (schedule.get(0).getTransfer() == 1)
				widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_SCHEDULE_LOADED, R.layout.widget_layout_one_transfer, schedule);
		}
		AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
	}

	private RodaliesWidget reloadWidget(Context context, int widgetID) {
		RodaliesWidget widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_UPDATING_TABLES, R.layout.widget_layout_updating, null);
		AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
		return widget;
	}
}
