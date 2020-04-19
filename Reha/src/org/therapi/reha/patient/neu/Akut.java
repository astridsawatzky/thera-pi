package org.therapi.reha.patient.neu;

import java.time.LocalDate;
import java.util.Objects;

public class Akut {

private boolean akutPat;

public final  LocalDate seit;
public final  LocalDate bis;

    Akut(boolean akutPat, LocalDate akutSeit , LocalDate akutBis) {
        seit = akutSeit;
        bis = akutBis;
        this.akutPat = akutPat;
    }




    public boolean isAkut() {
       return akutPat;

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
