package terminKalender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hauptFenster.Reha;

public class KOllegenListe {

    private static final Logger logger = LoggerFactory.getLogger(KOllegenListe.class);
    public static List<Kollegen> vKKollegen = new Vector<>();

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

    public static String getKollegenUeberDBZeile(int reihe) {
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
        try (Statement stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet kollegenRs = stmt.executeQuery(
                        "SELECT Matchcode,Nachname,Nicht_Zeig,Kalzeile,Abteilung FROM kollegen2");) {

            while (kollegenRs.next()) {
                String matchCode = Optional.ofNullable(kollegenRs.getString("Matchcode"))
                                           .orElse("");
                String nachname = kollegenRs.getString("Nachname");
                String nichtzeig = Optional.ofNullable(kollegenRs.getString("Nicht_Zeig"))
                                           .orElse("F");
                String abteilung = kollegenRs.getString("Abteilung");
                int kalZeile = Integer.parseInt(kollegenRs.getString("Kalzeile"));
                if (kalZeile > maxKalZeile) {
                    maxKalZeile = kalZeile;
                }
                Kollegen kollege = new Kollegen(matchCode, nachname, kalZeile, abteilung, nichtzeig);
                vKKollegen.add(kollege);
            }
            Collections.sort(vKKollegen);

        } catch (SQLException ex) {
            logger.error("Laden der Mitarbeiter fehlgeschlagen.", ex);
        }
    }

    public static List<Kollegen> getUrlaubsKollegen() {
        return vKKollegen;
    }

}
