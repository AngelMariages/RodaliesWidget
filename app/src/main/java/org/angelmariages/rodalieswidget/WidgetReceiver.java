package org.angelmariages.rodalieswidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.angelmariages.rodalieswidget.utils.U;

public class WidgetReceiver extends BroadcastReceiver {

    public WidgetReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        U.log("onReceive() Receiver: " + intent.getAction());
        String intentAction = intent.getAction();
        // TODO: 1/12/17 Check if all broadcast send the ID

        int widgetID = intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);

        if(widgetID != -1) {
            if(intentAction.equalsIgnoreCase(U.ACTION_CLICK_SWAP_BUTTON)) {
                int[] oldStations = U.getStations(context, widgetID);
                if(oldStations[0] == -1 || oldStations[1] == -1) {
                    Intent noStationsIntent = new Intent(context, WidgetManager.class);
                    noStationsIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetID);
                    noStationsIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
                    noStationsIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_STATIONS);
                    context.sendBroadcast(noStationsIntent);
                } else {
                    int[] stationsTmp = new int[2];

                    stationsTmp[0] = oldStations[1];
                    stationsTmp[1] = oldStations[0];
                    oldStations = stationsTmp;
                    U.saveStations(context, widgetID, oldStations[0], oldStations[1]);
                    updateStationsTextViews(context, widgetID);
                }
            } else if(intentAction.equalsIgnoreCase(U.ACTION_SEND_NEW_STATIONS)) {
                int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
                int newStation = intent.getIntExtra(U.EXTRA_CONFIG_STATION, -1);

                if(originOrDestination != -1 && newStation != -1) {
                    int[] stations = U.getStations(context, widgetID);

                    if(originOrDestination == U.ORIGIN) stations[0] = newStation;
                    else stations[1] = newStation;

                    U.saveStations(context, widgetID, stations[0], stations[1]);
                    updateStationsTextViews(context, widgetID);
                }
            }
        }
    }

    public void updateStationsTextViews(Context context, int widgetID) {
        int[] stations = U.getStations(context, widgetID);
        Intent updateStationsTextIntent = new Intent(context, WidgetManager.class);
        updateStationsTextIntent.setAction(U.ACTION_UPDATE_STATIONS + String.valueOf(widgetID));
        updateStationsTextIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        updateStationsTextIntent.putExtra(U.EXTRA_ORIGIN, stations[0]);
        updateStationsTextIntent.putExtra(U.EXTRA_DESTINATION, stations[1]);
        context.sendBroadcast(updateStationsTextIntent);
    }
}
