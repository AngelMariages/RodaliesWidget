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

import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.Strategy;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesSchedule;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;

public class RodaliesStrategy implements Strategy {

    private final RodaliesAPI rodaliesAPI;

    public RodaliesStrategy() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://serveis.rodalies.gencat.cat/")
                .addConverterFactory(
                        TikXmlConverterFactory
                                .create(
                                        new TikXml.Builder()
                                                .exceptionOnUnreadXml(false)
                                                .build()
                                )
                )
                .build();

        rodaliesAPI = retrofit.create(RodaliesAPI.class);
    }

    @Override
    public List<TrainTime> getSchedule(String origin, String destination, int division, int deltaDays) throws ServiceDisruptionError {
        Calendar calendarInstance = getCalendar(deltaDays);

        Call<RodaliesSchedule> page = rodaliesAPI.getSchedules(origin, destination, formatDateToString(calendarInstance));
        try {
            RodaliesSchedule rodaliesSchedule = page.execute().body();

            return RodaliesScheduleParser.parse(rodaliesSchedule, origin, destination, calendarInstance);
        } catch (ServiceDisruptionError e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Calendar getCalendar(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, deltaDays);
        return cal;
    }

    // TODO: This is old, should be moved to a "Utils"?
    private String formatDateToString(Calendar calendarInstance) {
        return String.format(Locale.getDefault(), "%02d/%02d/%d",
                calendarInstance.get(Calendar.DAY_OF_MONTH),
                calendarInstance.get(Calendar.MONTH) + 1,
                calendarInstance.get(Calendar.YEAR)
        );
    }
}