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

package org.angelmariages.rodalieswidget.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.widget.Toast;

import com.ddmeng.preferencesprovider.provider.PreferencesStorageModule;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kizitonwose.colorpreferencecompat.ColorPreferenceCompat;
import com.takisoft.preferencex.PreferenceFragmentCompat;
import com.takisoft.preferencex.RingtonePreference;

import org.angelmariages.rodalieswidget.R;
import org.angelmariages.rodalieswidget.TutorialActivity;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.U;

import static androidx.preference.Preference.*;


public class PreferencesFragment extends PreferenceFragmentCompat {
	private FirebaseAnalytics mFirebaseAnalytics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context mContext = this.getContext();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getActivity());

		SwitchPreferenceCompat show_all_times = (SwitchPreferenceCompat) findPreference("show_all_times");
		SwitchPreferenceCompat scroll_to_time = (SwitchPreferenceCompat) findPreference("scroll_to_time");
		SwitchPreferenceCompat show_more_transfer_trains = (SwitchPreferenceCompat) findPreference("show_more_transfer_trains");
		SwitchPreferenceCompat group_transfer_exits = (SwitchPreferenceCompat) findPreference("group_transfer_exits");
		Preference pref_donation = findPreference("pref_donation");
		Preference pref_view_tutorial = findPreference("pref_view_tutorial");
		RingtonePreference pref_set_sound = (RingtonePreference) findPreference("pref_set_sound");

		Preference pref_color_widget_background = findPreference("pref_color_widget_background");
		Preference pref_color_title_background = findPreference("pref_color_title_background");
		Preference pref_color_data_background = findPreference("pref_color_data_background");
		Preference pref_color_control_buttons = findPreference("pref_color_control_buttons");
		Preference pref_color_active_text = findPreference("pref_color_active_text");
		Preference pref_color_disabled_text = findPreference("pref_color_disabled_text");
		Preference pref_color_contrast_text = findPreference("pref_color_contrast_text");

		/*pref_color_widget_background.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				onPreferenceChangeC("pref_color_widget_background", newValue);
				PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
				preferences.put("pref_color_widget_background", (int) newValue);

				return true;
			}
		});*/
		pref_color_widget_background.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_widget_background", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_widget_background", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});

		pref_color_title_background.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_title_background", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_title_background", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});
		pref_color_data_background.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_data_background", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_data_background", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});
		pref_color_control_buttons.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_control_buttons", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_control_buttons", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});
		pref_color_active_text.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_active_text", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_active_text", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});
		pref_color_disabled_text.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_disabled_text", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_disabled_text", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});
		pref_color_contrast_text.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				ColorPickerDialogBuilder
						.with(mContext)
						.setTitle("Choose color")
						.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
						.density(12)
						.setPositiveButton("Seleccionar", new ColorPickerClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
								onPreferenceChangeC("pref_color_contrast_text", selectedColor);
								PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "colors");
								preferences.put("pref_color_contrast_text", selectedColor);
								((ColorPreferenceCompat) preference).setValue(selectedColor);
							}
						})
						.setNegativeButton("Cancel·lar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.build()
						.show();
				return true;
			}
		});

		show_all_times.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				onPreferenceChangeC("show_all_times", newValue);
				return true;
			}
		});

		scroll_to_time.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				onPreferenceChangeC("scroll_to_time", newValue);
				return true;
			}
		});

		show_more_transfer_trains.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				onPreferenceChangeC("show_more_transfer_trains", newValue);
				return true;
			}
		});

		group_transfer_exits.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				onPreferenceChangeC("group_transfer_exits", newValue);
				return true;
			}
		});

		pref_donation.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				onPreferenceChangeC("pref_donation", true);
				return false;
			}
		});

		pref_set_sound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				return false;
			}
		});

		pref_set_sound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String ringtoneUri = (String) newValue;
				PreferencesStorageModule preferences = new PreferencesStorageModule(PreferencesFragment.this.getActivity(), "alarms");

				if (ringtoneUri == null) {
					preferences.remove(Constants.PREFERENCE_STRING_ALARM_URI);
				} else if (ringtoneUri.isEmpty()) {
					preferences.put(Constants.PREFERENCE_STRING_ALARM_URI, "--silent--");
				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(PreferencesFragment.this.getActivity(), Uri.parse(ringtoneUri));
					Toast.makeText(PreferencesFragment.this.getActivity(), ringtone.getTitle(PreferencesFragment.this.getActivity()), Toast.LENGTH_SHORT).show();
					preferences.put(Constants.PREFERENCE_STRING_ALARM_URI, ringtoneUri);
				}
				return true;
			}
		});

		pref_view_tutorial.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				PreferenceManager.getDefaultSharedPreferences(PreferencesFragment.this.getActivity()).edit().putBoolean("tutorial_viewed", false).apply();

				startActivity(new Intent(PreferencesFragment.this.getActivity(), TutorialActivity.class));

				return true;
			}
		});
	}

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