package org.angelmariages.rodalieswidget.timetables;

import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Locale;

public class GetTimeTablesRenfe {
    private static final String url = "http://horarios.renfe.com/cer/hjcer310.jsp?";
    private final Calendar cal = Calendar.getInstance();

    private int origen;
    private int desti;
    private int nucli;

    public GetTimeTablesRenfe() {}

    public void get(int origen, int desti, int nucli) {
        this.origen = origen;
        this.desti = desti;
        this.nucli = nucli;

        // TODO: 1/26/17 Check if file exists before getting from internet
        getPageFromInternet();
    }

    private void getPageFromInternet() {
        String query = "nucleo=" + nucli;
        query += "&o=" + origen;
        query += "&d=" + desti;
        query += "&df=" + getTodayDate();
        query += "&ho=00&i=s&cp=NO&TXTInfo=";

        try {
            URLConnection urlConnection = new URL(url + query).openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;
            while((line = in.readLine()) != null) {
                html.append(line);
                System.out.println(line);
            }
        } catch(MalformedURLException e) {
            System.out.println("ERROR: URL malformada.");
        } catch(IOException e) {
            System.out.println("No es pot obrir el stream.");
        }
    }

    private String getTodayDate() {
        return String.format(Locale.getDefault(), "%02d%02d%d",
                cal.get(Calendar.YEAR) ,cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    private int getCurrentHour() {
        return Integer.parseInt(String.format(Locale.getDefault(), "%02d",cal.get(Calendar.HOUR_OF_DAY)));
    }
}
