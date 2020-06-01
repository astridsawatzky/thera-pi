package opRgaf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import specs.Contracts;

public class Kennung {
    private static final DateTimeFormatter ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    String name;
    String vorname;
    LocalDate geburtstag;

    public Kennung(String name, String vorname, LocalDate geburtstag) {
        super();
        this.name = name;
        this.vorname = vorname;
        this.geburtstag = geburtstag;
    }

    public Kennung(String aValue) {
        String[] values = aValue.split(",");
        Contracts.require(values.length==3, "Eingabeformat: Nachname,Vorname,Geburtstag");
        
        name= values[0];
        

    }

    @Override
    public String toString() {
        return name.toUpperCase() + "," + vorname.toUpperCase() + ","
                + geburtstag.format(ddMMyyyy);
    }

}
