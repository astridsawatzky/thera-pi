package hmv;

import java.util.EnumSet;

import core.Disziplin;
import mandant.Mandant;

public class Context {

   final Mandant mandant;
   final User user;
   final EnumSet<Disziplin> disziplinen;

    public Context(Mandant mandant, User user, EnumSet<Disziplin> disziplinen) {
        super();
        this.mandant = mandant;
        this.user = user;
        this.disziplinen = disziplinen;
    }

}
