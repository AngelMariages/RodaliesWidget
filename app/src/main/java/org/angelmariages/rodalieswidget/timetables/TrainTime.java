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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TrainTime implements Serializable {
	private static final long serialVersionUID = 12345L;

	private long date;
	private int transfer = 0;
	private String line;
	private String departureTime;
	private String arrivalTime;
	private String travelTime;
	private String lineTransferOne = null;
	private String stationTransferOne = null;
	private String departureTimeTransferOne = null;
	private String arrivalTimeTransferOne = null;
	private String lineTransferTwo = null;
	private String stationTransferTwo = null;
	private String departureTimeTransferTwo = null;
	private String arrivalTimeTransferTwo = null;
	private String origin;
	private String destination;
	private boolean isDirectTrain = false;
	private boolean isSameOriginTrain = false;

	public TrainTime() {}

	public TrainTime(String line, String departureTime, String arrivalTime, String origin, String destination, Calendar from_date) {
		this.line = line;
		this.departureTime = formatHour(departureTime);
		this.arrivalTime = formatHour(arrivalTime);
		this.origin = origin;
		this.destination = destination;

		this.date = from_date.getTimeInMillis();

		this.travelTime = calcTravelTime();
	}

	public TrainTime(String line, String departureTime, String arrivalTime, String lineTransferOne, String stationTransferOne, String departureTimeTransferOne, String arrivalTimeTransferOne, String origin, String destination, boolean isDirectTrain, Calendar from_date) {
		this.transfer = 1;
		this.line = line;
		this.departureTime = formatHour(departureTime);
		this.arrivalTime = formatHour(arrivalTime);
		this.lineTransferOne = lineTransferOne;
		this.stationTransferOne = stationTransferOne;
		this.departureTimeTransferOne = formatHour(departureTimeTransferOne);
		this.arrivalTimeTransferOne = formatHour(arrivalTimeTransferOne);
		this.origin = origin;
		this.destination = destination;
		this.isDirectTrain = isDirectTrain;

		this.isSameOriginTrain = isSameOriginTrain(line, departureTime, arrivalTime);
		this.date = from_date.getTimeInMillis();

		this.travelTime = calcTravelTime();
	}

	public TrainTime(String line, String departureTime, String arrivalTime, String lineTransferOne, String stationTransferOne, String departureTimeTransferOne, String arrivalTimeTransferOne, String lineTransferTwo, String stationTransferTwo, String departureTimeTransferTwo, String arrivalTimeTransferTwo, String origin, String destination, Calendar from_date) {
		this.transfer = 2;
		this.line = line;
		this.departureTime = formatHour(departureTime);
		this.arrivalTime = formatHour(arrivalTime);
		this.lineTransferOne = lineTransferOne;
		this.stationTransferOne = stationTransferOne;
		this.departureTimeTransferOne = formatHour(departureTimeTransferOne);
		this.arrivalTimeTransferOne = formatHour(arrivalTimeTransferOne);
		this.lineTransferTwo = lineTransferTwo;
		this.stationTransferTwo = stationTransferTwo;
		this.departureTimeTransferTwo = formatHour(departureTimeTransferTwo);
		this.arrivalTimeTransferTwo = formatHour(arrivalTimeTransferTwo);
		this.origin = origin;
		this.destination = destination;

		this.isSameOriginTrain = isSameOriginTrain(line, departureTime, arrivalTime);
		this.date = from_date.getTimeInMillis();

		this.travelTime = calcTravelTime();
	}

	private boolean isSameOriginTrain(String line, String departure_time, String arrival_time) {
		return (line == null || line.isEmpty()) ||(departure_time == null || departure_time.isEmpty()) || (arrival_time == null || arrival_time.isEmpty());
	}

	private String calcTravelTime() {
		String firstDepartureTime = departureTime;
		if (firstDepartureTime == null) {
			if (departureTimeTransferOne != null) {
				firstDepartureTime = departureTimeTransferOne;
			} else if (departureTimeTransferTwo != null) {
				firstDepartureTime = departureTimeTransferTwo;
			}
		}

		String lastArrivalTime = arrivalTimeTransferTwo;
		if (lastArrivalTime == null) {
			if (arrivalTimeTransferOne != null) {
				lastArrivalTime = arrivalTimeTransferOne;
			} else if (arrivalTime != null) {
				lastArrivalTime = arrivalTime;
			}
		}

		if (firstDepartureTime != null && lastArrivalTime != null) {
			try {
				SimpleDateFormat format;
				if (firstDepartureTime.contains(":")) format = new SimpleDateFormat("HH:mm");
				else format = new SimpleDateFormat("HH.mm");

				format.setTimeZone(TimeZone.getTimeZone("UTC"));

				Date departureDate = new Date(format.parse(firstDepartureTime).getTime());
				Date arrivalTime = new Date(format.parse(lastArrivalTime).getTime());

				return format.format(new Date((arrivalTime.getTime() - departureDate.getTime())));
			} catch (ParseException e) {
				System.out.println("PARSE EXCEPTION: " + e.getMessage());
				//U.log("PARSE EXCEPTION: ");
				//U.log(e.getMessage());
			}
		}

		return "00:00";
	}

	@Override
	public String toString() {
		return "TrainTime {" +
				"\n\ttransfer=" + transfer +
				"\n\tline='" + line + '\'' +
				"\n\tdepartureTime='" + departureTime + '\'' +
				"\n\tarrivalTime='" + arrivalTime + '\'' +
				"\n\ttravelTime='" + travelTime + '\'' +
				"\n\tlineTransferOne='" + lineTransferOne + '\'' +
				"\n\tstationTransferOne='" + stationTransferOne + '\'' +
				"\n\tdepartureTimeTransferOne='" + departureTimeTransferOne + '\'' +
				"\n\tarrivalTimeTransferOne='" + arrivalTimeTransferOne + '\'' +
				"\n\tlineTransferTwo='" + lineTransferTwo + '\'' +
				"\n\tstationTransferTwo='" + stationTransferTwo + '\'' +
				"\n\tdepartureTimeTransferTwo='" + departureTimeTransferTwo + '\'' +
				"\n\tarrivalTimeTransferTwo='" + arrivalTimeTransferTwo + '\'' +
				"\n\torigin=" + origin +
				"\n\tdestination=" + destination +
				"\n\tisDirectTrain=" + isDirectTrain +
				"\n\tisSameOriginTrain=" + isSameOriginTrain +
				"\n}";
	}

	private String formatHour(String hour) {
		if (hour == null) return null;
		String[] splitDot = hour.split("\\.");
		String[] splitTwoDots = hour.split(":");
		if (splitDot.length == 2)
			return String.format("%02d:%02d", Integer.parseInt(splitDot[0]), Integer.parseInt(splitDot[1]));
		else if (splitTwoDots.length == 2)
			return String.format("%02d:%02d", Integer.parseInt(splitTwoDots[0]), Integer.parseInt(splitTwoDots[1]));
		else if (splitTwoDots.length == 3)
			return String.format("%02d:%02d", Integer.parseInt(splitTwoDots[0]), Integer.parseInt(splitTwoDots[1]));
		else return null;
	}

	public int getTransfer() {
		return transfer;
	}

	public String getLine() {
		return line;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public String getTravelTime() {
		return travelTime;
	}

	public String getLineTransferOne() {
		return lineTransferOne;
	}

	public String getStationTransferOne() {
		return stationTransferOne;
	}

	public String getDepartureTimeTransferOne() {
		return departureTimeTransferOne;
	}

	public String getArrivalTimeTransferOne() {
		return arrivalTimeTransferOne;
	}

	public String getLineTransferTwo() {
		return lineTransferTwo;
	}

	public String getStationTransferTwo() {
		return stationTransferTwo;
	}

	public String getDepartureTimeTransferTwo() {
		return departureTimeTransferTwo;
	}

	public String getArrivalTimeTransferTwo() {
		return arrivalTimeTransferTwo;
	}

	public boolean isDirectTrain() {
		return isDirectTrain;
	}

	public boolean isSameOriginTrain() {
		return isSameOriginTrain;
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
		if(departureTime != null) {
			time = departureTime;
		} else if(departureTimeTransferOne != null) {
			time = departureTimeTransferOne;
		} else if(departureTimeTransferTwo != null) {
			time = departureTimeTransferTwo;
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