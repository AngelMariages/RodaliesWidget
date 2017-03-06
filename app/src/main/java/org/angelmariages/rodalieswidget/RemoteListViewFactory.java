package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.StationUtils;
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
			TrainTime trainTime = schedule.get(position);
			switch (transfers) {
				case 0: {
					row = new RemoteViews(context.getPackageName(), R.layout.time_list);
					setTexts(row, trainTime);
				}
				break;
				case 1: {
					if (trainTime.getDeparture_time() != null) {
						if(trainTime.getDeparture_time_transfer_one() != null) {
							row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);
							setTexts(row, trainTime);
							setTransferOneTexts(row, trainTime);
						} else {
							row = new RemoteViews(context.getPackageName(), R.layout.time_list);
							setTexts(row, trainTime);
						}
					} else {
						row = new RemoteViews(context.getPackageName(), R.layout.time_list);
						setTextsSingleOneTransfer(row, trainTime);
					}
				}
				break;
				case 2: {
					row = new RemoteViews(context.getPackageName(), R.layout.time_list_two_transfer);
					setTexts(row, trainTime);
					setTransferOneTexts(row, trainTime);
					setTransferTwoTexts(row, trainTime);
				}
				break;
			}

			if (row != null) {
				Intent intent = new Intent(context, WidgetManager.class);
				intent.putExtra(U.EXTRA_RIDE_LENGTH, schedule.get(position).getTravel_time());
				row.setOnClickFillInIntent(R.id.timesListLayout, intent);
			}

			return row;
		}
	}

	private void setTexts(RemoteViews row, TrainTime trainTime) {
		row.setTextViewText(R.id.lineText, trainTime.getLine());
		row.setTextViewText(R.id.departureTimeText, trainTime.getDeparture_time());
		row.setTextViewText(R.id.arrivalTimeText, trainTime.getArrival_time());
		try {
			row.setInt(R.id.lineText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine()).getColor()));
		} catch (Exception e) {
			U.log("Unknown color for: " + trainTime.getLine());
		}
	}

	private void setTextsSingleOneTransfer(RemoteViews row, TrainTime trainTime) {
		row.setTextViewText(R.id.lineText, trainTime.getLine_transfer_one());
		row.setTextViewText(R.id.departureTimeText, trainTime.getDeparture_time_transfer_one());
		row.setTextViewText(R.id.arrivalTimeText, trainTime.getArrival_time_transfer_one());
		try {
			row.setInt(R.id.lineText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one()).getColor()));
		} catch (Exception e) {
			U.log("Unknown color for: " + trainTime.getLine());
		}
	}

	private void setTransferOneTexts(RemoteViews row, TrainTime trainTime) {
		row.setTextViewText(R.id.lineTransferOneText, trainTime.getLine_transfer_one());
		row.setTextViewText(R.id.transferOneDepartureTimeText, trainTime.getDeparture_time_transfer_one());
		row.setTextViewText(R.id.transferOneArrivalTimeText, trainTime.getArrival_time_transfer_one());

		try {
			row.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_one()).getColor()));
		} catch (Exception e) {
			U.log("Unknown color for: " + trainTime.getLine_transfer_one());
		}
	}

	private void setTransferTwoTexts(RemoteViews row, TrainTime trainTime) {
		if (trainTime.getDeparture_time_transfer_two() == null) {
			row.setViewVisibility(R.id.timesListLayoutTransferTwo, View.GONE);
		} else {
			row.setTextViewText(R.id.lineTransferTwoText, trainTime.getLine_transfer_two());
			row.setTextViewText(R.id.transferTwoDepartureTimeText, trainTime.getDeparture_time_transfer_two());
			row.setTextViewText(R.id.transferTwoArrivalTimeText, trainTime.getArrival_time_transfer_two());

			try {
				row.setInt(R.id.lineTransferTwoText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.getLine_transfer_two()).getColor()));
			} catch (Exception e) {
				U.log("Unknown color for: " + trainTime.getLine_transfer_two());
			}
		}
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
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
