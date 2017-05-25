package org.angelmariages.rodalieswidget;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
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

	public static class PreferencesFragment extends PreferenceFragment {
	    private FirebaseAnalytics mFirebaseAnalytics;

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(requestCode == U.RINGTONE_SELECT_REQUEST_CODE && resultCode == RESULT_OK) {
				Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				Ringtone ringtone = RingtoneManager.getRingtone(this.getActivity(), ringtoneUri);
				Toast.makeText(this.getActivity(), "Ringtone selected: " + ringtone.getTitle(this.getActivity()), Toast.LENGTH_SHORT).show();
				new AppPreferences(this.getActivity()).put(U.PREFERENCE_STRING_ALARM_URI, ringtoneUri.toString());
			} else super.onActivityResult(requestCode, resultCode, data);
		}

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
	        Preference pref_set_sound = findPreference("pref_set_sound");

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

		    pref_set_sound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			    @Override
			    public boolean onPreferenceClick(Preference preference) {
				    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for alarm:");
				    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

				    String ringtoneSaved = new AppPreferences(PreferencesFragment.this.getActivity()).getString(U.PREFERENCE_STRING_ALARM_URI, null);
				    if(ringtoneSaved != null) intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtoneSaved));

				    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
				    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
				    startActivityForResult(intent, U.RINGTONE_SELECT_REQUEST_CODE);

				    return false;
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
	        if(key.equalsIgnoreCase("show_more_transfer_trains")) key = "more_transfer_trains";
	        mFirebaseAnalytics.setUserProperty(key, newValue.toString());
			Bundle bundle = new Bundle();
			bundle.putString(key, newValue.toString());
			mFirebaseAnalytics.logEvent("preference_changed", bundle);
        }
    }
}