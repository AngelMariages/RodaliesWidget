package xyz.cesarbiker.rodalieswidget;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class GetHoraris {
    private static final String url = "http://serveis.rodalies.gencat.cat/gencat_rodalies_serveis/AppJava/restServices/getHoraris?";
    private final Calendar cal = Calendar.getInstance();
    private Context context;
    private int origen = -1, desti = -1;

    public GetHoraris(Context context) {
        this.context = context;
    }

    public ArrayList<Horari> get(int origen, int desti) {
        this.origen = origen;
        this.desti = desti;
        try {
            return getXMLFromToday();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(XPathExpressionException e) {
            e.printStackTrace();
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        } catch(SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Horari> getXMLFromToday() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        String xmlFileReaded = openXMLFile();

        if(xmlFileReaded.isEmpty()) {
            Utils.log("Getting from internet!");
            xmlFileReaded = getXMLFromWeb();
            saveXMLFile(xmlFileReaded);
            removeOldXML();
            Utils.log("Getting from internet, DONE!");
        } else {
            Utils.log("Getting from file!");
        }

        return parseXMLFile(xmlFileReaded);
    }

    private ArrayList<Horari> parseXMLFile(String xmlData) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        int currentHour = getCurrentHour();
        InputSource source = new InputSource(new StringReader(xmlData));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(source);

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile("/horaris/resultats/item").evaluate(document, XPathConstants.NODESET);

        ArrayList<Horari> horaris = new ArrayList<>();

        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String linia = element.getElementsByTagName("linia").item(0).getTextContent();
                String hora_sortida = element.getElementsByTagName("hora_sortida").item(0).getTextContent();
                String hora_arribada = element.getElementsByTagName("hora_arribada").item(0).getTextContent();
                String duracio_trajecte = element.getElementsByTagName("duracio_trajecte").item(0).getTextContent();

                int hora = Integer.parseInt(hora_sortida.split(":")[0]);
                if(hora == 0 || hora >= currentHour) {
                    horaris.add(new Horari(
                            hora_sortida,
                            hora_arribada,
                            duracio_trajecte,
                            origen,
                            desti,
                            linia
                    ));
                }
            }
        }

        return horaris;
    }

    private void saveXMLFile(String data) {
        String fileName = "horaris_" + origen + "_" + desti + "_" + getTodayDateWithoutPath() + ".xml";

        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String openXMLFile() {
        String fileName = "horaris_" + origen + "_" + desti + "_" + getTodayDateWithoutPath() + ".xml";

        StringBuilder allLines = new StringBuilder();

        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(context.openFileInput(fileName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                allLines.append(line);
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        return allLines.toString();
    }

    private void removeOldXML() {
        File filesDir = context.getFilesDir();
        final String endsWith = "_" + getTodayDateWithoutPath() + ".xml";

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.equals("instant-run")) return false;
                return !(name.startsWith("horaris_") && name.endsWith(endsWith));
            }
        };

        for(File file : filesDir.listFiles(filenameFilter)) {
            Utils.log("Deleting file: " + file.getName() + ";RESULT: " + file.delete());
        }
    }

    private String getXMLFromWeb() throws IOException {
        String query  = "origen=" + origen +
                "&desti=" + desti +
                "&dataViatge=" + getTodayDate() +
                "&horaIni=0";
        URLConnection urlConnection = new URL(url + query).openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder allLines = new StringBuilder();
        String line;
        while((line = in.readLine()) != null) {
            allLines.append(line);
        }
        return allLines.toString();
    }

    private String getTodayDate() {
        return String.format(Locale.getDefault(), "%02d/%02d/%d",
                        cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private String getTodayDateWithoutPath() {
        return String.format(Locale.getDefault(), "%02d%02d%d",
                cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    private int getCurrentHour() {
        return Integer.parseInt(String.format(Locale.getDefault(), "%02d",cal.get(Calendar.HOUR_OF_DAY)));
    }
}
