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
	}

	private void setCoreListView() {
		final Context context = this;
		final int widgetID = this.widgetID;
		final ListView coreListView = (ListView) findViewById(R.id.coreListView);

		if (coreListView != null) {
			final ArrayList<String> coreList = new ArrayList<>();
			coreList.addAll(StationUtils.nucliIDs.values());

			final CoreSelectAdapter coreSelectAdapter = new CoreSelectAdapter(this, coreList, widgetID);
			coreSelectAdapter.setOnCoreSelectListener(new CoreSelectAdapter.OnCoreSelectListener() {
				@Override
				public void onCoreSelect(String coreName) {
					coreListView.setVisibility(View.GONE);

					int idFromNucli = StationUtils.getIDFromNucli(coreName);
					U.saveCore(context, widgetID, idFromNucli);

					setStationListView(idFromNucli);

					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.toggleSoftInputFromWindow(coreListView.getApplicationWindowToken(),
							InputMethodManager.SHOW_IMPLICIT, 0);
				}
			});

			coreListView.setAdapter(coreSelectAdapter);
		}
	}

	private void setStationListView(int idFromNucli) {
		EditText searchEditView = (EditText) findViewById(R.id.searchEditText);
		ListView stationListView = (ListView) findViewById(R.id.stationListView);

		if (stationListView != null) {
			final ArrayList<String> stationList = new ArrayList<>();
			Collection<String> values = StationUtils.nuclis.get(idFromNucli).values();

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

	@Override
	protected void onStop() {
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(),
					InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			inputMethodManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
		}
		super.onStop();
	}
}
