package terminKalender;

import java.util.Comparator;

class Kollegen implements Comparable<Kollegen> {
    String Matchcode, Vorname, Nachname, Abteilung, Zeigen;
    int Reihe, Position;

    public Kollegen(String m, String n, String v, int r, String a, String z, int p) {
        Matchcode = m;
        Nachname = n;
        Vorname = v;
        Reihe = r;
        Abteilung = a;
        Zeigen = z;
        Position = p;
    }

 public static final   Comparator<Kollegen> compareByMatchcode = Comparator.comparing(k -> k.Matchcode);
 public static final   Comparator<Kollegen> compareByAge = Comparator.comparing(k -> k.Reihe);

    @Override
    public int compareTo(Kollegen o) {
        
        return compareByMatchcode.thenComparing(compareByAge).compare(this, o);
        
    }
}