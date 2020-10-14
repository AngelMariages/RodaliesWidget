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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;

class StationsAdapter extends BaseAdapter {
	private final Context mContext;
	private final ArrayList<String> stationList = new ArrayList<>();
	private final int widgetID;
	private final int originOrDestination;
	private final ArrayList<String> initialStationList;

	StationsAdapter(Context context, ArrayList<String> stationList, int widgetID, int originOrDestination) {
		this.mContext = context;
		this.initialStationList = stationList;
		this.stationList.addAll(initialStationList);
		this.widgetID = widgetID;
		this.originOrDestination = originOrDestination;
	}

	@Override
	public int getCount() {
		return stationList.size();
	}

	@Override
	public String getItem(int i) {
		return stationList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		if (view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.station_select_text_view, viewGroup, false);
		}
		TextView textView = (TextView) view.findViewById(R.id.station_list_text);
		textView.setText(stationList.get(position));

		view.setOnClickListener(view1 -> {
			Intent updateStationsIntent = new Intent(mContext, WidgetReceiver.class);
			updateStationsIntent.setAction(Constants.ACTION_SEND_NEW_STATIONS);
			updateStationsIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID);
			// TODO: 14-Jan-17 Could do this better
			updateStationsIntent.putExtra(Constants.EXTRA_CONFIG_STATION, StationUtils.getIDFromName(stationList.get(position), U.getCore(mContext, widgetID)));
			updateStationsIntent.putExtra(Constants.EXTRA_ORIGINorDESTINATION, originOrDestination);
			mContext.sendBroadcast(updateStationsIntent);
			((Activity) mContext).finish();
		});

		return view;
	}

	void filterStations(String input) {
		stationList.clear();
		if (!input.isEmpty()) {
			input = input.toLowerCase();
			input = Normalizer.normalize(input, Normalizer.Form.NFD)
					.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			String tmpName;
			for (String station : initialStationList) {
				tmpName = Normalizer.normalize(station, Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				if (tmpName.toLowerCase().contains(input)) {
					stationList.add(station);
				}
			}
		} else {
			stationList.addAll(initialStationList);
		}
		Collections.sort(stationList);
		notifyDataSetChanged();
	}
}
