package opRgaf;

import java.time.LocalDate;

import opRgaf.rezept.Money;
import opRgaf.rezept.Rezeptnummer;

public class OffenePosten {
    Kennung kennung;
    String rgNr;
    LocalDate rgDatum;
    Money gesamtBetrag;
    Money offen;
    Money bearbeitungsGebuehr;
    LocalDate bezahltAm;
    LocalDate mahnungEins;
    LocalDate mahnungZwei;
    String krankenKassenName;
    Rezeptnummer rezNummer;

    int tabellenId;
    int patid;

}
