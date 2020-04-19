package org.therapi.reha.patient.neu;

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

}
