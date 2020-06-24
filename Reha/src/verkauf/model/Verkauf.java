package verkauf.model;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Verkauf {
    private static final int mwStVoll = 19;
    private static final int mwStVermindert = 7;
    private double summeMwStVermindert = 0;
    private double summeMwStVoll = 0;
    private double  betragBrutto = 0;
    private double  rabatt = 0;
    // private Date datum;
    private ArrayList<ArtikelVerkauf> artikelVerkaufList;
    DecimalFormat df = new DecimalFormat("0.00");

    public Verkauf() {
        this.artikelVerkaufList = new ArrayList<ArtikelVerkauf>();
    }

    public void fuegeArtikelHinzu(ArtikelVerkauf posten) {
        this.artikelVerkaufList.add(posten);
        posten.setPosition(this.artikelVerkaufList.lastIndexOf(posten));

        aktualisiereSummen();

    }

    public void gewaehreRabatt(Double rabatt) {
        this.rabatt = rabatt;
        aktualisiereSummen();
    }

    private void rabattiereWerte() {
        this.betragBrutto = this.betragBrutto * (1 - (this.rabatt / 100));
        this.summeMwStVermindert = this.summeMwStVermindert * (1 - (this.rabatt / 100));
        this.summeMwStVoll = this.summeMwStVoll * (1 - (this.rabatt / 100));
    }

    public void loescheArtikel(int n) {

        this.artikelVerkaufList.remove(n);

        aktualisiereSummen();
    }
    private void aktualisiereSummen() {
        summeMwStVermindert=0;
        summeMwStVoll=0;
        betragBrutto=0;
        for (ArtikelVerkauf artikelVerkauf : artikelVerkaufList) {
            double gesamtPositionsBrutto = artikelVerkauf.getPreis() * artikelVerkauf.getAnzahl() ;
            betragBrutto += gesamtPositionsBrutto;
            if(artikelVerkauf.hatVolleMwSt()) {
                summeMwStVoll+= gesamtPositionsBrutto * mwStVoll / 100d;
            } else if (artikelVerkauf.hatVerminderteMwSt()) {
                summeMwStVermindert += gesamtPositionsBrutto * mwStVermindert /100d;
            }

        }
        rabattiereWerte();

    }


    public double getBetrag7() {
        return this.summeMwStVermindert;
    }

    public double getBetrag19() {
        return this.summeMwStVoll;
    }

    public double getBetragBrutto() {
        return this.betragBrutto;
    }

    public double getRabatt() {
        return this.rabatt;
    }

    public int getAnzahlPositionen() {
        return artikelVerkaufList.size();
    }

    public String[][] liefereTabDaten() {
        ArtikelVerkauf[] positionen = new ArtikelVerkauf[this.artikelVerkaufList.size()];
        positionen = this.artikelVerkaufList.toArray(positionen);
        String[][] returns = new String[positionen.length][8];
        for (int i = 0; i < positionen.length; i++) {
            returns[i][0] = String.valueOf(positionen[i].getEan());
            returns[i][1] = positionen[i].getBeschreibung();
            returns[i][2] = df.format(positionen[i].getPreis());
            returns[i][3] = df.format(positionen[i].getAnzahl());
            returns[i][4] = df.format(positionen[i].getRabatt());
            returns[i][5] = df.format(positionen[i].getPreis() * positionen[i].getAnzahl());
            returns[i][6] = df.format(positionen[i].getMwst());
            returns[i][7] = String.valueOf(positionen[i].getPosition());
        }

        return returns;
    }

    public ArtikelVerkauf[] liefereArtikel() {
        return  this.artikelVerkaufList.toArray(new ArtikelVerkauf[0]);
    }

    public void fuehreVerkaufdurch(int patid, String vnummer) {
        for (Artikel artikelVerkauf : artikelVerkaufList) {
            artikelVerkauf.verkaufeArtikel(0, vnummer, 0d, patid);
        }

    }

    public ArtikelVerkauf lieferePosition(int i) {
        ArtikelVerkauf a = this.artikelVerkaufList.get(i);
        this.loescheArtikel(i);
        return a;
    }
}
