package org.angelmariages.rodalieswidget.timetables.rnfe.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class RnfeTransfer {
	@SerializedName("horaSalida")
	private String departureTime;

	@SerializedName("horaLlegada")
	private String arrivalTime;

	@SerializedName("cdgoEstacion")
	private String transferStationID;

	@SerializedName("cdgoTren")
	private String trenID;

	@SerializedName("linea")
	private String line;

	public RnfeTransfer() {

	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getTransferStationID() {
		return transferStationID;
	}

	public void setTransferStationID(String transferStationID) {
		this.transferStationID = transferStationID;
	}

	public String getTrenID() {
		return trenID;
	}

	public void setTrenID(String trenID) {
		this.trenID = trenID;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@NonNull
	@Override
	public String toString() {
		return "RnfeTransfer{" +
				"departureTime='" + departureTime + '\'' +
				", arrivalTime='" + arrivalTime + '\'' +
				", transferStationID='" + transferStationID + '\'' +
				", trenID='" + trenID + '\'' +
				", line='" + line + '\'' +
				'}';
	}
}
