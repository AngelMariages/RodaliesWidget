package org.angelmariages.rodalieswidget.timetables;

import org.angelmariages.rodalieswidget.utils.U;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TrainTime implements Serializable {
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
	private final int origin;
	private final int destination;
	private boolean direct_train = false;
	private boolean same_origin_train = false;

	TrainTime(String line, String departure_time, String arrival_time, String travel_time, int origin, int destination) {
		this.line = line;
		this.departure_time = formatHour(departure_time);
		this.arrival_time = formatHour(arrival_time);
		this.travel_time = formatHour(travel_time);
		this.origin = origin;
		this.destination = destination;
	}

	TrainTime(String line, String departure_time, String arrival_time, String line_transfer_one, String station_transfer_one, String departure_time_transfer_one, String arrival_time_transfer_one, String travel_time, int origin, int destination, boolean direct_train, boolean same_origin_train) {
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
	}

	TrainTime(String line, String departure_time, String arrival_time, String line_transfer_one, String station_transfer_one, String departure_time_transfer_one, String arrival_time_transfer_one, String line_transfer_two, String station_transfer_two, String departure_time_transfer_two, String arrival_time_transfer_two, int origin, int destination, boolean same_origin_train) {
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

	public String getTravel_time() {
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
}
