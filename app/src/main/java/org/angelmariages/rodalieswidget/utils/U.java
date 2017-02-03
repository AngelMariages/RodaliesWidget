package org.angelmariages.rodalieswidget.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public final class U {
    //====================== [ CONSTANTS ] ======================
    public static final int ORIGIN = 100;
    public static final int DESTINATION = 200;

    public static final String ACTION_CLICK_UPDATE_BUTTON = "org.angelmariages.RodaliesWidget.clickUpdateButtonId_";
    public static final String ACTION_CLICK_SWAP_BUTTON = "org.angelmariages.RodaliesWidget.clickSwapButtonId_";
    public static final String ACTION_UPDATE_STATIONS = "org.angelmariages.RodaliesWidget.sendNewSettingsId_";
    public static final String ACTION_SEND_NEW_STATIONS = "org.angelmariages.RodaliesWidget.sendNewStations";
    public static final String ACTION_CLICK_STATIONS_TEXT = "org.angelmariages.RodaliesWidget.clickStationsText_";
    public static final String ACTION_CLICK_LIST_ITEM = "org.angelmariages.RodaliesWidget.clickListItem_";
    public static final String ACTION_WIDGET_NO_DATA = "org.angelmariages.RodaliesWidget.widgetNoData_";

    public static final String EXTRA_OREGNorDESTINATION = "org.angelmariages.RodaliesWidget.originOrDestination";
    public static final String EXTRA_ORIGIN = "org.angelmariages.RodaliesWidget.extraOrigin";
    public static final String EXTRA_DESTINATION = "org.angelmariages.RodaliesWidget.extraDestination";
    public static final String EXTRA_WIDGET_ID = "org.angelmariages.RodaliesWidget.extraWidgetId";
    public static final String EXTRA_RIDE_LENGTH = "org.angelmariages.RodaliesWidget.extraRideLength";
    public static final String EXTRA_CONFIG_STATION = "org.angelmariages.RodaliesWidget.newSettings";
    public static final String EXTRA_WIDGET_STATE = "org.angelmariages.RodaliesWidget.extraWidgetState";
    public static final String EXTRA_SCHEDULE_DATA = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_DATA";
    public static final String EXTRA_SCHEDULE_BUNDLE = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_BUNDLE";

    private static final String PREFERENCE_KEY = "org.angelmariages.RodaliesWidget.PREFERENCE_FILE_KEY_ID_";
    private static final String PREFERENCE_STRING_ORIGIN = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ORIGIN";
    private static final String PREFERENCE_STRING_DESTINATION = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_DESTINATION";

    //====================== [ END_CONSTANTS ] ======================
    private static final boolean LOGGING = true;
    public static final int WIDGET_STATE_UPDATE_TABLES = 0;
    public static final int WIDGET_STATE_NO_INTERNET = 1;
	public static final int WIDGET_STATE_NO_STATIONS = 2;
	public static final int WIDGET_STATE_NO_TIMES = 3;
	public static final int WIDGET_STATE_UPDATING_TABLES = 4;

    private static FirebaseDatabase mFirebaseDatabase;

    public static void log(String message) {
        if(LOGGING) {
            Log.d("RodaliesLog", message);
        }
    }

    public static int getIdFromIntent(Intent intent) {
        return intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
    }

    public static int getStateFromIntent(Intent intent) {
        return intent.getIntExtra(U.EXTRA_WIDGET_STATE, -1);
    }

    public static void saveStations(Context context, int widgetID, String origin, String destination) {
        saveStations(context, widgetID, StationUtils.getIDFromName(origin), StationUtils.getIDFromName(destination));
    }

    public static void saveStations(Context context, int widgetID, int origin, int destination) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(U.PREFERENCE_STRING_ORIGIN, origin);
        editor.putInt(U.PREFERENCE_STRING_DESTINATION, destination);
        editor.apply();
    }

    public static int[] getStations(Context context, int widgetID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(U.PREFERENCE_KEY + String.valueOf(widgetID), Context.MODE_PRIVATE);
        if(sharedPreferences != null) {
            return new int[] {
                    sharedPreferences.getInt(U.PREFERENCE_STRING_ORIGIN, -1),
                    sharedPreferences.getInt(U.PREFERENCE_STRING_DESTINATION, -1)
            };
        }
        return new int[]{-1, -1};
    }

    private static FirebaseDatabase getFirebaseDatabase() {
        if(mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        return mFirebaseDatabase;
    }

    public static void logUpdates(Context context, int widgetID) {
        mFirebaseDatabase = U.getFirebaseDatabase();

        final int[] stations = U.getStations(context, widgetID);

        if(stations.length == 2 && stations[0] != -1 && stations[1] != -1) {
            DatabaseReference mRefJourneys = mFirebaseDatabase.getReference("statics/journeys");
            DatabaseReference mRefStations = mFirebaseDatabase.getReference("statics/stations");

            mRefJourneys.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot originDestination = dataSnapshot.child(stations[0] + "@@" + stations[1]);

                    if (originDestination.exists()) {
                        int value = Integer.parseInt(String.valueOf(originDestination.getValue()));
                        originDestination.getRef().setValue(value + 1);
                    } else {
                        originDestination.getRef().setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    U.log("FirebaseError (mRefJourneys): " + databaseError.getMessage());
                }
            });

            mRefStations.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot origin = dataSnapshot.child(String.valueOf(stations[0])).child("departures");
                    DataSnapshot destination = dataSnapshot.child(String.valueOf(stations[1])).child("arrivals");

                    if (origin.exists()) {
                        int value = Integer.parseInt(String.valueOf(origin.getValue()));
                        origin.getRef().setValue(value + 1);
                    } else {
                        origin.getRef().setValue(1);
                    }

                    if (destination.exists()) {
                        int value = Integer.parseInt(String.valueOf(destination.getValue()));
                        destination.getRef().setValue(value + 1);
                    } else {
                        destination.getRef().setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    U.log("FirebaseError (mRefStations): " + databaseError.getMessage());
                }
            });
        }
    }
}
