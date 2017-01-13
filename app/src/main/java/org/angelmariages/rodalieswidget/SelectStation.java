package org.angelmariages.rodalieswidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

public class SelectStation extends AppCompatActivity {
    private int widgetID;
    private int originOrDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_station);

        Intent selectIntent = getIntent();

        widgetID = selectIntent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
        originOrDestination = selectIntent.getIntExtra(U.EXTRA_OREGNorDESTINATION, -1);

        //This should not had been created
        if(originOrDestination == -1)  finish();

        setListView();
    }

    private void setListView() {
        ListView stationListView = (ListView) findViewById(R.id.stationListView);

        if(stationListView != null) {
            final ArrayList<String> stationList = new ArrayList<>();
            Collections.addAll(stationList, StationUtils.STATION_NAMES);

            stationListView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    stationList));

            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent updateStationsIntent = new Intent(getApplicationContext(), WidgetReceiver.class);
                    updateStationsIntent.setAction(U.ACTION_SEND_NEW_STATIONS);
                    updateStationsIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
                    updateStationsIntent.putExtra(U.EXTRA_CONFIG_STATION, StationUtils.STATION_IDS[position]);
                    updateStationsIntent.putExtra(U.EXTRA_OREGNorDESTINATION, originOrDestination);
                    getApplicationContext().sendBroadcast(updateStationsIntent);
                    finish();
                }
            });
        } else {
            U.log("Error when creating the stations list view, finishing dialog");
            finish();
        }
    }

}
