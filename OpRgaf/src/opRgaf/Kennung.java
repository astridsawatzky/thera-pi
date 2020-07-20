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
    private boolean isEmpty=true;

    public Kennung(String name, String vorname, LocalDate geburtstag) {
        super();
        this.name = name;
        this.vorname = vorname;
        this.geburtstag = geburtstag;
        isEmpty =false;
    }

    public Kennung(String aValue) {
        if (aValue != null) {
            String[] values = aValue.split(",");
            Contracts.require(values.length == 3, "Eingabeformat: Nachname,Vorname,Geburtstag but got " + aValue);

            name = values[0];
            vorname = values[1];
            Function<? super String, ? extends LocalDate> mapper = (t) -> {
                return LocalDate.parse(t.trim(),DateTimeFormatters.dMYYYYmitPunkt);
            };
            geburtstag = Optional.ofNullable(values[2]).map(mapper).orElse(null);;
            isEmpty = false;
        } else {
            isEmpty =true;
        }

    }

    @Override
    public String toString() {
        if (isEmpty) {
            return "";
        } else {



        return name + ","
              + vorname + ","
              + Optional.ofNullable(geburtstag).map(d->d.format(DateTimeFormatters.ddMMYYYYmitPunkt)).orElse(null);
    }}

}
