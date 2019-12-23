package org.therapi.reha.patient.neu;

import mandant.IK;

public class Krankenkasse {
    public Krankenkasse(String kassenid) {
        ik= new IK(kassenid);
    }
    IK ik;
    String name;

}
