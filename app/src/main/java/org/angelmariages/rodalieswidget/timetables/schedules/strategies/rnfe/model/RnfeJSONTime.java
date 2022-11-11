/*
 * MIT License
 *
 * Copyright (c) 2021 Àngel Mariages
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
 *
 */

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model;

import com.squareup.moshi.Json;

import org.angelmariages.rodalieswidget.utils.U;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RnfeJSONTime implements Comparable<RnfeJSONTime> {
    @Json(name = "horaLlegada")
    private String arrivalTime;
    
    @Json(name = "lineaEstOrigen")
    private String originLine;
    
    @Json(name = "duracion")
    private String duracion;
    
    @Json(name = "cdgoTren")
    private String trenID;
    
    @Json(name = "accesible")
    private Boolean accesible;
    
    @Json(name = "linea")
    private String line;
    
    @Json(name = "lineaEstDestino")
    private String destinationLine;
    
    @Json(name = "horaSalida")
    private String departureTime;
    
    @Json(name = "trans")
    private List<RnfeJSONTransfer> transfer = new ArrayList<>();
    
    public RnfeJSONTime() {
        
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

    public Boolean getAccesible() {
        return accesible;
    }

    public void setAccesible(Boolean accesible) {
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
    public List<RnfeJSONTransfer> getTransfers() {
        return transfer;
    }

    public void setTransfer(List<RnfeJSONTransfer> transfer) {
        this.transfer = transfer;
    }

    @Override
    public int compareTo(RnfeJSONTime comparedTo) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            Date departureDate = new Date(format.parse(getDepartureTime()).getTime());
            Date departureDateCompared = new Date(format.parse(comparedTo.getDepartureTime()).getTime());

            return departureDate.compareTo(departureDateCompared);
        } catch (ParseException e) {
            U.log("Can't compare: " + e.getMessage());
            U.logException(e);
            return 0;
        }
    }

    @Override
    public String toString() {
        return "RnfeTime{" +
                "arrivalTime='" + arrivalTime + '\'' +
                ", originLine='" + originLine + '\'' +
                ", duracion='" + duracion + '\'' +
                ", trenID='" + trenID + '\'' +
                ", accesible='" + accesible + '\'' +
                ", line='" + line + '\'' +
                ", destinationLine='" + destinationLine + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", transfer=" + transfer +
                '}';
    }
}
