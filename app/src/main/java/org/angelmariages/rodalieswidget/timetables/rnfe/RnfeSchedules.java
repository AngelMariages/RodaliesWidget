package org.angelmariages.rodalieswidget.timetables.rnfe;

import android.os.Build;

import com.google.gson.annotations.SerializedName;

import org.angelmariages.rodalieswidget.timetables.rnfe.model.RnfeSchedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RnfeSchedules /*implements Parcelable*/ {
	@SerializedName("actTiempoReal")
	private String realTime;

	@SerializedName("peticion")
	private HashMap<String, String> request;

	@SerializedName("horario")
	private LinkedList<RnfeSchedule> schedule;

	public RnfeSchedules() {
	}

	public String getRealTime() {
		return realTime;
	}

	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}

	public HashMap<String, String> getRequest() {
		return request;
	}

	public void setRequest(HashMap<String, String> request) {
		this.request = request;
	}

	public List<RnfeSchedule> getSchedule() {
		return schedule;
	}

	public LinkedList<RnfeSchedule> getSortedList() {
		if (schedule != null) {
			LinkedList<RnfeSchedule> sorted = (LinkedList<RnfeSchedule>) schedule.clone();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				sorted.sort(RnfeSchedule::compareTo);
			} else {
				Collections.sort(sorted, RnfeSchedule::compareTo);
			}

			return sorted;
		}

		return null;
	}

	public void setSchedule(LinkedList<RnfeSchedule> schedule) {
		this.schedule = schedule;
	}
}

