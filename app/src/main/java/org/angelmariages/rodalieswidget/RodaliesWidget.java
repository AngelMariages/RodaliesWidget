package org.angelmariages.rodalieswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import org.angelmariages.rodalieswidget.timetables.GetSchedule;
import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;

class RodaliesWidget extends RemoteViews {
	private int state = U.WIDGET_STATE_SCHEDULE_LOADED;
	private final Context context;
	private final int widgetID;

	RodaliesWidget(Context context, int widgetID, int state, int layout, ArrayList<TrainTime> schedule) {
		super(context.getPackageName(), layout);
		this.context = context;
		this.widgetID = widgetID;
		setStationNames();
		setPendingIntents();
		//@TODO manage web service status !important

		this.state = state;

		if (state == U.WIDGET_STATE_UPDATING_TABLES) {
			new GetSchedule(context).execute(widgetID);
		} else if (state == U.WIDGET_STATE_SCHEDULE_LOADED) {
			Intent adapterIntent = new Intent(context, WidgetService.class);
			adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
			adapterIntent.setData(Uri.fromParts("content", String.valueOf(widgetID) + Math.random(), null));

			Bundle bundle = new Bundle();
			bundle.putSerializable(U.EXTRA_SCHEDULE_DATA, schedule);
			adapterIntent.putExtra(U.EXTRA_SCHEDULE_BUNDLE, bundle);

			if(schedule != null && schedule.size() > 0) {
				switch (schedule.get(0).getTransfer()) {
					case 1: {
						String transferStation = null;
						try {
							transferStation = StationUtils.getNameFromID(Integer.parseInt(schedule.get(0).getStation_transfer_one()));
						} catch (NumberFormatException ignored) {}
						if(transferStation != null) {
							this.setTextViewText(R.id.transferOneTitleText, transferStation);
							this.setTextViewText(R.id.lineTransferOneText, schedule.get(0).getLine_transfer_one());
							try {
								this.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_one()).getBColor()));
								this.setTextColor(R.id.lineTransferOneText, StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_one()).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + schedule.get(0).getLine_transfer_one());
							}
						}
						else this.setViewVisibility(R.id.transferOneTitleText, View.GONE);
					} break;
					case 2: {
						String transferStation = null, transferStationTwo = null;
						try {
							transferStation = StationUtils.getNameFromID(Integer.parseInt(schedule.get(0).getStation_transfer_one()));
							transferStationTwo = StationUtils.getNameFromID(Integer.parseInt(schedule.get(0).getStation_transfer_two()));
						} catch (NumberFormatException ignored) { }
						if(transferStation != null) {
							this.setTextViewText(R.id.transferOneTitleText, transferStation);
							this.setTextViewText(R.id.lineTransferOneText, schedule.get(0).getLine_transfer_one());
							try {
								this.setInt(R.id.lineTransferOneText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_one()).getBColor()));
								this.setTextColor(R.id.lineTransferOneText, StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_one()).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + schedule.get(0).getLine_transfer_one());
							}
						}
						else this.setViewVisibility(R.id.transferOneTitleText, View.GONE);
						if(transferStationTwo != null) {
							this.setTextViewText(R.id.transferTwoTitleText, transferStationTwo);
							this.setTextViewText(R.id.lineTransferTwoText, schedule.get(0).getLine_transfer_two());
							try {
								this.setInt(R.id.lineTransferTwoText, "setBackgroundColor", Color.parseColor(StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_two()).getBColor()));
								this.setTextColor(R.id.lineTransferTwoText, StationUtils.ColorLines.valueOf(schedule.get(0).getLine_transfer_two()).getTColor());
							} catch (Exception e) {
								U.log("Unknown color for setTexts: " + schedule.get(0).getLine_transfer_two());
							}
						}
						else this.setViewVisibility(R.id.transferTwoTitleText, View.GONE);
					} break;
				}
			}
			this.setRemoteAdapter(R.id.scheduleListView, adapterIntent);
		} else if (state == U.WIDGET_STATE_NO_INTERNET) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_internet));
		} else if (state == U.WIDGET_STATE_NO_STATIONS) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_stations));
		} else if (state == U.WIDGET_STATE_NO_TIMES) {
			this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_times));
		}

		setStationNames();
		if (state != U.WIDGET_STATE_UPDATING_TABLES) setPendingIntents();
	}

	private void setStationNames() {
		int[] stations = U.getStations(context, widgetID);
		if (stations.length > 0) {
			updateStationsText(StationUtils.getNameFromID(stations[0]), StationUtils.getNameFromID(stations[1]));
		}
	}

	private void setPendingIntents() {
		setListViewClickIntent();
		setUpdateButtonIntent();
		setSwapButtonIntent();
		setConfigStationIntent();
	}

	private void setListViewClickIntent() {
		//It this intent is not set the intent when on click on a row of the list view doesn't work
		Intent listViewClickIntent = new Intent(context, WidgetManager.class);
		listViewClickIntent.setAction(U.ACTION_CLICK_LIST_ITEM + getWidgetID());
		listViewClickIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		PendingIntent clickPI = PendingIntent.getBroadcast(context, 0,
				listViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setPendingIntentTemplate(R.id.scheduleListView, clickPI);
	}

	private void setUpdateButtonIntent() {
		Intent updateButtonIntent = new Intent(context, WidgetManager.class);
		updateButtonIntent.setAction(U.ACTION_CLICK_UPDATE_BUTTON + getWidgetID());
		updateButtonIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		updateButtonIntent.putExtra(U.EXTRA_WIDGET_STATE, state);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				updateButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.updateButton, pendingIntent);
	}

	private void setSwapButtonIntent() {
		Intent swapButtonIntent = new Intent(context, WidgetManager.class);
		swapButtonIntent.setAction(U.ACTION_CLICK_SWAP_BUTTON + getWidgetID());
		swapButtonIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		swapButtonIntent.putExtra(U.EXTRA_WIDGET_STATE, state);
		PendingIntent swapPI = PendingIntent.getBroadcast(context, 0,
				swapButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.swapButton, swapPI);
	}

	private void setConfigStationIntent() {
		Intent originStationIntent = new Intent(context, WidgetManager.class);
		originStationIntent.setAction(U.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_O");
		originStationIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		originStationIntent.putExtra(U.EXTRA_OREGNorDESTINATION, U.ORIGIN);
		PendingIntent showDialogPI1 = PendingIntent.getBroadcast(context, 0,
				originStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.originLayout, showDialogPI1);

		Intent destinationStationIntent = new Intent(context, WidgetManager.class);
		destinationStationIntent.setAction(U.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_D");
		destinationStationIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
		destinationStationIntent.putExtra(U.EXTRA_OREGNorDESTINATION, U.DESTINATION);
		PendingIntent showDialogPI2 = PendingIntent.getBroadcast(context, 0,
				destinationStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.setOnClickPendingIntent(R.id.destinationLayout, showDialogPI2);
	}

	private void updateStationsText(String originText, String destinationText) {
		String nullOrigin = context.getResources().getString(R.string.no_origin_set);
		String nullDestination = context.getResources().getString(R.string.no_destination_set);
		if (originText == null) originText = nullOrigin;
		if (destinationText == null) destinationText = nullDestination;

		this.setTextViewText(R.id.originTextView, originText);
		this.setTextViewText(R.id.destinationTextView, destinationText);
	}

	private String getWidgetID() {
		return String.valueOf(widgetID);
	}


}

