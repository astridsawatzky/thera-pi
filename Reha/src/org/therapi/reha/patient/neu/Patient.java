package org.therapi.reha.patient.neu;

import java.time.LocalDate;
import java.util.List;

public class Patient {

    String anrede;
    String titel;
    String nachname;
    String vorname;
    Adresse wohnadresse;
    Adresse abweichende;
    LocalDate geburtstag;
    Telefonnummer privat;
    Telefonnummer geschaeft;
    Telefonnummer mobil;
    Emailadresse email;
    Akut akut;
    PlanDaten daten;
    Krankenversicherung kv;
    Kollege behandler;
    Arzt hauptarzt;
    String abwAnrede;
    public String abwTitel;
    public String abwN_Name;
    public String abwV_Name;
    public Hausbesuch hb;
    public List<Merkmal> kennzeichen;
    public Merkmale merkmale;
    public int patIntern;
    public String anamnese;
    public String memo;

    public int db_id;
    public LocalDate anlageDatum;


    public Patient() {

    }

}
