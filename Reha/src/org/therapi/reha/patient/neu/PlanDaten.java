package org.therapi.reha.patient.neu;

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
}
