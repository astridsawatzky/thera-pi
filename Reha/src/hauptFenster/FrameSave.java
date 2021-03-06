package hauptFenster;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.SwingWorker;

import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import systemEinstellungen.SystemConfig;
import umfeld.Betriebsumfeld;

public class FrameSave {
    public FrameSave(final Dimension groesse, final Point position, final int container, final int autosize,
            final String xinifile, final String hashmap) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Settings inifile = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/",
                            xinifile);
                    inifile.setIntegerProperty("Container", "StarteIn", container, null);
                    inifile.setIntegerProperty("Container", "ImmerOptimieren", autosize, null);
                    inifile.setIntegerProperty("Container", "ZeigeAnPositionX", (autosize == 1 ? 0 : position.x), null);
                    inifile.setIntegerProperty("Container", "ZeigeAnPositionY", (autosize == 1 ? 0 : position.y), null);
                    inifile.setIntegerProperty("Container", "DimensionX", (autosize == 1 ? -1 : groesse.width), null);
                    inifile.setIntegerProperty("Container", "DimensionY", (autosize == 1 ? -1 : groesse.height), null);
                    INITool.saveIni(inifile);

                    SystemConfig.hmContainer.put(hashmap, container);
                    SystemConfig.hmContainer.put(hashmap + "Opti", autosize);
                    SystemConfig.hmContainer.put(hashmap + "LocationX", (autosize == 1 ? 0 : position.x));
                    SystemConfig.hmContainer.put(hashmap + "LocationY", (autosize == 1 ? 0 : position.y));
                    SystemConfig.hmContainer.put(hashmap + "DimensionX", (autosize == 1 ? -1 : groesse.width));
                    SystemConfig.hmContainer.put(hashmap + "DimensionY", (autosize == 1 ? -1 : groesse.height));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

        }.execute();

    }

}
