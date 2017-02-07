package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.U;

class RemoteListViewFactory implements RemoteViewsService.RemoteViewsFactory {
	private int transfers = 0;
	private Context context = null;

	private ArrayList<TrainTime> schedule;

	@SuppressWarnings("unchecked")
	RemoteListViewFactory(Context context, Intent intent) {
		this.context = context;
		int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		U.log("RemoteListViewFactory()");
		if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
			Bundle bundle = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE);
			schedule = (ArrayList<TrainTime>) bundle.getSerializable(U.EXTRA_SCHEDULE_DATA);
			if (schedule != null && schedule.size() > 0) transfers = schedule.get(0).getTransfer();
			else sendNoDataForSchedule(widgetId);
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		if (schedule != null) return schedule.size();
		else return 0;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position >= getCount()) return null;

		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.time_list);
		if (transfers == 1)
			row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);

		row.setTextViewText(R.id.departureTimeText, schedule.get(position).getDeparture_time());
		row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time());

		if (transfers == 1) {
			row.setTextViewText(R.id.transferDepartureTimeText, schedule.get(position).getDeparture_time_transfer_one());
			row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time_transfer_one());
		}

		Intent intent = new Intent(context, WidgetManager.class);
		intent.putExtra(U.EXTRA_RIDE_LENGTH, schedule.get(position).getTravel_time());
		row.setOnClickFillInIntent(R.id.timesListLayout, intent);

		return row;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private void sendNoDataForSchedule(int widgetId) {
		Intent noDataIntent = new Intent(context, WidgetManager.class);
		noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetId);
		noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_INTERNET);
		context.sendBroadcast(noDataIntent);
	}
}
