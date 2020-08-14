package abrechnung;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import CommonTools.JRtaComboBox;
import systemEinstellungen.SystemConfig;

/**
 * Diziplinen in unterschiedlichen Darstellungen:
 * z.B. Physiotherapie
 * kurz = KG
 * mittel = Physio
 * lang = Physio-Rezept
 *
 *
 */

public class Disziplinen {
    private static final List<String> DISZIS_OHNE_REHA_LANG = Collections.unmodifiableList(new ArrayList<>(Arrays.asList("Physio-Rezept", "Massage/Lymphdrainage-Rezept",
            "Ergotherapie-Rezept", "Logopädie-Rezept", "Podologie-Rezept")));
    private static final List<String> DISZIS_NUR_REHA_LANG = Collections.unmodifiableList(Arrays.asList("Rehasport-Rezept", "Funktionstraining-Rezept"));
    private static final List<String> diszisMittel= Collections.unmodifiableList(new ArrayList<>(Arrays.asList("Physio","Massage","Ergo","Logo","Podo","Rsport","Ftrain")));   // aus AktuelleRezepte
    private static final String[] diszisKurz = new String [] {"KG","MA","ER","LO","PO","RS","FT"};     // aus AbrechnungGKV (!)
    private static final String[] hmCodePrefix = new String [] {"2","1","5","3","7","8",""};              // passende Prefixe nach HMpositionsnummernverzeichnis
    private ArrayList<String> aktiveDiszisLang = new ArrayList<>();
    private String[] aktiveDiszisKurz;
    private JRtaComboBox cmbDiszi;
    private JRtaComboBox cmbDisziActive;
    private ArrayList<String> activeDiszisLang;


    /**
     * Verwaltet die enthaltenen Heilmittelsparten.
     * @deprecated Use {@link #Disziplinen(Vector<Vector<String>>,String,boolean)} instead
     */
    public Disziplinen() {
        this(SystemConfig.rezeptKlassenAktiv, SystemConfig.initRezeptKlasse, SystemConfig.mitRs);
    }

    /**
     * Verwaltet die enthaltenen Heilmittelsparten.
     * @param rezeptKlassenAktivVector Vector der aktiven Disziplinen
     * @param initRezeptKlasse die vorausgewaehlte HMSparte in Comboboxen, if null or empty String the first in the list is chosen.
     * @param mitRS include RS and FT
     */
    Disziplinen(Vector<Vector<String>> rezeptKlassenAktivVector, String initRezeptKlasse, boolean mitRS) {
        // kleiner Überblick über das real existierende Chaos
        // aus HMRCheck
        //      String diszis[] =    {"2", "1", "5", "3", "8", "7"};    // auch in SysUtilTarifgruppen
        //      String diszikurz[] = {"KG","MA","ER","LO","RH","PO"};   // <- gibt es keine RH-Rezepte (? s.u.)

        // aus SysUtilTarifgruppen          // <- McM: das ist die umfangreichste - als Basis nehmen?
        //      tabName = new String[]    {"kgtarif","matarif","ertarif","lotarif","rhtarif","potarif","rstarif","fttarif"};
        //      dummydiszi = new String[] {"Physio","Massage","Ergo","Logo","Reha","Podo","Rsport","Ftrain"};

        // aus Rezeptneuanlage (+REHA-Verordnung, -Rehasport-Rezept, -Funktionstraining-Rezept)
        // String[] idiszi = {"Physio-Rezept","Massage/Lymphdrainage-Rezept","Ergotherapie-Rezept","Logopädie-Rezept","REHA-Verordnung","Podologie-Rezept"};



        aktiveDiszisLang.addAll( DISZIS_OHNE_REHA_LANG);
        if (mitRS) {
            aktiveDiszisLang.addAll(DISZIS_NUR_REHA_LANG);
        }
        String initVal = initRezeptKlasse;

        if (initVal==null || initVal.isEmpty()) {
            initVal = aktiveDiszisLang.get(0);
        }
        this.cmbDiszi = new JRtaComboBox(aktiveDiszisLang.toArray(new String[aktiveDiszisLang.size()]));
        cmbDiszi.setSelectedItem(initVal); // default setzen

        // weitere ComboBox mit nur den aktiven Rezeptklassen erzeugen
        activeDiszisLang = new ArrayList<>();
        ArrayList<String> listAktiveRezeptKlassen = new ArrayList<>();

      for(      Vector<String> rezeptKlasse : rezeptKlassenAktivVector) {
            activeDiszisLang.add(rezeptKlasse
                                                                  .get(0));
            listAktiveRezeptKlassen.add( rezeptKlasse
                                                                    .get(1));
        }

          aktiveDiszisKurz = listAktiveRezeptKlassen.toArray(new String[listAktiveRezeptKlassen.size()]);
        this.cmbDisziActive = new JRtaComboBox( activeDiszisLang.toArray(new String[activeDiszisLang.size()]));
        cmbDisziActive.setSelectedItem(initVal);
    }

