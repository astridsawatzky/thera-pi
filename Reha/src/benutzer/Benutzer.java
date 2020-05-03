package benutzer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JOptionPane;

import crypt.Verschluesseln;
import hauptFenster.Reha;

public class Benutzer {

    public static Vector<Vector<String>> pKollegen = new Vector<>();

    /** * Ende Klasse. */
    
    public static void benutzerLaden() {
        Reha obj = Reha.instance;
    
        if (!pKollegen.isEmpty()) {
            pKollegen.clear();
        }
    
        try (Statement stmt = obj.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rehaLoginRs = stmt.executeQuery("SELECT * from rehalogin")) {
            Vector<String> aKollegen = new Vector<>();
            String test = "";
            Verschluesseln man = Verschluesseln.getInstance();
            while (rehaLoginRs.next()) {
                try {
                    test = rehaLoginRs.getString("user");
                    aKollegen.add(test != null ? man.decrypt(test) : "");
                    test = rehaLoginRs.getString("password");
                    aKollegen.add(test != null ? man.decrypt(test) : "");
                    test = rehaLoginRs.getString("rights");
                    aKollegen.add(test != null ? man.decrypt(test) : "");
                    test = rehaLoginRs.getString("email");
                    aKollegen.add(test != null ? test : "");
                    test = rehaLoginRs.getString("id");
                    aKollegen.add(test != null ? test : "");
                } catch (Exception ex) {
                    aKollegen.add("none");
                    aKollegen.add("none");
                    aKollegen.add("none");
                    aKollegen.add("none");
                    test = rehaLoginRs.getString("id");
                    aKollegen.add(test != null ? test : "");
                    JOptionPane.showMessageDialog(null, "Fehler in der Entschlüsselung bei User ID = " + test);
                    ex.printStackTrace();
                }
                pKollegen.add((Vector<String>) aKollegen.clone());
                aKollegen.clear();
            }
            Comparator<Vector> comparator = new Comparator<Vector>() {
                @Override
                public int compare(Vector o1, Vector o2) {
                    String s1 = (String) o1.get(0);
                    String s2 = (String) o2.get(0);
                    return s1.compareTo(s2);
                }
            };
            Collections.sort(pKollegen, comparator);
        } catch (SQLException ex) {
        }
    }

}
