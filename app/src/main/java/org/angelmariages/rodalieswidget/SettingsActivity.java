package org.angelmariages.rodalieswidget;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import net.grandcentrix.tray.AppPreferences;

import org.angelmariages.rodalieswidget.utils.U;

public class SettingsActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
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
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				notificationBuilder.setSmallIcon(R.mipmap.ic_notification_white);
			} else {
				notificationBuilder.setSmallIcon(R.mipmap.ic_notification);
			}
			notificationBuilder.setAutoCancel(true);

			Intent startFirstTimeIntent = new Intent(context, FirstTimeActivity.class);
			startFirstTimeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startFirstTimeIntent.setAction("notification");
			PendingIntent startFirstTimePI = PendingIntent.getActivity(context, 0, startFirstTimeIntent, 0);
			notificationBuilder.setContentIntent(startFirstTimePI);

			notificationBuilder.setContentTitle(context.getString(R.string.app_name));
			notificationBuilder.setContentText(context.getString(R.string.notification_content_first_time));

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			if (notificationManager != null) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
			addPreferencesFromResource(R.xml.pref_widget);

			SwitchPreference show_all_times = (SwitchPreference) findPreference("show_all_times");
			SwitchPreference scroll_to_time = (SwitchPreference) findPreference("scroll_to_time");
			//SwitchPreference download_return_schedule = (SwitchPreference) findPreference("download_return_schedule");
			SwitchPreference show_more_transfer_trains = (SwitchPreference) findPreference("show_more_transfer_trains");
			SwitchPreference group_transfer_exits = (SwitchPreference) findPreference("group_transfer_exits");
			Preference pref_donation = findPreference("pref_donation");
			Preference pref_view_tutorial = findPreference("pref_view_tutorial");
			RingtonePreference pref_set_sound = (RingtonePreference) findPreference("pref_set_sound");

			show_all_times.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					onPreferenceChangeC("show_all_times", newValue);
					return true;
				}
			});

			scroll_to_time.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					onPreferenceChangeC("scroll_to_time", newValue);
					return true;
				}
			});

	        /*download_return_schedule.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		        @Override
		        public boolean onPreferenceChange(Preference preference, Object newValue) {
			        onPreferenceChangeC("download_return_schedule", newValue);
			        return true;
		        }
	        });*/

			show_more_transfer_trains.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					onPreferenceChangeC("show_more_transfer_trains", newValue);
					return true;
				}
			});

			group_transfer_exits.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					onPreferenceChangeC("group_transfer_exits", newValue);
					return true;
				}
			});

			pref_donation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					onPreferenceChangeC("pref_donation", true);
					return false;
				}
			});

			pref_set_sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					return false;
				}
			});

			pref_set_sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					String ringtoneUri = (String) newValue;

					if (ringtoneUri == null) {
						new AppPreferences(PreferencesFragment.this.getActivity()).remove(U.PREFERENCE_STRING_ALARM_URI);
					} else if (ringtoneUri.isEmpty()) {
						new AppPreferences(PreferencesFragment.this.getActivity()).put(U.PREFERENCE_STRING_ALARM_URI, "--silent--");
					} else {
						Ringtone ringtone = RingtoneManager.getRingtone(PreferencesFragment.this.getActivity(), Uri.parse(ringtoneUri));
						Toast.makeText(PreferencesFragment.this.getActivity(), ringtone.getTitle(PreferencesFragment.this.getActivity()), Toast.LENGTH_SHORT).show();
						new AppPreferences(PreferencesFragment.this.getActivity()).put(U.PREFERENCE_STRING_ALARM_URI, ringtoneUri);
					}
					return true;
				}
			});

			pref_view_tutorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					PreferenceManager.getDefaultSharedPreferences(PreferencesFragment.this.getActivity()).edit().putBoolean("tutorial_viewed", false).apply();

					startActivity(new Intent(PreferencesFragment.this.getActivity(), FirstTimeActivity.class));

					return false;
				}
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