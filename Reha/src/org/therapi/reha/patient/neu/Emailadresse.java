package org.therapi.reha.patient.neu;

public class Emailadresse {
    public static final Emailadresse EMPTY = new Emailadresse("");

    public Emailadresse(String emailA) {
        this.adresse=emailA;
    }

    String adresse;

    public String getAdresse() {
        return adresse;
    }

}
