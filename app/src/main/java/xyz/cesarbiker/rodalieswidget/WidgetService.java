package xyz.cesarbiker.rodalieswidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import xyz.cesarbiker.rodalieswidget.utils.U;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        U.log("onGetViewFactory()");
        return new RemoteListViewFactory(this.getApplicationContext(), intent);
    }
}
