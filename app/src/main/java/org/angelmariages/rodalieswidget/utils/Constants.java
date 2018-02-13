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

package org.angelmariages.rodalieswidget.utils;

import org.angelmariages.rodalieswidget.BuildConfig;

public class Constants {
	//====================== [ CONSTANTS ] ======================
	public static final int ORIGIN = 100;
	public static final int DESTINATION = 200;
	public static final String ACTION_CLICK_UPDATE_BUTTON = "org.angelmariages.RodaliesWidget.clickUpdateButtonId_";
	public static final String ACTION_CLICK_SWAP_BUTTON = "org.angelmariages.RodaliesWidget.clickSwapButtonId_";
	public static final String ACTION_UPDATE_STATIONS = "org.angelmariages.RodaliesWidget.sendNewSettingsId_";
	public static final String ACTION_SEND_NEW_STATIONS = "org.angelmariages.RodaliesWidget.sendNewStations";
	public static final String ACTION_CLICK_STATIONS_TEXT = "org.angelmariages.RodaliesWidget.clickStationsText_";
	public static final String ACTION_CLICK_LIST_ITEM = "org.angelmariages.RodaliesWidget.clickListItem_";
	public static final String ACTION_WIDGET_NO_DATA = "org.angelmariages.RodaliesWidget.widgetNoData_";
	public static final String ACTION_SEND_SCHEDULE = "org.angelmariages.RodaliesWidget.ACTION_WIDGET_SEND_SCHEDULE_";
	public static final String ACTION_NOTIFY_UPDATE = "org.angelmariages.RodaliesWidget.ACTION_NOTIFY_UPDATE_";
	public static final String EXTRA_ORIGINorDESTINATION = "org.angelmariages.RodaliesWidget.originOrDestination";
	public static final String EXTRA_PROMOTION_LINE = "org.angelmariages.RodaliesWidget.EXTRA_PROMOTION_LINE";
	public static final String EXTRA_ORIGIN = "org.angelmariages.RodaliesWidget.extraOrigin";
	public static final String EXTRA_DESTINATION = "org.angelmariages.RodaliesWidget.extraDestination";
	public static final String EXTRA_WIDGET_ID = "org.angelmariages.RodaliesWidget.extraWidgetId";
	public static final String EXTRA_ALARM_DEPARTURE_TIME = "org.angelmariages.RodaliesWidget.extraRideLength";
	public static final String EXTRA_CONFIG_STATION = "org.angelmariages.RodaliesWidget.newSettings";
	public static final String EXTRA_WIDGET_STATE = "org.angelmariages.RodaliesWidget.extraWidgetState";
	public static final String EXTRA_SCHEDULE_DATA = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_DATA";
	public static final String EXTRA_SCHEDULE_BUNDLE = "org.angelmariages.RodaliesWidget.EXTRA_SCHEDULE_BUNDLE";
	public static final String EXTRA_SWITCH_TO = "org.angelmariages.RodaliesWidget.EXTRA_SWITCH_TO";
	public static final String PREFERENCE_STRING_ALARM_URI = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ALARM_URI";
	public static final int WIDGET_STATE_SCHEDULE_LOADED = 0;
	public static final int WIDGET_STATE_NO_INTERNET = 1;
	public static final int WIDGET_STATE_NO_STATIONS = 2;
	public static final int WIDGET_STATE_NO_TIMES = 3;
	public static final int WIDGET_STATE_UPDATING_TABLES = 4;
	static final String PREFERENCE_KEY = "org.angelmariages.RodaliesWidget.PREFERENCE_FILE_KEY_ID_";
	static final String PREFERENCE_GLOBAL_KEY = "org.angelmariages.RodaliesWidget.PREFERENCE_GLOBAL_KEY";
	static final String PREFERENCE_STRING_ORIGIN = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ORIGIN";
	static final String PREFERENCE_STRING_DESTINATION = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_DESTINATION";
	static final String PREFERENCE_STRING_ALARM_FOR_ID = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_ALARM_FOR_ID_";
	static final String PREFERENCE_STRING_CORE_ID = "org.angelmariages.RodaliesWidget.PREFERENCE_STRING_CORE_ID_";
	static final String PREFERENCE_BOOLEAN_FIRST_TIME = "org.angelmariages.RodaliesWidget.PREFERENCE_BOOLEAN_FIRST_TIME";
	//====================== [ END_CONSTANTS ] ======================
	static final boolean LOGGING = BuildConfig.DEBUG;
}
