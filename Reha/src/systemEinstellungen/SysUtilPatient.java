package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaTextField;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import gui.Cursors;
import hauptFenster.Reha;
import umfeld.Betriebsumfeld;

public class SysUtilPatient extends JXPanel
        implements  ActionListener,  SysInitCommon_If {

    JButton[] button = new JButton[11];
    JRtaTextField vorlage = null;
    JRtaTextField[] krit = new JRtaTextField[6];
    JRtaTextField[] icon = new JRtaTextField[6];
    JRadioButton oben = null;
    JRadioButton unten = null;
    JCheckBox optimize = null;
    ButtonGroup bgroup = new ButtonGroup();
    JLabel datLabel = null;
    JLabel[] kritlab =  new JLabel[6];
    SysUtilVorlagen vorlagen = null;
    boolean formok = true;

    public SysUtilPatient() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));

        SwingUtilities.invokeLater(() -> setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit")));

        JScrollPane jscr = new JScrollPane();
        jscr.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        jscr.setOpaque(false);
        jscr.getViewport()
            .setOpaque(false);
        jscr.setViewportView(getVorlagenSeite());
        jscr.getVerticalScrollBar()
            .setUnitIncrement(15);
        jscr.validate();

        add(jscr, BorderLayout.CENTER);
        AbbruchOderSpeichern footer = new AbbruchOderSpeichern(this);
        this.add(footer.getPanel(), BorderLayout.SOUTH);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                fuelleMitWerten();
                return null;
            }
        }.execute();

        return;
    }

    private void fuelleMitWerten() {
        if (!formok) {
            return;
        }

        if (SystemConfig.hmContainer.get("Patient") == 0) {
            oben.setSelected(true);
        } else {
            unten.setSelected(true);
        }
        if (SystemConfig.hmContainer.get("PatientOpti") == 0) {
            optimize.setSelected(false);
        } else {
            optimize.setSelected(true);
        }

        vorlagen.readFromIni();
        Settings inif = vorlagen.getInif();

        for (int i = 0; i < 6; i++) {
            krit[i].setText(SystemConfig.vPatMerker.get(i));
            String sico = "";
            if (SystemConfig.vPatMerkerIcon.get(i) == null) {
                sico = "";
            } else {
                sico = inif.getStringProperty("Kriterien", "Image" + (i + 1));
                // Name aus .ini lesen
                                                                               // (vPatMerkerIconFile enthält Pfad)
                kritlab[i].setIcon(SystemConfig.vPatMerkerIcon.get(i));
            }
            icon[i].setText(sico);
            icon[i].setEditable(false);
        }
        for (int i = 0; i < 9; i++) {
            button[i].addActionListener(this);
        }
    }

    /**************
     * Beginn der Methode f�r die Objekterstellung und -platzierung
     *********/
    private JPanel getVorlagenSeite() {

        oben = new JRadioButton();
        bgroup.add(oben);
        unten = new JRadioButton();
        bgroup.add(unten);
        optimize = new JCheckBox();

        vorlagen = new SysUtilVorlagen(this);
        vorlagen.setVPfad(Path.Instance.getProghome() + "vorlagen/" + Betriebsumfeld.getAktIK());
        vorlagen.setIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK(), "patient.ini");
        vorlagen.setLabels("Formulare", "PatientFormulareAnzahl", "PFormular");
        vorlagen.activateEditing();

        krit[0] = new JRtaTextField("", true);
        krit[1] = new JRtaTextField("", true);
        krit[2] = new JRtaTextField("", true);
        krit[3] = new JRtaTextField("", true);
        krit[4] = new JRtaTextField("", true);
        krit[5] = new JRtaTextField("", true);
        button[0] = new JButton("auswählen");
        button[0].setActionCommand("iwahl0");
        button[1] = new JButton("auswählen");
        button[1].setActionCommand("iwahl1");
        button[2] = new JButton("auswählen");
        button[2].setActionCommand("iwahl2");
        button[3] = new JButton("auswählen");
        button[3].setActionCommand("iwahl3");
        button[4] = new JButton("auswählen");
        button[4].setActionCommand("iwahl4");
        button[5] = new JButton("auswählen");
        button[5].setActionCommand("iwahl5");
        icon[0] = new JRtaTextField("", true);
        icon[1] = new JRtaTextField("", true);
        icon[2] = new JRtaTextField("", true);
        icon[3] = new JRtaTextField("", true);
        icon[4] = new JRtaTextField("", true);
        icon[5] = new JRtaTextField("", true);
        kritlab[0] = new JLabel("1. Icon");
        kritlab[1] = new JLabel("2. Icon");
        kritlab[2] = new JLabel("3. Icon");
        kritlab[3] = new JLabel("4. Icon");
        kritlab[4] = new JLabel("5. Icon");
        kritlab[5] = new JLabel("6. Icon");
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        // FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu, 40dlu, 40dlu,
        // 4dlu, 40dlu,0dlu",
        // 1. 2. 3. 4. 5. 6. 7.
        FormLayout lay = new FormLayout("right:max(120dlu;p), 15dlu, 40dlu:g, 4dlu, 45dlu, 4dlu, 10dlu, 15dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19.
                "p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 2dlu , p, 5dlu, p, 2dlu, p, 5dlu,p, 2dlu, p, "
                        // 20. 21. 22. 23. 24 25 26 27 28 29 30 31 32
                        + "5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 2dlu, p, 10dlu");
        PanelBuilder builder = new PanelBuilder(lay);
        // PanelBuilder builder = new PanelBuilder(lay, new FormDebugPanel()); // debug
        // mode

        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();
        int rowCnt = 1;

        builder.addLabel("Fenster startet im ...", cc.xy(1, rowCnt, CellConstraints.LEFT, CellConstraints.BOTTOM));
        builder.addLabel((SystemConfig.desktopHorizontal ? "oberen" : "linken") + " Container",
                cc.xyw(4, rowCnt, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.add(oben, cc.xy(7, rowCnt++, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel((SystemConfig.desktopHorizontal ? "unteren" : "rechten") + " Container",
                cc.xyw(4, ++rowCnt, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.add(unten, cc.xy(7, rowCnt++, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Fenstergröße automatisch optimieren", cc.xy(1, ++rowCnt));
        builder.add(optimize, cc.xy(7, rowCnt++, CellConstraints.RIGHT, CellConstraints.BOTTOM));

        builder.add(vorlagen.getPanel(), cc.xyw(1, ++rowCnt, 8));

        rowCnt = 9;
        builder.addSeparator("Kriteriendefinitionen / Icons", cc.xyw(1, rowCnt++, 7));
        builder.addLabel("1. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[0], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[0], cc.xy(1, ++rowCnt));
        builder.add(icon[0], cc.xy(3, rowCnt));
        builder.add(button[0], cc.xyw(5, rowCnt++, 3));

        builder.addLabel("2. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[1], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[1], cc.xy(1, ++rowCnt));
        builder.add(icon[1], cc.xy(3, rowCnt));
        builder.add(button[1], cc.xyw(5, rowCnt++, 3));

        builder.addLabel("3. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[2], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[2], cc.xy(1, ++rowCnt));
        builder.add(icon[2], cc.xy(3, rowCnt));
        builder.add(button[2], cc.xyw(5, rowCnt++, 3));

        builder.addLabel("4. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[3], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[3], cc.xy(1, ++rowCnt));
        builder.add(icon[3], cc.xy(3, rowCnt));
        builder.add(button[3], cc.xyw(5, rowCnt++, 3));

        builder.addLabel("5. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[4], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[4], cc.xy(1, ++rowCnt));
        builder.add(icon[4], cc.xy(3, rowCnt));
        builder.add(button[4], cc.xyw(5, rowCnt++, 3));

        builder.addLabel("6. Kriterium", cc.xy(1, ++rowCnt));
        builder.add(krit[5], cc.xyw(3, rowCnt++, 5));
        builder.add(kritlab[5], cc.xy(1, ++rowCnt));
        builder.add(icon[5], cc.xy(3, rowCnt));
        builder.add(button[5], cc.xyw(5, rowCnt++, 3));

        return builder.getPanel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();
        for (int i = 0; i < 1; i++) {

            if (cmd.contains("iwahl")) {
                int wahl = Integer.valueOf(cmd.substring(cmd.length() - 1));
                setCursor(Cursors.wartenCursor);
                String sicon = dateiDialog(Path.Instance.getProghome() + "icons/");
                if (!sicon.equals("")) {
                    icon[wahl].setText(sicon);
                    kritlab[wahl].setIcon(new ImageIcon(Path.Instance.getProghome() + "icons/" + sicon));
                } else {
                    icon[wahl].setText("");
                    kritlab[wahl].setIcon(null);
                }
                break;
            }
        }
    }

    private void doSpeichern() {
        try {
            String wert = "";
            Settings inif = vorlagen.getInif();

            wert = (unten.isSelected() ? "1" : "0");
            SystemConfig.hmContainer.put("Patient", Integer.valueOf(wert));
            inif.setStringProperty("Container", "StarteIn", wert, null);

            wert = (optimize.isSelected() ? "1" : "0");
            SystemConfig.hmContainer.put("PatientOpti", Integer.valueOf(wert));
            inif.setStringProperty("Container", "ImmerOptimieren", wert, null);

            formok = vorlagen.saveToIni();

            for (int i = 0; i < 6; i++) {
                wert = krit[i].getText();
                inif.setStringProperty("Kriterien", "Krit" + (i + 1), wert, null);
                SystemConfig.vPatMerker.set(i, wert);

                wert = icon[i].getText();
                inif.setStringProperty("Kriterien", "Image" + (i + 1), icon[i].getText(), null);
                SystemConfig.vPatMerkerIcon.set(i,
                        (wert.equals("") ? null : new ImageIcon(Path.Instance.getProghome() + "icons/" + wert)));
                SystemConfig.vPatMerkerIconFile.set(i,
                        (wert.equals("") ? null : Path.Instance.getProghome() + "icons/" + wert));

            }
            INITool.saveIni(inif);
            JOptionPane.showMessageDialog(null, "Konfiguration erfolgrein in patient.ini gespeichert.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler beim speichern der Konfiguration in patient.ini!!!!");
        }
    }

    private String dateiDialog(String pfad) {
        String sret = "";
        final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(pfad);

        chooser.setCurrentDirectory(file);

        chooser.setVisible(true);
        setCursor(Cursors.normalCursor);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            sret = inputVerzFile.getName()
                                .trim();
        }

        return sret;
    }

    /**********************************************/
    class TitelEditor extends AbstractCellEditor implements TableCellEditor {
        Object value;
        JComponent component = new JFormattedTextField();

        public TitelEditor() {
            component.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent arg0) {
                    //// System.out.println("********Button in KeyPressed*********");
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        arg0.consume();
                        stopCellEditing();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            ((JFormattedTextField) component).setText((String) value);
            ((JFormattedTextField) component).requestFocus();
            ((JFormattedTextField) component).setCaretPosition(0);
            return component;
        }

        @Override
        public Object getCellEditorValue() {

            return ((JFormattedTextField) component).getText();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() == 2;
            }

            return true;
        }


        @Override
        public boolean stopCellEditing() {
            value = ((JFormattedTextField) component).getText();
            super.stopCellEditing();
            return true;
        }

        public boolean startCellEditing() {
            return false;
        }

    }

       @Override
    public void Abbruch() {
        SystemInit.abbrechen();
    }

    @Override
    public void Speichern() {
        doSpeichern();
    }

    @Override
    public void AddEntry(int instanceNb) {
    }

    @Override
    public void RemoveEntry(int instanceNb) {
    }

}
