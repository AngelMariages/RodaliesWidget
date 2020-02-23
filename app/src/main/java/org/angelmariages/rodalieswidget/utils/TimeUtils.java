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

import android.text.format.DateUtils;

import org.angelmariages.rodalieswidget.timetables.TrainTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {
	public static int getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.HOUR_OF_DAY)));
	}

	public static int getCurrentMinute() {
		Calendar cal = Calendar.getInstance();
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.MINUTE)));
	}

	public static String getTodayDateWithoutPath() {
		return getTodayDateWithoutPath(0);
	}

	public static String getTodayDateWithoutPath(int deltaDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, deltaDays);
		return String.format(Locale.getDefault(), "%02d%02d%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}

	//TODO: Could do better with LocalDate
	public static boolean isFuture(String dateWithoutPath) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		try {
			Calendar date = Calendar.getInstance();
			date.setTime(format.parse(dateWithoutPath));
			return DateUtils.isToday(date.getTime().getTime()) || date.after(Calendar.getInstance());
		} catch (ParseException e) {
			U.log("Can't parse Date for isFuture " + dateWithoutPath);
			return false;
		}
	}

	public static boolean isYesterday(String dateWithoutPath) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		try {
			Calendar date = Calendar.getInstance();
			date.setTime(format.parse(dateWithoutPath));

			Calendar yesterday = Calendar.getInstance();
			yesterday.add(Calendar.DAY_OF_YEAR, -1);

			return date.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) && date.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR);
		} catch (ParseException e) {
			U.log("Can't parse Date for isFuture " + dateWithoutPath);
			return false;
		}
	}

	public static Calendar getCalendarForDelta(int deltaDays) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, deltaDays);
		return cal;
	}

	private static Calendar getCurrentCalendarNow() {
		return Calendar.getInstance();
	}

	public static boolean isScheduleExpired(ArrayList<TrainTime> schedule) {
		if (schedule != null && schedule.size() > 0) {
			TrainTime trainTime = schedule.get(schedule.size() - 1);
			return trainTime.getDate() != null && !isScheduledTrain(trainTime);
		}
		return true;
	}

	public static long getCurrentDateAsTimestamp() {
		Calendar cal = getCalendarForDelta(0);
		return cal.getTimeInMillis();
	}

	public static boolean isScheduledTrain(TrainTime trainTime) {
		Calendar cal = getCurrentCalendarNow();
		return trainTime.getDate() != null && cal.before(trainTime.getDateWithTime());
	}
}
