package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model;

import com.squareup.moshi.Json;

import java.util.List;

public class RnfeXMLTime {
    @Json(name = "HoraLlegada")
    private String arrivalTime;
    
    @Json(name = "Linea")
    private String line;
    
    @Json(name = "HoraSalida")
    private String departureTime;
    
    @Json(name = "Transbordo")
    private List<RnfeXMLTransfer> transfer;

    @Override
    public String toString() {
        return "RnfeXMLTime{" +
                "arrivalTime='" + arrivalTime + '\'' +
                ", line='" + line + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", transfer=" + transfer +
                '}';
    }
}
