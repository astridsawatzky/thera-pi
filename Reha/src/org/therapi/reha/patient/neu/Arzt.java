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
    Emailadresse email1;
    Emailadresse email2;
    String bsnr;

}
/**
Die Betriebsstättennummer ist neunstellig. Die ersten beiden Ziffern stellen den
KV-Landes- oder Bezirksstellenschlüssel gemäß Anlage 1 zu dieser Richtlinie
dar. Die Ziffern drei bis neun werden von der KV vergeben. Dabei sind die Ziffern
drei bis sieben so zu wählen, dass anhand der ersten sieben Stellen die Betriebsstätte eindeutig zu identifizieren ist.
**/
