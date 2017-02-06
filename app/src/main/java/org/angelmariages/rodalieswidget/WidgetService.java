package org.angelmariages.rodalieswidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import org.angelmariages.rodalieswidget.utils.U;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        U.log("onGetViewFactory()");
	    return new RemoteListViewFactory(this.getApplicationContext(), intent);
    }
}
