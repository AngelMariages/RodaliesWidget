package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;

public class RnfeXMLSchedule {
    @Json(name = "PeticionHorarios")
    private Map<String, Object> request;

    @Json(name = "Horarios")
    private Map<String, Object> requestSchedules;

    @Json(name = "Horario")
    private List<RnfeXMLTime> schedule;

    @Override
    public String toString() {
        return "RnfeXMLSchedule{" +
                "request=" + request +
                ", requestSchedules=" + requestSchedules +
                ", schedule=" + schedule +
                '}';
    }
}
