package org.angelmariages.rodalieswidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

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
		for (String s : stationList) {
			U.log(s);
		}
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

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent updateStationsIntent = new Intent(mContext, WidgetReceiver.class);
				updateStationsIntent.setAction(U.ACTION_SEND_NEW_STATIONS);
				updateStationsIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
				// TODO: 14-Jan-17 Could do this better
				updateStationsIntent.putExtra(U.EXTRA_CONFIG_STATION, StationUtils.getIDFromName(stationList.get(position)));
				updateStationsIntent.putExtra(U.EXTRA_OREGNorDESTINATION, originOrDestination);
				mContext.sendBroadcast(updateStationsIntent);
				((Activity) mContext).finish();
			}
		});

		return view;
	}

	void filterStations(String input) {
		input = input.toLowerCase();

		stationList.clear();
		if (!input.isEmpty() && input.length() != 0) {
			for (String station : initialStationList) {
				if (station.toLowerCase().contains(input)) {
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
