package openMaps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Main {

    private static String API_KEY = "";

    public static void main(String[] args) throws Exception {
        List<String> ini = readFileInList("./ini/openroute.ini");
        API_KEY = ini.get(0);

        String mandAdr;
        String patientAdr;

        mandAdr = formatMessFromReha(args[1]);
        patientAdr = formatMessFromReha(args[0]);

        Route route = new Route().withStart(mandAdr)
                                 .withEnd(patientAdr);
        Distance distanz = route.getDistanceFromOpenRouteService(API_KEY);
        System.out.println(Math.round(distanz.getKilometer() * 2) + ";" + distanz.getDurationInMinutes() * 2 + ";"
                + distanz.getMeter() + ";" + distanz.getDuration() + ";");
    }

    /**
     * removes the comma between plz and City in the string sent by Reha.
     * 
     * @param adresseFromReha
     * @return
     */
    private static String formatMessFromReha(String adresseFromReha) {
        String mandAdr;
        mandAdr = adresseFromReha.replaceAll("%20", " ");
        String[] adresse = mandAdr.split(",");
        return adresse[0] + "," + adresse[1] + " " + adresse[2];
    }

    public static List<String> readFileInList(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {

            // ignore
        }
        return lines;
    }
}
