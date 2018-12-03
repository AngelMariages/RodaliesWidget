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

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.takisoft.preferencex.PreferenceFragmentCompat;

import org.angelmariages.rodalieswidget.utils.U;


public class PreferencesFragment extends PreferenceFragmentCompat {
	private FirebaseAnalytics mFirebaseAnalytics;

	//@Override
	//public void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		/*mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());
		addPreferencesFromResource(R.xml.widget_preferences);

		SwitchPreference show_all_times = (SwitchPreference) findPreference("show_all_times");
		SwitchPreference scroll_to_time = (SwitchPreference) findPreference("scroll_to_time");
		SwitchPreference show_more_transfer_trains = (SwitchPreference) findPreference("show_more_transfer_trains");
		SwitchPreference group_transfer_exits = (SwitchPreference) findPreference("group_transfer_exits");
		Preference pref_donation = findPreference("pref_donation");
		Preference pref_view_tutorial = findPreference("pref_view_tutorial");
		//RingtonePreference pref_set_sound = (RingtonePreference) findPreference("pref_set_sound");

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
		});*/

		/*pref_set_sound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String ringtoneUri = (String) newValue;

				if (ringtoneUri == null) {
					new AppPreferences(SettingsActivity.PreferencesFragment.this.getActivity()).remove(Constants.PREFERENCE_STRING_ALARM_URI);
				} else if (ringtoneUri.isEmpty()) {
					new AppPreferences(SettingsActivity.PreferencesFragment.this.getActivity()).put(Constants.PREFERENCE_STRING_ALARM_URI, "--silent--");
				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(SettingsActivity.PreferencesFragment.this.getActivity(), Uri.parse(ringtoneUri));
					Toast.makeText(SettingsActivity.PreferencesFragment.this.getActivity(), ringtone.getTitle(SettingsActivity.PreferencesFragment.this.getActivity()), Toast.LENGTH_SHORT).show();
					new AppPreferences(SettingsActivity.PreferencesFragment.this.getActivity()).put(Constants.PREFERENCE_STRING_ALARM_URI, ringtoneUri);
				}
				return true;
			}
		});

		pref_view_tutorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				PreferenceManager.getDefaultSharedPreferences(SettingsActivity.PreferencesFragment.this.getActivity()).edit().putBoolean("tutorial_viewed", false).apply();

				startActivity(new Intent(SettingsActivity.PreferencesFragment.this.getActivity(), TutorialActivity.class));

				return false;
			}
		});*/
	//}

	@Override
	public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.widget_preferences, rootKey);
	}

	private void onPreferenceChangeC(String key, final Object newValue) {
		if (key.equalsIgnoreCase("show_more_transfer_trains")) key = "more_transfer_trains";

		U.setUserProperty(getActivity().getApplicationContext(), key, newValue);
		Bundle bundle = new Bundle();
		bundle.putString(key, newValue.toString());
		mFirebaseAnalytics.logEvent("preference_changed", bundle);
	}
}