package org.therapi.reha.patient.neu;

import java.time.LocalDate;
import java.util.Objects;

public class Akut {
   public static final Akut EMPTY = new Akut() {
        @Override
        public boolean isAkut() {
          return false;
        };
    };

    public Akut(LocalDate akutSeit , LocalDate akutBis) {
        seit = akutSeit;
        bis = akutBis;
    }


    private Akut() {
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

    @Override
    public int hashCode() {
        return Objects.hash(bis, seit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Akut other = (Akut) obj;
        return Objects.equals(bis, other.bis) && Objects.equals(seit, other.seit);
    }


}
