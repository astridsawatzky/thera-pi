package terminKalender;

import java.util.Comparator;

import mitarbeiter.Mitarbeiter;

public class Kollegen implements Comparable<Kollegen> {
    String Matchcode;
    private String Nachname;
    String Abteilung;
    String Zeigen;
    int Reihe;
    static final Kollegen NULL_KOLLEGE = new Kollegen("./.", "", 0, "", "F");

    public Kollegen(String m, String n, int r, String a, String z) {
        Matchcode = m;
        Nachname = n;
        Reihe = r;
        Abteilung = a;
        Zeigen = z;
    }

    private Kollegen() {
        //for converter
    }

    public static final Kollegen of(Mitarbeiter ma) {
        Kollegen neuerKollege = new Kollegen();
        neuerKollege.Matchcode=ma.getMatchcode();
        neuerKollege.Nachname=ma.getNachname();
        neuerKollege.Abteilung =ma.getAbteilung();
        neuerKollege.Zeigen=ma.isNicht_zeig()?"F":"T";
        neuerKollege.Reihe=ma.getKalzeile();

        return neuerKollege;

    }

    private static final Comparator<Kollegen> compareByMatchcode = Comparator.comparing(k -> k.Matchcode);
    private static final Comparator<Kollegen> compareByAge = Comparator.comparing(k -> k.Reihe);

    @Override
    public int compareTo(Kollegen o) {

        return compareByMatchcode.thenComparing(compareByAge)
                                 .compare(this, o);

    }

    public String getMatchcode() {
        return Matchcode;
    }

    public String getKalenderZeile() {
      return  String.format("%02d", Reihe);
    }
}