    public JRtaComboBox getComboBox() {
        return this.cmbDiszi;
    }

    public String getCurrDisziKurz() {
        if (diszisMittel.size() >= cmbDiszi.getSelectedIndex()) {
            return diszisMittel.get(cmbDiszi.getSelectedIndex());
        } else {
            System.out.println("getCurrDiszi err: size " + diszisMittel.size() + " vs idx " + cmbDiszi.getSelectedIndex());
            return diszisMittel.get(0); // use default ("Physio")
        }
    }

    public int getCurrDisziIdx() {
        return getIndex(getCurrDisziKurz());
    }

    public void setCurrTypeOfVO(String currTypeOfVO) {
        this.cmbDiszi.setSelectedItem(currTypeOfVO);
    }

    /** Liefert die aktuell ausgewählte Rezeptklasse. */
    public String getCurrRezClass() {
        if (diszisKurz.length >= cmbDiszi.getSelectedIndex()) {
            return diszisKurz[cmbDiszi.getSelectedIndex()];
        } else {
            System.out.println("getCurrDiszi err: size " + diszisMittel.size() + " vs idx " + cmbDiszi.getSelectedIndex());
            return diszisKurz[0]; // use default ("KG")
        }
    }


    public boolean currIsPodo() {
        return "Podo".equals(getCurrDisziKurz());
    }
    public boolean currIsRsport() {
        return "Rsport".equals(getCurrDisziKurz());
    }
    public boolean currIsFtrain() {
        return "Ftrain".equals(getCurrDisziKurz());
    }

    /**
     * Liefert Index der als Kurzbezeichnung (z.B. "Physio") uebergebenen Disziplin
     */
    public int getIndex(String Disziplin) {
        return diszisMittel.indexOf(Disziplin);
    }

    /** Liefert String-Feld der aktiven Rezeptklassen (z.B. [KG, PO]) */
    public String[] getActiveRK() {
        return this.aktiveDiszisKurz;
    }

    /** Liefert ComboBox, die Verordnungen der aktiven HM-Sparten auflistet. */
    public JRtaComboBox getComboBoxActiveRK() {
        return this.cmbDisziActive;
    }

    public String getCurrDisziFromActRK() {
        return cmbDisziActive.getSelectedItem()
                             .toString();
    }

    public void setCurrDisziActRK(String currTypeOfVO) {
        this.cmbDisziActive.setSelectedItem(currTypeOfVO);
    }

    /** Liefert numer. Prefix des HM-Codes fuer die uebergebene Disziplin */
    public String getPrefix(int Disziplin) {
        return hmCodePrefix[Disziplin];
    }

    /**
     * Liefert den Rezeptklasse-Prefix (z.B. "KG") fuer die uebergebene Disziplin
     */
    public String getRezClass(int Disziplin) {
        return diszisKurz[Disziplin];
    }

    /**
     * Liefert Kurzbezeichnung (z.B. "Physio") zur uebergebenen Rezeptklasse (z.B. "KG")
     */
    public String getDisziKurzFromRK(String rk) {
        ArrayList<String> rKl = new ArrayList<>(Arrays.asList(diszisKurz));
        return diszisMittel.get(rKl.indexOf(rk));
    }

    /**
     * Liefert Kurzbezeichnung (z.B. "Physio") zum uebergebenen VO-Typ (z.B. "Physio-Rezept")
     */
    public String getDisziKurzFromTypeOfVO(String typeOfVO) {
        for (int i = 0; i < aktiveDiszisLang.size(); i++) {
            if (typeOfVO.equalsIgnoreCase(aktiveDiszisLang.get(i))) {
                return diszisMittel.get(i);
            }
        }
        System.out.println("getDiszi err: not found: " + typeOfVO + " set " + diszisMittel.get(0) + " as default");
        return diszisMittel.get(0); // use default ("Physio")
    }

    public String getRezClass(String typeOfVO) {
        for (int i = 0; i < aktiveDiszisLang.size(); i++) {
            if (typeOfVO.equalsIgnoreCase(aktiveDiszisLang.get(i))) {
                return diszisKurz[i];
            }
        }
        System.out.println("getRezClass err: not found: " + typeOfVO + " set " + diszisKurz[0] + " as default");
        return diszisKurz[0]; // use default ("KG")
    }
}
