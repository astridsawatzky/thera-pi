package theraPi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ini4j.Ini;

import mandant.Mandant;

class MandantList {
    private static final String THERA_PI_MANDANTEN = "TheraPiMandanten";
    private static final String ANZAHL_MANDANTEN = "AnzahlMandanten";
    private static final String DEFAULT_MANDANT = "DefaultMandant";
    private static final String AUSWAHL_IMMER_ZEIGEN = "AuswahlImmerZeigen";
    List<Mandant> list;
    private int defIndex = 0;
    private boolean showAlways;

    public MandantList(Ini ini) {
        int anzahlMandanten = Integer.parseInt(ini.get(THERA_PI_MANDANTEN, ANZAHL_MANDANTEN));
        defIndex = Integer.parseInt(ini.get(THERA_PI_MANDANTEN, DEFAULT_MANDANT));
        list = new LinkedList<Mandant>();
        for (int i = 1; i <= anzahlMandanten; i++) {
            String ik = ini.get(THERA_PI_MANDANTEN, "MAND-IK" + (i));
            String name = ini.get(THERA_PI_MANDANTEN, "MAND-NAME" + (i));
            list.add(i - 1, new Mandant(ik, name));

        }
        setShowAllways(ini);

    }

    private void setShowAllways(Ini ini) {
        String auswahlZeigen = ini.get(THERA_PI_MANDANTEN, AUSWAHL_IMMER_ZEIGEN);
        if (auswahlZeigen == null) {
            showAlways = true;
        } else if ("1".equals(auswahlZeigen)) {
            showAlways = true;
        } else {
            showAlways = Boolean.valueOf(auswahlZeigen);
        }
    }

    boolean showAllways() {
        return showAlways;
    }

    public Mandant defaultMandant() {
        return list.get(defIndex - 1);
    }

    List<Mandant> asList() {
        return Collections.unmodifiableList(list);
    }

}
