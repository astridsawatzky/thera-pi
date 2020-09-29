package systemEinstellungen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import hauptFenster.Reha;
import terminKalender.TerminFenster;

public class SysUtilKalenderfarben extends JXPanel implements ActionListener {

    JXTable FarbTab = null;
    JComboBox<String> colorSetCombo = null;
    JComboBox alphawahl = null;
    JButton anwendenButton = null;
    JButton abbrechenButton = null;
    Vector columnData = new Vector();
    String colorset = null;
    JButton defaultSave = null;
    KalenderFarbenModel ftm;
    JScrollPane jscroll = null;
    static String colorini = "color.ini";

    public SysUtilKalenderfarben() {
        super(new GridLayout(1, 1));
        // System.out.println("Aufruf SysUtilKalenderfarben");
        this.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 0));
        /****/
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        /****/
        jscroll = new JScrollPane();
        jscroll.setOpaque(false);
        jscroll.getViewport()
               .setOpaque(false);
        jscroll.setBorder(null);
        jscroll.getVerticalScrollBar()
               .setUnitIncrement(15);
        jscroll.setViewportView(getVorlagenSeite());
        jscroll.validate();
        add(jscroll);

//         add(getVorlagenSeite());
        validate();
        // setVisible(true);
        return;
    }

    /**************
     * Beginn der Methode f�r die Objekterstellung und -platzierung
     *********/
    private JPanel getVorlagenSeite() {
        anwendenButton = new JButton("anwenden");
        anwendenButton.setPreferredSize(new Dimension(70, 20));
        anwendenButton.addActionListener(this);
        anwendenButton.setActionCommand("anwenden");

        abbrechenButton = new JButton("abbrechen");
        abbrechenButton.setPreferredSize(new Dimension(70, 20));
        abbrechenButton.addActionListener(this);
        abbrechenButton.setActionCommand("abbrechen");

        colorSetCombo = new JComboBox<String>();
        colorSetCombo.addItem("akt. Einstellung");

        for (String sysdefname : SystemConfig.vSysDefNamen) {
            colorSetCombo.addItem(sysdefname);

        }

        colorSetCombo.setSelectedIndex(0);
        colorSetCombo.setActionCommand("defwechsel");
        colorSetCombo.addActionListener(this);
        String[] alf = { "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0" };
        alphawahl = new JComboBox(alf);
        alphawahl.setSelectedItem(new Float(TKSettings.KalenderAlpha).toString());
        alphawahl.setActionCommand("alpha");
        alphawahl.addActionListener(this);

        ftm = new KalenderFarbenModel();

        String[] dat = { "Code", "Bedeutung", "Hintergrund", "Schriftfarbe", "Darstellung" };
        ftm.setDataVector((Vector) SystemConfig.vSysColDlg.clone(), new Vector(Arrays.asList(dat)));
        FarbTab = new FarbTabelle(ftm);

        FarbTab.validate();

        JScrollPane listscr = new JScrollPane(FarbTab);
        listscr.setOpaque(true);
        listscr.getViewport()
               .setOpaque(true);
        listscr.validate();
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout lay = new FormLayout("p:g, 24dlu, 56dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "10dlu, p, 10dlu, 70dlu:g, 10dlu,p, 10dlu, p, 20dlu, 10dlu, 10dlu, p, 4dlu, p, 4dlu, p");

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        // builder.getPanel().setVisible(true);
        // builder.getPanel().validate();

        builder.addLabel("Farbset zur Bearbeitung auswählen", cc.xy(1, 2));
        builder.add(colorSetCombo, cc.xy(3, 2));

        builder.add(listscr, cc.xyw(1, 4, 3));

        builder.addLabel("Bearbeitung abbrechen, bisheriges Farbschema bleibt erhalten", cc.xy(1, 6));
        builder.add(abbrechenButton, cc.xy(3, 6));

        builder.addLabel("Farbschema auf Kalender anwenden", cc.xy(1, 8));
        builder.add(anwendenButton, cc.xy(3, 8));

        builder.addSeparator("Optional: Transparenz einstellen", cc.xyw(1, 10, 3));

        builder.addLabel("Transparenz wählen", cc.xy(1, 12));
        builder.add(alphawahl, cc.xy(3, 12));
        builder.addLabel("Der eingestellte Wert ist sofort im Kalender sichtbar.", cc.xy(1, 14));

        return builder.getPanel();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
        case "alpha":
            TerminFenster.setTransparenz(new Float((String) alphawahl.getSelectedItem()));
            break;
        case "defwechsel":
            setColorData(colorSetCombo.getSelectedIndex());
            break;
        case "defaultsave":
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    saveColorData(colorSetCombo.getSelectedIndex());

                }
            });
            break;
        case "anwenden":
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    saveColorData(0);

                }
            });
            break;
        case "abbrechen":
            SystemInit.abbrechen();
            break;
        }

    }

    private void saveColorData(int def) {
        try {
            String defName = ((String) colorSetCombo.getSelectedItem()).trim();
            int defNum = colorSetCombo.getSelectedIndex();
            int lang = SystemConfig.vSysColsNamen.size();
            Color hg, vg;
            String farbsplit;
            if (def == 0) {
                defName = "UserFarben";
            }

            Settings ini = INITool.openIni(Path.Instance.getProghome() + "ini/" + Reha.getAktIK() + "/", colorini);

            for (int i = 0; i < lang; i++) {
                hg = ((Color) FarbTab.getValueAt(i, 2));
                vg = ((Color) FarbTab.getValueAt(i, 3));
                farbsplit = Integer.toString(hg.getRed()) + "," + Integer.toString(hg.getGreen()) + ","
                        + Integer.toString(hg.getBlue()) + "," + Integer.toString(vg.getRed()) + ","
                        + Integer.toString(vg.getGreen()) + "," + Integer.toString(vg.getBlue());
                ini.setStringProperty(defName, SystemConfig.vSysColsNamen.get(i), farbsplit, null);
                ini.setStringProperty("Terminkalender", "FarbenBedeutung" + (i + 1),
                        ((String) FarbTab.getValueAt(i, 1)), null);
                SystemConfig.vSysColsObject.get(def)
                                           .set(i, new Color[] { hg, vg });
                SystemConfig.aktTkCol.put(SystemConfig.vSysColsNamen.get(i), new Color[] { hg, vg });

            }

            if (def == 0) {
                TKSettings.KalenderHintergrund = SystemConfig.aktTkCol.get("AusserAZ")[0];
            }
            TerminFenster.setTransparenz(Float.parseFloat((String) alphawahl.getSelectedItem()));
            SystemConfig.UpdateIni("terminkalender.ini", "Kalender", "KalenderHintergrundAlpha",
                    (String) alphawahl.getSelectedItem());
            INITool.saveIni(ini);
            ini = null;
            JOptionPane.showMessageDialog(null, "Konfiguration in color.ini erfolgreich gespeichert");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Speichern der Konfiguration in color.ini fehlgeschlagen");
        }
    }

    private void setColorData(int def) {
        try {
            int lang = FarbTab.getRowCount();
            int i;
            // System.out.println("in set color Data");
            for (i = 0; i < lang; i++) {
                FarbTab.setValueAt(SystemConfig.vSysColsObject.get(def)
                                                              .get(i)[0],
                        i, 2);
                FarbTab.setValueAt(SystemConfig.vSysColsObject.get(def)
                                                              .get(i)[1],
                        i, 3);
                FarbTab.validate();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Vector getColorData() {
        Vector vec = new Vector();
        JLabel BeispielDummi = new JLabel("so sieht's aus");
        int i, lang;
        lang = SystemConfig.vSysColsNamen.size();
        for (i = 0; i < lang; i++) {
            Vector ovec = new Vector();
            ovec.add(SystemConfig.vSysColsCode.get(i));
            ovec.add(SystemConfig.vSysColsBedeut.get(i));
            ovec.add(SystemConfig.vSysColsObject.get(0)
                                                .get(i)[0]);
            ovec.add(SystemConfig.vSysColsObject.get(0)
                                                .get(i)[1]);
            ovec.add(BeispielDummi);
            vec.add(ovec.clone());
        }
        return (Vector) vec.clone();
    }

    /********* Vor Ende Klassenklammer ***************/
}

class KalenderFarbenModel extends DefaultTableModel {

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex >= 2 && columnIndex <= 3) {
            return Color.class;
        } else if (columnIndex == 4) {
            return JLabel.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        switch (col) {
        case 2:
            return true;
        case 3:
            return true;
        case 4:
            return false;
        case 1:
            if (row >= 14 && row <= 22) {
                return true;
            }
            return false;
        default:
            return false;
        }

    }

}
