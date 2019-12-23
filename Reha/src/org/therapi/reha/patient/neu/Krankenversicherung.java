package org.therapi.reha.patient.neu;

public class Krankenversicherung {
    Krankenkasse kk;
    String versicherungsnummer;
    public Krankenversicherung(Krankenkasse kk, String versicherungsnummer, String status, Befreiung befreit) {
        super();
        this.kk = kk;
        this.versicherungsnummer = versicherungsnummer;
        this.status = status;
        this.befreit = befreit;
    }

    String status;
    Befreiung befreit;

}
