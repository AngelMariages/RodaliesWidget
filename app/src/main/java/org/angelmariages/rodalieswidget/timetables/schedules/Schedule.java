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

package org.angelmariages.rodalieswidget.timetables.schedules;

import org.angelmariages.rodalieswidget.timetables.TrainTime;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.RnfeStrategy;
import org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.RodaliesStrategy;

import java.util.List;

public class Schedule {
    private final String origin;
    private final String destination;
    private final int division;
    private final RnfeStrategy rnfeStrategy;
    private final RodaliesStrategy rodaliesStrategy;
    private final int deltaDays;

    public Schedule(String origin, String destination, int division, int deltaDays) {
        this.origin = origin;
        this.destination = destination;
        this.division = division;
        this.deltaDays = deltaDays; // TODO: date should be calculated here instead of passing around delta days
        this.rnfeStrategy = new RnfeStrategy();
        this.rodaliesStrategy = new RodaliesStrategy();
    }

    public List<TrainTime> get() {
        if (division == 50) { // Rodalies
            return getFromRodalies();
        } else {
            return getFromRnfe();
        }
    }

    private List<TrainTime> getFromRnfe() {
        return this.rnfeStrategy.getSchedule(this.origin, this.destination, this.division, deltaDays);
    }

    private List<TrainTime> getFromRodalies() {
        return rodaliesStrategy.getSchedule(this.origin, this.destination, this.division, deltaDays);
    }
}
