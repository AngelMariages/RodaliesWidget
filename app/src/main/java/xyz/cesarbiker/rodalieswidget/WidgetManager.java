package xyz.cesarbiker.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WidgetManager extends AppWidgetProvider {
    private FirebaseDatabase mFirebaseDatabase;
    private int numberOfWidgets = 0;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Utils.log("onEnabled");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // TODO: 07-May-16 Borrar shared preferences
        Utils.log("onDeleted");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Utils.log("onUpdate; num of widgets:" + appWidgetIds.length);
        numberOfWidgets = appWidgetIds.length;

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetID = appWidgetIds[i];

            RodaliesWidget widget = new RodaliesWidget(context, widgetID);

            appWidgetManager.updateAppWidget(widgetID, widget);

            logUpdates(context, widgetID);

            Utils.log("Updating widget id:" + i + " widgetID? " + widgetID);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 22-Mar-16 Comprovar null Intent, etc
        super.onReceive(context, intent);
        String intentAction = intent.getAction();

        Utils.log("onRecive(); intentAction: " + intentAction);

        if(intentAction.startsWith(Utils.ACTION_CLICK_UPDATE_BUTTON)) {
            int widgetID = Utils.getIdFromIntent(intent);

            updateTimeTables(context, widgetID);

            logUpdates(context, widgetID);
        } else if(intentAction.startsWith(Utils.ACTION_CLICK_SWAP_BUTTON)) {
            int widgetID = Utils.getIdFromIntent(intent);

            swapStations(context, widgetID);
        } else if(intentAction.startsWith(Utils.ACTION_CLICK_STATIONS_TEXT)) {
            int widgetID = Utils.getIdFromIntent(intent);

            int originOrDestination = intent.getIntExtra(Utils.EXTRA_ORIGINorDESTINATION, -1);
            if(widgetID != -1 && originOrDestination != -1) {
                Intent dialogActivity = new Intent(context, SelectStation.class);
                dialogActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogActivity.putExtra(Utils.EXTRA_ORIGINorDESTINATION, originOrDestination);
                dialogActivity.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);

                context.startActivity(dialogActivity);
            }
        } else if(intentAction.startsWith(Utils.ACTION_UPDATE_STATIONS)) {
            int widgetID = Utils.getIdFromIntent(intent);

            String newOrigin = intent.getStringExtra(Utils.EXTRA_ORIGIN);
            String newDestination = intent.getStringExtra(Utils.EXTRA_DESTINATION);

            Utils.log("Got update to: " + widgetID);
            Utils.log("UpdateContains: " + newOrigin + "," + newDestination);

            updateStationTexts(newOrigin, newDestination, context, widgetID);
        } else if(intentAction.startsWith(Utils.ACTION_CLICK_LIST_ITEM)) {
            String duracioTrajecte = intent.getStringExtra(Utils.EXTRA_RIDELENGTH);

            Toast.makeText(context, "Duració del trajecte: " + duracioTrajecte, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Utils.log("onDisabled()");
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Utils.log("onRestored()");
    }

    private void updateStationTexts(String originText, String destinationText, Context context, int widgetID) {
        if(widgetID != -1) {
            RodaliesWidget widget = new RodaliesWidget(context, widgetID);
            widget.updateStationsText(originText, destinationText);
        } else {
            Utils.log("ERROR: Widget id not found");
        }
    }

    private void swapStations(Context context, int widgetID) {
        Intent swapIntent = new Intent();
        swapIntent.setAction(Utils.ACTION_CLICK_SWAP_BUTTON);
        swapIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        context.sendBroadcast(swapIntent);
        updateTimeTables(context, widgetID);
    }

    private void updateTimeTables(Context context, int widgetID) {
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(widgetID, R.id.horarisListView);
    }

    private void logUpdates(Context context, int widgetID) {
        mFirebaseDatabase = Utils.getFirebaseDatabase();

        final String[] stations = Utils.getStations(context, widgetID);

        if(stations != null) {
            DatabaseReference mRefJourneys = mFirebaseDatabase.getReference("statics/journeys");
            DatabaseReference mRefStations = mFirebaseDatabase.getReference("statics/stations");


            mRefJourneys.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot origenDesti = dataSnapshot.child(stations[0] + "@@" + stations[1]);

                    if (origenDesti.exists()) {
                        int value = Integer.parseInt(String.valueOf(origenDesti.getValue()));
                        origenDesti.getRef().setValue(value + 1);
                    } else {
                        origenDesti.getRef().setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Utils.log("Error" + databaseError.getMessage());
                }
            });

            mRefStations.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot origen = dataSnapshot.child(stations[0]).child("departures");
                    DataSnapshot desti = dataSnapshot.child(stations[1]).child("arrivals");

                    if (origen.exists()) {
                        int value = Integer.parseInt(String.valueOf(origen.getValue()));
                        origen.getRef().setValue(value + 1);
                    } else {
                        origen.getRef().setValue(1);
                    }

                    if (desti.exists()) {
                        int value = Integer.parseInt(String.valueOf(desti.getValue()));
                        desti.getRef().setValue(value + 1);
                    } else {
                        desti.getRef().setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Utils.log("Error" + databaseError.getMessage());
                }
            });
        }
    }
}
