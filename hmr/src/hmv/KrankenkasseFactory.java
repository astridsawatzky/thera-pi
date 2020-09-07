package hmv;

import core.Krankenkasse;
import mandant.IK;

public class KrankenkasseFactory {
    Krankenkasse krankenkasse = new Krankenkasse("000000000");

    boolean isIkSet = false;

    public KrankenkasseFactory withIk(IK ik) {
        krankenkasse.setIk(ik);
        isIkSet = true;
        return this;
    }

    public KrankenkasseFactory withName(String name) {
        krankenkasse.setName(name);
        return this;
    }

    public KrankenkasseFactory withID(int id) {
        krankenkasse.setId(id);
        return this;
    }

    public Krankenkasse build() {
        if (isIkSet) {

            return krankenkasse;
        } else {
            throw new IllegalStateException("ik was not set");
        }

    }

}
