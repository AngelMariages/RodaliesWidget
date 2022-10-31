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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RnfeJSONSchedule {
    @Json(name = "actTiempoReal")
    private Boolean realTime;
    
    @Json(name = "peticion")
    private Map<String, String> request;
    
    @Json(name = "horario")
    private List<RnfeJSONTime> schedule;

    public RnfeJSONSchedule() {
    }

    public Boolean getRealTime() {
        return realTime;
    }

    public void setRealTime(Boolean realTime) {
        this.realTime = realTime;
    }

    public Map<String, String> getRequest() {
        return request;
    }

    public void setRequest(HashMap<String, String> request) {
        this.request = request;
    }

    public List<RnfeJSONTime> getSchedule() {
        return schedule;
    }
    
    public LinkedList<RnfeJSONTime> getSortedList() {
        if (schedule != null) {
            LinkedList<RnfeJSONTime> sorted = new LinkedList<>(schedule);

            sorted.sort(RnfeJSONTime::compareTo);

            return sorted;
        }
        
        return null;
    }

    public void setSchedule(LinkedList<RnfeJSONTime> schedule) {
        this.schedule = schedule;
    }
}
