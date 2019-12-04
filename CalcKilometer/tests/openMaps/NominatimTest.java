package openMaps;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.Test;

public class NominatimTest {

    @Test
    public void krummestrasseGrooßLaasch() throws Exception {
        Coordinate koords = new Nominatim().getCoordsForAddressFromNominatim("Krumme Str 1, 19288  Groß Laasch");
        Coordinate expected = new Coordinate("11.5496839581399", "53.34282395");
        assertEquals(expected, koords);
    }

    @Test
    public void krummestrasseLulu() throws Exception {
        Coordinate koords = new Nominatim().getCoordsForAddressFromNominatim(" Krumme Str. 1, 19288 Ludwigslust");
        Coordinate expected = new Coordinate("11.4954756765725", "53.33205955");
        assertEquals(expected, koords);

    }

    @Test
    public void pulverstrasse() throws Exception {
        Coordinate koords = new Nominatim().getCoordsForAddressFromNominatim("Pulverstraße 57 22880 Wedel");
        Coordinate expected = new Coordinate("9.72066", "53.57165");
        assertEquals(expected, koords);
    }

    @Test
    public void krummestraßemit2ergebnissen() throws Exception {

        List<String> lines = Files.readAllLines((new File("tests/nominatim.xml").toPath()));

        List<Coordinate> resultlist=new Nominatim().analyzeallXml(String.join("\n", lines));
        assertEquals(2,resultlist.size());

    }
}
