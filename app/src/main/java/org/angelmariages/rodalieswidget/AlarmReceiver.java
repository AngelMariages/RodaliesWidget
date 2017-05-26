package org.angelmariages.rodalieswidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import net.grandcentrix.tray.AppPreferences;

import org.angelmariages.rodalieswidget.utils.U;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		int widgetID = intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
		if(widgetID == -1) return;

		U.log("Alarm received, removing it " + widgetID);
		int[] stations = U.getStations(context, widgetID);
		if (stations.length == 2) {
			U.removeAlarm(context, widgetID, stations[0], stations[1]);
		}

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		String customUri = new AppPreferences(context).getString(U.PREFERENCE_STRING_ALARM_URI, null);
		if(customUri != null) {
			if(customUri.equalsIgnoreCase("--silent--")) soundUri = null;
			else soundUri = Uri.parse(customUri);
		}

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
		notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
		notificationBuilder.setContentTitle(context.getString(R.string.app_name));
		notificationBuilder.setContentText(context.getString(R.string.notification_content_text));
		notificationBuilder.setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 });
		notificationBuilder.setLights(Color.RED, 1000, 1000);
		if(soundUri != null) notificationBuilder.setSound(soundUri);
		notificationBuilder.setAutoCancel(true);

		Notification notification = notificationBuilder.build();
		notification.flags |= Notification.FLAG_INSISTENT;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);

		U.logEventAlarmFired(context);
	}
}
