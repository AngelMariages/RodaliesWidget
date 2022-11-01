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

package org.angelmariages.rodalieswidget.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.frybits.harmony.Harmony;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.angelmariages.rodalieswidget.AlarmReceiver;

import java.util.Calendar;

public class AlarmUtils {
    private static void logEventAlarmSet(Context context, String departure_time, String origin, String destination) {
        Bundle bundle = null;
        if (!origin.equalsIgnoreCase("-1") && !destination.equalsIgnoreCase("-1")) {
            bundle = new Bundle();
            bundle.putString("origin_station", origin);
            bundle.putString("destination_station", destination);
            bundle.putString("departure_time", departure_time);
        }

        FirebaseAnalytics.getInstance(context).logEvent("alarm_set", bundle);
    }

    public static void logEventAlarmFired(Context context) {
        FirebaseAnalytics.getInstance(context).logEvent("alarm_fired", null);
    }

    private static int getIntentFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA | PendingIntent.FLAG_IMMUTABLE;
        }

        return PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA;
    }

    public static void setAlarm(Context context, @NonNull String departureTime, @NonNull String alarmTime, int widgetID, String origin, String destination) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        String[] hourMinutes = alarmTime.split(":");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 272829,
                new Intent(context, AlarmReceiver.class).putExtra(Constants.EXTRA_WIDGET_ID, widgetID),
                getIntentFlags());

        if (hourMinutes.length == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourMinutes[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(hourMinutes[1]));
            calendar.set(Calendar.SECOND, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            saveAlarm(context, widgetID, origin, destination, departureTime);

            U.sendNotifyUpdate(widgetID, context);

            logEventAlarmSet(context, departureTime, origin, destination);

        }
    }

    private static void saveAlarm(Context context, int widgetID, String origin, String destination, String departureTime) {
        SharedPreferences prefs = Harmony.getSharedPreferences(context, Constants.PREFERENCE_GLOBAL_KEY);
        String alarmPrefName = Constants.PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination;
        prefs.edit().putString(alarmPrefName, departureTime).apply();
    }

    public static String getAlarm(Context context, int widgetID, String origin, String destination) {
        SharedPreferences prefs = Harmony.getSharedPreferences(context, Constants.PREFERENCE_GLOBAL_KEY);

        return prefs.getString(Constants.PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination, null);
    }

    public static void removeAlarm(Context context, int widgetID, String origin, String destination) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 272829,
                new Intent(context, AlarmReceiver.class).putExtra(Constants.EXTRA_WIDGET_ID, widgetID),
                getIntentFlags());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) alarmManager.cancel(pendingIntent);

        SharedPreferences prefs = Harmony.getSharedPreferences(context, Constants.PREFERENCE_GLOBAL_KEY);
        prefs.edit().remove(Constants.PREFERENCE_STRING_ALARM_FOR_ID + widgetID + origin + destination).apply();

        U.sendNotifyUpdate(widgetID, context);
    }
}
