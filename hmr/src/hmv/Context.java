package hmv;

import java.util.EnumSet;

import core.Disziplin;
import core.Patient;
import mandant.Mandant;

public class Context {

   final Mandant mandant;
   final User user;
   final EnumSet<Disziplin> disziplinen;
   final Patient patient;

    public Context(Mandant mandant, User user, EnumSet<Disziplin> disziplinen, Patient patient) {
        super();
        this.mandant = mandant;
        this.user = user;
        this.disziplinen = disziplinen;
        this.patient = patient;
    }

}
