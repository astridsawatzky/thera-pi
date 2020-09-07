package core;

import mandant.IK;

public class Krankenkasse {

    IK ik;
    String name;
    int id=-1;


    public Krankenkasse(String kassenik) {
        ik= new IK(kassenik);
    }


    public Krankenkasse(String kassenik, String name) {
        this(kassenik);
        this.name = name;

    }



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
