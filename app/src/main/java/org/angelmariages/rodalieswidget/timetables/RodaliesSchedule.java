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

package org.angelmariages.rodalieswidget.timetables;

import android.content.Context;
import androidx.annotation.NonNull;

import org.angelmariages.rodalieswidget.utils.TimeUtils;
import org.angelmariages.rodalieswidget.utils.U;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

class RodaliesSchedule extends ScheduleProvider {
	private final String origin;
	private final String destination;
	private final Context context;
	private String station_transfer_one;
	private String station_transfer_two;

	RodaliesSchedule(String origin, String destination, Context context) {
		this.origin = origin;
		this.destination = destination;
		this.context = context;
	}

	@NonNull
	private String getPageFromInternet(int deltaDays) {
		String url = "http://serveis.rodalies.gencat.cat/gencat_rodalies_serveis/AppJava/restServices/getHoraris?";
		String query = "origen=" + origin +
				"&desti=" + destination +
				"&dataViatge=" + getTodayDate(deltaDays) +
				"&horaIni=0";

		return super.doServiceRequest(url + query, context);
	}

	public ArrayList<TrainTime> getSchedule(int deltaDays) {
		return getSchedule(deltaDays, 0);
	}

	private ArrayList<TrainTime> getSchedule(int deltaDays, int times) {
		try {
			super.resetTimeoutIntents();
			return parseXMLFile(getPageFromInternet(deltaDays), TimeUtils.getCalendarForDelta(deltaDays));
		} catch (Exception e) {
			U.log("Error on getSchedule: " + Arrays.toString(e.getStackTrace()));
			if (times > 3) return null;
			return getSchedule(deltaDays, times + 1);
		}
	}

	public ArrayList<TrainTime> getSchedule() {
		return getSchedule(0);
	}

	private ArrayList<TrainTime> parseXMLFile(String xmlData, Calendar currentCalendar) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		if (xmlData == null) return null;
		StringReader stringReader = new StringReader(xmlData);
		InputSource source = new InputSource(stringReader);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(source);

		XPath xPath = XPathFactory.newInstance().newXPath();

		int transfers = getTransfers(document);

		ArrayList<TrainTime> times = new ArrayList<>();

		// TODO: 8/02/17 maybe do this accesign directly to the items??
		NodeList nodeList = (NodeList) xPath.compile("/horaris/resultats/item").evaluate(document, XPathConstants.NODESET);
		if (transfers == 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line, departure_time, arrival_time, journey_time;

					Element element = (Element) nodeItem;
					line = element.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = element.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = element.getElementsByTagName("hora_arribada").item(0).getTextContent();
					journey_time = element.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					times.add(new TrainTime(line, departure_time, arrival_time, journey_time, origin, destination, currentCalendar));
				}
			}
		} else if (transfers == 1) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line, departure_time, arrival_time, journey_time, line_transfer_one, departure_time_transfer_one, arrival_time_transfer_one;

					Element parentElement = (Element) nodeItem;

					int recorreguts = parentElement.getElementsByTagName("recorregut").getLength();
					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					if (recorreguts > 0) {
						arrival_time_transfer_one = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
						journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

						Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
						line_transfer_one = insideItem.getAttribute("linea");
						departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
						arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

						times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination, false, false, currentCalendar));

						if (recorreguts > 1) {
							for (int j = 1; j < recorreguts; j++) {
								Element recorregutElement2 = (Element) parentElement.getElementsByTagName("recorregut").item(j);

								arrival_time_transfer_one = recorregutElement2.getElementsByTagName("hora_arribada").item(0).getTextContent();
								journey_time = recorregutElement2.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

								Element insideItem2 = (Element) recorregutElement2.getElementsByTagName("item").item(0);
								line_transfer_one = insideItem2.getAttribute("linea");
								departure_time_transfer_one = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();

								times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination, false, true, currentCalendar));
							}
						}
					} else {//Direct train
						line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
						arrival_time = parentElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
						journey_time = parentElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();
						times.add(new TrainTime(line, departure_time, arrival_time, null, station_transfer_one, null, null, journey_time, origin, destination, true, false, currentCalendar));
					}
				}
			}
		} else if (transfers == 2) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line, departure_time, arrival_time, line_transfer_one, departure_time_transfer_one, arrival_time_transfer_one,
							line_transfer_two, departure_time_transfer_two, arrival_time_transfer_two;

					Element parentElement = (Element) nodeItem;

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					int recorreguts = parentElement.getElementsByTagName("recorregut").getLength();
					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					arrival_time_transfer_two = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
					//journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					if (recorreguts > 0) {
						Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
						line_transfer_one = insideItem.getAttribute("linea");
						departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
						arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

						Element insideItem2 = (Element) recorregutElement.getElementsByTagName("item").item(1);
						line_transfer_two = insideItem2.getAttribute("linea");
						departure_time_transfer_two = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();
						arrival_time_transfer_one = insideItem2.getElementsByTagName("hora_arribada").item(0).getTextContent();

						times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, line_transfer_two, station_transfer_two, departure_time_transfer_two, arrival_time_transfer_two, origin, destination, false, currentCalendar));

						if (recorreguts > 1) {
							for (int j = 1; j < recorreguts; j++) {
								Element recorregutElement2 = (Element) parentElement.getElementsByTagName("recorregut").item(j);
								arrival_time_transfer_two = recorregutElement2.getElementsByTagName("hora_arribada").item(0).getTextContent();

								Element insideItem3 = (Element) recorregutElement2.getElementsByTagName("item").item(0);
								line_transfer_one = insideItem3.getAttribute("linea");
								departure_time_transfer_one = insideItem3.getElementsByTagName("hora_sortida").item(0).getTextContent();

								Element insideItem4 = (Element) recorregutElement2.getElementsByTagName("item").item(1);
								line_transfer_two = insideItem4.getAttribute("linea");
								departure_time_transfer_two = insideItem4.getElementsByTagName("hora_sortida").item(0).getTextContent();
								arrival_time_transfer_one = insideItem4.getElementsByTagName("hora_arribada").item(0).getTextContent();

								times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, line_transfer_two, station_transfer_two, departure_time_transfer_two, arrival_time_transfer_two, origin, destination, true, currentCalendar));
							}
						}
					}
				}
			}
		}

		stringReader.close();


		return times;
	}

	private int getTransfers(Document document) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList transfersNode = (NodeList) xPath.compile("/horaris/transbordament/estacio").evaluate(document, XPathConstants.NODESET);

		for (int i = 0; i < transfersNode.getLength(); i++) {
			Node node = transfersNode.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (i == 0 && station_transfer_one == null)
					station_transfer_one = element.getAttribute("codi");
				if (i == 1 && station_transfer_two == null)
					station_transfer_two = element.getAttribute("codi");
			}
		}

		return transfersNode.getLength();
	}

	private String getTodayDate(int deltaDays) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, deltaDays);
		return String.format(Locale.getDefault(), "%02d/%02d/%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}
}
