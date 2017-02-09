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

public class RodaliesSchedule {
	private final int origin;
	private final int destination;
	private final Calendar cal = Calendar.getInstance();

	public RodaliesSchedule(int origin, int destination) {
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

	public ArrayList<TrainTime> getSchedule() {
		try {
			parseXMLFile(getPageFromInternet());
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

		ArrayList<TrainTime> horaris = new ArrayList<>();

		// TODO: 8/02/17 maybe do this accesign directly to the items??
		NodeList nodeList = (NodeList) xPath.compile("/horaris/resultats/item").evaluate(document, XPathConstants.NODESET);
		if(transfers == 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line = null;
					String departure_time = null, arrival_time = null;
					String journey_time = null;

					Element element = (Element) nodeItem;
					line = element.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = element.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = element.getElementsByTagName("hora_arribada").item(0).getTextContent();
					journey_time = element.getElementsByTagName("duracio_trajecte").item(0).getTextContent();
				}
			}
		} else if(transfers == 1) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line = null;
					String departure_time = null, arrival_time = null;
					String journey_time = null;
					String departure_time_transfer_one = null, arrival_time_transfer_one = null;

					Element parentElement = (Element) nodeItem;

					int recorreguts = parentElement.getElementsByTagName("recorregut").getLength();
					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					arrival_time_transfer_one = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
					journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
					departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

					//add

					if(recorreguts == 2) {
						departure_time = null;
						arrival_time = null;
						journey_time = null;
						departure_time_transfer_one = null;
						arrival_time_transfer_one = null;
						Element recorregutElement2 = (Element) parentElement.getElementsByTagName("recorregut").item(1);

						arrival_time_transfer_one = recorregutElement2.getElementsByTagName("hora_arribada").item(0).getTextContent();
						journey_time = recorregutElement2.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

						Element insideItem2 = (Element) recorregutElement2.getElementsByTagName("item").item(0);
						departure_time_transfer_one = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();

					}
				}
			}
		} else if(transfers == 2) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
					String line = null;
					String departure_time = null, arrival_time = null;
					String journey_time = null;
					String departure_time_transfer_one = null, arrival_time_transfer_one = null;
					String departure_time_transfer_two = null, arrival_time_transfer_two = null;

					Element parentElement = (Element) nodeItem;

					int recorreguts = parentElement.getElementsByTagName("recorregut").getLength();
					Element recorregutElement = (Element) parentElement.getElementsByTagName("recorregut").item(0);

					line = parentElement.getElementsByTagName("linia").item(0).getTextContent();
					departure_time = parentElement.getElementsByTagName("hora_sortida").item(0).getTextContent();

					arrival_time_transfer_two = recorregutElement.getElementsByTagName("hora_arribada").item(0).getTextContent();
					journey_time = recorregutElement.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

					Element insideItem = (Element) recorregutElement.getElementsByTagName("item").item(0);
					departure_time_transfer_one = insideItem.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time = insideItem.getElementsByTagName("hora_arribada").item(0).getTextContent();

					Element insideItem2 = (Element) recorregutElement.getElementsByTagName("item").item(1);
					departure_time_transfer_two = insideItem2.getElementsByTagName("hora_sortida").item(0).getTextContent();
					arrival_time_transfer_one = insideItem2.getElementsByTagName("hora_arribada").item(0).getTextContent();

					System.out.print("line = " + line);
					System.out.print(" departure_time = " + departure_time);
					System.out.print(" arrival_time = " + arrival_time);
					System.out.print(" departure_time_transfer_one = " + departure_time_transfer_one);
					System.out.print(" arrival_time_transfer_one = " + arrival_time_transfer_one);
					System.out.print(" departure_time_transfer_two = " + departure_time_transfer_two);
					System.out.print(" arrival_time_transfer_two = " + arrival_time_transfer_two);
					System.out.println(" journey_time = " + journey_time);
				}
			}
		}

		return horaris;
	}

	private int getTransfers(Document document) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();

		NodeList transfersNode = (NodeList) xPath.compile("/horaris/transbordament/estacio").evaluate(document, XPathConstants.NODESET);

		for (int i = 0; i < transfersNode.getLength(); i++) {
			Node node = transfersNode.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				System.out.println("Codi: " + element.getAttribute("codi"));
			}
		}

		String lineTransferOne = (String) xPath.compile("/horaris/resultats/item/recorregut/item/@linea").evaluate(document, XPathConstants.STRING);
		if (!lineTransferOne.isEmpty()) {
			System.out.println("lineTransferOne = " + lineTransferOne);
		}

		String lineTransferTwo = (String) xPath.compile("/horaris/resultats/item/recorregut/item[2]/@linea").evaluate(document, XPathConstants.STRING);
		if (!lineTransferTwo.isEmpty()) {
			System.out.println("lineTransferTwo = " + lineTransferTwo);
		}

		return transfersNode.getLength();
	}

	private String getTodayDate() {
		return String.format(Locale.getDefault(), "%02d/%02d/%d",
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
	}
}
