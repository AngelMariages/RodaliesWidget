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

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.angelmariages.rodalieswidget.utils.AlarmUtils;
import org.angelmariages.rodalieswidget.utils.Constants;
import org.angelmariages.rodalieswidget.utils.U;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SelectAlarmActivity extends AppCompatActivity {

	private final int stepSize = 5;
	private final int min = 5;
	private int mAlarmTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		final int widgetID = intent.getIntExtra(Constants.EXTRA_WIDGET_ID, -1);
		final String stations[] = U.getStations(this, widgetID);

		if(widgetID == -1 && stations.length != 2) {
			U.log("Error getting the widget ID, finishing alarm activity");
			finish();
		}

		String savedDepartureTime = AlarmUtils.getAlarm(this, widgetID, stations[0], stations[1]);
		if (savedDepartureTime != null) {
			setContentView(R.layout.activity_remove_alarm);

			setResourcesForRemovingAlarm(widgetID, stations[0], stations[1]);
		} else {
			setContentView(R.layout.activity_select_alarm);

			setResourcesForAddingAlarm(widgetID, stations[0], stations[1]);
		}
	}

	private void setResourcesForRemovingAlarm(final int widgetID, final String origin, final String destination) {
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button okButton = (Button) findViewById(R.id.okButton);

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmUtils.removeAlarm(SelectAlarmActivity.this, widgetID, origin, destination);
				Toast.makeText(SelectAlarmActivity.this, R.string.toast_alarm_removed, Toast.LENGTH_LONG).show();
				U.log("Removing alarm");
				U.sendNotifyUpdate(widgetID, SelectAlarmActivity.this);
				finish();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void setResourcesForAddingAlarm(final int widgetID, final String origin, final String destination) {
		final Resources res = getResources();
		final String departureTime = getIntent().getAction();


		final TextView alarmBeforeTextView = (TextView) findViewById(R.id.timeSelectedTextView);
		final TextView trainDepartureTextView = (TextView) findViewById(R.id.trainDepartureAlarmTextView);
		final TextView alarmSetTextView = (TextView) findViewById(R.id.alarmSetTextView);

		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final Button okButton = (Button) findViewById(R.id.okButton);

		trainDepartureTextView.setText(String.format(res.getString(R.string.alarm_set_train_departure_text), departureTime));

		alarmBeforeTextView.setText(
				String.format(res.getString(R.string.alarm_set_train_before_text), min)
		);

		alarmSetTextView.setText(
				String.format(res.getString(R.string.alarm_set_train_alarm_text), getAlarmTime(departureTime, min))
		);

		SeekBar alarmSeekBar = (SeekBar) findViewById(R.id.seekBar);
		alarmSeekBar.setMax(115);
		alarmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser) {
					mAlarmTime = (progress / stepSize) * stepSize;
					seekBar.setProgress(mAlarmTime);

					alarmBeforeTextView.setText(
							String.format(res.getString(R.string.alarm_set_train_before_text),
									mAlarmTime + min)
					);

					alarmSetTextView.setText(
							String.format(res.getString(R.string.alarm_set_train_alarm_text),
									getAlarmTime(departureTime, mAlarmTime + min))
					);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String alarmTime = getAlarmTime(departureTime, mAlarmTime + min);

				AlarmUtils.setAlarm(SelectAlarmActivity.this, departureTime, alarmTime, widgetID, origin, destination);
				Toast.makeText(SelectAlarmActivity.this, String.format(getString(R.string.alarm_set_alarm_toast), alarmTime),
						Toast.LENGTH_LONG).show();
				finish();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private String getAlarmTime(String departureTime, int alarmTime) {
		if (departureTime == null) return "00:00";
		try {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date alarmDate = new Date(format.parse(departureTime).getTime() - alarmTime * 1000 * 60);
			return format.format(alarmDate);
		} catch (ParseException e) {
			U.log("PARSE EXCEPTION: ");
			U.log(e.getMessage());
		}
		return "00:00";
	}
}
