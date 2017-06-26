package org.angelmariages.rodalieswidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

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
	}

	private void setCoreListView() {
		final ListView coreListView = (ListView) findViewById(R.id.coreListView);

		if (coreListView != null) {
			final ArrayList<String> coreList = new ArrayList<>();
			coreList.add("Asturias");//20
			coreList.add("Barcelona");//50
			coreList.add("Bilbao");//60
			coreList.add("Cádiz");//31
			coreList.add("Madrid");//10
			coreList.add("Málaga");//32
			coreList.add("Murcia/Alicante");//41
			coreList.add("Santander");//62
			coreList.add("San Sebastián");//61
			coreList.add("Sevilla");//30
			coreList.add("Valencia");//40
			coreList.add("Zaragoza");//70

			final CoreSelectAdapter coreSelectAdapter = new CoreSelectAdapter(this, coreList, widgetID);
			coreSelectAdapter.setOnCoreSelectListener(new CoreSelectAdapter.OnCoreSelectListener() {
				@Override
				public void onCoreSelect(String coreName) {
					U.log("Core selected: " + coreName);
					coreListView.setVisibility(View.GONE);

					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInputFromWindow(coreListView.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}
			});

			coreListView.setAdapter(coreSelectAdapter);
		}
	}

	private void setStationListView() {
		EditText searchEditView = (EditText) findViewById(R.id.searchEditText);
		ListView stationListView = (ListView) findViewById(R.id.stationListView);

		if (stationListView != null) {
			final ArrayList<String> stationList = new ArrayList<>();
			Collection<String> values = StationUtils.nucli50.values();

			stationList.addAll(values);

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
