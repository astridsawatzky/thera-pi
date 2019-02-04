package theraPi;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import environment.Path;
import hauptFenster.Reha;
import mandant.Mandant;

public class TheraPi {

     

   private static Logger logger = LoggerFactory.getLogger(TheraPi.class);

    public static void main(String[] args) throws InvalidFileFormatException, IOException {
        setLookAndFeel();
         String proghome = Path.Instance.getProghome();

        String iniUrl = proghome + "ini/mandanten.ini";
        Ini mandantenIni = new Ini(new File(iniUrl));

        MandantList liste = new MandantList(mandantenIni);

        if (liste.showAllways()) {
            MandantSelector mandantSelector = new MandantSelector(liste, new ImageIcon(proghome + "icons/TPorg.png"));
            mandantSelector.validate();
            mandantSelector.pack();
            mandantSelector.setLocationRelativeTo(null);
            mandantSelector.setVisible(true);
            Mandant current = mandantSelector.chosen();
           logger.debug(current.toString());
            startReha(current);
        } else {
          logger.debug(liste.defaultMandant().toString());
            startReha(liste.defaultMandant());

        }

    }

    private static void startReha(Mandant mandant) {
        new Reha(mandant).start();
    }

    private static void setLookAndFeel() {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {

            if (info.getName().contains("PlasticXP")) {

                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception justLog) {
                    // cannot happen, we searched, if it is there, but anyways
                    // ignore if it cannot be set default will be used, so we ignore
                   logger.debug("plasticxp L&F could not be set", justLog);
                }

                break;

            }
        }

    }

}
