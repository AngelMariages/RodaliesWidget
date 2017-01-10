package xyz.cesarbiker.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetReceiver extends BroadcastReceiver {
    private final Context context;
    private int widgetID;
    private String[] stations;

    public WidgetReceiver(int widgetID, Context context) {
        this.widgetID = widgetID;
        this.context = context;
        stations = Utils.getStations(context, widgetID);
        updateStationsTextViews();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        int idNewSettings = intent.getIntExtra(Utils.EXTRA_WIDGET_ID, -1);
        if(idNewSettings == widgetID) {
            if(intentAction.equalsIgnoreCase(Utils.ACTION_CLICK_SWAP_BUTTON)) {
                String[] stationsTmp = new String[2];
                stationsTmp[0] = stations[1];
                stationsTmp[1] = stations[0];
                stations = stationsTmp;
                Utils.saveStations(context, widgetID, stations[0], stations[1]);
                updateStationsTextViews();
            }
            if(intentAction.equalsIgnoreCase(Utils.ACTION_SEND_NEWSTATIONS)) {
                int originOrDestination = intent.getIntExtra(Utils.EXTRA_ORIGINorDESTINATION, -1);
                String newStation = intent.getStringExtra(Utils.EXTRA_NEWSTATIONS);

                if(originOrDestination != -1 && newStation != null) {
                    if(originOrDestination == Utils.ORIGIN) stations[0] = newStation;
                    else stations[1] = newStation;

                    Utils.saveStations(context, widgetID, stations[0], stations[1]);
                    updateStationsTextViews();
                }
            }
        }
    }

    public void updateStationsTextViews() {
        Intent updateStationsTextIntent = new Intent(context, WidgetManager.class);
        updateStationsTextIntent.setAction(Utils.ACTION_UPDATE_STATIONS + String.valueOf(widgetID));
        updateStationsTextIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        updateStationsTextIntent.putExtra(Utils.EXTRA_ORIGIN, stations[0]);
        updateStationsTextIntent.putExtra(Utils.EXTRA_DESTINATION, stations[1]);
        context.sendBroadcast(updateStationsTextIntent);
    }
}
