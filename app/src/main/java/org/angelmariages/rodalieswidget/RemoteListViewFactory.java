package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

class RemoteListViewFactory implements RemoteViewsService.RemoteViewsFactory {
	private final boolean group_transfer_exits, show_more_transfer_trains;
	private final int currentHour, currentMinute;
	private final int widgetID;
	private final int core;
	private String alarm_departure_time;
	private int transfers = 0;
	private Context context = null;

	private ArrayList<TrainTime> schedule;

	@SuppressWarnings("unchecked")
	RemoteListViewFactory(Context context, Intent intent) {
		this.context = context;
		widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		group_transfer_exits = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("group_transfer_exits", false);
		show_more_transfer_trains = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_more_transfer_trains", false);

		currentHour = U.getCurrentHour();
		currentMinute = U.getCurrentMinute();

		core = U.getCore(context, widgetID);

		U.log("RemoteListViewFactory()");
		if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
			Bundle bundle = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE);
			schedule = (ArrayList<TrainTime>) bundle.getSerializable(U.EXTRA_SCHEDULE_DATA);
			if (schedule == null) U.sendNoInternetError(widgetID, context);
			else if (schedule.size() == 0) U.sendNoTimesError(widgetID, context);
			else {
				TrainTime trainTime = schedule.get(0);
				transfers = trainTime.getTransfer();

				alarm_departure_time = U.getAlarm(context, widgetID, trainTime.getOrigin(), trainTime.getDestination());
			}
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		U.log("onDataSetChanged()");

		if (schedule.size() > 0) {
			TrainTime trainTime = schedule.get(0);
			alarm_departure_time = U.getAlarm(context, widgetID, trainTime.getOrigin(), trainTime.getDestination());
		}

		U.log("Alarm departure time: " + alarm_departure_time);

