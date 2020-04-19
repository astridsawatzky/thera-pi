package org.therapi.reha.patient.neu;

public class Arzt {

    int id;

    String anrede;
    String titel;
    String vorname;
    String nachname;
    Adresse praxis;
    LANR arztnummer;
    String facharzt;
    Telefonnummer telefon;
    Telefonnummer fax;
    String klinik;
    String mtext;
    Emailadresse email1 = Emailadresse.EMPTY;
    Emailadresse email2= Emailadresse.EMPTY;
    String bsnr;
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
    public Telefonnummer getTelefon() {
        return telefon;
    }
    public Telefonnummer getFax() {
        return fax;
    }
    public String getKlinik() {
        return klinik;
    }
    public String getMtext() {
        return mtext;
    }
    public Emailadresse getEmail1() {
        return email1;
    }
    public Emailadresse getEmail2() {
        return email2;
    }
    public String getBsnr() {
        return bsnr;
    }

}
/**
Die Betriebsstättennummer ist neunstellig. Die ersten beiden Ziffern stellen den
KV-Landes- oder Bezirksstellenschlüssel gemäß Anlage 1 zu dieser Richtlinie
dar. Die Ziffern drei bis neun werden von der KV vergeben. Dabei sind die Ziffern
drei bis sieben so zu wählen, dass anhand der ersten sieben Stellen die Betriebsstätte eindeutig zu identifizieren ist.
**/
