package terminKalender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hauptFenster.Reha;

public class KollegenLaden {

    private static final Logger logger = LoggerFactory.getLogger(KollegenLaden.class);
    public static Vector<Kollegen> vKKollegen = new Vector<>();
    private static Vector<Urlaubskollege> urlaubsKollegen = new Vector<>();

    public static int maxKalZeile;

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

    public static void Init() {
        Reha obj = Reha.instance;

        if (!vKKollegen.isEmpty()) {
            vKKollegen.clear();
            urlaubsKollegen.clear();
            maxKalZeile = 0;
        }

        try (Statement stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet kollegenRs = stmt.executeQuery(
                        "SELECT Matchcode,Nachname,Nicht_Zeig,Kalzeile,Abteilung FROM kollegen2");) {
            int durchlauf = 0;

            Urlaubskollege kollege = new Urlaubskollege("./.","","","00");
            urlaubsKollegen.add(kollege);

            vKKollegen.add(new Kollegen("./.", "", "", 0, "", "F", 0));

            durchlauf++;
            while (kollegenRs.next()) {
                String matchcode = kollegenRs.getString("Matchcode");
                String nachname = Optional.ofNullable(kollegenRs.getString("Nachname")).orElse("");
                String nichtzeig = Optional.ofNullable(kollegenRs.getString("Nicht_Zeig")).orElse("F");
                String kalzeileString = kollegenRs.getString("Kalzeile");
                String kalenderzeileFormatted = String.format("%02d", Integer.parseInt(kalzeileString));

                Urlaubskollege aKollegen1 = new Urlaubskollege(matchcode, nachname, nichtzeig, kalenderzeileFormatted);
                urlaubsKollegen.add(aKollegen1);

                if (Integer.parseInt(kalzeileString) > maxKalZeile) {
                    maxKalZeile = Integer.parseInt(kalzeileString);
                }
                vKKollegen.add(new Kollegen(Optional.ofNullable(matchcode)
                                                    .orElse(""),
                        kollegenRs.getString("Nachname"), kollegenRs.getString("Nicht_Zeig"), Integer.parseInt(kalzeileString),
                        kollegenRs.getString("Abteilung"), aKollegen1.nichtzeig, durchlauf));
                durchlauf++;
            }
            Collections.sort(vKKollegen);

        } catch (SQLException ex) {
            logger.error("Laden der Mitarbeiter fehlgeschlagen.", ex);
        }
    }

    public static Vector<Urlaubskollege> getUrlaubsKollegen() {
        return urlaubsKollegen;
    }


}

