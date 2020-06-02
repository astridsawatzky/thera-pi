package opRgaf;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import opRgaf.CommonTools.DateTimeFormatters;
import specs.Contracts;

public class Kennung {
    String name = "";
    String vorname = "";
    LocalDate geburtstag = null;

    public Kennung(String name, String vorname, LocalDate geburtstag) {
        super();
        this.name = name;
        this.vorname = vorname;
        this.geburtstag = geburtstag;
    }

    public Kennung(String aValue) {
        if (aValue != null) {
            String[] values = aValue.split(",");
            Contracts.require(values.length == 3, "Eingabeformat: Nachname,Vorname,Geburtstag");

            name = values[0];
            vorname = values[1];
            Function<? super String, ? extends LocalDate> mapper = (t) -> {
                return LocalDate.parse(t.trim(),DateTimeFormatters.ddMMYYYYmitPunkt);
            };
            geburtstag = Optional.ofNullable(values[2]).map(mapper).orElse(null);;
        }

    }

    @Override
    public String toString() {
        System.out.println(name);
        System.out.println(vorname);
        System.out.println(geburtstag);
        return name + ","
              + vorname + ","
              + Optional.ofNullable(geburtstag).map(d->d.format(DateTimeFormatters.ddMMYYYYmitPunkt)).orElse(null);
    }

}
