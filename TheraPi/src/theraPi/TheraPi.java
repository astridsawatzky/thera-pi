package theraPi;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import environment.Path;
import hauptFenster.Reha;
import mandant.Mandant;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class TheraPi {

    public static String proghome;

   

    public static void main(String[] args) throws InvalidFileFormatException, IOException {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
      
        setLookAndFeel();
       
        proghome = Path.Instance.getProghome();

        String iniUrl = proghome + "ini/mandanten.ini";
        Ini mandantenIni = new Ini(new File(iniUrl));

        MandantList liste = new MandantList(mandantenIni);

        if (liste.showAllways()) {
            MandantSelector mandantSelector = new MandantSelector(liste);
            mandantSelector.validate();
            mandantSelector.pack();
            mandantSelector.setLocationRelativeTo(null);
            mandantSelector.setVisible(true);
            Mandant current = mandantSelector.chosen();
       //     logger.debug(current.toString());
            startReha(current);
        } else {
         //   logger.debug(liste.defaultMandant().toString());
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
           //         logger.debug("plasticxp L&F could not be set", justLog);
                }

                break;

            }
        }

    }

}
