package CommonTools;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZeitFunk {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZeitFunk.class);

    public static long ZeitDifferenzInMinuten(String szeit1, String szeit2) {

        return ChronoUnit.MINUTES.between(LocalTime.parse(szeit1), LocalTime.parse(szeit2));
    }

    public static long MinutenSeitMitternacht(String szeit1) {

        LocalTime until;
        try {
            until = LocalTime.parse(szeit1);
        } catch (DateTimeParseException e) {
            LOGGER.error("could not parse: " + szeit1);
            String[] parts = szeit1.split(":");
            if (parts.length==3) {
                until =  LocalTime.of(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]),Integer.valueOf(parts[2]));
            }
            else {
                throw e;
            }
        }
        return ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, until);

    }

    public static String MinutenZuZeit(long minutes) {
        return LocalTime.of(0, 0)
                        .plusMinutes(minutes)
                        .toString()
                        .concat(":00");

    }

    public static String ZeitPlusMinuten(String zeit, String minuten) {
        LocalTime lt1 = LocalTime.parse(zeit);
        return lt1.plusMinutes(Integer.parseInt(minuten))
                  .toString();
    }

    public static String ZeitMinusMinuten(String zeit, String minuten) {
        LocalTime lt1 = LocalTime.parse(zeit);
        return lt1.minusMinutes(Integer.parseInt(minuten))
                  .toString();

    }

}
