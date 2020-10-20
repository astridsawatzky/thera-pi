package systemEinstellungen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import umfeld.Betriebsumfeld;

public class BehandlerSets {

   private static final Logger logger = LoggerFactory.getLogger(BehandlerSets.class);

    static ArrayList<BehandlerSet> behandlersets = new ArrayList<>();

    public static List<BehandlerSet> alleBehandlersets() {
        return behandlersets;
    }



    public static void laden(Settings termkalini) {

        behandlersets= new ArrayList<>();
        behandlerSetsLaden(termkalini);
    }

    public static void behandlerSetsLaden(Settings termkalini) {
        alleBehandlersets().clear();
        try {
            int lesen = Integer.parseInt(String.valueOf(termkalini.getStringProperty("Kalender", "AnzahlSets")));
            for (int i = 1; i < (lesen + 1); i++) {

                BehandlerSet set= new BehandlerSet();

                set.index=i;
                set.setName(termkalini.getStringProperty("Kalender", "NameSet" + i));
                set.members = Arrays.asList( String.valueOf(termkalini.getStringProperty("Kalender", "FeldSet" + i))
                                 .split(","));
                alleBehandlersets().add( set);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Fehler bei der Verarbeitung der terminkalender.ini, Mehode:NurSets!\nFehlertext: "
                            + ex.getMessage());
            logger.error("Fehler bei der Verarbeitung der terminkalender.ini, Mehode:NurSets!\nFehlertext: ", ex);
        }
    }

    static void behandlerSetsLaden() {
        Settings termkalini = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/",
                "terminkalender.ini");
        behandlerSetsLaden(termkalini);

    }



    public static BehandlerSet find(String ret) {
        for (BehandlerSet behandlerSet : behandlersets) {
            if(behandlerSet.getName().equals(ret)) {
                return behandlerSet;
            }

        }

        return BehandlerSet.EMPTY;
    }



}
