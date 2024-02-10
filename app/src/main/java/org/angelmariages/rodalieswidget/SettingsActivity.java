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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.frybits.harmony.Harmony;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.U;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();

        requestToPinWidget(this);
    }

    private static void requestToPinWidget(Context context) {
        requestToPinWidget(context, false);
    }

    private static boolean requestToPinWidget(Context context, boolean force) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName myProvider = new ComponentName(context, WidgetManager.class);

        Bundle bundle = new Bundle();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            bundle.putBoolean("old_android", true);
            analytics.logEvent("install_widget", bundle);
            return false;
        }

        if (!appWidgetManager.isRequestPinAppWidgetSupported()) {
            bundle.putBoolean("pin_widget_not_supported", true);
            analytics.logEvent("install_widget", bundle);
            return false;
        }

        if (!force && U.getFirstWidgetId(context) != -1) {
            bundle.putBoolean("already_has_widget", true);
            analytics.logEvent("install_widget", bundle);
            return false;
        }

        // Create the PendingIntent object only if your app needs to be notified
        // that the user allowed the widget to be pinned. Note that, if the pinning
        // operation fails, your app isn't notified. This callback receives the ID
        // of the newly-pinned widget (EXTRA_APPWIDGET_ID).
//        PendingIntent successCallback = PendingIntent.getBroadcast(
//                this,
//                0,
//                new Intent(),
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );

        boolean success = appWidgetManager.requestPinAppWidget(myProvider, null, null);

        bundle.putBoolean("request_result", success);
        analytics.logEvent("install_widget", bundle);

        return success;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sendWidgetInstallNotification(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendWidgetInstallNotification(getApplicationContext());
    }

    private void sendWidgetInstallNotification(Context context) {
        String noWidgetChannel = "no_widget_channel";
        boolean isFirstTime = U.isFirstTime(context);
        int firstWidgetId = U.getFirstWidgetId(context);
        U.log("First widget id? " + firstWidgetId);
        if (!isFirstTime && firstWidgetId == -1) {
            U.log("Doesn't have widget");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, noWidgetChannel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.mipmap.ic_notification_white);
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_notification);
            }
            notificationBuilder.setAutoCancel(true);

            Intent startFirstTimeIntent = new Intent(context, FirstTimeActivity.class);
            startFirstTimeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startFirstTimeIntent.setAction("notification");
            PendingIntent startFirstTimePI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startFirstTimePI = PendingIntent.getActivity(context, 0, startFirstTimeIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                startFirstTimePI = PendingIntent.getActivity(context, 0, startFirstTimeIntent, PendingIntent.FLAG_IMMUTABLE);
            }
            notificationBuilder.setContentIntent(startFirstTimePI);

            notificationBuilder.setContentTitle(context.getString(R.string.app_name));
            notificationBuilder.setContentText(context.getString(R.string.notification_content_first_time));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(noWidgetChannel, "Widget help", NotificationManager.IMPORTANCE_LOW);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                notificationManager.notify(2, notificationBuilder.build());
            }
        }
    }

    public static class PreferencesFragment extends PreferenceFragment {
        private FirebaseAnalytics mFirebaseAnalytics;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());
            addPreferencesFromResource(R.xml.widget_preferences);

            SwitchPreference show_all_times = (SwitchPreference) findPreference("show_all_times");
            SwitchPreference scroll_to_time = (SwitchPreference) findPreference("scroll_to_time");
            SwitchPreference show_more_transfer_trains = (SwitchPreference) findPreference("show_more_transfer_trains");
            SwitchPreference group_transfer_exits = (SwitchPreference) findPreference("group_transfer_exits");
            SwitchPreference pref_anonymous_data_collection = (SwitchPreference) findPreference("pref_anonymous_data_collection");
            Preference pref_view_tutorial = findPreference("pref_view_tutorial");
            RingtonePreference pref_set_sound = (RingtonePreference) findPreference("pref_set_sound");
            Preference pref_about = findPreference("pref_version");

            pref_about.setTitle(getString(R.string.pref_version, U.getVersionName()));

            show_all_times.setOnPreferenceChangeListener((preference, newValue) -> {
                onPreferenceChangeC("show_all_times", newValue);
                return true;
            });

            scroll_to_time.setOnPreferenceChangeListener((preference, newValue) -> {
                onPreferenceChangeC("scroll_to_time", newValue);
                return true;
            });

            show_more_transfer_trains.setOnPreferenceChangeListener((preference, newValue) -> {
                onPreferenceChangeC("show_more_transfer_trains", newValue);
                return true;
            });

            group_transfer_exits.setOnPreferenceChangeListener((preference, newValue) -> {
                onPreferenceChangeC("group_transfer_exits", newValue);
                return true;
            });

            pref_anonymous_data_collection.setOnPreferenceChangeListener((preference, newValue) -> {
                onPreferenceChangeC("pref_anonymous_data_collection", newValue);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled((Boolean) newValue);
                return true;
            });

            pref_set_sound.setOnPreferenceChangeListener((preference, newValue) -> false);

            pref_set_sound.setOnPreferenceChangeListener((preference, newValue) -> {
                String ringtoneUri = (String) newValue;
                SharedPreferences prefs = Harmony.getSharedPreferences(PreferencesFragment.this.getActivity(), Constants.PREFERENCE_GLOBAL_KEY);


                if (ringtoneUri == null) {
                    prefs.edit().remove(Constants.PREFERENCE_STRING_ALARM_URI).apply();
                } else if (ringtoneUri.isEmpty()) {
                    prefs.edit().putString(Constants.PREFERENCE_STRING_ALARM_URI, "--silent--").apply(); // TODO: constant
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(PreferencesFragment.this.getActivity(), Uri.parse(ringtoneUri));
                    Toast.makeText(PreferencesFragment.this.getActivity(), ringtone.getTitle(PreferencesFragment.this.getActivity()), Toast.LENGTH_SHORT).show();
                    prefs.edit().putString(Constants.PREFERENCE_STRING_ALARM_URI, ringtoneUri).apply();
                }
                return true;
            });

            pref_view_tutorial.setOnPreferenceClickListener(preference -> {
                PreferenceManager.getDefaultSharedPreferences(PreferencesFragment.this.getActivity()).edit().putBoolean("tutorial_viewed", false).apply();

                if (!requestToPinWidget(PreferencesFragment.this.getActivity(), true)) {
                    startActivity(new Intent(PreferencesFragment.this.getActivity(), FirstTimeActivity.class));
                }

                return false;
            });
        }

        private void onPreferenceChangeC(String key, final Object newValue) {
            if (key.equalsIgnoreCase("show_more_transfer_trains")) key = "more_transfer_trains";

            U.setUserProperty(getActivity().getApplicationContext(), key, newValue);
            Bundle bundle = new Bundle();
            bundle.putString(key, newValue.toString());
            mFirebaseAnalytics.logEvent("preference_changed", bundle);
        }
    }
}