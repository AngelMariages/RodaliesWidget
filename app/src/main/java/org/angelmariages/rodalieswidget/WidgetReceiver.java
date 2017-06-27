package org.angelmariages.rodalieswidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.angelmariages.rodalieswidget.utils.U;

public class WidgetReceiver extends BroadcastReceiver {

	// TODO: 2/6/17 Refactor this whole class, all the methods in the widgetManager should go here
	public WidgetReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		U.log("onReceive() Receiver: " + intent.getAction());
		String intentAction = intent.getAction();

		int widgetID = U.getIdFromIntent(intent);

		if (widgetID != -1) {
			if (intentAction.equalsIgnoreCase(U.ACTION_CLICK_SWAP_BUTTON)) {
				String[] oldStations = U.getStations(context, widgetID);
				if (oldStations[0].equalsIgnoreCase("-1") || oldStations[1].equalsIgnoreCase("-1")) {
					U.sendNoStationsSetError(widgetID, context);
				} else {
					String[] stationsTmp = new String[2];

					stationsTmp[0] = oldStations[1];
					stationsTmp[1] = oldStations[0];
					oldStations = stationsTmp;
					U.saveStations(context, widgetID, oldStations[0], oldStations[1]);
					updateStationsTextViews(context, widgetID);
				}
			} else if (intentAction.equalsIgnoreCase(U.ACTION_SEND_NEW_STATIONS)) {
				int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
				String newStation = intent.getStringExtra(U.EXTRA_CONFIG_STATION);
				U.log("OR= " + originOrDestination + " , " + newStation);

				if (originOrDestination != -1 && newStation != null) {
					String[] stations = U.getStations(context, widgetID);

					if (originOrDestination == U.ORIGIN) stations[0] = newStation;
					else stations[1] = newStation;

					U.saveStations(context, widgetID, stations[0], stations[1]);
					updateStationsTextViews(context, widgetID);
				}
			}
		}
	}

	private void updateStationsTextViews(Context context, int widgetID) {
		String[] stations = U.getStations(context, widgetID);
		Intent updateStationsTextIntent = new Intent(context, WidgetManager.class);
		updateStationsTextIntent.setAction(U.ACTION_UPDATE_STATIONS + String.valueOf(widgetID));
		updateStationsTextIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		updateStationsTextIntent.putExtra(U.EXTRA_ORIGIN, stations[0]);
		updateStationsTextIntent.putExtra(U.EXTRA_DESTINATION, stations[1]);
		context.sendBroadcast(updateStationsTextIntent);
	}
}
