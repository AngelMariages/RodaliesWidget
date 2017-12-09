package org.angelmariages.rodalieswidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CoreSelectAdapter extends BaseAdapter {
	private final Context mContext;
	private final int widgetID;
	private int originOrDestination;
	private final ArrayList<String> coreList;
	private OnCoreSelectListener onCoreSelectListener;

	CoreSelectAdapter(Context context, ArrayList<String> coreList, int widgetID) {
		this.mContext = context;
		this.coreList = coreList;
		this.widgetID = widgetID;
	}

	@Override
	public int getCount() {
		return coreList.size();
	}

	@Override
	public String getItem(int i) {
		return coreList.get(i);
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
		textView.setText(coreList.get(position));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onCoreSelectListener != null) {
					onCoreSelectListener.onCoreSelect(coreList.get(position));
				}
			}
		});

		return view;
	}

	void setOnCoreSelectListener(OnCoreSelectListener onCoreSelectListener) {
		this.onCoreSelectListener = onCoreSelectListener;
	}

	interface OnCoreSelectListener {
		void onCoreSelect(String coreName);
	}
}
