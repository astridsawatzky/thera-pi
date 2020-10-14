package abrechnung;

import java.time.LocalDate;
import java.util.Map;

import CommonTools.DateTimeFormatters;
import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class RGRData {

    private Rezeptnummer rezr;
    private LocalDate date;
    private Money rgbetrag;
    @Override
    public String toString() {
        return "RGRData [rezr=" + rezr + ", date=" + date + ", rgbetrag=" + rgbetrag + ", pauschale=" + pauschale
                + ", behandlung=" + behandlung + "]";
    }

    private Money pauschale;
    private String behandlung;

    public RGRData(Map<String, String> hmRezgeb) {
        rezr = new Rezeptnummer(hmRezgeb.get("<rgreznum>"));
        date = LocalDate.parse(hmRezgeb.get("<rgdatum>"),DateTimeFormatters.dMYYYYmitPunkt);
        rgbetrag = new Money(hmRezgeb.get("<rgbetrag>"));
        pauschale = new Money( hmRezgeb.get("<rgpauschale>"));
        behandlung = (hmRezgeb.get("<rgbehandlung>"));

    }

}
