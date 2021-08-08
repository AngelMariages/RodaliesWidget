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

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesXMLTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RodaliesScheduleParser {
    private RodaliesScheduleParser() {}

    public static List<TrainTime> parse(List<RodaliesXMLTime> schedule, String origin, String destination, Calendar calendarInstance) {
        int transfers = getTransfers(schedule);

        List<TrainTime> trainTimes = new ArrayList<>();

        for (RodaliesXMLTime rodaliesXMLTime : schedule) {
            TrainTime currentTrainTime = null;

            switch (transfers) {
                case 0: {
                    currentTrainTime = new TrainTime(
                            rodaliesXMLTime.getLine(),
                            rodaliesXMLTime.getDepartureTime(),
                            rodaliesXMLTime.getArrivalTime(),
                            origin,
                            destination,
                            calendarInstance
                    );
                }
                break;
            }


            if (currentTrainTime != null) {
                trainTimes.add(currentTrainTime);
            }
        }

        return trainTimes.size() > 0 ? trainTimes : null;
    }

    private static int getTransfers(List<RodaliesXMLTime> schedule) {
        if (schedule.size() > 0) {
            return schedule.get(0).getRodaliesXMLTimeRoute() != null ? 1 : 0;
        }

        return -1;
    }
}
