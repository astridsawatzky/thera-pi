package rehaStatistik.Tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import rehaStatistik.RehaStatistik;

public class SystemPreislisten {
    public static Vector<Vector<Vector<String>>> vKGPreise = new Vector<Vector<Vector<String>>>();
    public static Vector<Vector<Vector<String>>> vMAPreise = new Vector<Vector<Vector<String>>>();
    public static Vector<Vector<Vector<String>>> vERPreise = new Vector<Vector<Vector<String>>>();
    public static Vector<Vector<Vector<String>>> vLOPreise = new Vector<Vector<Vector<String>>>();
    public static Vector<Vector<Vector<String>>> vRHPreise = new Vector<Vector<Vector<String>>>();

    public static HashMap<String, Vector<Vector<Vector<String>>>> hmPreise = new HashMap<String, Vector<Vector<Vector<String>>>>();

    public static HashMap<String, Vector<String>> hmPreisGruppen = new HashMap<String, Vector<String>>();

    public static HashMap<String, Vector<String>> hmPreisBereich = new HashMap<String, Vector<String>>();

    public static HashMap<String, Vector<Integer>> hmZuzahlRegeln = new HashMap<String, Vector<Integer>>();

    public static HashMap<String, Vector<Integer>> hmHMRAbrechnung = new HashMap<String, Vector<Integer>>();

    public static HashMap<String, Vector<String>> hmNeuePreiseAb = new HashMap<String, Vector<String>>();

    public static HashMap<String, Vector<Integer>> hmNeuePreiseRegel = new HashMap<String, Vector<Integer>>();

    public static HashMap<String, Vector<Vector<String>>> hmHBRegeln = new HashMap<String, Vector<Vector<String>>>();

    public static HashMap<String, Vector<String>> hmBerichtRegeln = new HashMap<String, Vector<String>>();

    private static Vector<String> dummy = new Vector<String>();
    private static Vector<Integer> intdummy = new Vector<Integer>();
    private static Vector<Vector<String>> hbdummy = new Vector<Vector<String>>();

    // private static Vector<Vector<String>> preisliste = new
    // Vector<Vector<String>>();

