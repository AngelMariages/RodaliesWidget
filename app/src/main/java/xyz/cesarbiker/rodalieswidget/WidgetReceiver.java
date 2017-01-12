package xyz.cesarbiker.rodalieswidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xyz.cesarbiker.rodalieswidget.utils.U;

public class WidgetReceiver extends BroadcastReceiver {
    private final Context context;
    private int widgetID;
    private int[] stations;

    public WidgetReceiver(int widgetID, Context context) {
        this.widgetID = widgetID;
        this.context = context;
        stations = U.getStations(context, widgetID);
        updateStationsTextViews();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        // TODO: 1/12/17 Check if all broadcast send the ID
        int idNewSettings = intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
        if(idNewSettings == widgetID) {
            if(intentAction.equalsIgnoreCase(U.ACTION_CLICK_SWAP_BUTTON)) {
                int[] stationsTmp = new int[2];
                stationsTmp[0] = stations[1];
                stationsTmp[1] = stations[0];
                stations = stationsTmp;
                U.saveStations(context, widgetID, stations[0], stations[1]);
                updateStationsTextViews();
            } else if(intentAction.equalsIgnoreCase(U.ACTION_SEND_NEW_STATIONS)) {
                int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
                int newStation = intent.getIntExtra(U.EXTRA_CONFIG_STATION, -1);

                if(originOrDestination != -1 && newStation != -1) {
                    if(originOrDestination == U.ORIGIN) stations[0] = newStation;
                    else stations[1] = newStation;

                    U.saveStations(context, widgetID, stations[0], stations[1]);
                    updateStationsTextViews();
                }
            }
        }
    }

    public void updateStationsTextViews() {
        Intent updateStationsTextIntent = new Intent(context, WidgetManager.class);
        updateStationsTextIntent.setAction(U.ACTION_UPDATE_STATIONS + String.valueOf(widgetID));
        updateStationsTextIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        updateStationsTextIntent.putExtra(U.EXTRA_ORIGIN, stations[0]);
        updateStationsTextIntent.putExtra(U.EXTRA_DESTINATION, stations[1]);
        context.sendBroadcast(updateStationsTextIntent);
    }
}
