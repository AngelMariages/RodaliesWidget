package org.angelmariages.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import org.angelmariages.rodalieswidget.timetables.GetHoraris;
import org.angelmariages.rodalieswidget.timetables.Horari;
import org.angelmariages.rodalieswidget.utils.U;

class RemoteListViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int widgetID;

    private ArrayList<Horari> taulaHoraris = new ArrayList<>();

    RemoteListViewFactory(Context context, Intent intent) {
        this.context = context;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    /*intentFilter.addAction(U.ACTION_CLICK_SWAP_BUTTON );
    intentFilter.addAction(U.ACTION_SEND_NEW_STATIONS);*/

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
            taulaHoraris = new GetHoraris(context).get(stations[0], stations[1]);
            if(taulaHoraris == null) {
                Intent noDataIntent = new Intent(context, WidgetManager.class);
                noDataIntent.setAction(U.ACTION_WIDGET_NO_DATA + widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
                noDataIntent.putExtra(U.EXTRA_WIDGET_STATE, U.WIDGET_STATE_NO_INTERNET);
                context.sendBroadcast(noDataIntent);
                U.log("taulaHoraris NULL!");
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
        // TODO: 09-May-16 ArrayIndexOutOfBoundsException
        // TODO: 21-Mar-16 Comprovar horaris correctes(Sense error)
        if(position >= getCount()) return null;// TODO: 08-Jan-17 Set loading view

        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.list_element);

        row.setTextViewText(R.id.horarisSortidaText, taulaHoraris.get(position).getHora_sortida());
        row.setTextViewText(R.id.horarisArribadaText, taulaHoraris.get(position).getHora_arribada());

        Intent intent = new Intent(context, WidgetManager.class);
        intent.putExtra(U.EXTRA_RIDE_LENGTH, taulaHoraris.get(position).getDuracio_trajecte());
        row.setOnClickFillInIntent(R.id.horarisListLayout, intent);

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
