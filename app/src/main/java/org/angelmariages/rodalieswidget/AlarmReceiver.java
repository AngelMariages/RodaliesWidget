package org.angelmariages.rodalieswidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import net.grandcentrix.tray.AppPreferences;

import org.angelmariages.rodalieswidget.utils.U;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		int widgetID = intent.getIntExtra(U.EXTRA_WIDGET_ID, -1);
		if (widgetID == -1) return;

		U.log("Alarm received, removing it " + widgetID);
		String[] stations = U.getStations(context, widgetID);
		if (stations.length == 2) {
			U.removeAlarm(context, widgetID, stations[0], stations[1]);
		}

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		String customUri = new AppPreferences(context).getString(U.PREFERENCE_STRING_ALARM_URI, null);
		if (customUri != null) {
			if (customUri.equalsIgnoreCase("--silent--")) soundUri = null;
			else soundUri = Uri.parse(customUri);
		}

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "alarm_channel_id");
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			notificationBuilder.setSmallIcon(R.mipmap.ic_notification_white);
		} else {
			notificationBuilder.setSmallIcon(R.mipmap.ic_notification);
		}
		notificationBuilder.setContentTitle(context.getString(R.string.app_name));
		notificationBuilder.setContentText(context.getString(R.string.notification_content_text));
		notificationBuilder.setVibrate(new long[]{0, 1000, 1000, 1000, 1000});
		notificationBuilder.setLights(Color.RED, 1000, 1000);
		if (soundUri != null) notificationBuilder.setSound(soundUri);
		notificationBuilder.setAutoCancel(true);

		Notification notification = notificationBuilder.build();
		notification.flags |= Notification.FLAG_INSISTENT;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (notificationManager != null) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
				NotificationChannel notificationChannel = new NotificationChannel("alarm_channel_id", "Alarms", NotificationManager.IMPORTANCE_LOW);
				notificationManager.createNotificationChannel(notificationChannel);
			}

			notificationManager.notify(1, notification);
		}

		U.logEventAlarmFired(context);
	}
}
