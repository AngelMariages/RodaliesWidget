package xyz.cesarbiker.rodalieswidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

public class RemoteListViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int widgetID;
    private WidgetReceiver widgetReceiver;

    private ArrayList<Horari> taulaHoraris = new ArrayList<>();

    public RemoteListViewFactory(Context context, Intent intent) {
        this.context = context;
        widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Utils.log("Id: " + widgetID, "i");
    }

    @Override
    public void onCreate() {
        Utils.log("onCreate!!!!!");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.ACTION_CLICK_SWAP_BUTTON );
        intentFilter.addAction(Utils.ACTION_SEND_NEWSTATIONS );

        widgetReceiver = new WidgetReceiver(widgetID, context);

        context.registerReceiver(widgetReceiver, intentFilter);
    }

    @Override
    public void onDataSetChanged() {
        Utils.log("onDataSetChanged() I'm widgetID: " + widgetID);
        String[] stations = Utils.getStations(context, widgetID);

        if(stations != null) {
            taulaHoraris = new GetHoraris(context).get(Utils.getIDFromEstacio(stations[0]), Utils.getIDFromEstacio(stations[1]));
        } else {
            taulaHoraris = new GetHoraris(context).get(79409, 71801);
        }
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(widgetReceiver);
    }

    @Override
    public int getCount() {
        return taulaHoraris.size();
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
        intent.putExtra(Utils.EXTRA_RIDELENGTH, taulaHoraris.get(position).getDuracio_trajecte());
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
