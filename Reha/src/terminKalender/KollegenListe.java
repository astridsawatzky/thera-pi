package terminKalender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hauptFenster.Reha;
import mitarbeiter.Mitarbeiter;
import mitarbeiter.MitarbeiterDto;

public class KollegenListe {

    private static final Logger logger = LoggerFactory.getLogger(KollegenListe.class);
    public static List<Kollegen> vKKollegen = new LinkedList<>();

    static int suchen(String ss) {
        int ret = -1;
        int lang = vKKollegen.size();
        int i;
        for (i = 0; i < lang; i++) {
            if (vKKollegen.get(i).Matchcode.equals(ss)) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    public static int getDBZeile(int kollege) {
        return vKKollegen.get(kollege).Reihe;
    }

    public static String getKollegenUeberReihe(int reihe) {
        String ret = "";
        int lang = vKKollegen.size();
        int i;
        for (i = 0; i < lang; i++) {
            if (vKKollegen.get(i).Reihe == reihe) {
                ret = vKKollegen.get(i).Matchcode;
                break;
            }
        }
        return ret;
    }

    public static String getMatchCodeUeberDBZeile(int reihe) {
        return byDBZeile(reihe).getMatchcode();
    }

    public static Kollegen byDBZeile(int reihe) {
        for (Kollegen kollegen : vKKollegen) {
            if (kollegen.Reihe == reihe) {
                return kollegen;
            }
        }
        return Kollegen.NULL_KOLLEGE;
    }

    public static String getMatchcode(int kollege) {
        return vKKollegen.get(kollege).Matchcode;
    }

    public static String getZeigen(int kollege) {
        return vKKollegen.get(kollege).Zeigen;
    }

    public static String getAbteilung(int kollege) {
        return vKKollegen.get(kollege).Abteilung;
    }

    public static String searchAbteilung(int dbzeile) {
        String sret = "";
        for (int i = 0; i < vKKollegen.size(); i++) {
            if (vKKollegen.get(i).Reihe == dbzeile) {
                sret = vKKollegen.get(i).Abteilung;
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

        List<Mitarbeiter> mitarbeiterListe= new MitarbeiterDto(reha.mandant().ik()).all();

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

}
