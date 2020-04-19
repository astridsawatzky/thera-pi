package org.therapi.reha.patient.neu;

import mandant.IK;

public class Krankenkasse {
    public Krankenkasse(String kassenid) {
        ik= new IK(kassenid);
    }
    IK ik;
    String name;
    int id;
    public IK getIk() {
        return ik;
    }
    public void setIk(IK ik) {
        this.ik = ik;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
