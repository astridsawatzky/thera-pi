package hmv;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import core.Arzt;
import core.Disziplin;
import core.Krankenkasse;
import core.Krankenversicherung;
import core.Patient;
import mandant.Mandant;

public class Hmv {
    User angelegtvon;
    //mandant
    Mandant mandant;
    Disziplin disziplin;
    //Patient
    Patient patient;
    Arzt arzt;
    Krankenversicherung kv;

    //HMV
    LocalDate ausstellungsdatum;
    Period maxBisStart;

    Diagnose diag;


    Behandlung beh;

    public Hmv(Context context) {
        mandant = context.mandant;
        patient = context.patient;
        arzt = patient.hauptarzt.get();
        kv = patient.kv;
        angelegtvon = context.user;
    }



    public Patient patient() {
        return null;
    }





}
