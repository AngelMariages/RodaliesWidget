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

package org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model.RnfeJSONTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model.RnfeJSONTransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RnfeScheduleParser {
    private RnfeScheduleParser() { }

    private static int getTransfers(List<RnfeJSONTime> schedule) {
        if (schedule != null && schedule.size() > 0) {
            return schedule.get(0).getTransfers().size();
        }

        return -1;
    }

    public static List<TrainTime> parse(List<RnfeJSONTime> schedule, String origin, String destination) {
        int transfers = RnfeScheduleParser.getTransfers(schedule);
        List<TrainTime> trainTimes = new ArrayList<>();

        for (RnfeJSONTime rnfeJSONTime : schedule) {
            TrainTime currentTrainTime = null;

            switch (transfers) {
                case 0: {
                    currentTrainTime = new TrainTime(
                            rnfeJSONTime.getLine(),
                            rnfeJSONTime.getDepartureTime(),
                            rnfeJSONTime.getArrivalTime(),
                            origin,
                            destination,
                            Calendar.getInstance()
                    );
                }
                break;
                case 1: {
                    RnfeJSONTransfer rnfeJSONTransfer = rnfeJSONTime.getTransfers().get(0);

                    currentTrainTime = new TrainTime(
                            rnfeJSONTime.getLine(),
                            rnfeJSONTime.getDepartureTime(),
                            rnfeJSONTransfer.getArrivalTime(), // Transfer arrival is first train arrival
                            rnfeJSONTransfer.getLine(),
                            rnfeJSONTransfer.getTransferStationID(),
                            rnfeJSONTransfer.getDepartureTime(),
                            rnfeJSONTime.getArrivalTime(), // First train arrival is transfer arrival
                            origin,
                            destination,
                            false,
                            Calendar.getInstance()
                    );
                }
                break;
                case 2: {
                    RnfeJSONTransfer rnfeJSONTransfer1 = rnfeJSONTime.getTransfers().get(0);
                    RnfeJSONTransfer rnfeJSONTransfer2 = rnfeJSONTime.getTransfers().size() > 1 ? rnfeJSONTime.getTransfers().get(1) : null;

                    currentTrainTime = new TrainTime(
                            rnfeJSONTime.getLine(),
                            rnfeJSONTime.getDepartureTime(),
                            rnfeJSONTransfer1.getArrivalTime(), // Transfer 1 arrival is first train arrival
                            rnfeJSONTransfer1.getLine(),
                            rnfeJSONTransfer1.getTransferStationID(),
                            rnfeJSONTransfer1.getDepartureTime(),
                            rnfeJSONTransfer2 != null ? rnfeJSONTransfer2.getArrivalTime() : null, // Transfer 2 arrival is Transfer 1 arrival
                            rnfeJSONTransfer2 != null ? rnfeJSONTransfer2.getLine() : null,
                            rnfeJSONTransfer2 != null ? rnfeJSONTransfer2.getTransferStationID() : null,
                            rnfeJSONTransfer2 != null ? rnfeJSONTransfer2.getDepartureTime() : null,
                            rnfeJSONTime.getArrivalTime(), // First train arrival is Transfer 2 arrival
                            origin,
                            destination,
                            Calendar.getInstance()
                    );
                }
            }


            if (currentTrainTime != null) {
                trainTimes.add(currentTrainTime);
            }
        }

        return trainTimes.size() > 0 ? trainTimes : null;
    }
}
