package systemEinstellungen.config;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import CommonTools.INIFile;
import CommonTools.INITool;
import crypt.Verschluesseln;

public class Datenbank {

    private static Vector<ArrayList<String>> vDatenBank = new Vector<ArrayList<String>>();

//    public static Vector<ArrayList<String>> getvDatenBank() {
//        return vDatenBank;
//    }

    public String treiber() {
        return vDatenBank.get(0)
                         .get(0);
    }

    public String password() {
        return Datenbank.vDatenBank.get(0)
                                   .get(4);
    }

    public String user() {
        return Datenbank.vDatenBank.get(0)
                                   .get(3);
    }

    public String jdbcDB() {
        return Datenbank.vDatenBank.get(0)
                                   .get(1);
    }

    public void datenbankEinstellungeneinlesen(INIFile inifile) {

        int lesen = inifile.getIntegerProperty("DatenBank", "AnzahlConnections");
        for (int i = 1; i < (lesen + 1); i++) {
            ArrayList<String> aKontakt = new ArrayList<String>();
            aKontakt.add(String.valueOf(inifile.getStringProperty("DatenBank", "DBTreiber" + i)));
            aKontakt.add(String.valueOf(inifile.getStringProperty("DatenBank", "DBKontakt" + i)));
            aKontakt.add(String.valueOf(inifile.getStringProperty("DatenBank", "DBType" + i)));
            String sbenutzer = String.valueOf(inifile.getStringProperty("DatenBank", "DBBenutzer" + i));
            aKontakt.add(String.valueOf(sbenutzer));
            String pw = String.valueOf(inifile.getStringProperty("DatenBank", "DBPasswort" + i));
            String decrypted = null;
            if (pw == null) {
                decrypted = String.valueOf("");
                JOptionPane.showMessageDialog(null, "Passwort der MySql-Datenbank = null");
            } else if (!pw.equals("")) {
                Verschluesseln man = Verschluesseln.getInstance();
                decrypted = man.decrypt(pw);
            } else {
                Object ret = JOptionPane.showInputDialog(null,
                        "Geben Sie bitte das Passwort für die MySql-Datenbank ein", "");
                if (ret == null) {
                    decrypted = String.valueOf("");
                } else {
                    decrypted = ((String) ret).trim();
                    Verschluesseln man = Verschluesseln.getInstance();
                    inifile.setStringProperty("DatenBank", "DBPasswort" + i, man.encrypt(((String) ret)), null);
                    INITool.saveIni(inifile);
                }
            }

            aKontakt.add(decrypted);
            vDatenBank.add(aKontakt);

        }
    }

    public String typ() {
        return vDatenBank.get(0)
                         .get(2);
    }

}
