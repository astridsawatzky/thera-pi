package terminKalender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hauptFenster.Reha;
import mitarbeiter.Mitarbeiter;
import mitarbeiter.MitarbeiterDto;
import umfeld.Betriebsumfeld;

public class KollegenListe {



    private static final Logger logger = LoggerFactory.getLogger(KollegenListe.class);
    public static List<Kollegen> vKKollegen = new LinkedList<>();

    static int suchen(String ss) {
        int ret = -1;
        int lang = vKKollegen.size();
        int i;
        for (i = 0; i < lang; i++) {
            if (vKKollegen.get(i).getMatchcode().equals(ss)) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    public static int getDBZeile(int kollege) {
        return vKKollegen.get(kollege).getReihe();
    }

    public static String getKollegenUeberReihe(int reihe) {
        String ret = "";
        int lang = vKKollegen.size();
        int i;
        for (i = 0; i < lang; i++) {
            if (vKKollegen.get(i).getReihe() == reihe) {
                ret = vKKollegen.get(i).getMatchcode();
                break;
            }
        }
        return ret;
    }

    public static String getMatchCodeUeberDBZeile(int reihe) {
        return byDBZeile(reihe).getMatchcode();
    }

    private static Kollegen byDBZeile(int reihe) {
        for (Kollegen kollegen : vKKollegen) {
            if (kollegen.getReihe() == reihe) {
                return kollegen;
            }
        }
        return Kollegen.NULL_KOLLEGE;
    }

    public static String getMatchcode(int kollege) {
        return vKKollegen.get(kollege).getMatchcode();
    }

    public static String getZeigen(int kollege) {
        return vKKollegen.get(kollege).getZeigen();
    }

    public static String getAbteilung(int kollege) {
        return vKKollegen.get(kollege).getAbteilung();
    }

    public static String searchAbteilung(int dbzeile) {
        String sret = "";
        for (int i = 0; i < vKKollegen.size(); i++) {
            if (vKKollegen.get(i).getReihe() == dbzeile) {
                sret = vKKollegen.get(i).getAbteilung();
                break;
            }
        }
        return sret;
    }

    public static int maxKalZeile = 0;

    public static void Init() {
        Reha obj = Reha.instance;

        if (!vKKollegen.isEmpty()) {
            vKKollegen.clear();
        }

        vKKollegen.add(Kollegen.NULL_KOLLEGE);

        ladeKollegenAusDB(obj);
    }

    private static void ladeKollegenAusDB(Reha reha) {

        List<Mitarbeiter> mitarbeiterListe= new MitarbeiterDto(Betriebsumfeld.umfeld.getMandant().ik()).all();

        for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
            vKKollegen.add(Kollegen.of(mitarbeiter));
            if (mitarbeiter.getKalzeile() > maxKalZeile) {
                maxKalZeile = mitarbeiter.getKalzeile();
                }
        }


        Collections.sort(vKKollegen);

    }

    public static List<Kollegen> getKollegen() {
        return vKKollegen;
    }

    public static int size() {
        return vKKollegen.size();
    }

    public static Kollegen getByMatchcode(String matchcode) {
        return Kollegen.of(new MitarbeiterDto(Betriebsumfeld.umfeld.getMandant().ik()).byMatchcode(matchcode).get());
    }

}
