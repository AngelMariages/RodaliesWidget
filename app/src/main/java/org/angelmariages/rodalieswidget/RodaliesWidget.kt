/*
 * MIT License
 *
 * Copyright (c) 2022 Àngel Mariages
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
package org.angelmariages.rodalieswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.angelmariages.rodalieswidget.timetables.TrainTime
import org.angelmariages.rodalieswidget.timetables.schedules.retriever.GetScheduleKotlin
import org.angelmariages.rodalieswidget.utils.Constants
import org.angelmariages.rodalieswidget.utils.StationUtils
import org.angelmariages.rodalieswidget.utils.U
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
internal class RodaliesWidget(
    private val context: Context,
    private val widgetID: Int,
    state: Int,
    layout: Int,
    schedule: ArrayList<TrainTime>?,
    deltaDays: Int
) : RemoteViews(
    context.packageName, layout
) {
    private val state: Int

    init {
        setStationNames()
        setPendingIntents()
        //@TODO manage web service status !important
        this.state = state
        when (state) {
            Constants.WIDGET_STATE_UPDATING_TABLES -> {
                startForegroundService(context)
                // GetSchedule().execute(context, widgetID, deltaDays)
                GlobalScope.launch(Dispatchers.IO) {
                    GetScheduleKotlin().execute(context, widgetID, deltaDays)
                    println("Finished!")
                }
            }
            Constants.WIDGET_STATE_SCHEDULE_LOADED -> {
                val adapterIntent = Intent(context, WidgetService::class.java)
                adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
                adapterIntent.data = Uri.fromParts("content", widgetID.toString() + Math.random(), null)
                val bundle = Bundle()
                bundle.putSerializable(Constants.EXTRA_SCHEDULE_DATA, schedule)
                adapterIntent.putExtra(Constants.EXTRA_SCHEDULE_BUNDLE, bundle)
                if (schedule != null && schedule.size > 0) {
                    val core = U.getCore(context, widgetID)
                    val trainTime = schedule[0]
                    when (trainTime.transfer) {
                        1 -> {
                            var transferStation: String? = null
                            try {
                                transferStation =
                                    StationUtils.getNameFromID(trainTime.stationTransferOne, core)
                            } catch (ignored: NumberFormatException) {
                            }
                            if (transferStation != null) {
                                setTextViewText(R.id.transferOneTitleText, transferStation)
                                setTextViewText(R.id.lineTransferOneText, trainTime.lineTransferOne)
                                try {
                                    setInt(
                                        R.id.lineTransferOneText,
                                        "setBackgroundColor",
                                        Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.lineTransferOne + core).bColor)
                                    )
                                    setTextColor(
                                        R.id.lineTransferOneText,
                                        StationUtils.ColorLines.valueOf(trainTime.lineTransferOne + core).tColor
                                    )
                                } catch (e: Exception) {
                                    U.log("Unknown color for setTexts: " + trainTime.lineTransferOne + core)
                                }
                            } else setViewVisibility(R.id.transferOneTitleText, View.GONE)
                        }
                        2 -> {
                            var transferStation: String? = null
                            var transferStationTwo: String? = null
                            try {
                                transferStation =
                                    StationUtils.getNameFromID(trainTime.stationTransferOne, core)
                                transferStationTwo =
                                    StationUtils.getNameFromID(trainTime.stationTransferTwo, core)
                            } catch (ignored: NumberFormatException) {
                            }
                            // TODO: makes sense to have the line of the transfer? sometimes it's different
                            if (transferStation != null) {
                                setTextViewText(R.id.transferOneTitleText, transferStation)
                                setTextViewText(R.id.lineTransferOneText, trainTime.lineTransferOne)
                                try {
                                    setInt(
                                        R.id.lineTransferOneText,
                                        "setBackgroundColor",
                                        Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.lineTransferOne + core).bColor)
                                    )
                                    setTextColor(
                                        R.id.lineTransferOneText,
                                        StationUtils.ColorLines.valueOf(trainTime.lineTransferOne + core).tColor
                                    )
                                } catch (e: Exception) {
                                    U.log("Unknown color for setTexts: " + trainTime.lineTransferOne + core)
                                }
                            } else setViewVisibility(R.id.transferOneTitleText, View.GONE)
                            if (transferStationTwo != null) {
                                setTextViewText(R.id.transferTwoTitleText, transferStationTwo)
                                setTextViewText(R.id.lineTransferTwoText, trainTime.lineTransferTwo)
                                try {
                                    setInt(
                                        R.id.lineTransferTwoText,
                                        "setBackgroundColor",
                                        Color.parseColor(StationUtils.ColorLines.valueOf(trainTime.lineTransferTwo + core).bColor)
                                    )
                                    setTextColor(
                                        R.id.lineTransferTwoText,
                                        StationUtils.ColorLines.valueOf(trainTime.lineTransferTwo + core).tColor
                                    )
                                } catch (e: Exception) {
                                    U.log("Unknown color for setTexts: " + trainTime.lineTransferTwo + core)
                                }
                            } else setViewVisibility(R.id.transferTwoTitleText, View.GONE)
                        }
                    }
                }
                this.setRemoteAdapter(R.id.scheduleListView, adapterIntent)
            }
            Constants.WIDGET_STATE_NO_INTERNET -> {
                setTextViewText(R.id.reasonTextView, context.resources.getString(R.string.no_internet))
            }
            Constants.WIDGET_STATE_NO_STATIONS -> {
                setTextViewText(R.id.reasonTextView, context.resources.getString(R.string.no_stations))
            }
            Constants.WIDGET_STATE_NO_TIMES -> {
                setTextViewText(R.id.reasonTextView, context.resources.getString(R.string.no_times))
            }
            Constants.WIDGET_STATE_PROGRAMED_DISRUPTIONS -> {
                setTextViewText(R.id.reasonTextView, context.resources.getString(R.string.programed_disruptions))
            }
        }
        setStationNames()
        if (state != Constants.WIDGET_STATE_UPDATING_TABLES) {
            setPendingIntents()
            stopForegroundService(context)
        }
    }

    private fun startForegroundService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(
                Intent(
                    context,
                    ScheduleUpdateNotificationService::class.java
                )
            )
        }
    }

    private fun stopForegroundService(context: Context) {
        context.stopService(Intent(context, ScheduleUpdateNotificationService::class.java))
    }

    private fun setStationNames() {
        val stations = U.getStations(context, widgetID)
        if (stations.size == 2) {
            val core = U.getCore(context, widgetID)

            /*Crashlytics.setString("origin", stations[0]);
			Crashlytics.setString("destination", stations[1]);
			Crashlytics.setInt("core", core);*/

            updateStationsText(
                StationUtils.getNameFromID(stations[0], core),
                StationUtils.getNameFromID(stations[1], core)
            )
        }
    }

    private fun setPendingIntents() {
        setListViewClickIntent()
        setUpdateButtonIntent()
        setSwapButtonIntent()
        setConfigStationIntent()
    }

    private fun setListViewClickIntent() {
        //It this intent is not set the intent when on click on a row of the list view doesn't work
        val listViewClickIntent = Intent(context, WidgetManager::class.java)
        listViewClickIntent.action = Constants.ACTION_CLICK_LIST_ITEM + getWidgetID()
        listViewClickIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
        val clickPI = PendingIntent.getBroadcast(
            context, 0,
            listViewClickIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setPendingIntentTemplate(R.id.scheduleListView, clickPI)
    }

    private fun setUpdateButtonIntent() {
        val updateButtonIntent = Intent(context, WidgetManager::class.java)
        updateButtonIntent.action = Constants.ACTION_CLICK_UPDATE_BUTTON + getWidgetID()
        updateButtonIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
        updateButtonIntent.putExtra(Constants.EXTRA_WIDGET_STATE, state)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0,
            updateButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.updateButton, pendingIntent)
    }

    private fun setSwapButtonIntent() {
        val swapButtonIntent = Intent(context, WidgetManager::class.java)
        swapButtonIntent.action = Constants.ACTION_CLICK_SWAP_BUTTON + getWidgetID()
        swapButtonIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
        swapButtonIntent.putExtra(Constants.EXTRA_WIDGET_STATE, state)
        val swapPI = PendingIntent.getBroadcast(
            context, 0,
            swapButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.swapButton, swapPI)
    }

    private fun setConfigStationIntent() {
        val originStationIntent = Intent(context, WidgetManager::class.java)
        originStationIntent.action = Constants.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_O"
        originStationIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
        originStationIntent.putExtra(Constants.EXTRA_ORIGINorDESTINATION, Constants.ORIGIN)
        val showDialogPI1 = PendingIntent.getBroadcast(
            context, 0,
            originStationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.originLayout, showDialogPI1)
        val destinationStationIntent = Intent(context, WidgetManager::class.java)
        destinationStationIntent.action =
            Constants.ACTION_CLICK_STATIONS_TEXT + getWidgetID() + "_D"
        destinationStationIntent.putExtra(Constants.EXTRA_WIDGET_ID, widgetID)
        destinationStationIntent.putExtra(
            Constants.EXTRA_ORIGINorDESTINATION,
            Constants.DESTINATION
        )
        val showDialogPI2 = PendingIntent.getBroadcast(
            context, 0,
            destinationStationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.destinationLayout, showDialogPI2)
    }

    private fun updateStationsText(originText: String?, destinationText: String?) {
        val nullOrigin = context.resources.getString(R.string.no_origin_set)
        val nullDestination = context.resources.getString(R.string.no_destination_set)
        setTextViewText(R.id.originTextView, originText ?: nullOrigin)
        setTextViewText(R.id.destinationTextView, destinationText ?: nullDestination)
    }

    private fun getWidgetID(): String {
        return widgetID.toString()
    }
}