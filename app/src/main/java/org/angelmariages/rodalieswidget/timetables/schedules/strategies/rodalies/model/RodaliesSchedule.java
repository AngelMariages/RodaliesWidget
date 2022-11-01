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

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Xml(name = "horaris")
public class RodaliesSchedule {
    @Element(name = "error")
    public List<Error> errors;

    @Element(name = "resultats")
    public List<RodaliesXMLTime> schedule;

    @Element(name = "transbordament")
    public List<RodaliesXMLTransfer> transfersList;

    public List<Error> getErrors() {
        return errors;
    }

    public List<RodaliesXMLTime> getSchedule() {
        return schedule;
    }

    public List<RodaliesXMLTransfer> getTransfersList() {
        return transfersList;
    }

    @NotNull
    @Override
    public String toString() {
        return "RodaliesSchedule{" +
                "schedule=" + schedule +
                ", transfersList=" + transfersList +
                '}';
    }
}