package org.therapi.reha.patient.neu;

import java.util.Objects;
import java.util.Optional;

public class Adresse {
    public final Optional<String> zusatz;
    public final String strasse;
    public final String plz;
    public final String ort;

    public Adresse(String zusatz, String strasse, String plz, String ort) {
        super();
        this.zusatz = Optional.ofNullable(zusatz);
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ort, plz, strasse, zusatz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Adresse other = (Adresse) obj;
        return Objects.equals(ort, other.ort) && Objects.equals(plz, other.plz)
                && Objects.equals(strasse, other.strasse) && Objects.equals(zusatz, other.zusatz);
    }



}
