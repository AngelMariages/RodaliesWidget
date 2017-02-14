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
			if (schedule == null) U.sendNoInternetError(widgetId, context);
			else if (schedule.size() == 0) U.sendNoTimesError(widgetId, context);
			else transfers = schedule.get(0).getTransfer();
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
		if (schedule != null) return schedule.size() + 1;
		else return 0;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position == schedule.size()) {
			return new RemoteViews(context.getPackageName(), R.layout.data_info_line);
		} else {
			RemoteViews row = null;
			if (transfers == 0) {
				row = new RemoteViews(context.getPackageName(), R.layout.time_list);

				row.setTextViewText(R.id.lineText, schedule.get(position).getLine());
				row.setTextViewText(R.id.departureTimeText, schedule.get(position).getDeparture_time());
				row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time());
			} else if (transfers == 1) {
				row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);

				if (schedule.get(position).getDeparture_time() != null)
					row.setTextViewText(R.id.lineText, schedule.get(position).getLine());
				else
					row.setTextViewText(R.id.lineText, "");
				row.setTextViewText(R.id.lineTransferOneText, schedule.get(position).getLine_transfer_one());
				row.setTextViewText(R.id.departureTimeText, schedule.get(position).getDeparture_time());
				row.setTextViewText(R.id.transferOneDepartureTimeText, schedule.get(position).getDeparture_time_transfer_one());
				if (schedule.get(position).getArrival_time_transfer_one() != null)
					row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time_transfer_one());
				else
					row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time());
			} else if(transfers == 2) {
				row = new RemoteViews(context.getPackageName(), R.layout.time_list_two_transfer);

				if (schedule.get(position).getDeparture_time() != null)
					row.setTextViewText(R.id.lineText, schedule.get(position).getLine());
				else
					row.setTextViewText(R.id.lineText, "");

				row.setTextViewText(R.id.lineTransferOneText, schedule.get(position).getLine_transfer_one());
				row.setTextViewText(R.id.departureTimeText, schedule.get(position).getDeparture_time());
				row.setTextViewText(R.id.transferOneDepartureTimeText, schedule.get(position).getDeparture_time_transfer_one());

				row.setTextViewText(R.id.lineTransferTwoText, schedule.get(position).getLine_transfer_two());
				row.setTextViewText(R.id.transferTwoDepartureTimeText, schedule.get(position).getDeparture_time_transfer_two());
				if (schedule.get(position).getArrival_time_transfer_one() != null)
					row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time_transfer_two());
				else
					row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time());
			}

			if (row != null) {
				Intent intent = new Intent(context, WidgetManager.class);
				intent.putExtra(U.EXTRA_RIDE_LENGTH, schedule.get(position).getTravel_time());
				row.setOnClickFillInIntent(R.id.timesListLayout, intent);
			}

			return row;
		}
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
