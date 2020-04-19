package org.therapi.reha.patient.neu;

public class Merkmale {


    private boolean eins;
    private boolean zwei;
    private boolean drei;
    private boolean vier;
    private boolean fuenf;
    private boolean sechs;
    public Merkmale(boolean merk1, boolean merk2, boolean merk3, boolean merk4, boolean merk5, boolean merk6) {
        eins = merk1;
        zwei = merk2;
        drei = merk3;
        vier = merk4;
        fuenf = merk5;
        sechs = merk6;
    }

    public Merkmale() {
    }
    public boolean eins() {
        return eins;
    }
    public boolean zwei() {
        return zwei;
    }
    public boolean drei() {
        return drei;
    }
    public boolean vier() {
        return vier;
    }
    public boolean fuenf() {
        return fuenf;
    }
    public boolean sechs() {
        return sechs;
    }


}
