/*
 * MIT License
 *
 * Copyright (c) 2017 Àngel Mariages
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
