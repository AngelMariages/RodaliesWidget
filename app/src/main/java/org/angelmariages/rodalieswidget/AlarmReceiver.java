package org.angelmariages.rodalieswidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.angelmariages.rodalieswidget.utils.U;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		U.log("Alarm received, removing it");
		Toast.makeText(context, "Alarm received, removing it", Toast.LENGTH_LONG).show();
		U.removeAlarm(context);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.drawable.ic_alarm);
		mBuilder.setContentTitle("Alarm Widget!!");
		mBuilder.setContentText("Alarm received!");
		mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
	}
}
