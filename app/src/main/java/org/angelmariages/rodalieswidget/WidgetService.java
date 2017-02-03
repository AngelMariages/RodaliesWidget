package org.angelmariages.rodalieswidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.utils.U;

import java.io.Serializable;
import java.util.ArrayList;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        U.log("onGetViewFactory()");
	    if (intent.hasExtra(U.EXTRA_SCHEDULE_BUNDLE)) {
		    Serializable serializable = intent.getBundleExtra(U.EXTRA_SCHEDULE_BUNDLE).getSerializable(U.EXTRA_SCHEDULE_DATA);
		    if(serializable instanceof ArrayList) {
				ArrayList<TrainTime> scheduleTimes = (ArrayList<TrainTime>) serializable;
			    for (TrainTime scheduleTime : scheduleTimes) {
				    System.out.println(scheduleTime.getArrival_time());
			    }
			    return new RemoteListViewFactory(this.getApplicationContext(), intent);
		    }
	    }
	    return null;
    }
}
