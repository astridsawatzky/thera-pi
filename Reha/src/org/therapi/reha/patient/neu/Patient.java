package org.therapi.reha.patient.neu;

import java.time.LocalDate;

public class Patient {

    String anrede;
    String titel;
    String nachname;
    String vorname;
    Adresse wohnadresse;
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


    public Patient() {

    }

}
