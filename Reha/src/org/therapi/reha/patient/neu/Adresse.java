package org.therapi.reha.patient.neu;

import java.util.Optional;

public class Adresse {
    public final Optional<String> zusatz;
    public final String strasse;
    public final PLZ plz;
    public final String ort;

    public Adresse(String zusatz, String strasse, PLZ plz, String ort) {
        super();
        this.zusatz = Optional.ofNullable(zusatz);
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
    }

}
