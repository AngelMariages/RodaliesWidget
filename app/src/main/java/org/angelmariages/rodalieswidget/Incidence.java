/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
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
 */

package org.angelmariages.rodalieswidget;public class Incidence {
	public String Affects, CA, Date, ID, Title, Subtitle, Text;

	public Incidence() { }

	public String getAffects() {
		return Affects;
	}

	public void setAffects(String affects) {
		this.Affects = affects;
	}

	public String getCA() {
		return CA;
	}

	public void setCA(String CA) {
		this.CA = CA;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

	public String getSubtitle() {
		return Subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.Subtitle = subtitle;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		this.Text = text;
	}

	@Override
	public String toString() {
		return "Incidence{" +
				"Affects='" + Affects + '\'' +
				", CA='" + CA + '\'' +
				", Date='" + Date + '\'' +
				", id='" + ID + '\'' +
				", Title='" + Title + '\'' +
				", Subtitle='" + Subtitle + '\'' +
				", Text='" + Text + '\'' +
				'}';
	}
}
