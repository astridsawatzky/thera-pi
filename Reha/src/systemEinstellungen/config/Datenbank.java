package systemEinstellungen.config;

import javax.swing.JOptionPane;

import CommonTools.ini.INIFile;
import CommonTools.ini.INITool;
import crypt.Verschluesseln;

public class Datenbank {

    private static DBKontakt contact = new DBKontakt();

    public String treiber() {
        return contact.treiber;
    }

    public String password() {
        return contact.decryptedPassword;
    }

    public String user() {
        return contact.user;
    }

    public String jdbcDB() {
        return contact.jdbcDB;
    }

    public String typ() {
        return contact.typ;
    }

    public void datenbankEinstellungeneinlesen(INIFile inifile) {

        contact.treiber = inifile.getStringProperty("DatenBank", "DBTreiber1");
        contact.jdbcDB = inifile.getStringProperty("DatenBank", "DBKontakt1");
        contact.typ = inifile.getStringProperty("DatenBank", "DBType1");
        contact.user = inifile.getStringProperty("DatenBank", "DBBenutzer1");

        String pw = inifile.getStringProperty("DatenBank", "DBPasswort1");
        contact.decryptedPassword = processPassword(inifile, pw);

    }

    protected String processPassword(INIFile inifile, String pw) {
        String decryptedPassword;
        if (pw == null) {
            decryptedPassword = "";
            JOptionPane.showMessageDialog(null, "Passwort der MySql-Datenbank = null");
        } else if ("".equals(pw)) {
            String ret = JOptionPane.showInputDialog(null, "Geben Sie bitte das Passwort für die MySql-Datenbank ein",
                    "");
            if (ret == null) {
                decryptedPassword = "";
            } else {
                decryptedPassword = ret.trim();
                Verschluesseln man = Verschluesseln.getInstance();
                inifile.setStringProperty("DatenBank", "DBPasswort1", man.encrypt(ret), null);
                INITool.saveIni(inifile);
            }
        } else {
            Verschluesseln man = Verschluesseln.getInstance();
            decryptedPassword = man.decrypt(pw);
        }
        return decryptedPassword;
    }

}
