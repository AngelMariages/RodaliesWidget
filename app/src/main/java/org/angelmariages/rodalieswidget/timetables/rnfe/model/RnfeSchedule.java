package org.angelmariages.rodalieswidget.timetables.rnfe.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class RnfeSchedule implements Comparable {
	@SerializedName("horaLlegada")
	private String arrivalTime;

	@SerializedName("lineaEstOrigen")
	private String originLine;

	@SerializedName("duracion")
	private String duracion;

	@SerializedName("cdgoTren")
	private String trenID;

	@SerializedName("accesible")
	private String accesible;

	@SerializedName("linea")
	private String line;

	@SerializedName("lineaEstDestino")
	private String destinationLine;

	@SerializedName("horaSalida")
	private String departureTime;

	@SerializedName("trans")
	private LinkedList<RnfeTransfer> transfer;

	public RnfeSchedule() {

	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getOriginLine() {
		return originLine;
	}

	public void setOriginLine(String originLine) {
		this.originLine = originLine;
	}

	public String getDuracion() {
		return duracion;
	}

	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public String getTrenID() {
		return trenID;
	}

	public void setTrenID(String trenID) {
		this.trenID = trenID;
	}

	public String getAccesible() {
		return accesible;
	}

	public void setAccesible(String accesible) {
		this.accesible = accesible;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getDestinationLine() {
		return destinationLine;
	}

	public void setDestinationLine(String destinationLine) {
		this.destinationLine = destinationLine;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public LinkedList<RnfeTransfer> getTransfer() {
		return transfer;
	}

	public void setTransfer(LinkedList<RnfeTransfer> transfer) {
		this.transfer = transfer;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof RnfeSchedule) {
			RnfeSchedule compared = (RnfeSchedule) o;
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");

			try {
				Date departureDate = new Date(format.parse(getDepartureTime()).getTime());
				Date departureDateCompared = new Date(format.parse(compared.getDepartureTime()).getTime());

				return departureDate.compareTo(departureDateCompared);
			} catch (ParseException e) {
				return 0;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return "\nRnfeSchedule{" +
				"\tarrivalTime='" + arrivalTime + '\'' +
				"\t, originLine='" + originLine + '\'' +
				"\t, duracion='" + duracion + '\'' +
				"\t, trenID='" + trenID + '\'' +
				"\t, accesible='" + accesible + '\'' +
				"\t, line='" + line + '\'' +
				"\t, destinationLine='" + destinationLine + '\'' +
				"\t, departureTime='" + departureTime + '\'' +
				"\t, transfer=" + transfer +
				'}';
	}
}
