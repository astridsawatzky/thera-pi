package core;

import mandant.IK;

public class Krankenkasse {
	
	// zwei (nun drei) konstruktor methoden die auch noch gleich heißen... 
	// eventuell Namen aendern zu KrankenkasseIK( String kassenid) und KrankenkasseName(String string, String name)?
	
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

    
    /*
	 * Folgender Absatz von Methoden für die KrankenkasseFactory Klasse 
	 * inserted for /hmr/src/hmv/KrankenkasseFactory.java by Marvin
	 */
    
    // Fragwürdig ob nicht lieber eine der anderen beiden Methoden genutzt werden sollte...
    // zur Nutzung von .withXYZ methoden jedoch eingebaut
    public Krankenkasse() {
    	// TODO Auto-generated constructor stub
    }
    
    

}
