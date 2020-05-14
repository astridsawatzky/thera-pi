package terminKalender;

import java.util.Comparator;

import mitarbeiter.Mitarbeiter;

public class Kollegen implements Comparable<Kollegen> {
    private Mitarbeiter ma;
    public Mitarbeiter getMa() {
        return ma;
    }


    static final Mitarbeiter NULL_MITARBEITER = new Mitarbeiter();
    static {
        NULL_MITARBEITER.setMatchcode("./.");
        NULL_MITARBEITER.setKalzeile(0);
        NULL_MITARBEITER.setAbteilung("");
        NULL_MITARBEITER.setNicht_zeig(false);
    }
    static final Kollegen NULL_KOLLEGE = Kollegen.of(NULL_MITARBEITER);



    private Kollegen(Mitarbeiter mitarbeiter) {
        ma = mitarbeiter;
    }

    static final Kollegen of(Mitarbeiter ma) {
        Kollegen neuerKollege = new Kollegen(ma);

        return neuerKollege;

    }

    private static final Comparator<Kollegen> compareByMitarbeiter = Comparator.comparing(Kollegen::getMa);

    @Override
    public int compareTo(Kollegen other) {

       return  compareByMitarbeiter.compare(this,other);

    }

    public String getMatchcode() {
        return ma.getMatchcode();
    }

    public String getKalenderZeile() {
      return  String.format("%02d", ma.getKalzeile());
    }

    String getAbteilung() {
        return ma.getAbteilung();
    }

    String getZeigen() {
        return ma.isNicht_zeig()?"T":"F";
    }

    int getReihe() {
        return ma.getKalzeile();
    }

}