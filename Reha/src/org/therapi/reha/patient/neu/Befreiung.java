package org.therapi.reha.patient.neu;

import java.time.LocalDate;

public class Befreiung {
    public Befreiung(LocalDate von, LocalDate befBis) {
        super();
        this.von = von;
        this.bis = befBis;
    }
    LocalDate von;
    LocalDate bis;

}
