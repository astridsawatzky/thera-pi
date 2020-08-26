package core;

import java.util.EnumSet;

public enum Disziplin {
    KG("Physio", "Physio-Rezept", "2"),
    MA("Massage", "Massage/Lymphdrainage-Rezept", "1"),
    ER("Ergo", "Ergotherapie-Rezept", "5"),
    LO("Logo", "Logop\u00e4die-Rezept", "3"),
    PO("Podo", "Podologie-Rezept", "7"),
    RS("Rsport", "Rehasport-Rezept", "8"),
    FT("Ftrain", "Funktionstraining-Rezept", ""),
    RH("Reha","Reha",""),
    COMMON("Common","Common","") ,
    INV("invalid", "invalid", ""),
    ET("Essen", "Ern\u00e4hrungstherapie", "?");

    public final String medium;
    public final String lang;
    public final String hmpraefix;

    private Disziplin(String medium, String lang, String hmpraefix) {
        this.medium = medium;
        this.lang = lang;
        this.hmpraefix = hmpraefix;
    }

    public static Disziplin ofMedium(String medium) {
        if(medium == null) {
            return INV;
        }
        for (Disziplin d : Disziplin.values()) {
            if (d.medium.toLowerCase().equals(medium.toLowerCase())) {
                return d;
            }
        }
        return INV;
    }

    /**
     * Returns an enumset with RH and FT.
     *
     * The enumset is recreated on each call as there is no way to create an
     * immutable enumset.
     *
     * @return
     */
    public EnumSet<Disziplin> ohneReha() { // NO_UCD (unused code)
        return EnumSet.of(KG, MA, ER, LO, PO);
    }
}
