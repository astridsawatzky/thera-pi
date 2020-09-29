package systemEinstellungen.abrechnung.formulare;

import java.io.ByteArrayInputStream;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFrame;

import CommonTools.ini.INIFile;

public class SwingPanelTest {

    public static void main(String[] args) {


        INIFile setting = new INIFile(new ByteArrayInputStream(TestInis.initialString.getBytes()), "testgkvsetting");

        GKVAbrechnungsParameter gKVAbrechnungsParameter = new parameterMapper().readSettings(setting);

        SwingPanel panel = new SwingPanel(Environment.printservices());
        panel.setgKVAbrechnungsParameter(gKVAbrechnungsParameter);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane()
             .add(panel.panel());

        frame.pack();
        frame.setVisible(true);

    }

}
