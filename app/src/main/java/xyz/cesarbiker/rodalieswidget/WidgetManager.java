package xyz.cesarbiker.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import xyz.cesarbiker.rodalieswidget.utils.StationUtils;
import xyz.cesarbiker.rodalieswidget.utils.U;

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

            RodaliesWidget widget = new RodaliesWidget(context, widgetID, 0);

            appWidgetManager.updateAppWidget(widgetID, widget);

            U.logUpdates(context, widgetID);

            U.log("Updating widget id:" + i + " widgetID? " + widgetID);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 22-Mar-16 Comprovar null Intent, etc
        super.onReceive(context, intent);
        String intentAction = intent.getAction();

        U.log("onReceive(); intentAction: " + intentAction);

        if(intentAction.startsWith(U.ACTION_CLICK_UPDATE_BUTTON)) {
            int widgetID = U.getIdFromIntent(intent);
            int widgetState = U.getStateFromIntent(intent);

            updateTimeTables(context, widgetID, widgetState);

            U.logUpdates(context, widgetID);
        } else if(intentAction.startsWith(U.ACTION_CLICK_SWAP_BUTTON)) {
            int widgetID = U.getIdFromIntent(intent);
            int widgetState = U.getStateFromIntent(intent);

            swapStations(context, widgetID, widgetState);
        } else if(intentAction.startsWith(U.ACTION_CLICK_STATIONS_TEXT)) {
            int widgetID = U.getIdFromIntent(intent);

            int originOrDestination = intent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);
            if(widgetID != -1 && originOrDestination != -1) {
                Intent dialogActivity = new Intent(context, SelectStation.class);
                dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogActivity.putExtra(U.EXTRA_OREGNorDESTINATION, originOrDestination);
                dialogActivity.putExtra(U.EXTRA_WIDGET_ID, widgetID);

                context.startActivity(dialogActivity);
            }
        } else if(intentAction.startsWith(U.ACTION_UPDATE_STATIONS)) {
            int widgetID = U.getIdFromIntent(intent);

            int newOrigin = intent.getIntExtra(U.EXTRA_ORIGIN, -1);
            int newDestination = intent.getIntExtra(U.EXTRA_DESTINATION, -1);

            U.log("Got update to: " + widgetID);
            U.log("UpdateContains: " + newOrigin + "," + newDestination);

            updateStationTexts(StationUtils.getNameFromID(newOrigin), StationUtils.getNameFromID(newDestination),
                    context, widgetID);
        } else if(intentAction.startsWith(U.ACTION_CLICK_LIST_ITEM)) {
            String duracioTrajecte = intent.getStringExtra(U.EXTRA_RIDE_LENGTH);

            Toast.makeText(context, "Duració del trajecte: " + duracioTrajecte, Toast.LENGTH_SHORT).show();
        } else if(intentAction.startsWith(U.ACTION_WIDGET_NO_DATA)) {
            int widgetID = U.getIdFromIntent(intent);
            int widgetState = U.getStateFromIntent(intent);

            AppWidgetManager.getInstance(context).updateAppWidget(widgetID,
                    new RodaliesWidget(context, widgetID, widgetState));
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
        if(widgetID != -1) {
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

    private void updateTimeTables(Context context, int widgetID, int widgetState) {
        U.log("UpdateTimeTables" + widgetState);
        if(widgetState == U.WIDGET_STATE_UPDATE_TABLES) {
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.horarisListView);
        } else {
            reloadWidget(context, widgetID);
        }
    }

    private void reloadWidget(Context context, int widgetID) {
        RodaliesWidget widget = new RodaliesWidget(context, widgetID, U.WIDGET_STATE_UPDATE_TABLES);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetID, widget);
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.horarisListView);
    }
}
