package org.angelmariages.rodalieswidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
		if (originOrDestination == -1) finish();

		setCoreListView();
		setStationListView();

		if (this.getWindow() != null) {
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	private void setCoreListView() {
		final ListView coreListView = (ListView) findViewById(R.id.coreListView);

		if(coreListView != null) {
			final ArrayList<String> coreList = new ArrayList<>();
			coreList.add("Barcelona");
			coreList.add("Madrid");
			coreList.add("Zaragoza");
			coreList.add("Valencia");
			coreList.add("Andalucia");

			final CoreSelectAdapter coreSelectAdapter = new CoreSelectAdapter(this, coreList, widgetID);
			coreSelectAdapter.setOnCoreSelectListener(new CoreSelectAdapter.OnCoreSelectListener() {
				@Override
				public void onCoreSelect(String coreName) {
					U.log("Core selected: " + coreName);
					coreListView.setVisibility(View.GONE);
				}
			});
		}
	}

	private void setStationListView() {
		EditText searchEditView = (EditText) findViewById(R.id.searchEditText);
		ListView stationListView = (ListView) findViewById(R.id.stationListView);

		if (stationListView != null) {
			final ArrayList<String> stationList = new ArrayList<>();
			Collections.addAll(stationList, StationUtils.STATION_NAMES);

			final StationsAdapter stationsAdapter = new StationsAdapter(this, stationList, widgetID, originOrDestination);
			stationListView.setAdapter(stationsAdapter);
			searchEditView.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				}

				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					stationsAdapter.filterStations(charSequence.toString());
				}

				@Override
				public void afterTextChanged(Editable editable) {

				}
			});
		} else {
			U.log("Error when creating the stations list view, finishing dialog");
			finish();
		}
	}

}
