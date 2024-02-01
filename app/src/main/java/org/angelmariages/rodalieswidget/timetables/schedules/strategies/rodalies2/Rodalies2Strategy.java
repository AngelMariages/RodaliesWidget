/*
 * MIT License
 *
 * Copyright (c) 2024 Àngel Mariages
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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies2;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.Strategy;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies2.model.RodaliesJSONSchedule;
import org.angelmariages.rodalieswidget.utils.U;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class Rodalies2Strategy implements Strategy {
    private static final JsonAdapter<RodaliesJSONSchedule> MOSHI_ADAPTER = new Moshi.Builder().build().adapter(RodaliesJSONSchedule.class);

    public Rodalies2Strategy() {
    }


    @Override
    public List<TrainTime> getSchedule(String origin, String destination, int division, int deltaDays) {
        Rodalies2JSONAPI jsonapi = new Rodalies2JSONAPI(origin, destination);

        try {
            String pageFromInternet = jsonapi.getPageFromInternet(deltaDays);

            if (pageFromInternet == null) {
                U.log("Page is null!");
                return null;
            }

            U.log(pageFromInternet);

            RodaliesJSONSchedule fromMoshi = MOSHI_ADAPTER.fromJson(pageFromInternet);

            if (fromMoshi == null) {
                // TODO: Log exception
                return null;
            }

            Calendar calendarInstance = getCalendar(deltaDays);

            return RodaliesScheduleParser2Kt.parse(fromMoshi, origin, destination, calendarInstance);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Calendar getCalendar(int deltaDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, deltaDays);
        return cal;
    }

}
