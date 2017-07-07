package org.angelmariages.rodalieswidget.timetables;

import org.angelmariages.rodalieswidget.utils.StationUtils;
import org.angelmariages.rodalieswidget.utils.U;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("unused")
class RenfeSchedule {
	private final Calendar cal = Calendar.getInstance();

	private final String origin;
	private final String destination;
	private final int nucli;
	private String transferStationOne;
	private String transferStationTwo;

	private int lastRowIndex;

	RenfeSchedule(String origin, String destination, int nucli) {
		this.origin = origin;
		this.destination = destination;
		this.nucli = nucli;
	}

	private String getPageFromInternet() {
		HttpURLConnection connection = null;
		BufferedReader in = null;
		StringBuilder html = new StringBuilder();
		String query = "nucleo=" + nucli;
		query += "&o=" + origin;
		query += "&d=" + destination;
		query += "&df=" + getTodayDate();
		query += "&ho=00&i=s&cp=NO&TXTInfo=";

		try {
			String url = "http://horarios.renfe.com/cer/hjcer310.jsp?";
			connection = (HttpURLConnection) new URL(url + query).openConnection();
			connection.setConnectTimeout(2500);
			connection.setReadTimeout(2500);
			connection.setRequestMethod("GET");

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				html.append(line);
			}
		} catch (MalformedURLException e) {
			U.log("ERROR: URL malformada.");
		} catch (IOException e) {
			U.log("No es pot obrir el stream.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					U.log("Errpr tancant l'stream: " + e.getMessage());
				}
			}
		}

		return html.toString();
	}

	private int getTransfers(ArrayList<String> rows) {
		if (rows == null || rows.size() == 0) return 0;
		int numTds = numOfCols(rows.get(0));
		int transfers = -1;

		switch (numTds) {
			case 5:
				transfers = 0;
				break;
			case 8:
			case 9:
				transfers = 1;
				break;
			case 12:
				transfers = 2;
				break;
		}

		return transfers;
	}

	ArrayList<TrainTime> getSchedule() {
		String html = getPageFromInternet();

		ArrayList<String> rows = getRows(html);

		transferStationOne = StationUtils.getIDFromName(transferStationOne, nucli);
		transferStationTwo = StationUtils.getIDFromName(transferStationTwo, nucli);

		return parseTimes(rows, getTransfers(rows));
	}

	private ArrayList<TrainTime> parseTimes(ArrayList<String> rows, int transfers) {
		ArrayList<TrainTime> times = new ArrayList<>();
		//int startRow = transfers == 0 ? 2 : 4;

		String line = null, line_transfer_one = null, line_transfer_two = null, line_tmp = null;
		String departure_time = null, arrival_time = null;
		String departure_time_tmp = null, arrival_time_tmp = null;
		String journey_time = null;
		String departure_time_transfer_one = null, arrival_time_transfer_one = null;
		String departure_time_transfer_two = null, arrival_time_transfer_two = null;

		for (String row : rows) {
			ArrayList<String> cols = getCols(row);

			switch (transfers) {
				case 0: {
					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line = getTextInsideTd(cols.get(y));
								break;
							case 1:
								break; // Accesible
							case 2:
								departure_time = getTextInsideTd(cols.get(y));
								break;
							case 3:
								arrival_time = getTextInsideTd(cols.get(y));
								break;
							case 4:
								journey_time = getTextInsideTd(cols.get(y));
								break;
						}
					}
					times.add(new TrainTime(line, departure_time, arrival_time, journey_time, origin, destination));
				}
				break;
				case 1: {
					boolean isDirectTrain = false;

					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line_tmp = getTextInsideTd(cols.get(y));
								if (line_tmp != null && !line_tmp.isEmpty())
									line = line_tmp;
								break;
							case 1:
								break; // Accesible tren 1
							case 2:
								departure_time_tmp = getTextInsideTd(cols.get(y));
								if (departure_time_tmp != null && !departure_time_tmp.isEmpty())
									departure_time = departure_time_tmp;
								break;
							case 3:
								arrival_time_tmp = getTextInsideTd(cols.get(y));

								if (arrival_time_tmp.contains("irecto")) isDirectTrain = true;
								else {
									if (arrival_time_tmp != null && !arrival_time_tmp.isEmpty())
										arrival_time = arrival_time_tmp;
								}
								break;
							case 4:
								if (!isDirectTrain)
									departure_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 5:
								if (!isDirectTrain)
									line_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 6:
								if (isDirectTrain) arrival_time = getTextInsideTd(cols.get(y));
								break; // Accesible tren 2
							case 7:
								if (isDirectTrain) journey_time = getTextInsideTd(cols.get(y));
								else arrival_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 8:
								if (!isDirectTrain) journey_time = getTextInsideTd(cols.get(y));
								break;
						}
					}
					// TODO: 21/06/2017 Check direct train, maybe there aren't direct trains

					boolean isSameOrigin = (line_tmp == null || line_tmp.isEmpty()) ||
							(departure_time_tmp == null || departure_time_tmp.isEmpty()) ||
							(arrival_time_tmp == null || arrival_time_tmp.isEmpty());

					if (isDirectTrain) {
						departure_time_transfer_one = null;
						departure_time_transfer_two = null;
						line_transfer_one = null;
					}

					times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, transferStationOne, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination, isDirectTrain, isSameOrigin));

				}
				break;
				case 2: {
					for (int y = 0; y < cols.size(); y++) {
						switch (y) {
							case 0:
								line_tmp = getTextInsideTd(cols.get(y));
								if (line_tmp != null && !line_tmp.isEmpty())
									line = line_tmp;
								break;
							case 1:
								break; // Accesible tren 1
							case 2:
								departure_time_tmp = getTextInsideTd(cols.get(y));
								if (departure_time_tmp != null && !departure_time_tmp.isEmpty())
									departure_time = departure_time_tmp;
								break;
							case 3:
								arrival_time_tmp = getTextInsideTd(cols.get(y));
								if (arrival_time_tmp != null && !arrival_time_tmp.isEmpty())
									arrival_time = arrival_time_tmp;
								break;
							case 4:
								departure_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 5:
								line_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 6:
								break; // Accesible tren 2
							case 7:
								arrival_time_transfer_one = getTextInsideTd(cols.get(y));
								break;
							case 8:
								departure_time_transfer_two = getTextInsideTd(cols.get(y));
								break;
							case 9:
								line_transfer_two = getTextInsideTd(cols.get(y));
								break;
							case 10:
								break; // Accesible tren 3
							case 11:
								arrival_time_transfer_two = getTextInsideTd(cols.get(y));
								break;
						}
					}

					boolean isSameOrigin = (line_tmp == null || line_tmp.isEmpty()) ||
							(departure_time_tmp == null || departure_time_tmp.isEmpty()) ||
							(arrival_time_tmp == null || arrival_time_tmp.isEmpty());

					times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, transferStationOne, departure_time_transfer_one, arrival_time_transfer_one, line_transfer_two, transferStationTwo, departure_time_transfer_two, arrival_time_transfer_two, origin, destination, isSameOrigin));
				}
				break;
			}
		}

		return times;
	}

	private ArrayList<String> getRows(String html) {
		ArrayList<String> rows = new ArrayList<>();
		if (html == null || html.isEmpty()) return rows;
		lastRowIndex = 0;
		String transferKey = "colspan=2 align=center";

		int transferIndex;
		if (transferStationOne == null && transferStationTwo == null) {
			transferIndex = html.indexOf(transferKey);
			if (transferIndex != -1) {
				int transferStrLength = transferKey.length();
				transferStationOne = html.substring(transferIndex + transferStrLength, html.indexOf("</td>", transferIndex)).trim();
				if (transferStationOne.contains("Transbordo")) {
					transferIndex = html.indexOf(transferKey, transferIndex + transferStrLength);

					transferStationOne = html.substring(transferIndex + transferStrLength, html.indexOf("</td>", transferIndex)).trim();
				}
				if (transferStationOne.contains("Transbordo")) {
					transferIndex = html.indexOf(transferKey, transferIndex + transferStrLength);

					transferStationOne = html.substring(transferIndex + transferStrLength, html.indexOf("</td>", transferIndex)).trim();
				}

				transferIndex = html.indexOf(transferKey, transferIndex + transferStrLength);
				if (transferIndex != -1) {
					transferStationTwo = html.substring(transferIndex + transferStrLength, html.indexOf("</td>", transferIndex)).trim();
				}

				if (transferStationOne != null) {
					transferStationOne = transferStationOne.replace("<b>", "");
					transferStationOne = transferStationOne.replace("</b>", "");
					transferStationOne = transferStationOne.replace(">", "");
					transferStationOne = transferStationOne.trim();
				}
				if (transferStationTwo != null) {
					transferStationTwo = transferStationTwo.replace("<b>", "");
					transferStationTwo = transferStationTwo.replace("</b>", "");
					transferStationTwo = transferStationTwo.replace(">", "");
					transferStationTwo = transferStationTwo.trim();
				}
			}
		}

		rows.add(findNextRow(html, 0));
		while (lastRowIndex != -1) {
			String nextRow = findNextRow(html, lastRowIndex);
			if (nextRow != null)
				rows.add(nextRow);
		}

		return rows;
	}

	private String findNextRow(String html, int startFrom) {
		int start = html.indexOf("<tr ", startFrom);
		int end = html.indexOf("<tr ", start + 1);
		if (end == -1) {
			lastRowIndex = -1;
			if (start == -1) return null;
			else {
				return html.substring(start, html.indexOf("</tr>", start));
			}
		}
		lastRowIndex = end;
		return html.substring(start, end);
	}

	private ArrayList<String> getCols(String row) {
		ArrayList<String> cols = new ArrayList<>();
		if (row == null) return cols;
		int lastIndex[] = new int[1];
		lastIndex[0] = 0;

		cols.add(findNextCol(row, 0, lastIndex));
		while (lastIndex[0] != -1) {
			String nextCol = findNextCol(row, lastIndex[0], lastIndex);
			if (nextCol != null)
				cols.add(nextCol);
		}

		return cols;
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
		if (html == null) return 0;
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