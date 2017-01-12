package xyz.cesarbiker.rodalieswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import xyz.cesarbiker.rodalieswidget.utils.StationUtils;
import xyz.cesarbiker.rodalieswidget.utils.U;

class RodaliesWidget extends RemoteViews {
    private Context context;
    private int widgetID;
    private AppWidgetManager appWidgetManager;

    RodaliesWidget(Context context, int widgetID) {
        super(context.getPackageName(), R.layout.widget_layout);
        this.context = context;
        this.widgetID = widgetID;
        this.appWidgetManager = AppWidgetManager.getInstance(context);

        Intent adapterIntent = new Intent(context, WidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));

        this.setRemoteAdapter(R.id.horarisListView, adapterIntent);
        setStationNames();
        setPendingIntents();
    }

    RodaliesWidget(Context context, int widgetID, int reason) {
        super(context.getPackageName(), R.layout.widget_layout_no_data);
        this.context = context;
        this.widgetID = widgetID;
        this.appWidgetManager = AppWidgetManager.getInstance(context);

        Intent adapterIntent = new Intent(context, WidgetService.class);
        adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        adapterIntent.setData(Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME)));

        this.setRemoteAdapter(R.id.horarisListView, adapterIntent);
        setStationNames();
        setPendingIntents();

        if(reason == 0) {
            this.setTextViewText(R.id.reasonTextView, context.getResources().getString(R.string.no_internet));
        }
    }
    private void setStationNames() {
        int[] stations = U.getStations(context, widgetID);
        if(stations.length > 0) {
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
        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0,
                listViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setPendingIntentTemplate(R.id.horarisListView, clickPI);
    }

    private void setUpdateButtonIntent() {
        Intent updateButtonIntent = new Intent(context, WidgetManager.class);
        updateButtonIntent.setAction(U.ACTION_CLICK_UPDATE_BUTTON + getWidgetID());
        updateButtonIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                updateButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.actualitzarButton, pendingIntent);
    }

    private void setSwapButtonIntent() {
        Intent swapButtonIntent = new Intent(context, WidgetManager.class);
        swapButtonIntent.setAction(U.ACTION_CLICK_SWAP_BUTTON + getWidgetID());
        swapButtonIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        PendingIntent swapPI = PendingIntent.getBroadcast(context, 0,
                swapButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.intercanviarButton, swapPI);
    }

    private void setConfigStationIntent() {
        Intent originStationIntent = new Intent(context, WidgetManager.class);
        originStationIntent.setAction(U.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_O");
        originStationIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        originStationIntent.putExtra(U.EXTRA_OREGNorDESTINATION, U.ORIGIN);
        PendingIntent showDialogPI1 = PendingIntent.getBroadcast(context, 0,
                originStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.origenLayout, showDialogPI1);

        Intent destinationStationIntent = new Intent(context, WidgetManager.class);
        destinationStationIntent.setAction(U.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_D");
        destinationStationIntent.putExtra(U.EXTRA_WIDGET_ID, widgetID);
        destinationStationIntent.putExtra(U.EXTRA_OREGNorDESTINATION, U.DESTINATION);
        PendingIntent showDialogPI2 = PendingIntent.getBroadcast(context, 0,
                destinationStationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        this.setOnClickPendingIntent(R.id.destiLayout, showDialogPI2);
    }

    void updateStationsText(String originText, String destinationText) {
        String nullOrigin = context.getResources().getString(R.string.no_origin_set);
        String nullDestination = context.getResources().getString(R.string.no_destination_set);
        if(originText == null) originText = nullOrigin;
        if(destinationText == null) destinationText = nullDestination;

        this.setTextViewText(R.id.origenTextView, originText);
        this.setTextViewText(R.id.destiTextView, destinationText);
        appWidgetManager.updateAppWidget(widgetID, this);
        U.saveStations(context, widgetID, originText, destinationText);
    }

    private String getWidgetID() {
        return String.valueOf(widgetID);
    }


}

