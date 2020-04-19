package org.therapi.reha.patient.neu;

import java.util.Objects;

public class PlanDaten {

    public PlanDaten(String termine1, String termine2) {
        moeglicheTermine1 = termine1;
        moeglicheTermine2 = termine2;
    }
    String moeglicheTermine1;
    String moeglicheTermine2;
    public String getMoeglicheTermine1() {
        return moeglicheTermine1;
    }
    public String getMoeglicheTermine2() {
        return moeglicheTermine2;
    }
    @Override
    public int hashCode() {
        return Objects.hash(moeglicheTermine1, moeglicheTermine2);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlanDaten other = (PlanDaten) obj;
        return Objects.equals(moeglicheTermine1, other.moeglicheTermine1)
                && Objects.equals(moeglicheTermine2, other.moeglicheTermine2);
    }
}
