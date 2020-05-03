package terminKalender;

import java.util.Comparator;

public class Kollegen implements Comparable<Kollegen> {
    String Matchcode;
    private String Vorname;
    private String Nachname;
    String Abteilung;
    String Zeigen;
    int Reihe;
    int Position;

    public Kollegen(String m, String n, String v, int r, String a, String z, int p) {
        Matchcode = m;
        Nachname = n;
        Vorname = v;
        Reihe = r;
        Abteilung = a;
        Zeigen = z;
        Position = p;
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