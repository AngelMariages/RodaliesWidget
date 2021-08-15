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
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesSchedule;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesXMLTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesXMLTimeRoute;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.RodaliesXMLTimeRouteItem;
import org.angelmariages.rodalieswidget.utils.U;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RodaliesScheduleParser {
    private RodaliesScheduleParser() {
    }

    public static List<TrainTime> parse(RodaliesSchedule schedule, String origin, String destination, Calendar calendarInstance) {
        int transfers = getTransfers(schedule);

        List<TrainTime> trainTimes = new ArrayList<>();

        U.log(schedule.toString());

        for (RodaliesXMLTime rodaliesXMLTime : schedule.getSchedule()) {
            switch (transfers) {
                case 0: {
                    trainTimes.add(
                            new TrainTime(
                                    rodaliesXMLTime.getLine(),
                                    rodaliesXMLTime.getDepartureTime(),
                                    rodaliesXMLTime.getArrivalTime(),
                                    origin,
                                    destination,
                                    calendarInstance
                            )
                    );
                }
                case 1: {
                    int numRoutes = getNumRoutes(rodaliesXMLTime);

                    if (numRoutes > 0) {
                        RodaliesXMLTimeRoute firstRoute = rodaliesXMLTime.getRodaliesXMLTimeRoutes().get(0);
                        RodaliesXMLTimeRouteItem firstRouteItem = firstRoute.getRodaliesXMLTimeRoutesItems().get(0);

                        /*
                            Train 1:
                                Line: rodaliesXMLTime
                                Departure: rodaliesXMLTime
                                Arrival: firstRouteItem
                            Train 2:
                                Line: firstRouteItem
                                Code: firstRouteItem
                                Departure: firstRouteItem
                                Arrival: firstRoute
                        */
                        trainTimes.add(
                                new TrainTime(
                                        rodaliesXMLTime.getLine(),
                                        rodaliesXMLTime.getDepartureTime(),
                                        firstRouteItem.getArrivalTime(),
                                        firstRouteItem.getLine(),
                                        firstRouteItem.getStationCode(),
                                        firstRouteItem.getDepartureTime(),
                                        firstRoute.getArrivalTime(),
                                        origin,
                                        destination,
                                        false,
                                        calendarInstance
                                )
                        );

                        // There's a second train from origin to firstTransfer at another hour
                        /*
                            Train 1:
                                Line: rodaliesXMLTime
                                Departure: rodaliesXMLTime
                                Arrival: secondRouteItem
                            Train 2:
                                Line: secondRouteItem
                                Code: secondRouteItem
                                Departure: secondRouteItem
                                Arrival: secondRoute
                        */
                        if (numRoutes > 1) {
                            for (int i = 1; i < numRoutes; i++) {
                                RodaliesXMLTimeRoute secondRoute = rodaliesXMLTime.getRodaliesXMLTimeRoutes().get(i);
                                RodaliesXMLTimeRouteItem secondRouteItem = secondRoute.getRodaliesXMLTimeRoutesItems().get(0);

                                trainTimes.add(
                                        new TrainTime(
                                                rodaliesXMLTime.getLine(),
                                                rodaliesXMLTime.getDepartureTime(),
                                                secondRouteItem.getArrivalTime(),
                                                secondRouteItem.getLine(),
                                                secondRouteItem.getStationCode(),
                                                secondRouteItem.getDepartureTime(),
                                                secondRoute.getArrivalTime(),
                                                origin,
                                                destination,
                                                false,
                                                calendarInstance
                                        )
                                );
                            }
                        }
                    } else {
                        // Direct train
                        // It's like a train without transfers
                        String transferStation = schedule.getTransfersList().get(0).getStationCode();
                        trainTimes.add(
                                new TrainTime(
                                        rodaliesXMLTime.getLine(),
                                        rodaliesXMLTime.getDepartureTime(),
                                        rodaliesXMLTime.getArrivalTime(),
                                        null,
                                        transferStation,
                                        null,
                                        null,
                                        origin,
                                        destination,
                                        true,
                                        calendarInstance
                                )
                        );
                    }
                }
                break;
                case 2: {
                    String transferStationOne = schedule.getTransfersList().get(0).getStationCode();
                    String transferStationTwo = schedule.getTransfersList().get(1).getStationCode();
                    int numRoutes = getNumRoutes(rodaliesXMLTime);

                    U.log("first: " + transferStationOne + " second: " + transferStationTwo);


                    if (numRoutes > 0) {
                        for (int i = 0; i < numRoutes; i++) {
                            RodaliesXMLTimeRoute route = rodaliesXMLTime.getRodaliesXMLTimeRoutes().get(i);
                            RodaliesXMLTimeRouteItem firstRouteItem = route.getRodaliesXMLTimeRoutesItems().get(0);
                            RodaliesXMLTimeRouteItem secondRouteItem = route.getRodaliesXMLTimeRoutesItems().get(1);


                            trainTimes.add(
                                    new TrainTime(
                                            rodaliesXMLTime.getLine(),
                                            rodaliesXMLTime.getDepartureTime(),
                                            firstRouteItem.getArrivalTime(),
                                            firstRouteItem.getLine(),
                                            transferStationOne,
                                            firstRouteItem.getDepartureTime(),
                                            secondRouteItem.getArrivalTime(),
                                            secondRouteItem.getLine(),
                                            transferStationTwo,
                                            secondRouteItem.getDepartureTime(),
                                            route.getArrivalTime(),
                                            origin,
                                            destination,
                                            calendarInstance
                                    )
                            );
                        }
                    }
                }
                break;
            }
        }

        return trainTimes.size() > 0 ? trainTimes : null;
    }

    private static int getNumRoutes(RodaliesXMLTime rodaliesXMLTime) {
        if (rodaliesXMLTime.getRodaliesXMLTimeRoutes() != null) {
            return rodaliesXMLTime.getRodaliesXMLTimeRoutes().size();
        }

        return -1;
    }

    private static int getTransfers(RodaliesSchedule schedule) {
        if (schedule != null) {
            return schedule.getTransfersList().size();
        }

        return -1;
    }
}
