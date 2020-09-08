package hmv;

import java.time.LocalDate;
import java.util.Optional;

import core.Adresse;
import core.Arzt;
import core.Befreiung;
import core.Krankenkasse;
import core.Krankenversicherung;
import core.LANR;
import core.Patient;
import core.VersichertenStatus;
import mandant.IK;

public class CoreTestDataFactory {

    public static Arzt createArztEisenbart() {
        return new ArztFactory().withNachname("Eisenbart")
                                          .withArztnummer(new LANR("081500000"))
                                          .withBsnr("000008150")
                                          .build();
    }

    public static Patient createPatientSimonLant() {
        Patient patient = new Patient(new Adresse("", "hohle gasse 5", "12345", "Baumburg"));
        patient.nachname = "Lant";
        patient.vorname = "Simon";
        Krankenkasse kk = new KrankenkasseFactory().withIk(new IK("999999999"))
                                                   .withName("donotpay")
                                                   .build();
        Befreiung befreit = new Befreiung(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));

        patient.kv = new Krankenversicherung(Optional.of(kk), "0815", VersichertenStatus.RENTNER, befreit);

        patient.geburtstag = LocalDate.of(1904, 2, 29);

        patient.hauptarzt = Optional.of(CoreTestDataFactory.createArztEisenbart());
        return patient;
    }



}
