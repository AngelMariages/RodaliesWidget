package xyz.cesarbiker.rodalieswidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class SelectStation extends AppCompatActivity {
    private int widgetID;
    private int originOrDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_station);

        Intent selectIntent = getIntent();

        widgetID = selectIntent.getIntExtra(Utils.EXTRA_WIDGET_ID, -1);
        originOrDestination = selectIntent.getIntExtra(Utils.EXTRA_ORIGINorDESTINATION, -1);

        //This should not had been created
        if(originOrDestination == -1)  finish();

        setListView();
    }

    private void setListView() {
        ListView stationListView = (ListView) findViewById(R.id.stationListView);

        if(stationListView != null) {
            final ArrayList<String> stationList = new ArrayList<>();
            Collections.addAll(stationList, Utils.STATION_NAMES);

            stationListView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    stationList));

            stationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent updateStationsIntent = new Intent();
                    updateStationsIntent.setAction(Utils.ACTION_SEND_NEWSTATIONS);
                    updateStationsIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
                    updateStationsIntent.putExtra(Utils.EXTRA_NEWSTATIONS, stationList.get(position));
                    updateStationsIntent.putExtra(Utils.EXTRA_ORIGINorDESTINATION, originOrDestination);
                    getApplicationContext().sendBroadcast(updateStationsIntent);
                    finish();
                }
            });
        } else {
            Utils.log("Error when creating the stations list view, finishing dialog");
            finish();
        }
    }

}
