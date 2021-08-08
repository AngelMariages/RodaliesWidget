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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model;

import androidx.annotation.NonNull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class RodaliesXMLTimeRoute {
    @Element(name = "linia", required = false)
    private String line;

    @Element(name = "hora_sortida")
    private String departureTime;

    @Element(name = "hora_arribada")
    private String arrivalTime;

    @Element(name = "duracio_trajecte")
    private String travelTime;

    @Element(name = "item")
    private RodaliesXMLTimeRouteItem rodaliesXMLTimeRouteItem;

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

    public RodaliesXMLTimeRouteItem getRodaliesXMLTimeRouteItem() {
        return rodaliesXMLTimeRouteItem;
    }

    @NonNull
    @Override
    public String toString() {
        return "RodaliesXMLTimeRoute{" +
                "line='" + line + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", travelTime='" + travelTime + '\'' +
                ", rodaliesXMLTimeRouteItem=" + rodaliesXMLTimeRouteItem +
                '}';
    }
}
