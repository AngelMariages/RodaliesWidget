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
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class IncidencesFragment extends Fragment implements IncidencesGetListener, SwipeRefreshLayout.OnRefreshListener {

	private ExpandableListView expandableListView;
	private HashMap<String, ArrayList<Incidence>> incidences = new HashMap<>();
	private ArrayList<String> incidencesKeys = new ArrayList<>();
	private ParentList parentList;
	private IncidencesManager incidencesManager = new IncidencesManager(this);
	private SwipeRefreshLayout swipeLayout;

	public IncidencesFragment() {
		incidencesManager.getIncidenceKeys();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_incidences, container, false);
		swipeLayout = rootView.findViewById(R.id.swiperefresh);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setRefreshing(true);

		DisplayMetrics metrics = new DisplayMetrics();
		if (this.getActivity() != null) {
			this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int width = metrics.widthPixels;

			expandableListView = rootView.findViewById(R.id.incidencesList);

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				expandableListView.setIndicatorBounds(width - getPixelsFromDps(50), width - getPixelsFromDps(10));
			} else {
				expandableListView.setIndicatorBoundsRelative(width - getPixelsFromDps(50), width - getPixelsFromDps(10));
			}
		}

		parentList = new ParentList(incidences, incidencesKeys);
		expandableListView.setAdapter(parentList);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			expandableListView.setNestedScrollingEnabled(true);
		} else {
			ViewCompat.setNestedScrollingEnabled(expandableListView, true);
		}
		return rootView;
	}

	@Override
	public void onIncidencesGet(String key, ArrayList<Incidence> incidencesFromKey) {
		incidencesKeys.add(key);
		Collections.sort(incidencesKeys);
		incidences.put(key, incidencesFromKey);
		parentList.notifyDataSetChanged();
		swipeLayout.setRefreshing(false);
	}

	@Override
	public void onRefresh() {
		parentList.clearData();
		incidencesManager.getIncidenceKeys();
	}

	public int getPixelsFromDps(float dps) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dps * scale + 0.5f);
	}

	public class SecondLevelExpandableListView extends ExpandableListView {

		public SecondLevelExpandableListView(Context context) {
			super(context);
		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			//999999 is a size in pixels. ExpandableListView requires a maximum height in order to do measurement calculations.
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	class ParentList extends BaseExpandableListAdapter {

		private HashMap<String, ArrayList<Incidence>> incidences;
		private ArrayList<String> incidenceKeys;
		private int lastGroupExpanded;

		ParentList(HashMap<String, ArrayList<Incidence>> incidences, ArrayList<String> incidencesKeys) {
			this.incidences = incidences;
			this.incidenceKeys = incidencesKeys;
		}

		public void clearData() {
			incidences.clear();
			incidenceKeys.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return this.incidenceKeys.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;//We're returning one because there's only one child (the expandable list view)
		}

		@Override
		public ArrayList<Incidence> getGroup(int groupPosition) {
			return incidences.get(incidenceKeys.get(groupPosition));
		}

		@Override
		public Incidence getChild(int groupPosition, int childPosition) {
			return getGroup(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			if (groupPosition != lastGroupExpanded) {
				expandableListView.collapseGroup(lastGroupExpanded);
			}

			lastGroupExpanded = groupPosition;
			super.onGroupExpanded(groupPosition);
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) IncidencesFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (infalInflater != null) {
					convertView = infalInflater.inflate(R.layout.expandable_listview_group_text, null);

					holder = new ChildViewHolder();

					holder.text1 = convertView.findViewById(R.id.text1);
					holder.text1.setText(incidenceKeys.get(groupPosition));

					convertView.setTag(holder);
				}
			} else {
				holder = (ChildViewHolder) convertView.getTag();
				holder.text1.setText(incidenceKeys.get(groupPosition));
			}

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(IncidencesFragment.this.getContext());
			secondLevelELV.setAdapter(new ChildList(incidences.get(incidenceKeys.get(groupPosition))));

			DisplayMetrics metrics = new DisplayMetrics();
			IncidencesFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int width = metrics.widthPixels;

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				secondLevelELV.setIndicatorBounds(width - getPixelsFromDps(50), width - getPixelsFromDps(10));
			} else {
				secondLevelELV.setIndicatorBoundsRelative(width - getPixelsFromDps(50), width - getPixelsFromDps(10));
			}
			return secondLevelELV;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	class ChildList extends BaseExpandableListAdapter {

		private ArrayList<Incidence> incidencesFromKey;

		ChildList(ArrayList<Incidence> incidencesFromKey) {
			this.incidencesFromKey = incidencesFromKey;
		}

		public void clearData() {
			incidences.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return this.incidencesFromKey.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 4;
		}

		@Override
		public Incidence getGroup(int groupPosition) {
			U.log("GetGroup: " + groupPosition);
			return incidencesFromKey.get(groupPosition);
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			switch (childPosition) {
				case 0: {
					return getGroup(groupPosition).Affects;
				}
				case 1: {
					return getGroup(groupPosition).Date;
				}
				case 2: {
					return getGroup(groupPosition).Title;
				}
				case 3: {
					return getGroup(groupPosition).Text;
				}
			}

			return "---";
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ChildViewHolder holder;

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) IncidencesFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (infalInflater != null) {
					convertView = infalInflater.inflate(R.layout.expandable_listview_second_group_text, null);

					holder = new ChildViewHolder();

					holder.text1 = convertView.findViewById(R.id.text1);
					holder.text1.setText(getGroup(groupPosition).getTitle());

					convertView.setTag(holder);
				}
			} else {
				holder = (ChildViewHolder) convertView.getTag();
				holder.text1.setText(getGroup(groupPosition).getTitle());
			}

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) IncidencesFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (infalInflater != null) {
					convertView = infalInflater.inflate(R.layout.expandable_listview_child, null);

					holder = new ChildViewHolder();

					holder.text1 = convertView.findViewById(R.id.text1);
					holder.text1.setText(getChild(groupPosition, childPosition));

					convertView.setTag(holder);
				}
			} else {
				holder = (ChildViewHolder) convertView.getTag();
				holder.text1.setText(getChild(groupPosition, childPosition));
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public class ChildViewHolder {
		TextView text1;

		public ChildViewHolder() {
		}
	}
}