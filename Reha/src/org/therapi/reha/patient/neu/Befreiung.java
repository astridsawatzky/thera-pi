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
    public LocalDate getVon() {
        return von;
    }
    public LocalDate getBis() {
        return bis;
    }
public boolean istbefreit(LocalDate wann) {

   return  wann.isAfter(von) && wann.isBefore(bis);
}
}
