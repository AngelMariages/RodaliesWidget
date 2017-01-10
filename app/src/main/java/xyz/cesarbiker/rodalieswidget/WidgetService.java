package xyz.cesarbiker.rodalieswidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Arrays;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Utils.log("onGetViewFactory()");
        return new RemoteListViewFactory(this.getApplicationContext(), intent);
    }
}
