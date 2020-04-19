package org.therapi.reha.patient.neu;

import java.util.Objects;

public class Arzt {

    int id;

    String anrede;
    String titel;
    String vorname;
    String nachname;
    Adresse praxis;
    LANR arztnummer;
    String facharzt;
   String telefon;
    String fax ;
    String klinik;
    String mtext;
    String email1;
    String email2;
    String bsnr;

    public String matchcode;
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
        return Objects.hash(anrede, arztnummer, bsnr, email1, email2, facharzt, fax, id, klinik, matchcode, mtext,
                nachname, praxis, telefon, titel, vorname);
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
        return Objects.equals(anrede, other.anrede) && Objects.equals(arztnummer, other.arztnummer)
                && Objects.equals(bsnr, other.bsnr) && Objects.equals(email1, other.email1)
                && Objects.equals(email2, other.email2) && Objects.equals(facharzt, other.facharzt)
                && Objects.equals(fax, other.fax) && id == other.id && Objects.equals(klinik, other.klinik)
                && Objects.equals(matchcode, other.matchcode) && Objects.equals(mtext, other.mtext)
                && Objects.equals(nachname, other.nachname) && Objects.equals(praxis, other.praxis)
                && Objects.equals(telefon, other.telefon) && Objects.equals(titel, other.titel)
                && Objects.equals(vorname, other.vorname);
    }
    @Override
    public String toString() {
        return "Arzt [id=" + id + ", anrede=" + anrede + ", titel=" + titel + ", vorname=" + vorname + ", nachname="
                + nachname + ", praxis=" + praxis + ", arztnummer=" + arztnummer + ", facharzt=" + facharzt
                + ", telefon=" + telefon + ", fax=" + fax + ", klinik=" + klinik + ", mtext=" + mtext + ", email1="
                + email1 + ", email2=" + email2 + ", bsnr=" + bsnr + ", matchcode=" + matchcode + "]";
    }

}
/**
Die Betriebsstättennummer ist neunstellig. Die ersten beiden Ziffern stellen den
KV-Landes- oder Bezirksstellenschlüssel gemäß Anlage 1 zu dieser Richtlinie
dar. Die Ziffern drei bis neun werden von der KV vergeben. Dabei sind die Ziffern
drei bis sieben so zu wählen, dass anhand der ersten sieben Stellen die Betriebsstätte eindeutig zu identifizieren ist.
**/
