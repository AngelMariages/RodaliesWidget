package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.U;

class RemoteListViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private final int widgetID;

    private ArrayList<TrainTime> taulaHoraris = new ArrayList<>();

    RemoteListViewFactory(Context context, Intent intent) {
        this.context = context;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        U.log("RemoteListViewFactory()");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        U.log("onDataSetChanged() I'm widgetID: " + widgetID);
        int[] stations = U.getStations(context, widgetID);

        if(stations[0] == -1 || stations[1] == -1) {
            Intent noStationsIntent = new Intent(context, WidgetManager.class);
            noStationsIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetID);
            noStationsIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
            noStationsIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_STATIONS);
            context.sendBroadcast(noStationsIntent);
        } else {
            /*taulaHoraris = new GetTimeTablesRenfe(context).get(stations[0], stations[1]);
            new GetTimeTablesRenfe(context).get(stations[1], stations[0]);*/
            if(taulaHoraris == null) {
                Intent noDataIntent = new Intent(context, WidgetManager.class);
                noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_INTERNET);
                context.sendBroadcast(noDataIntent);
            } else if(taulaHoraris.size() == 0) {
                Intent noDataIntent = new Intent(context, WidgetManager.class);
                noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_TIMES);
                context.sendBroadcast(noDataIntent);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(taulaHoraris != null) return taulaHoraris.size();
        else return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position >= getCount()) return null;

        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.time_list);

        row.removeAllViews(R.id.timesListLayout);

        row.setTextViewText(R.id.departureTimeText, taulaHoraris.get(position).getDeparture_time());
        row.setTextViewText(R.id.arrivalTimeText, taulaHoraris.get(position).getArrival_time());

        Intent intent = new Intent(context, WidgetManager.class);
        intent.putExtra(U.EXTRA_RIDE_LENGTH, taulaHoraris.get(position).getTravel_time());
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
