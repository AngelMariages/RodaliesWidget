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

package org.angelmariages.rodalieswidget.timetables;

import org.angelmariages.rodalieswidget.utils.U;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TrainTime implements Serializable {
	private static final long serialVersionUID = 12345L;

	private final long date;
	private int transfer = 0;
	private final String line;
	private final String departure_time;
	private final String arrival_time;
	private final String travel_time;
	private String line_transfer_one = null;
	private String station_transfer_one = null;
	private String departure_time_transfer_one = null;
	private String arrival_time_transfer_one = null;
	private String line_transfer_two = null;
	private String station_transfer_two = null;
	private String departure_time_transfer_two = null;
	private String arrival_time_transfer_two = null;
	private final String origin;
	private final String destination;
	private boolean direct_train = false;
	private boolean same_origin_train = false;

	TrainTime(String line, String departure_time, String arrival_time, String travel_time, String origin, String destination, Calendar from_date) {
		this.line = line;
		this.departure_time = formatHour(departure_time);
		this.arrival_time = formatHour(arrival_time);
		this.travel_time = formatHour(travel_time);
		this.origin = origin;
		this.destination = destination;
		this.date = from_date.getTimeInMillis();
	}

	TrainTime(String line, String departure_time, String arrival_time, String line_transfer_one, String station_transfer_one, String departure_time_transfer_one, String arrival_time_transfer_one, String travel_time, String origin, String destination, boolean direct_train, boolean same_origin_train, Calendar from_date) {
		this.transfer = 1;
		this.line = line;
		this.departure_time = formatHour(departure_time);
		this.arrival_time = formatHour(arrival_time);
		this.travel_time = formatHour(travel_time);
		this.line_transfer_one = line_transfer_one;
		this.station_transfer_one = station_transfer_one;
		this.departure_time_transfer_one = formatHour(departure_time_transfer_one);
		this.arrival_time_transfer_one = formatHour(arrival_time_transfer_one);
		this.origin = origin;
		this.destination = destination;
		this.direct_train = direct_train;
		this.same_origin_train = same_origin_train;
		this.date = from_date.getTimeInMillis();
	}

	TrainTime(String line, String departure_time, String arrival_time, String line_transfer_one, String station_transfer_one, String departure_time_transfer_one, String arrival_time_transfer_one, String line_transfer_two, String station_transfer_two, String departure_time_transfer_two, String arrival_time_transfer_two, String origin, String destination, boolean same_origin_train, Calendar from_date) {
		this.transfer = 2;
		this.line = line;
		this.departure_time = formatHour(departure_time);
		this.arrival_time = formatHour(arrival_time);
		// TODO: 29/01/17 Travel time might be incorrect when there's no departure_time
		this.travel_time = formatHour(getTravelTime(departure_time, arrival_time_transfer_two));
		this.line_transfer_one = line_transfer_one;
		this.station_transfer_one = station_transfer_one;
		this.departure_time_transfer_one = formatHour(departure_time_transfer_one);
		this.arrival_time_transfer_one = formatHour(arrival_time_transfer_one);
		this.line_transfer_two = line_transfer_two;
		this.station_transfer_two = station_transfer_two;
		this.departure_time_transfer_two = formatHour(departure_time_transfer_two);
		this.arrival_time_transfer_two = formatHour(arrival_time_transfer_two);
		this.origin = origin;
		this.destination = destination;
		this.same_origin_train = same_origin_train;
		this.date = from_date.getTimeInMillis();
	}

	@Override
	public String toString() {
		return "TrainTime{" +
				"transfer=" + transfer +
				", line='" + line + '\'' +
				", departure_time='" + departure_time + '\'' +
				", arrival_time='" + arrival_time + '\'' +
				", travel_time='" + travel_time + '\'' +
				", line_transfer_one='" + line_transfer_one + '\'' +
				", station_transfer_one='" + station_transfer_one + '\'' +
				", departure_time_transfer_one='" + departure_time_transfer_one + '\'' +
				", arrival_time_transfer_one='" + arrival_time_transfer_one + '\'' +
				", line_transfer_two='" + line_transfer_two + '\'' +
				", station_transfer_two='" + station_transfer_two + '\'' +
				", departure_time_transfer_two='" + departure_time_transfer_two + '\'' +
				", arrival_time_transfer_two='" + arrival_time_transfer_two + '\'' +
				", origin=" + origin +
				", destination=" + destination +
				", direct_train=" + direct_train +
				", same_origin_train=" + same_origin_train +
				'}';
	}

	private String formatHour(String hour) {
		if (hour == null) return null;
		String[] splitDot = hour.split("\\.");
		String[] splitTwoDots = hour.split(":");
		if (splitDot.length == 2)
			return String.format("%02d:%02d", Integer.parseInt(splitDot[0]), Integer.parseInt(splitDot[1]));
		else if (splitTwoDots.length == 2)
			return String.format("%02d:%02d", Integer.parseInt(splitTwoDots[0]), Integer.parseInt(splitTwoDots[1]));
		else return null;
	}

	private String getTravelTime(String departure_time, String arrival_time) {
		if (departure_time == null || arrival_time == null) return "00:00";
		try {
			SimpleDateFormat format;
			if (departure_time.contains(":")) format = new SimpleDateFormat("HH:mm");
			else format = new SimpleDateFormat("HH.mm");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date departureDate = new Date(format.parse(departure_time).getTime());
			Date arrivalTime = new Date(format.parse(arrival_time).getTime());
			return format.format(new Date((arrivalTime.getTime() - departureDate.getTime())));
		} catch (ParseException e) {
			U.log("PARSE EXCEPTION: ");
			U.log(e.getMessage());
		}
		return "00:00";
	}

	public int getTransfer() {
		return transfer;
	}

	public String getLine() {
		return line;
	}

	public String getDeparture_time() {
		return departure_time;
	}

	public String getArrival_time() {
		return arrival_time;
	}

	String getTravel_time() {
		return travel_time;
	}

	public String getLine_transfer_one() {
		return line_transfer_one;
	}

	public String getStation_transfer_one() {
		return station_transfer_one;
	}

	public String getDeparture_time_transfer_one() {
		return departure_time_transfer_one;
	}

	public String getArrival_time_transfer_one() {
		return arrival_time_transfer_one;
	}

	public String getLine_transfer_two() {
		return line_transfer_two;
	}

	public String getStation_transfer_two() {
		return station_transfer_two;
	}

	public String getDeparture_time_transfer_two() {
		return departure_time_transfer_two;
	}

	public String getArrival_time_transfer_two() {
		return arrival_time_transfer_two;
	}

	public boolean isDirect_train() {
		return direct_train;
	}

	public boolean isSame_origin_train() {
		return same_origin_train;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public Calendar getDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		return cal;
	}

	public Calendar getDateWithTime() {
		String time = null;
		if(departure_time != null) {
			time = departure_time;
		} else if(departure_time_transfer_one != null) {
			time = departure_time_transfer_one;
		} else if(departure_time_transfer_two != null) {
			time = departure_time_transfer_two;
		}
		if (time != null) {
			String[] split = time.split(":");
			if (split.length == 2) {
				Calendar cal = (Calendar) getDate().clone();
				int hour = Integer.parseInt(split[0]);

				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, Integer.parseInt(split[1]));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				if(hour == 0) {
					cal.add(Calendar.DAY_OF_YEAR, 1);
				}
				return cal;
			}
		}
		return null;
	}
}
