package umfeld;

import mandant.Mandant;

public class Betriebsumfeld {
    public static Betriebsumfeld umfeld;

    private Mandant mandant;

    public Mandant getMandant() {
        return mandant;
    }

    public Betriebsumfeld(Mandant mandant) {
        this.mandant = mandant;
        umfeld = this;
    }

    public static String getAktIK() {
        return umfeld.mandant.ikDigitString();
    }

    public static String getAktMandant() {
        return umfeld.mandant.name();
    }
}
