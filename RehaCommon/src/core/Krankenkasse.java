package core;

import mandant.IK;

public class Krankenkasse {
    public Krankenkasse(String kassenid) {
        ik= new IK(kassenid);
    }


    public Krankenkasse(String string, String name) {
        this(string);
        this.name = name;

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
