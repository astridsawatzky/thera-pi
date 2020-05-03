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
    public static Vector<ArrayList<String>> vKollegen = new Vector<>();

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
            vKollegen.clear();
            maxKalZeile = 0;
        }

        try (Statement stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet kollegenRs = stmt.executeQuery(
                        "SELECT Matchcode,Nachname,Nicht_Zeig,Kalzeile,Abteilung FROM kollegen2");) {
            int durchlauf = 0;

            ArrayList<String> kollege = new ArrayList<>();
            kollege.add("./.");
            kollege.add("");
            kollege.add("");
            kollege.add("00");
            vKollegen.add(kollege);

            vKKollegen.add(new Kollegen("./.", "", "", 0, "", "F", 0));

            durchlauf++;
            while (kollegenRs.next()) {
                int kalenderZeile = 0;
                ArrayList<String> aKollegen1 = new ArrayList<>();
                aKollegen1.add(kollegenRs.getString("Matchcode"));
                String nachname = kollegenRs.getString("Nachname");
                aKollegen1.add(nachname != null ? nachname : "");
                String nichtzeig = kollegenRs.getString("Nicht_Zeig");
                aKollegen1.add(nichtzeig != null ? nichtzeig : "F");
                String kalzeileString = kollegenRs.getString("Kalzeile");
                kalenderZeile = Integer.parseInt(kalzeileString);
                if (kalenderZeile > maxKalZeile) {
                    maxKalZeile = kalenderZeile;
                }

                aKollegen1.add(String.format("%02d", kalenderZeile));
                vKollegen.add(aKollegen1);
                vKKollegen.add(new Kollegen(Optional.ofNullable(kollegenRs.getString("Matchcode"))
                                                    .orElse(""),
                        kollegenRs.getString("Nachname"), kollegenRs.getString("Nicht_Zeig"), kalenderZeile,
                        kollegenRs.getString("Abteilung"), aKollegen1.get(2), durchlauf));
                durchlauf++;
            }
            Collections.sort(vKKollegen);

        } catch (SQLException ex) {
            logger.error("Laden der Mitarbeiter fehlgeschlagen.", ex);
        }
    }
}