    public static void ladePreise(String disziplin) {
        String[] diszis = { "Physio", "Massage", "Ergo", "Logo", "Reha", "Common" };
        List<String> list = Arrays.asList(diszis);
        int treffer = list.indexOf(disziplin);

        if (treffer < 0) {
            return;
        }

        Settings inif = new INIFile(RehaStatistik.proghome + "ini/" + RehaStatistik.aktIK + "/preisgruppen.ini");
        int tarife = inif.getIntegerProperty("PreisGruppen_" + diszis[treffer], "AnzahlPreisGruppen");

        Comparator<Vector<String>> comparator = new Comparator<Vector<String>>() {
            @Override
            public int compare(Vector o1, Vector o2) {

                String s1 = o1.get(0)
                              .toString();
                String s2 = o2.get(0)
                              .toString();
                return s1.compareTo(s2);
            }
        };

        switch (treffer) {
        case 0:
            vKGPreise.clear();
            for (int i = 0; i < tarife; i++) {
                Vector<Vector<String>> preisliste = SqlInfo.holeFelder(
                        "select * from kgtarif" + Integer.toString(i + 1));
                Collections.sort(preisliste, comparator);
                vKGPreise.add( preisliste);
            }
            hmPreise.put("Physio", vKGPreise);
            dummy.clear();
            getPreisGruppen(inif, "Physio", tarife);
            hmPreisGruppen.put("Physio", (Vector<String>) dummy.clone());
            dummy.clear();
            getPreisBereich(inif, "Physio", tarife);
            hmPreisBereich.put("Physio", (Vector<String>) dummy.clone());
            dummy.clear();
            getNeuePreiseAb(inif, "Physio", tarife);
            hmNeuePreiseAb.put("Physio", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getNeuePreiseRegeln(inif, "Physio", tarife);
            hmNeuePreiseRegel.put("Physio", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getZuzahlRegeln(inif, "Physio", tarife);
            hmZuzahlRegeln.put("Physio", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getHMRAbrechnung(inif, "Physio", tarife);
            hmHMRAbrechnung.put("Physio", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            hbdummy.clear();
            getHBRegeln(inif, "Physio", tarife);
            hmHBRegeln.put("Physio", (Vector<Vector<String>>) hbdummy.clone());
            hbdummy.clear();
            dummy.clear();
            getBerichtRegeln(inif, "Physio", tarife);
            hmBerichtRegeln.put("Physio", (Vector<String>) dummy.clone());
            dummy.clear();
            return;
        case 1:
            vMAPreise.clear();
            for (int i = 0; i < tarife; i++) {
                Vector<Vector<String>> preisliste = SqlInfo.holeFelder(
                        "select * from matarif" + Integer.toString(i + 1));
                Collections.sort(preisliste, comparator);
                vMAPreise.add(preisliste);
            }
            hmPreise.put("Massage", vMAPreise);
            dummy.clear();
            getPreisGruppen(inif, "Massage", tarife);
            hmPreisGruppen.put("Massage", (Vector<String>) dummy.clone());
            dummy.clear();
            getPreisBereich(inif, "Massage", tarife);
            hmPreisBereich.put("Massage", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getNeuePreiseRegeln(inif, "Massage", tarife);
            hmNeuePreiseRegel.put("Massage", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getNeuePreiseAb(inif, "Massage", tarife);
            hmNeuePreiseAb.put("Massage", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getZuzahlRegeln(inif, "Massage", tarife);
            hmZuzahlRegeln.put("Massage", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getHMRAbrechnung(inif, "Massage", tarife);
            hmHMRAbrechnung.put("Massage", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            hbdummy.clear();
            getHBRegeln(inif, "Massage", tarife);
            hmHBRegeln.put("Massage", (Vector<Vector<String>>) hbdummy.clone());
            hbdummy.clear();
            dummy.clear();
            getBerichtRegeln(inif, "Massage", tarife);
            hmBerichtRegeln.put("Massage", (Vector<String>) dummy.clone());
            dummy.clear();
            return;
        case 2:
            vERPreise.clear();
            for (int i = 0; i < tarife; i++) {
                Vector<Vector<String>> preisliste = SqlInfo.holeFelder(
                        "select * from ertarif" + Integer.toString(i + 1));
                Collections.sort((Vector) preisliste, comparator);
                vERPreise.add((Vector<Vector<String>>) preisliste);
            }
            hmPreise.put("Ergo", vERPreise);
            dummy.clear();
            getPreisGruppen(inif, "Ergo", tarife);
            hmPreisGruppen.put("Ergo", (Vector<String>) dummy.clone());
            dummy.clear();
            getPreisBereich(inif, "Ergo", tarife);
            hmPreisBereich.put("Ergo", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getNeuePreiseRegeln(inif, "Ergo", tarife);
            hmNeuePreiseRegel.put("Ergo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getNeuePreiseAb(inif, "Ergo", tarife);
            hmNeuePreiseAb.put("Ergo", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getZuzahlRegeln(inif, "Ergo", tarife);
            hmZuzahlRegeln.put("Ergo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getHMRAbrechnung(inif, "Ergo", tarife);
            hmHMRAbrechnung.put("Ergo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            hbdummy.clear();
            getHBRegeln(inif, "Ergo", tarife);
            hmHBRegeln.put("Ergo", (Vector<Vector<String>>) hbdummy.clone());
            hbdummy.clear();
            dummy.clear();
            getBerichtRegeln(inif, "Ergo", tarife);
            hmBerichtRegeln.put("Ergo", (Vector<String>) dummy.clone());
            dummy.clear();
            return;
        case 3:
            vLOPreise.clear();
            for (int i = 0; i < tarife; i++) {
                Vector<Vector<String>> preisliste = SqlInfo.holeFelder(
                        "select * from lotarif" + Integer.toString(i + 1));
                Collections.sort( preisliste, comparator);
                vLOPreise.add( preisliste);
            }
            hmPreise.put("Logo", vLOPreise);
            dummy.clear();
            getPreisGruppen(inif, "Logo", tarife);
            hmPreisGruppen.put("Logo", (Vector<String>) dummy.clone());
            dummy.clear();
            getPreisBereich(inif, "Logo", tarife);
            hmPreisBereich.put("Logo", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getNeuePreiseRegeln(inif, "Logo", tarife);
            hmNeuePreiseRegel.put("Logo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getNeuePreiseAb(inif, "Logo", tarife);
            hmNeuePreiseAb.put("Logo", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getZuzahlRegeln(inif, "Logo", tarife);
            hmZuzahlRegeln.put("Logo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getHMRAbrechnung(inif, "Logo", tarife);
            hmHMRAbrechnung.put("Logo", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            hbdummy.clear();
            getHBRegeln(inif, "Logo", tarife);
            hmHBRegeln.put("Logo", (Vector<Vector<String>>) hbdummy.clone());
            hbdummy.clear();
            dummy.clear();
            getBerichtRegeln(inif, "Logo", tarife);
            hmBerichtRegeln.put("Logo", (Vector<String>) dummy.clone());
            dummy.clear();
            return;
        case 4:
            vRHPreise.clear();
            for (int i = 0; i < tarife; i++) {

                Vector<Vector<String>> preisliste = SqlInfo.holeFelder(
                        "select * from rhtarif" + Integer.toString(i + 1));
                Collections.sort( preisliste, comparator);
                vRHPreise.add( preisliste);
            }
            hmPreise.put("Reha", vRHPreise);
            dummy.clear();
            getPreisGruppen(inif, "Reha", tarife);
            hmPreisGruppen.put("Reha", (Vector<String>) dummy.clone());
            dummy.clear();
            getPreisBereich(inif, "Reha", tarife);
            hmPreisBereich.put("Reha", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getNeuePreiseRegeln(inif, "Reha", tarife);
            hmNeuePreiseRegel.put("Reha", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getNeuePreiseAb(inif, "Reha", tarife);
            hmNeuePreiseAb.put("Reha", (Vector<String>) dummy.clone());
            dummy.clear();
            intdummy.clear();
            getZuzahlRegeln(inif, "Reha", tarife);
            hmZuzahlRegeln.put("Reha", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            getHMRAbrechnung(inif, "Reha", tarife);
            hmHMRAbrechnung.put("Reha", (Vector<Integer>) intdummy.clone());
            intdummy.clear();
            hbdummy.clear();
            getHBRegeln(inif, "Reha", tarife);
            hmHBRegeln.put("Reha", (Vector<Vector<String>>) hbdummy.clone());
            hbdummy.clear();
            dummy.clear();
            getBerichtRegeln(inif, "Reha", tarife);
            hmBerichtRegeln.put("Reha", (Vector<String>) dummy.clone());
            dummy.clear();
            return;
        case 5:
            dummy.clear();
            getPreisGruppen(inif, "Common", tarife);
            hmPreisGruppen.put("Common", (Vector<String>) dummy.clone());
            dummy.clear();
            break;
        }

    }

    /**********
     *
     *
     * @param f
     * @param disziplin
     * @param tarife
     * @param dummy
     * @return
     */
    public static void getPreisGruppen(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            dummy.add(f.getStringProperty("PreisGruppen_" + disziplin, "PGName" + Integer.toString(i + 1)));
        }
    }

    public static void getPreisBereich(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            dummy.add(f.getStringProperty("PreisGruppen_" + disziplin, "PGBereich" + Integer.toString(i + 1)));
        }
    }

    public static void getZuzahlRegeln(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            intdummy.add(f.getIntegerProperty("ZuzahlRegeln_" + disziplin, "ZuzahlRegel" + Integer.toString(i + 1)));
        }
    }

    public static void getHMRAbrechnung(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            intdummy.add(f.getIntegerProperty("HMRAbrechnung_" + disziplin, "HMRAbrechnung" + Integer.toString(i + 1)));
        }
    }

    public static void getHBRegeln(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            Vector<String> hbdummy_1 = new Vector<String>();
            hbdummy_1.clear();
            hbdummy_1.add(f.getStringProperty("HBRegeln_" + disziplin, "HBPosVoll" + (i + 1)));
            hbdummy_1.add(f.getStringProperty("HBRegeln_" + disziplin, "HBPosMit" + (i + 1)));
            hbdummy_1.add(f.getStringProperty("HBRegeln_" + disziplin, "HBKilometer" + (i + 1)));
            hbdummy_1.add(f.getStringProperty("HBRegeln_" + disziplin, "HBPauschal" + (i + 1)));
            hbdummy_1.add(f.getStringProperty("HBRegeln_" + disziplin, "HBHeimMitZuZahl" + (i + 1)));
            hbdummy.add( hbdummy_1);
        }
    }

    public static void getNeuePreiseRegeln(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            intdummy.add(f.getIntegerProperty("PreisRegeln_" + disziplin, "PreisRegel" + Integer.toString(i + 1)));
        }
    }

    public static void getNeuePreiseAb(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            dummy.add(f.getStringProperty("PreisRegeln_" + disziplin, "PreisAb" + Integer.toString(i + 1)));
        }
    }

    public static void getBerichtRegeln(Settings f, String disziplin, int tarife) {
        for (int i = 0; i < tarife; i++) {
            dummy.add(f.getStringProperty("BerichtRegeln_" + disziplin, "Bericht" + Integer.toString(i + 1)));
        }
    }

    class Sortiere {
        Vector<Vector<String>> vector = null;

        public Sortiere(Vector<Vector<String>> vec) {
            this.vector = vec;
        }

        public Vector<Vector<String>> sortieren() {
            Comparator<Vector> comparator = new Comparator<Vector>() {

                @Override
                public int compare(Vector o1, Vector o2) {
                    String s1 = o1.get(0)
                                  .toString();
                    String s2 = o2.get(0)
                                  .toString();
                    return s1.compareTo(s2);
                }
            };

            Collections.sort((Vector) this.vector, comparator);
            return this.vector;
        }
    }
}
