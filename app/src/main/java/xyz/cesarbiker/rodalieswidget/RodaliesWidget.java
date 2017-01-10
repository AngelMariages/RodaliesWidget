package xyz.cesarbiker.rodalieswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class RodaliesWidget extends RemoteViews {
    private final Context context;
    private final int widgetID;
    private final AppWidgetManager appWidgetManager;

    public RodaliesWidget(Context context, int widgetID) {
        super(context.getPackageName(), R.layout.widget_layout);
        this.context = context;
        this.widgetID = widgetID;
        this.appWidgetManager = AppWidgetManager.getInstance(context);

        Intent adapterIntent = new Intent(context, WidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));

        this.setRemoteAdapter(R.id.horarisListView, adapterIntent);
        // TODO: 08-Jan-17 Add empty view
        //this.setEmptyView();
        getStationNames();
        setPendingIntents();
    }

    private void getStationNames() {
        String[] stations = Utils.getStations(context, widgetID);
        if(stations != null) {
            updateStationsText(stations[0], stations[1]);
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
        listViewClickIntent.setAction(Utils.ACTION_CLICK_LIST_ITEM + getWidgetID());
        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0,
                listViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setPendingIntentTemplate(R.id.horarisListView, clickPI);
    }

    private void setUpdateButtonIntent() {
        Intent updateButtonIntent = new Intent(context, WidgetManager.class);
        updateButtonIntent.setAction(Utils.ACTION_CLICK_UPDATE_BUTTON + getWidgetID());
        updateButtonIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                updateButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.actualitzarButton, pendingIntent);
    }

    private void setSwapButtonIntent() {
        Intent swapButtonIntent = new Intent(context, WidgetManager.class);
        swapButtonIntent.setAction(Utils.ACTION_CLICK_SWAP_BUTTON + getWidgetID());
        swapButtonIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        PendingIntent swapPI = PendingIntent.getBroadcast(context, 0,
                swapButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.intercanviarButton, swapPI);
    }

    private void setConfigStationIntent() {
        Intent originStationIntent = new Intent(context, WidgetManager.class);
        originStationIntent.setAction(Utils.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_O");
        originStationIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        originStationIntent.putExtra(Utils.EXTRA_ORIGINorDESTINATION, Utils.ORIGIN);
        PendingIntent showDialogPI1 = PendingIntent.getBroadcast(context, 0,
                originStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.origenLayout, showDialogPI1);

        Intent destinationStationIntent = new Intent(context, WidgetManager.class);
        destinationStationIntent.setAction(Utils.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_D");
        destinationStationIntent.putExtra(Utils.EXTRA_WIDGET_ID, widgetID);
        destinationStationIntent.putExtra(Utils.EXTRA_ORIGINorDESTINATION, Utils.DESTINATION);
        PendingIntent showDialogPI2 = PendingIntent.getBroadcast(context, 0,
                destinationStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.destiLayout, showDialogPI2);
    }

    void updateStationsText(String originText, String destinationText) {
        this.setTextViewText(R.id.origenTextView, originText);
        this.setTextViewText(R.id.destiTextView, destinationText);
        appWidgetManager.updateAppWidget(widgetID, this);
        Utils.saveStations(context, widgetID, originText, destinationText);
    }

    private String getWidgetID() {
        return String.valueOf(widgetID);
    }


}

