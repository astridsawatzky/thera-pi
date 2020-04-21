package terminKalender;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public class Termin {

    public final String bezeichnung;
    public final String notiz;
    public final LocalTime start;
    public final Duration dauer;
    public final LocalTime ende;


    public Termin(String bezeichnung, String notiz, LocalTime start, Duration dauer, LocalTime ende) {

        this.bezeichnung = bezeichnung;
        this.notiz = notiz;
        this.start = start;
        this.dauer = dauer;
        this.ende = ende;
    }

    public static final Termin EMPTY = new Termin(null, null, null, Duration.ofMinutes(0), null);

    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung, dauer, ende, notiz, start);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Termin)) {
            return false;
        }
        Termin other = (Termin) obj;
        return Objects.equals(bezeichnung, other.bezeichnung) && Objects.equals(dauer, other.dauer)
                && Objects.equals(ende, other.ende) && Objects.equals(notiz, other.notiz)
                && Objects.equals(start, other.start);
    }

}
