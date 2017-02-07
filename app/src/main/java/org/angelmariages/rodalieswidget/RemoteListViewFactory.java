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
    private Context context = null;
    private final int widgetID;

    private ArrayList<TrainTime> schedule;

    RemoteListViewFactory(Context context, Intent intent) {
        this.context = context;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        U.log("RemoteListViewFactory()");
        if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
            Bundle bundle = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE);
            schedule = (ArrayList<TrainTime>) bundle.getSerializable(U.EXTRA_SCHEDULE_DATA);
	        // TODO: 2/6/17 Check if null

            for (TrainTime trainTime : schedule) {
                U.log(trainTime.toString());
            }
        }
    }

    @Override
    public void onCreate() {
        U.log("onCreate()");
    }

    @Override
    public void onDataSetChanged() {
        U.log("onDataSetChanged() I'm widgetID: " + widgetID);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(schedule != null) return schedule.size();
        else return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
	    if(position >= getCount()) return null;

        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.time_list);

        //row.removeAllViews(R.id.timesListLayout);

        row.setTextViewText(R.id.departureTimeText, schedule.get(position).getDeparture_time());
        row.setTextViewText(R.id.arrivalTimeText, schedule.get(position).getArrival_time());

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
}
