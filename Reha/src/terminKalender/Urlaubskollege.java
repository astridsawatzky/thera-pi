package terminKalender;

final public class Urlaubskollege {
    String matchcode;
    String nachname;
    String nichtzeig;
    String kalenderZeile;
    public Urlaubskollege(String matchcode, String nachname, String nichtzeig, String kalenderZeile) {
        super();
        this.matchcode = matchcode;
        this.nachname = nachname;
        this.nichtzeig = nichtzeig;
        this.kalenderZeile = kalenderZeile;
    }
    public String getMatchcode() {
        return matchcode;
    }
    public String getKalenderZeile() {
        return kalenderZeile;
    }



}