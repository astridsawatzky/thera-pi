package mitarbeiter;

import java.time.LocalDate;
import java.util.Comparator;


public class Mitarbeiter implements Comparable<Mitarbeiter> {



    String    anrede="" ;
    String    vorname ="";
    String    nachname="";
    String    strasse="";
    String    plz="";
    String    ort="";
    String    telefon1="";
    String    telfon2="";
    LocalDate geboren;
    String    matchcode ="./.";
    String    ztext="";
    int    kal_teil;
    int    pers_nr;
    double   astunden;
    boolean    nicht_zeig;// enum('t','f') null default null,
    String    abteilung="";
    int    deftakt;
    int    kalzeile;
    int    id =0;
    boolean isdirty=true;
    public String getAnrede() {
        return anrede;
    }
    public void setAnrede(String anrede) {
        this.anrede = anrede;
        isdirty=true;
    }
    public String getVorname() {
        return vorname;
    }
    public void setVorname(String vorname) {
        this.vorname = vorname;
        isdirty=true;
    }
    public String getNachname() {
        return nachname;
    }
    public void setNachname(String nachname) {
        this.nachname = nachname;
        isdirty=true;
    }
    public String getStrasse() {
        return strasse;
    }
    public void setStrasse(String strasse) {
        this.strasse = strasse;
        isdirty=true;
    }
    public String getPlz() {
        return plz;
    }
    public void setPlz(String plz) {
        this.plz = plz;
        isdirty=true;
    }
    public String getOrt() {
        return ort;
    }
    public void setOrt(String ort) {
        this.ort = ort;
        isdirty=true;
    }
    public String getTelefon1() {
        return telefon1;
    }
    public void setTelefon1(String telefon1) {
        this.telefon1 = telefon1;
        isdirty=true;
    }
    public String getTelfon2() {
        return telfon2;
    }
    public void setTelfon2(String telfon2) {
        this.telfon2 = telfon2;
        isdirty=true;
    }
    public LocalDate getGeboren() {
        return geboren;
    }
    public void setGeboren(LocalDate geboren) {
        this.geboren = geboren;
        isdirty=true;
    }
    public String getMatchcode() {
        return matchcode;
    }
    public void setMatchcode(String matchcode) {
        this.matchcode = matchcode;
        isdirty=true;
    }
    public String getZtext() {
        return ztext;
    }
    public void setZtext(String ztext) {
        this.ztext = ztext;
        isdirty=true;
    }
    public int getKal_teil() {
        return kal_teil;
    }
    public void setKal_teil(int kal_teil) {
        this.kal_teil = kal_teil;
        isdirty=true;
    }
    public int getPers_nr() {
        return pers_nr;
    }
    public void setPers_nr(int pers_nr) {
        this.pers_nr = pers_nr;
        isdirty=true;
    }
    public double getAstunden() {
        return astunden;
    }
    public void setAstunden(double astunden) {
        this.astunden = astunden;
        isdirty=true;
    }
    public boolean isNicht_zeig() {
        return nicht_zeig;
    }
    public void setNicht_zeig(boolean nicht_zeig) {
        this.nicht_zeig = nicht_zeig;
        isdirty=true;
    }
    public String getAbteilung() {
        return abteilung;
    }
    public void setAbteilung(String abteilung) {
        this.abteilung = abteilung;
        isdirty=true;
    }
    public int getDeftakt() {
        return deftakt;
    }
    public void setDeftakt(int deftakt) {
        this.deftakt = deftakt;
        isdirty=true;
    }
    public int getKalzeile() {
        return kalzeile;
    }
    public void setKalzeile(int kalzeile) {
        this.kalzeile = kalzeile;
        isdirty=true;
    }
    public boolean isIsdirty() {
        return isdirty;
    }

    void clean() {
        if(!isNew())
        isdirty=false;
    }
    public boolean isNew() {
        return id == 0;
    }


    private static final Comparator<Mitarbeiter> compareByMatchcode = Comparator.comparing(m -> m.matchcode.toLowerCase());
    private static final Comparator<Mitarbeiter> compareByAge = Comparator.comparing(m -> m.kalzeile);

    @Override
    public int compareTo(Mitarbeiter other) {

        return compareByMatchcode.thenComparing(compareByAge)
                                 .compare(this, other);

    }
    @Override
    public String toString() {
        return "Mitarbeiter [anrede=" + anrede + ", vorname=" + vorname + ", nachname=" + nachname + ", strasse="
                + strasse + ", plz=" + plz + ", ort=" + ort + ", telefon1=" + telefon1 + ", telfon2=" + telfon2
                + ", geboren=" + geboren + ", matchcode=" + matchcode + ", ztext=" + ztext + ", kal_teil=" + kal_teil
                + ", pers_nr=" + pers_nr + ", astunden=" + astunden + ", nicht_zeig=" + nicht_zeig + ", abteilung="
                + abteilung + ", deftakt=" + deftakt + ", kalzeile=" + kalzeile + ", id=" + id + ", isdirty=" + isdirty
                + "]";
    }


}
