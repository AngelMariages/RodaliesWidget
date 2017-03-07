package org.angelmariages.rodalieswidget.timetables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class RenfeSchedule {
	private final Calendar cal = Calendar.getInstance();

	private final int origin;
	private final int destination;
	private final int nucli;

	RenfeSchedule(int origin, int destination, int nucli) {
		this.origin = origin;
		this.destination = destination;
		this.nucli = nucli;
	}

	private String getPageFromInternet() {
		StringBuilder html = new StringBuilder();
		String query = "nucleo=" + nucli;
		query += "&o=" + origin;
		query += "&d=" + destination;
		query += "&df=" + getTodayDate();
		query += "&ho=00&i=s&cp=NO&TXTInfo=";

		try {
			String url = "http://horarios.renfe.com/cer/hjcer310.jsp?";
			URLConnection urlConnection = new URL(url + query).openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				html.append(line);
			}
		} catch (MalformedURLException e) {
			System.out.println("ERROR: URL malformada.");
		} catch (IOException e) {
			System.out.println("No es pot obrir el stream.");
		}

		return html.toString();
	}

	private int getTransfers(String html) {
		ArrayList<String> rows = getRows(html);

		int numTds = numOfCols(rows.get(0));
		int transfers = -1;

		if (numTds == 5) transfers = 0;
		else if (numTds == 6) transfers = 1;
		else if (numTds == 7) transfers = 2;

		return transfers;
	}

	ArrayList<TrainTime> getSchedule() {
		String html = getPageFromInternet();
		ArrayList<String> rows = getRows(html);

		if (html.isEmpty()) return null;
		return parseTimes(rows, getTransfers(html));
	}

	private ArrayList<TrainTime> parseTimes(ArrayList<String> rows, int transfers) {
		ArrayList<TrainTime> times = new ArrayList<>();
		int startRow = transfers == 0 ? 2 : 4;

		for (int i = startRow; i < rows.size(); i++) {
			ArrayList<String> cols = getCols(rows.get(i));

			String line = null, line_transfer_one = null, line_transfer_two = null;
			String departure_time = null, arrival_time = null;
			String journey_time = null;
			String departure_time_transfer_one = null, arrival_time_transfer_one = null;
			String departure_time_transfer_two = null, arrival_time_transfer_two = null;

			// TODO: 2/7/17 optimize this, could be better without switch
			switch (transfers) {
				case 0: {
					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line = getTextInsideTd(cols.get(y));
								break;
							case 1:
								departure_time = getTextInsideTd(cols.get(y));
								break;
							case 2:
								arrival_time = getTextInsideTd(cols.get(y));
								break;
							case 3:
								journey_time = getTextInsideTd(cols.get(y));
								break;
						}
					}
					//times.add(new TrainTime(line, departure_time, arrival_time, journey_time, origin, destination));
				}
				break;
				case 1: {
					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line = getTextInsideTd(cols.get(y));
								break;
							case 1:
								departure_time = getTextInsideTd(cols.get(y));
								break;
							case 2:
								arrival_time = getTextInsideTd(cols.get(y));
								break;
							case 3:
								departure_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 4:
								line_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 5:
								arrival_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 6:
								journey_time = getTextInsideTd(cols.get(y));
								break;
						}
					}
					// TODO: 12/02/17 Check station for stransfer from renfe
					//times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, null, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination));
				}
				break;
				case 2: {
					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line = getTextInsideTd(cols.get(y));
								break;
							case 1:
								departure_time = getTextInsideTd(cols.get(y));
								break;
							case 2:
								arrival_time = getTextInsideTd(cols.get(y));
								break;
							case 3:
								departure_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 4:
								line_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 5:
								arrival_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 6:
								departure_time_transfer_two = getTextInsideTd(cols.get(y));
								break;
							case 7:
								line_transfer_two = getTextInsideTd(cols.get(y));
								break;
							case 8:
								arrival_time_transfer_two = getTextInsideTd(cols.get(y));
								break;
						}
					}
					//times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, null, departure_time_transfer_one, arrival_time_transfer_one, line_transfer_two, null, departure_time_transfer_two, arrival_time_transfer_two, origin, destination));
				}
				break;
			}
		}

		return times;
	}

	private ArrayList<String> getRows(String html) {
		ArrayList<String> rows = new ArrayList<>();
		int lastIndex[] = new int[1];
		lastIndex[0] = 0;

		rows.add(findNextRow(html, 0, lastIndex));
		while (lastIndex[0] != -1) {
			String nextRow = findNextRow(html, lastIndex[0], lastIndex);
			if (nextRow != null)
				rows.add(nextRow);
		}

		return rows;
	}

	private String findNextRow(String html, int startFrom, int[] lastIndex) {
		int start = html.indexOf("<tr>", startFrom);
		int end = html.indexOf("<tr>", start + 1);
		if (end == -1) {
			lastIndex[0] = -1;
			if (start == -1) return null;
			else {
				return html.substring(start, html.indexOf("</tr>", start));
			}
		}
		lastIndex[0] = end;
		return html.substring(start, end);
	}

	private ArrayList<String> getCols(String row) {
		ArrayList<String> rows = new ArrayList<>();
		int lastIndex[] = new int[1];
		lastIndex[0] = 0;

		rows.add(findNextCol(row, 0, lastIndex));
		while (lastIndex[0] != -1) {
			String nextCol = findNextCol(row, lastIndex[0], lastIndex);
			if (nextCol != null)
				rows.add(nextCol);
		}

		return rows;
	}

	private String findNextCol(String row, int startFrom, int[] lastIndex) {
		int start = row.indexOf("<td", startFrom);
		int end = row.indexOf("<td", start + 1);
		if (end == -1) {
			lastIndex[0] = -1;
			if (start == -1) return null;
			else return row.substring(start);
		}
		lastIndex[0] = end;
		return row.substring(start, end);
	}

	private String getTextInsideTd(String td) {
		int start = td.indexOf(">");
		int end = td.indexOf("</");
		if (start == -1 || end == -1) return null;
		return td.substring(start + 1, end).trim();
	}

	private int numOfCols(String html) {
		int lastIndex = 0;
		int count = 0;

		while (lastIndex != -1) {
			lastIndex = html.indexOf("<td", lastIndex);

			if (lastIndex != -1) {
				count++;
				lastIndex++;
			}
		}

		return count;
	}

	private String getTodayDate() {
		return String.format(Locale.getDefault(), "%d%02d%02d",
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
	}

	/*private int getCurrentHour() {
		return Integer.parseInt(String.format(Locale.getDefault(), "%02d",cal.get(Calendar.HOUR_OF_DAY)));
	}*/
}