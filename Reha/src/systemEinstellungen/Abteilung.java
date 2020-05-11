package systemEinstellungen;

import java.util.Objects;

public class Abteilung {
    @Override
    public String toString() {
        return bezeichnung;
    }

    public Abteilung(String bezeichnung) {
        this.bezeichnung = bezeichnung.trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(bezeichnung);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Abteilung))
            return false;
        Abteilung other = (Abteilung) obj;
        return Objects.equals(bezeichnung, other.bezeichnung);
    }

    String bezeichnung;

}
