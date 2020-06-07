package opRgaf;

import java.time.LocalDate;

import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class OffenePosten {
    Kennung kennung;
    String rgNr;
    Type type =Type.andere;
    public OffenePosten.Type getRGType() {
        return type;
    }
    LocalDate rgDatum;
    Money gesamtBetrag;
    Money offen;
    public Money getOffen() {
        return offen;
    }
    Money bearbeitungsGebuehr;
    LocalDate bezahltAm;
    LocalDate mahnungEins;
    LocalDate mahnungZwei;
    String krankenKassenName;
    Rezeptnummer rezNummer;

    int tabellenId;
    int patid;
boolean isStorniert;

    enum Type{
        RGR,
        AFR,
        VR,
        andere
    }
}
