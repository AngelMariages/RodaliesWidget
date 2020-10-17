/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.angelmariages.rodalieswidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

public class SelectStation extends AppCompatActivity {
	private int widgetID;
	private int originOrDestination;
	private ListView coreListView;
	private boolean isFromActionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_station);

		Intent selectIntent = getIntent();

		widgetID = selectIntent.getIntExtra(Constants.EXTRA_WIDGET_ID, -1);
		originOrDestination = selectIntent.getIntExtra(Constants.EXTRA_ORIGINorDESTINATION, -1);

		if (widgetID == -1) {
			isFromActionView = true;
			widgetID = U.getFirstWidgetId(this);
			//TODO if no widget is found, show tutorial
			originOrDestination = Constants.ORIGIN;
			U.saveStations(this, widgetID, "-1", "-1");
		}

		//This should not had been created
		if (originOrDestination == -1) finish();

		coreListView = findViewById(R.id.coreListView);

		setCoreListView();
	}

	@Override
	protected void onDestroy() {
		if (isFromActionView)
			startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
		super.onDestroy();
	}

	private void setCoreListView() {
		final Context context = this;

		int core = U.getCore(context, widgetID);

		if (core != -1) {
			coreListView.setVisibility(View.GONE);

			setStationListView(core);
		}


		if (coreListView != null) {
			final ArrayList<String> coreList = new ArrayList<>(StationUtils.nucliIDs.values());

			final CoreSelectAdapter coreSelectAdapter = new CoreSelectAdapter(this, coreList);
			coreSelectAdapter.setOnCoreSelectListener(coreName -> {
				coreListView.setVisibility(View.GONE);

				int idFromNucli = StationUtils.getIDFromNucli(coreName);
				U.saveCore(context, widgetID, idFromNucli);

				setStationListView(idFromNucli);
			});

			coreListView.setAdapter(coreSelectAdapter);
		}
	}

	private void setStationListView(int idFromNucli) {
		EditText searchEditView = findViewById(R.id.searchEditText);
		ListView stationListView = findViewById(R.id.stationListView);
		ImageButton changeCoreButton = findViewById(R.id.changeZoneButton);

		changeCoreButton.setOnClickListener(view -> coreListView.setVisibility(View.VISIBLE));

		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(searchEditView.getApplicationWindowToken(),
				InputMethodManager.SHOW_IMPLICIT, 0);

		if (stationListView != null) {
			Collection<String> values = Objects.requireNonNull(StationUtils.nuclis.get(idFromNucli)).values();

			final ArrayList<String> stationList = new ArrayList<>(values);

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
