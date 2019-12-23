package org.therapi.reha.patient.neu;

/**
 * Die Arztnummer setzt sich aus insgesamt neun Ziffern zusammen:
 * <p>
 * 1. einer sechsstelligen eineindeutigen Ziffernfolge (Ziffern 1-6)
 * <p>
 * 2. einer Prüfziffer (Ziffer 7)
 * <p>
 * 3. einem zweistelligen Arztgruppenschlüssel, der den Versorgungsbereich sowie
 * die Facharztgruppe, differenziert nach Schwerpunkten, angibt (Ziffern 8-9)
 * Arztnummer: nnnnnn m ff
 * <p>
 * ID Prüfziffer Fachgruppe
 * <p>
 * Die Prüfziffer wird mittels des Modulo 10-Verfahrens der Stellen 1-6 der
 * Arztnummer ermittelt. Bei diesem Verfahren werden die Ziffern 1-6 von links
 * nach rechts abwechselnd mit 4 und 9 multipliziert. Die Summe dieser Produkte
 * wird Modulo 10 berechnet. Die Prüfziffer ergibt sich aus der Differenz dieser
 * Zahl zu 10 (ist die Differenz 10, so ist die Prüfziffer 0).
 *
 */
public class LANR {
    public LANR(String nummer) {
        lanr = nummer;
    }

    String lanr;

}
