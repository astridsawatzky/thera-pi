package org.therapi.reha.patient.neu;

public class Person {

    private String anrede;
    private String titel;
    private String nName;
    private String vName;

    public Person(String abwAnrede, String abwTitel, String abwNName, String abwVName) {
        this.anrede = abwAnrede!=null?abwAnrede:"";
        this.titel = abwTitel!=null?abwTitel:"";
        this.nName = abwNName!=null?abwNName:"";
        this.vName = abwVName!=null?abwVName:"";
    }

    public String getAnrede() {
        return anrede;
    }

    public String getTitel() {
        return titel;
    }

    public String getnName() {
        return nName;
    }

    public String getvName() {
        return vName;
    }

}
