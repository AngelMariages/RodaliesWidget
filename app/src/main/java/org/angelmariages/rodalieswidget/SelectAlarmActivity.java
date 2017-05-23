package org.angelmariages.rodalieswidget;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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
		setContentView(R.layout.activity_select_alarm);

		final Resources res = getResources();
		final String departureTime = getIntent().getAction();

		final TextView alarmBeforeTextView = (TextView) findViewById(R.id.timeSelectedTextView);
		final TextView trainDepartureTextView = (TextView) findViewById(R.id.trainDepartureAlarmTextView);
		final TextView alarmSetTextView = (TextView) findViewById(R.id.alarmSetTextView);

		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		Button okButton = (Button) findViewById(R.id.okButton);

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
				U.setAlarm(SelectAlarmActivity.this, departureTime, getAlarmTime(departureTime, mAlarmTime + min));
				U.log("Setting alarm");
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
