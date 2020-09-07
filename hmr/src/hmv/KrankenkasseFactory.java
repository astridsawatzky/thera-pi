package hmv;

import core.Krankenkasse;
import mandant.IK;

public class KrankenkasseFactory {
	Krankenkasse krankenkasse = new Krankenkasse();
	
	public KrankenkasseFactory withIk (IK ik) {
		krankenkasse.setIk(ik);
        return this;
	}
	
	public KrankenkasseFactory withName (String name) {
		krankenkasse.setName(name);
        return this;
	}
	
	public KrankenkasseFactory withID (int id) {
		krankenkasse.setId(id);
        return this;
	}

}
