package org.therapi.reha.patient.neu;

public class Hausbesuch {
    public static final Hausbesuch EMPTY = new Hausbesuch(0.0);
    double entfernung;

    public Hausbesuch(double entfernung) {
        this.entfernung = entfernung;
    }

    public int getEntfernung() {
        return (int) entfernung;
    }


}
