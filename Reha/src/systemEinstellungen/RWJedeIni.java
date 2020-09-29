package systemEinstellungen;

import CommonTools.ini.INITool;
import CommonTools.ini.Settings;

public class RWJedeIni {
    private static Settings ini = null;

    public static void schreibeIniDatei(String pfad, String iniDatei, String gruppe, String element, String wert) {
        ini = INITool.openIni(pfad, iniDatei);
        ini.setStringProperty(gruppe, element, wert, null);
        INITool.saveIni(ini);
        ini = null;
    }

    public static String leseIniDatei(String pfad, String iniDatei, String gruppe, String element) {
        ini = INITool.openIni(pfad, iniDatei);
        String sret = ini.getStringProperty(gruppe, element);
        ini = null;
        return sret;
    }

}
