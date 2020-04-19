package org.therapi.reha.patient.neu;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class Patient {

    public String anrede;
    public String titel;
    public   String nachname;
    public    String vorname;
    public    Adresse wohnadresse;
    public   Optional< Adresse> abweichende=Optional.empty();
    public    LocalDate geburtstag;
    public   Telefonnummer privat;
    public   Telefonnummer geschaeft;
    public   Telefonnummer mobil;
    public    Emailadresse email;
    public   Akut akut;
    public   PlanDaten daten;
    public   Optional<Krankenversicherung> kv=Optional.empty();
    public    Optional<Kollege> behandler=Optional.empty();
    public   Optional<Arzt> hauptarzt=Optional.empty();
    public  String abwAnrede;
    public String abwTitel;
    public String abwN_Name;
    public String abwV_Name;
    public Merkmale merkmale =new Merkmale();
    public int patIntern;
    public String anamnese="";
    public String memo="";

    public int db_id;
    public LocalDate anlageDatum;
    public boolean hasAbweichendeAdresse;
    public Optional<Person> vertreter = Optional.empty();
    public int entfernung;


    public Patient() {

    }


    public boolean istHeimbewohner() {
        // TODO Auto-generated method stub
        return false;
    }



    public boolean u18ignorieren() {
      //  ChronoUnit.YEARS.between(temporal1Inclusive, temporal2Exclusive)
        // TODO Auto-generated method stub
        return false;
    }

}
