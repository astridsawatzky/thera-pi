package core;

import java.util.Objects;

public class Arzt {

    private int id;

    private String anrede;
    private String titel;
    private String vorname;
    private String nachname;
    private Adresse praxis;
    private LANR arztnummer;
    private String facharzt;
   private String telefon;
    private String fax ;
    private String klinik;
    private String mtext;
    private String email1;
    private String email2;
    private String bsnr;

    private String matchcode;
    public int getId() {
        return id;
    }
    public String getAnrede() {
        return anrede;
    }
    public String getTitel() {
        return titel;
    }
    public String getVorname() {
        return vorname;
    }
    public String getNachname() {
        return nachname;
    }
    public Adresse getPraxis() {
        return praxis;
    }
    public LANR getArztnummer() {
        return arztnummer;
    }
    public String getFacharzt() {
        return facharzt;
    }
    public String getTelefon() {
        return telefon;
    }
    public String getFax() {
        return fax;
    }
    public String getKlinik() {
        return klinik;
    }
    public String getMtext() {
        return mtext;
    }
    public String getEmail1() {
        return email1;
    }
    public String getEmail2() {
        return email2;
    }
    public String getBsnr() {
        return bsnr;
    }
    @Override
    public int hashCode() {
        return Objects.hash(getAnrede(), getArztnummer(), getBsnr(), getEmail1(), getEmail2(), getFacharzt(), getFax(), getId(), getKlinik(), getMatchcode(), getMtext(),
                getNachname(), getPraxis(), getTelefon(), getTitel(), getVorname());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Arzt other = (Arzt) obj;
        return Objects.equals(getAnrede(), other.getAnrede()) && Objects.equals(getArztnummer(), other.getArztnummer())
                && Objects.equals(getBsnr(), other.getBsnr()) && Objects.equals(getEmail1(), other.getEmail1())
                && Objects.equals(getEmail2(), other.getEmail2()) && Objects.equals(getFacharzt(), other.getFacharzt())
                && Objects.equals(getFax(), other.getFax()) && getId() == other.getId() && Objects.equals(getKlinik(), other.getKlinik())
                && Objects.equals(getMatchcode(), other.getMatchcode()) && Objects.equals(getMtext(), other.getMtext())
                && Objects.equals(getNachname(), other.getNachname()) && Objects.equals(getPraxis(), other.getPraxis())
                && Objects.equals(getTelefon(), other.getTelefon()) && Objects.equals(getTitel(), other.getTitel())
                && Objects.equals(getVorname(), other.getVorname());
    }
    @Override
    public String toString() {
        return "Arzt [id=" + getId() + ", anrede=" + getAnrede() + ", titel=" + getTitel() + ", vorname=" + getVorname() + ", nachname="
                + getNachname() + ", praxis=" + getPraxis() + ", arztnummer=" + getArztnummer() + ", facharzt=" + getFacharzt()
                + ", telefon=" + getTelefon() + ", fax=" + getFax() + ", klinik=" + getKlinik() + ", mtext=" + getMtext() + ", email1="
                + getEmail1() + ", email2=" + getEmail2() + ", bsnr=" + getBsnr() + ", matchcode=" + getMatchcode() + "]";
    }
    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }
    public void setPraxis(Adresse praxis) {
        this.praxis = praxis;
    }
    public void setFacharzt(String facharzt) {
        this.facharzt = facharzt;
    }
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public void setArztnummer(LANR arztnummer) {
        this.arztnummer = arztnummer;
    }
    public void setKlinik(String klinik) {
        this.klinik = klinik;
    }
    public void setMtext(String mtext) {
        this.mtext = mtext;
    }
    public void setEmail1(String email1) {
        this.email1 = email1;
    }
    public void setEmail2(String email2) {
        this.email2 = email2;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setBsnr(String bsnr) {
        this.bsnr = bsnr;
    }
    public String getMatchcode() {
        return matchcode;
    }
    public void setMatchcode(String matchcode) {
        this.matchcode = matchcode;
    }

}
/**
Die Betriebsstättennummer ist neunstellig. Die ersten beiden Ziffern stellen den
KV-Landes- oder Bezirksstellenschlüssel gemäß Anlage 1 zu dieser Richtlinie
dar. Die Ziffern drei bis neun werden von der KV vergeben. Dabei sind die Ziffern
drei bis sieben so zu wählen, dass anhand der ersten sieben Stellen die Betriebsstätte eindeutig zu identifizieren ist.
**/
