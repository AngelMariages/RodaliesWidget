package org.angelmariages.rodalieswidget.timetables;

import java.util.ArrayList;

interface ScheduleProvider {
    ArrayList<TrainTime> getSchedule();
    ArrayList<TrainTime> getSchedule(int deltaDays);
}
