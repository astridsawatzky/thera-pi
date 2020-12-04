package openMaps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import environment.Path;

public class CalcKilometer {
    private static String API_KEY = "";
    private static final Logger logger = LoggerFactory.getLogger(CalcKilometer.class);

    public static void main(String[] args) throws Exception {
        CalcKilometer calcKilometer = new CalcKilometer();
        API_KEY = calcKilometer.readApikey();

        String mandAdr = args[0];
        String patientAdr = args[1];
        Distance distanz = calcKilometer.distanzZwischen(mandAdr, patientAdr);
        logger.debug(Math.round(distanz.getKilometer() * 2) + ";" + distanz.getDurationInMinutes() * 2 + ";"
                + distanz.getMeter() + ";" + distanz.getDuration() + ";");
    }

    private String readApikey() {
        List<String> ini = readFileInList(Path.Instance.getProghome() + "/ini/openroute.ini");
        API_KEY = ini.get(0);
        return API_KEY;
    }

    public Distance distanzZwischen(String mandAdr, String patientAdr) throws IOException {
        API_KEY = readApikey();
        Route route = new Route().withStart(mandAdr)
                                 .withEnd(patientAdr);
        return route.getDistanceFromOpenRouteService(API_KEY);
    }



    private List<String> readFileInList(String fileName) {

        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            logger.error(fileName + " konnte nicht gelesen werden", e);
        }
        return lines;
    }
}
