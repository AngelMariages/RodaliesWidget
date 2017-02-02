package org.angelmariages.rodalieswidget.timetables;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.angelmariages.rodalieswidget.utils.U;

public class GetTimeTablesRodalies {
	// TODO: 29/01/17 This should be an async task etc...
	private final Calendar cal = Calendar.getInstance();
    private final Context context;
    private int origin = -1, destination = -1;

    public GetTimeTablesRodalies(Context context) {
        this.context = context;

    }

    public ArrayList<TrainTime> get(int origen, int desti) {
        this.origin = origen;
        this.destination = desti;
        try {
            return getJSONFromToday();
        } catch(IOException e) {
            if(e instanceof UnknownHostException) {
                U.log("Can't reach the internet");
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ArrayList<TrainTime> getJSONFromToday() throws IOException {
        String jsonFileRead = readJSONFile();

        if(jsonFileRead.isEmpty()) {
            U.log("Getting json from internet...");
            RenfeSchedule renfeSchedule = new RenfeSchedule(origin, destination, 50);
            ArrayList<TrainTime> schedule = renfeSchedule.getSchedule();
            ArrayList<TrainTime> hourSchedule = new ArrayList<>();
            if(schedule.size() > 0) {
                int currentHour = getCurrentHour();
                for (TrainTime trainTime : schedule) {
	                String departureTime = trainTime.getDeparture_time();
	                String departureTime_one = trainTime.getDeparture_time_transfer_one();
	                String departureTime_two = trainTime.getDeparture_time_transfer_two();
	                int hour = 24;
	                if(departureTime != null)
                        hour = Integer.parseInt(departureTime.split(":")[0]);
	                else if(departureTime_one != null)
		                hour = Integer.parseInt(departureTime_one.split(":")[0]);
	                else if(departureTime_two != null)
		                hour = Integer.parseInt(departureTime_two.split(":")[0]);

                    if(hour == 0 || hour >= currentHour) {
                        hourSchedule.add(trainTime);
                    }
                }

                jsonFileRead = ScheduleFileManager.getJSONString(schedule);
                saveJSONFile(jsonFileRead);
                removeOldJSON();
            }

            return hourSchedule;
        } else {
            U.log("Getting json from file...");
	        ArrayList<TrainTime> scheduleFromJSON = ScheduleFileManager.getScheduleFromJSON(jsonFileRead, origin, destination);
	        ArrayList<TrainTime> hourSchedule = new ArrayList<>();
	        if(scheduleFromJSON.size() > 0) {
		        int currentHour = getCurrentHour();
		        U.log(origin + " , " + destination);
		        U.log("SIZE JSON: " + scheduleFromJSON.size());
		        for (TrainTime trainTime : scheduleFromJSON) {
			        String departureTime = trainTime.getDeparture_time();
			        String departureTime_one = trainTime.getDeparture_time_transfer_one();
			        String departureTime_two = trainTime.getDeparture_time_transfer_two();
			        int hour = 24;
			        if(departureTime != null)
				        hour = Integer.parseInt(departureTime.split(":")[0]);
			        else if(departureTime_one != null)
				        hour = Integer.parseInt(departureTime_one.split(":")[0]);
			        else if(departureTime_two != null)
				        hour = Integer.parseInt(departureTime_two.split(":")[0]);

			        if(hour == 0 || hour >= currentHour) {
				        hourSchedule.add(trainTime);
			        }
			        U.log(trainTime.toString());
		        }
	        }

	        return hourSchedule;
        }
    }

    private String readJSONFile() {
        String fileName = "horaris_" + origin + "_" + destination + "_" + getTodayDateWithoutPath() + ".json";

        StringBuilder allLines = new StringBuilder();

        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(context.openFileInput(fileName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                allLines.append(line);
            }
        } catch (IOException e) {
            U.log("File " + fileName + " not found, getting from internet");
        }

        return allLines.toString();
    }

    private void saveJSONFile(String jsonFile) {
        if(jsonFile.isEmpty()) return;

        String fileName = "horaris_" + origin + "_" + destination + "_" + getTodayDateWithoutPath() + ".json";

        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonFile);
            outputStreamWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void removeOldJSON() {
        File filesDir = context.getFilesDir();
        final String endsWith = "_" + getTodayDateWithoutPath() + ".json";

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
	            return !name.equals("instant-run") && !(name.startsWith("horaris_") && name.endsWith(endsWith));
            }
        };

        for(File file : filesDir.listFiles(filenameFilter)) {
            U.log("Deleting file: " + file.getName() + ";RESULT: " + file.delete());
        }
	    // TODO: 29/01/17 Clear this in the next release
	    removeOldXML();
    }

    private void removeOldXML() {
        File filesDir = context.getFilesDir();
        final String endsWith = "_" + getTodayDateWithoutPath() + ".xml";

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
	            return !name.equals("instant-run") && !(name.endsWith(".json") && name.startsWith("horaris_") && name.endsWith(endsWith));
            }
        };

        for(File file : filesDir.listFiles(filenameFilter)) {
            U.log("Deleting file: " + file.getName() + ";RESULT: " + file.delete());
        }
    }

    private String getTodayDateWithoutPath() {
        return String.format(Locale.getDefault(), "%02d%02d%d",
                cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private int getCurrentHour() {
        return Integer.parseInt(String.format(Locale.getDefault(), "%02d",cal.get(Calendar.HOUR_OF_DAY)));
    }
}
