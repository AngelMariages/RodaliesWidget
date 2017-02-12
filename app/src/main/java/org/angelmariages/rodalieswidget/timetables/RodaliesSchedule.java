package org.angelmariages.rodalieswidget.timetables;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class RodaliesSchedule {
	private final int origin;
	private final int destination;
	private final Calendar cal = Calendar.getInstance();
	private String station_transfer_one;

	RodaliesSchedule(int origin, int destination) {
		this.origin = origin;
		this.destination = destination;
	}

	private String getPageFromInternet() {
		StringBuilder html = new StringBuilder();
		String query = "origen=" + origin +
				"&desti=" + destination +
				"&dataViatge=" + getTodayDate() +
				"&horaIni=0";

		try {
			String url = "http://serveis.rodalies.gencat.cat/gencat_rodalies_serveis/AppJava/restServices/getHoraris?";
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

	ArrayList<TrainTime> getSchedule() {
		try {
			return parseXMLFile(getPageFromInternet());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<TrainTime> parseXMLFile(String xmlData) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		InputSource source = new InputSource(new StringReader(xmlData));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(source);

		XPath xPath = XPathFactory.newInstance().newXPath();

		int transfers = getTransfers(document);

		ArrayList<TrainTime> times = new ArrayList<>();

		// TODO: 8/02/17 maybe do this accesign directly to the items??
		NodeList nodeList = (NodeList) xPath.compile("/horaris/resultats/item").evaluate(document, XPathConstants.NODESET);
		if(transfers == 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line, departure_time, arrival_time, journey_time;

					Element element = (Element) nodeItem;
					line = element.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = element.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = element.getElementsByTagName("hora_arribada").item(0).getTextContent();
					journey_time = element.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					times.add(new TrainTime(line, departure_time, arrival_time, journey_time, origin, destination));
				}
			}
		} else if(transfers == 1) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line,  departure_time, arrival_time, journey_time, line_transfer_one, departure_time_transfer_one, arrival_time_transfer_one;

					Element parentElement = (Element) nodeItem;

					int recorreguts = parentElement.getElementsByTagName("recorregut").getLength();
					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					if(recorreguts > 0) {
						arrival_time_transfer_one = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
						journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

						Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
						line_transfer_one = insideItem.getAttribute("linea");
						departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
						arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

						times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination));

						if (recorreguts == 2) {//There's no train from the origin station
							Element recorregutElement2 = (Element) parentElement.getElementsByTagName("recorregut").item(1);

							arrival_time_transfer_one = recorregutElement2.getElementsByTagName("hora_arribada").item(0).getTextContent();
							journey_time = recorregutElement2.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

							Element insideItem2 = (Element) recorregutElement2.getElementsByTagName("item").item(0);
							line_transfer_one = insideItem2.getAttribute("linea");
							departure_time_transfer_one = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();

							times.add(new TrainTime(null, null, null, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, journey_time, origin, destination));
						}
					} else {//Direct train
						arrival_time = parentElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
						journey_time = parentElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();
						times.add(new TrainTime(line, departure_time, arrival_time, null, null, null, null, journey_time, origin, destination));
					}
				}
			}
		} else if(transfers == 2) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line, departure_time, arrival_time, line_transfer_one, departure_time_transfer_one, arrival_time_transfer_one,
							line_transfer_two, departure_time_transfer_two, arrival_time_transfer_two;

					Element parentElement = (Element) nodeItem;

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					arrival_time_transfer_two = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
					//journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
					line_transfer_one = insideItem.getAttribute("linea");
					departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

					Element insideItem2 = (Element) recorregutElement.getElementsByTagName("item").item(1);
					line_transfer_two = insideItem.getAttribute("linea");
					departure_time_transfer_two = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time_transfer_one = insideItem2.getElementsByTagName("hora_arribada").item(0).getTextContent();

					times.add(new TrainTime(line, departure_time, arrival_time, line_transfer_one, station_transfer_one, departure_time_transfer_one, arrival_time_transfer_one, line_transfer_two, departure_time_transfer_two, arrival_time_transfer_two, origin, destination));
				}
			}
		}

		return times;
	}

	private int getTransfers(Document document) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList transfersNode = (NodeList) xPath.compile("/horaris/transbordament/estacio").evaluate(document, XPathConstants.NODESET);

		for (int i = 0; i < transfersNode.getLength(); i++) {
			Node node = transfersNode.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if(station_transfer_one == null) station_transfer_one = element.getAttribute("codi");
			}
		}

		return transfersNode.getLength();
	}

	private String getTodayDate() {
		return String.format(Locale.getDefault(), "%02d/%02d/%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}
}
