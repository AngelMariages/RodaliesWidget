/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import androidx.core.app.NotificationCompat;

import net.grandcentrix.tray.AppPreferences;

import org.angelmariages.rodalieswidget.utils.AlarmUtils;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.U;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		int widgetID = intent.getIntExtra(Constants.EXTRA_WIDGET_ID, -1);
		if (widgetID == -1) return;

		U.log("Alarm received, removing it " + widgetID);
		String[] stations = U.getStations(context, widgetID);
		if (stations.length == 2) {
			AlarmUtils.removeAlarm(context, widgetID, stations[0], stations[1]);
		}

		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		String customUri = new AppPreferences(context).getString(Constants.PREFERENCE_STRING_ALARM_URI, null);
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

		AlarmUtils.logEventAlarmFired(context);
	}
}
