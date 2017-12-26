package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

class WidgetListViewFactory implements RemoteViewsService.RemoteViewsFactory {
	private final boolean group_transfer_exits, show_more_transfer_trains;
	private final int widgetID;
	private final int core;
	private final FirebaseRemoteConfig firebaseRemoteConfig;
	private String alarm_departure_time;
	private int transfers = 0;
	private Context context = null;

	private ArrayList<TrainTime> schedule;
	private ArrayList<RemoteViews> remoteViews;
	private boolean remote_view_in_memory;
	private long publishAnalyticsTime;
	private long worstElapsed;

	@SuppressWarnings("unchecked")
	WidgetListViewFactory(Context context, Intent intent) {
		this.context = context;
		widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		group_transfer_exits = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("group_transfer_exits", false);
		show_more_transfer_trains = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_more_transfer_trains", false);

		core = U.getCore(context, widgetID);

		U.log("WidgetListViewFactory()");
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

		firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
	}

	@Override
	public void onCreate() {
		if (firebaseRemoteConfig != null && firebaseRemoteConfig.getBoolean("remote_view_in_memory")) {
			remote_view_in_memory = true;
			loadViewsToMemory();
		}
		U.setUserProperty(context, "r_view_loading_strategy", remote_view_in_memory ? "mem" : "cpu");
	}

	private void loadViewsToMemory() {
		if (schedule != null && schedule.size() > 0) {
			remoteViews = new ArrayList<>();
			for (int i = 0; i < getCount(); i++) {
				remoteViews.add(i, getRow(i));
			}
		}
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
			for (int i = 1; i < schedule.size() + 1; i++) {
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
			for (int i = 1; i < schedule.size() + 1; i++) {
				this.getViewAt(i).setImageViewResource(R.id.imageView, R.drawable.ic_no_alarm);
			}
		}

		if (remote_view_in_memory) loadViewsToMemory();
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		if (schedule != null && schedule.size() > 0) return schedule.size() + 3;
		else return 0;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		long before = System.currentTimeMillis();
		RemoteViews row = remote_view_in_memory ? remoteViews != null && remoteViews.size() > 0 ? remoteViews.get(position) : null : getRow(position);
		long elapsed = System.currentTimeMillis() - before;
		if (elapsed > worstElapsed) worstElapsed = elapsed;
		if (System.currentTimeMillis() - publishAnalyticsTime > 2000) {
			U.setUserProperty(context, "r_view_loading_time", String.valueOf(worstElapsed));
			publishAnalyticsTime = System.currentTimeMillis();
		}
		return row;
	}

	private RemoteViews getRow(int position) {
		if (position == schedule.size() + 1) {//Data origin row
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.data_info_line);
			if (core != 50)
				remoteViews.setTextViewText(R.id.dataInfoText, context.getString(R.string.dataInfoTextRenf));
			return remoteViews;
		} else if (position == 0 || position == schedule.size() + 2) {
			Calendar scheduleDate = schedule.get(0).getDate();
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.switch_day_line);
			String dayOfWeek = scheduleDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
			dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);
			String dayOfMonth = String.valueOf(scheduleDate.get(Calendar.DAY_OF_MONTH));
			String month = scheduleDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
			remoteViews.setTextViewText(R.id.dateText, dayOfWeek + " " + dayOfMonth + " " + month);

			long diff = scheduleDate.getTime().getTime() - U.getCalendarForDelta(0).getTime().getTime();
			diff /= 1000 * 60 * 60 * 24;

			Intent dayBackIntent = new Intent(context, WidgetManager.class);
			dayBackIntent.putExtra(U.EXTRA_SWITCH_TO, ((int) diff) - 1);
			remoteViews.setOnClickFillInIntent(R.id.dayBack, dayBackIntent);

			Intent dayForwardIntent = new Intent(context, WidgetManager.class);
			dayForwardIntent.putExtra(U.EXTRA_SWITCH_TO, ((int) diff) + 1);
			remoteViews.setOnClickFillInIntent(R.id.dayForward, dayForwardIntent);

			return remoteViews;
		} else {
			position -= 1;
			RemoteViews row = null;
			TrainTime trainTime = schedule.get(position);
			boolean isNotScheduledTrain = !U.isScheduledTrain(trainTime);
			switch (transfers) {
				case 0: {
					row = new RemoteViews(context.getPackageName(), R.layout.time_list);
					setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());

					setDisabledTexts(row, isNotScheduledTrain);

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

						setDisabledTexts(row, isNotScheduledTrain);

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

									setDisabledTexts(row, isNotScheduledTrain);
								} else {
									row = new RemoteViews(context.getPackageName(), R.layout.time_list_one_transfer);
									setTexts(row, trainTime.getLine(), trainTime.getDeparture_time(), trainTime.getArrival_time());
									setTextsTransferOne(row, trainTime.getLine_transfer_one(), trainTime.getDeparture_time_transfer_one(), trainTime.getArrival_time_transfer_one());

									setDisabledTexts(row, isNotScheduledTrain);
									setDisabledTextsTransferOne(row, isNotScheduledTrain);
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

							setDisabledTexts(row, isNotScheduledTrain);
							setDisabledTextsTransferOne(row, isNotScheduledTrain);

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

								setDisabledTexts(row, isNotScheduledTrain);
								setDisabledTextsTransferOne(row, isNotScheduledTrain);

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

								setDisabledTexts(row, isNotScheduledTrain);
								setDisabledTextsTransferOne(row, isNotScheduledTrain);
								setDisabledTextsTransferTwo(row, isNotScheduledTrain);

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

						setDisabledTexts(row, isNotScheduledTrain);
						setDisabledTextsTransferOne(row, isNotScheduledTrain);
						setDisabledTextsTransferTwo(row, isNotScheduledTrain);

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
				// TODO: 25/12/2017 ??
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