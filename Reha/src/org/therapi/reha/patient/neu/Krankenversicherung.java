package org.therapi.reha.patient.neu;

import java.util.Optional;

public class Krankenversicherung {
    Optional<Befreiung> befreit =Optional.empty();
    Krankenkasse kk;
    String status;

    String versicherungsnummer;
    public Krankenversicherung(Krankenkasse kk, String versicherungsnummer, String status, Befreiung befreit) {
        super();
        this.kk = kk;
        this.versicherungsnummer = versicherungsnummer;
        this.status = status;
        this.befreit = Optional.ofNullable(befreit) ;
    }
    public Optional<Befreiung> getBefreit() {
        return befreit;
    }
    public Krankenkasse getKk() {
        return kk;
    }
    public String getStatus() {
        return status;
    }
    public String getVersicherungsnummer() {
        return versicherungsnummer;
    }

}
