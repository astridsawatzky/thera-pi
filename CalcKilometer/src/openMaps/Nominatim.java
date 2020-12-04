package openMaps;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

class Nominatim {

    private static final Logger logger = LoggerFactory.getLogger(Nominatim.class);

    Coordinate getCoordsForAddressFromNominatim(String client) throws IOException {
        try {
            client = URLEncoder.encode(client, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // will never happen
        }
        String urlstart = "https://nominatim.openstreetmap.org/search?q=";
        UrlReader reader = new UrlReader();
        String[] args2 = { urlstart + client + "&format=xml" };
        String ergebnis = reader.readfromUrlt(args2);
        return analyzeallXml(ergebnis).get(0);
    }

    

    public List<Coordinate> analyzeallXml(String nominatimresult) {
        List<Coordinate> coords = new LinkedList<>();
        ;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(nominatimresult));
            Document doc = dBuilder.parse(is);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement()
               .normalize();

            NodeList nList = doc.getElementsByTagName("place");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String lon = eElement.getAttribute("lon");
                    String lat = eElement.getAttribute("lat");
                    String display = eElement.getAttribute("display_name");
                    coords.add(new Coordinate(lon, lat, display));
                }

            }
        } catch (Exception e) {
            logger.error("Fehler bei der Analyze der Rückgabe", e);
        }
        return coords;

    }

}
