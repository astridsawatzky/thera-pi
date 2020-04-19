package org.therapi.reha.patient.neu;

import java.util.Objects;

public class Kollege {
    public Kollege(String therapeut) {
        matchcode=therapeut;
    }
    String nachname;
    String vorname;
    String matchcode;
    public String getNachname() {
        return nachname;
    }
    public String getVorname() {
        return vorname;
    }
    public String getMatchcode() {
        return matchcode;
    }
    @Override
    public int hashCode() {
        return Objects.hash(matchcode, nachname, vorname);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Kollege other = (Kollege) obj;
        return Objects.equals(matchcode, other.matchcode) && Objects.equals(nachname, other.nachname)
                && Objects.equals(vorname, other.vorname);
    }
    @Override
    public String toString() {
        return "Kollege [nachname=" + nachname + ", vorname=" + vorname + ", matchcode=" + matchcode + "]";
    }

}
