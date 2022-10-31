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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies;

import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesDepartures;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesSchedule;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RodaliesAPI {
    @GET("gencat_rodalies_serveis/AppJava/restServices/getHoraris?horaIni=0")
    Call<RodaliesSchedule> getSchedules(
            @Query("origen") String origin,
            @Query("desti") String destination,
            @Query("dataViatge") String travelDate
            );

    @GET("gencat_rodalies_serveis/AppJava/restServices/getDepartures")
    Call<RodaliesDepartures> getDepartures(
            @Query("numestacio") String origin
    );
}
