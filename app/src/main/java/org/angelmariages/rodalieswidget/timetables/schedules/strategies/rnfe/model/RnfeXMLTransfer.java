package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model;

import com.squareup.moshi.Json;

public class RnfeXMLTransfer {
    @Json(name = "HoraLlegada")
    private String arrivalTime;
    
    @Json(name = "Linea")
    private String line;
    
    @Json(name = "HoraSalida")
    private String departureTime;
    
    @Json(name = "Enlace")
    private String transferStationID;

    @Override
    public String toString() {
        return "RnfeXMLTransfer{" +
                "arrivalTime='" + arrivalTime + '\'' +
                ", line='" + line + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", transferStationID='" + transferStationID + '\'' +
                '}';
    }
}
