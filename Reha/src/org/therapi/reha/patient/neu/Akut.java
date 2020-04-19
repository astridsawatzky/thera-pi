package org.therapi.reha.patient.neu;

import java.time.LocalDate;

public class Akut {
    public Akut(LocalDate akutSeit , LocalDate akutBis) {
        seit = akutSeit;
        bis = akutBis;
    }
    LocalDate seit;
    LocalDate bis;

    public boolean isAkut() {
        try {
            return bis.isAfter(LocalDate.now());
        } catch (Exception e) {
           return false;
        }

    }


}
