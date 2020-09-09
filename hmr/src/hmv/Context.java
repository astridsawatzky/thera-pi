package hmv;

import java.util.EnumSet;

import core.Disziplin;
import core.Patient;
import mandant.Mandant;
import specs.Contracts;

public class Context {

    final Mandant mandant;
    final User user;
    final EnumSet<Disziplin> disziplinen;
    final Patient patient;

    public Context(Mandant mandant, User user, EnumSet<Disziplin> disziplinen, Patient patient) {
        super();
        Contracts.require(mandant != null && user != null && disziplinen != null && patient != null

                , "no null values!");
        this.mandant = mandant;
        this.user = user;
        this.disziplinen = disziplinen;
        this.patient = patient;
    }

}
