package org.therapi.reha.patient.neu;

public class Arzt {
    private String id;
    public Arzt(String id, String lanr, String name) {
        this.id = id;
        this.nummer = new LANR(lanr);
        // TODO Auto-generated constructor stub
    }
    String anrede;
    String vorname;
    String nachname;
    Adresse praxis;
    LANR nummer;

}