		if (alarm_departure_time != null) {
			for (int i = 0; i < schedule.size(); i++) {
				TrainTime trainTime = schedule.get(i);
				if (trainTime.getTransfer() == 2 && trainTime.isSame_origin_train() && show_more_transfer_trains && group_transfer_exits) {
					if (trainTime.getDeparture_time_transfer_one().equalsIgnoreCase(alarm_departure_time)) {
						this.getViewAt(i).setImageViewResource(R.id.imageView, R.drawable.ic_alarm);
					}
				} else {
					if (trainTime.getDeparture_time().equalsIgnoreCase(alarm_departure_time)) {
						this.getViewAt(i).setImageViewResource(R.id.imageView, R.drawable.ic_alarm);
					}
				}
			}
		} else {
			for (int i = 0; i < schedule.size(); i++) {
				this.getViewAt(i).setImageViewResource(R.id.imageView, R.drawable.ic_no_alarm);
			}
		}
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
		if (position == schedule.size()) {//Data origin row
			return new RemoteViews(context.getPackageName(), R.layout.data_info_line);
		} else {
			RemoteViews row = null;
			TrainTime trainTime = schedule.get(position);
			boolean isBeforeCurrentHour = isBeforeCurrentHour(trainTime.getDeparture_time());
			switch (transfers) {
				case 0: {
					row = new RemoteViews(context.getPackageName(), R.layout.time_list);
					setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());

					setDisabledTexts(row, isBeforeCurrentHour);

					if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
						row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
					} else {
						row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
					}
				}
				break;
				case 1: {
					if (trainTime.isDirect_train()) {
						row = new RemoteViews(context.getPackageName(), R.layout.time_list);
						setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());

						setDisabledTexts(row, isBeforeCurrentHour);

						if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
							row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
						} else {
							row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
						}
					} else {
						if (trainTime.isSame_origin_train()) {
							if (show_more_transfer_trains) {
								if (group_transfer_exits) {
									row = new RemoteViews(context.getPackageName(), R.layout.time_list);
									setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());

									setDisabledTexts(row, isBeforeCurrentHour);
								} else {
									row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);
									setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());
									setTextsTransferOne(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());

									setDisabledTexts(row, isBeforeCurrentHour);
									setDisabledTextsTransferOne(row, isBeforeCurrentHour);
								}
								if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
								} else {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
								}
							}
						} else {
							row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);
							setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());
							setTextsTransferOne(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());

							setDisabledTexts(row, isBeforeCurrentHour);
							setDisabledTextsTransferOne(row, isBeforeCurrentHour);

							if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
								row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
							} else {
								row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
							}
						}
					}
				}
				break;
				case 2: {
					if (trainTime.isSame_origin_train()) {
						if (show_more_transfer_trains) {
							if (group_transfer_exits) {
								row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);
								setTexts(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());
								setTextsTransferOne(row, trainTime.getLine_transfer_two(), trainTime.getDeparture_time_transfer_two(), trainTime.getArrival_time_transfer_two());

								setDisabledTexts(row, isBeforeCurrentHour);
								setDisabledTextsTransferOne(row, isBeforeCurrentHour);

								if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time_transfer_one())) {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
								} else {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
								}
							} else {
								row = new RemoteViews(context.getPackageName(), R.layout.time_list_two_transfer);
								setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());
								setTextsTransferOne(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());
								setTextsTransferTwo(row, trainTime.getLine_transfer_two(), trainTime.getDeparture_time_transfer_two(), trainTime.getArrival_time_transfer_two());

								setDisabledTexts(row, isBeforeCurrentHour);
								setDisabledTextsTransferOne(row, isBeforeCurrentHour);
								setDisabledTextsTransferTwo(row, isBeforeCurrentHour);

								if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
								} else {
									row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
								}
							}
						}
					} else {
						row = new RemoteViews(context.getPackageName(), R.layout.time_list_two_transfer);
						setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());
						setTextsTransferOne(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());
						setTextsTransferTwo(row, trainTime.getLine_transfer_two(), trainTime.getDeparture_time_transfer_two(), trainTime.getArrival_time_transfer_two());

						setDisabledTexts(row, isBeforeCurrentHour);
						setDisabledTextsTransferOne(row, isBeforeCurrentHour);
						setDisabledTextsTransferTwo(row, isBeforeCurrentHour);

						if (alarm_departure_time != null && alarm_departure_time.equalsIgnoreCase(trainTime.getDeparture_time())) {
							row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_alarm);
						} else {
							row.setImageViewResource(R.id.alarmImageView, R.drawable.ic_no_alarm);
						}
					}
				}
				break;
			}

			if (row != null) {
				Intent intent = new Intent(context, WidgetManager.class);
				TrainTime trainT = schedule.get(0);
				if (trainT.getTransfer() == 2 && trainT.isSame_origin_train() && show_more_transfer_trains && group_transfer_exits)
					intent.putExtra(U.EXTRA_ALARM_DEPARTURE_TIME, trainT.getDeparture_time_transfer_one());
				else
					intent.putExtra(U.EXTRA_ALARM_DEPARTURE_TIME, schedule.get(position).getDeparture_time());
				row.setOnClickFillInIntent(R.id.timesListLayout, intent);
			}

			return row;
		}
	}

	private void setTexts(RemoteViews row, String line, String departure_time, String arrival_time) {
		row.setTextViewText(R.id.lineText, line);
		row.setTextViewText(R.id.departureTimeText, departure_time);
		row.setTextViewText(R.id.arrivalTimeText, arrival_time);
		try {
			row.setInt(R.id.lineText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(line + core).getBColor()));
			row.setTextColor(R.id.lineText, StationUtils.ColorLines.valueOf(line + core).getTColor());
		} catch (Exception e) {
			U.log("Unknown color for setTexts: " + line + core);
		}
	}

	private void setTextsTransferOne(RemoteViews row, String line, String departure_time, String arrival_time) {
		row.setTextViewText(R.id.lineTransferOneText, line);
		row.setTextViewText(R.id.transferOneDepartureTimeText, departure_time);
		row.setTextViewText(R.id.transferOneArrivalTimeText, arrival_time);
		try {
			row.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(line + core).getBColor()));
			row.setTextColor(R.id.lineTransferOneText, StationUtils.ColorLines.valueOf(line + core).getTColor());
		} catch (Exception e) {
			U.log("Unknown color for setTexts: " + line + core);
		}
	}

	private void setTextsTransferTwo(RemoteViews row, String line, String departure_time, String arrival_time) {
		row.setTextViewText(R.id.lineTransferTwoText, line);
		row.setTextViewText(R.id.transferTwoDepartureTimeText, departure_time);
		row.setTextViewText(R.id.transferTwoArrivalTimeText, arrival_time);
		try {
			row.setInt(R.id.lineTransferTwoText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(line + core).getBColor()));
			row.setTextColor(R.id.lineTransferTwoText, StationUtils.ColorLines.valueOf(line + core).getTColor());
		} catch (Exception e) {
			U.log("Unknown color for setTexts: " + line + core);
		}
	}

	private void setDisabledTexts(RemoteViews row, boolean disabled) {
		row.setTextColor(R.id.departureTimeText, disabled ? Color.LTGRAY : Color.BLACK);
		row.setTextColor(R.id.arrivalTimeText, disabled ? Color.LTGRAY : Color.BLACK);
	}

	private void setDisabledTextsTransferOne(RemoteViews row, boolean disabled) {
		row.setTextColor(R.id.transferOneDepartureTimeText, disabled ? Color.LTGRAY : Color.BLACK);
		row.setTextColor(R.id.transferOneArrivalTimeText, disabled ? Color.LTGRAY : Color.BLACK);
	}

	private void setDisabledTextsTransferTwo(RemoteViews row, boolean disabled) {
		row.setTextColor(R.id.transferTwoDepartureTimeText, disabled ? Color.LTGRAY : Color.BLACK);
		row.setTextColor(R.id.transferTwoArrivalTimeText, disabled ? Color.LTGRAY : Color.BLACK);
	}

	//@TODO Move this to Utils
	private boolean isBeforeCurrentHour(String time) {
		if (time == null) return false;
		String[] split = time.split(":");
		int hour = Integer.parseInt(split[0]);
		int minute = Integer.parseInt(split[1]);

		return hour != 0 && (hour < currentHour || hour == currentHour && minute <= currentMinute);
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
