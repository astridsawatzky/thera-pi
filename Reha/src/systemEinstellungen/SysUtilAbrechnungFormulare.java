package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaRadioButton;
import CommonTools.JRtaTextField;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import hauptFenster.Reha;
import umfeld.Betriebsumfeld;

class SysUtilAbrechnungFormulare extends JXPanel
        implements KeyListener, ActionListener, ItemListener, SysInitCommon_If {
    private static final long serialVersionUID = 1L;


    private JRtaComboBox[] jcmb = { null, null, null, null, null, null, null };
    private JButton[] but = { null, null, null, null, null };
    private JRtaTextField[] tf = { null, null, null, null };
    private JRtaRadioButton[] rbut = { null, null, null, null, null, null, null, null };
    private JRtaCheckBox cbemail;
    private JRtaCheckBox ChkUseTmplPrinter;
    private String[] exemplare = { "0", "1", "2", "3", "4", "5" };
    private ButtonGroup bg = new ButtonGroup();
    private ButtonGroup bg2 = new ButtonGroup();



    private boolean enableTaxPrinterSelect = true, usePrinterFromTemplate;

    public SysUtilAbrechnungFormulare() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        initFields();



        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                doEinstellungen(); // ala readFromIni();
                setFields();
                return null;
            }
        }.execute();
        JScrollPane jscr = new JScrollPane();
        jscr.setBorder(null);
        jscr.setOpaque(false);
        jscr.getViewport()
            .setOpaque(false);
        jscr.getVerticalScrollBar()
            .setUnitIncrement(15);
        jscr.setViewportView(getVorlagenSeite());
        jscr.validate();
        add(jscr, BorderLayout.CENTER);
        AbbruchOderSpeichern footer = new AbbruchOderSpeichern(this);
        add(footer.getPanel(), BorderLayout.SOUTH);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                doEinstellungen(); // ändern ala readFromIni();
                setFields();
                return null;
            }
        }.execute();
    }

    private String[] availablePrinternames() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

          String[] drucker = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            drucker[i] = services[i].getName();
        }

        return drucker;
    }

    private void initFields() {
        ChkUseTmplPrinter = new JRtaCheckBox("Einstellung aus Vorlage nutzen");
        ChkUseTmplPrinter.addItemListener(this);
    }

    private void setFields() {
        ChkUseTmplPrinter.setSelected(usePrinterFromTemplate);
        enableTaxPrinterSelect = !usePrinterFromTemplate;
        jcmb[1].setEnabled(enableTaxPrinterSelect);
    }

    /** Beginn der Methode für die Objekterstellung und -platzierung. */
    private JPanel getVorlagenSeite() {
        String columnspecs = "right:max(80dlu;p), 20dlu, 120dlu, 4dlu, 40dlu";
        // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
        // 22. 23. 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46
        // 47 48 49
        String rowspecs = "p, 10dlu, p, 3dlu, p, 8dlu, p, 3dlu, p,  3dlu, p, 2dlu, p, 3dlu, p, 10dlu"
                + ", p, 10dlu, p, 3dlu, p, 3dlu, p, 10dlu, p,  10dlu, p,  3dlu , p, 3dlu, p, 10dlu, p,10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p";
        FormLayout lay = new FormLayout(columnspecs, // , 4dlu, 40dlu, 4dlu,
                                                                                          // 40dlu",
               
                rowspecs);

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();
        int rowCnt = 1;
        builder.addSeparator("Heilmittelabrechnung GKV", cc.xyw(1, rowCnt++, 5)); // 1,1
        builder.addLabel("Drucker für Taxierung", cc.xy(1, ++rowCnt)); // 1,3
        jcmb[1] = new JRtaComboBox(availablePrinternames());
        builder.add(jcmb[1], cc.xyw(3, rowCnt++, 3)); // 3,3

        builder.add(ChkUseTmplPrinter, cc.xyw(3, ++rowCnt, 3)); // 3,5
        ChkUseTmplPrinter.setOpaque(false);
        ChkUseTmplPrinter.setToolTipText("So können den Vorlagen verschiedene Drucker zugewiesen werden");
        rowCnt++;

        builder.addLabel("Rechnungsformular", cc.xy(1, ++rowCnt));
        tf[0] = new JRtaTextField("nix", false);
        // tf[0].setEditable(false);
        builder.add(tf[0], cc.xy(3, rowCnt));
        builder.add(but[0] = macheBut("auswaehlen", "gkvrechnwahl"), cc.xy(5, rowCnt++));

        builder.addLabel("Rechnungsdrucker", cc.xy(1, ++rowCnt));
        jcmb[0] = new JRtaComboBox(availablePrinternames());

        builder.add(jcmb[0], cc.xyw(3, rowCnt++, 3));

        builder.addLabel("folgende Ausdrucke erstellen", cc.xy(1, ++rowCnt));
        builder.add(rbut[0] = macheRadio("nur den Begleitzettel ausdrucken", "nurbegleitzettel"),
                cc.xyw(3, rowCnt++, 3));
        rbut[0].setOpaque(false);
        bg.add(rbut[0]);
        builder.add(rbut[1] = macheRadio("Begleitzettel und Rechnung ausdrucken", "beides"), cc.xyw(3, ++rowCnt, 3));
        rbut[1].setOpaque(false);
        bg.add(rbut[1]);
        rowCnt++;

        builder.addLabel("Rechnungsexemplare", cc.xy(1, ++rowCnt));
        jcmb[2] = new JRtaComboBox(exemplare);
        builder.add(jcmb[2], cc.xy(5, rowCnt++));




        builder.addSeparator("Heilmittelabrechnung Privatpatienten", cc.xyw(1, ++rowCnt, 5));
        rowCnt++;

        builder.addLabel("Rechnungsformular", cc.xy(1, ++rowCnt));
        tf[1] = new JRtaTextField("nix", false);
        builder.add(tf[1], cc.xy(3, rowCnt));
        builder.add(but[1] = macheBut("auswaehlen", "prirechnwahl"), cc.xy(5, rowCnt++));

        builder.addLabel("Rechnungsdrucker", cc.xy(1, ++rowCnt));
        jcmb[3] = new JRtaComboBox(availablePrinternames());
        builder.add(jcmb[3], cc.xyw(3, rowCnt++, 3));

        builder.addLabel("Rechnungsexemplare", cc.xy(1, ++rowCnt));
        jcmb[4] = new JRtaComboBox(exemplare);
        builder.add(jcmb[4], cc.xy(5, rowCnt++));

        builder.addSeparator("Heilmittelabrechnung Berufsgenossenschaft", cc.xyw(1, ++rowCnt, 5));
        rowCnt++;

        builder.addLabel("Rechnungsformular", cc.xy(1, ++rowCnt));
        tf[2] = new JRtaTextField("nix", false);
        // tf[0].setEditable(false);
        builder.add(tf[2], cc.xy(3, rowCnt));
        builder.add(but[2] = macheBut("auswaehlen", "bgerechnwahl"), cc.xy(5, rowCnt++));

        builder.addLabel("Rechnungsdrucker", cc.xy(1, ++rowCnt));
        jcmb[5] = new JRtaComboBox(availablePrinternames());
        builder.add(jcmb[5], cc.xyw(3, rowCnt++, 3));

        builder.addLabel("Rechnungsexemplare", cc.xy(1, ++rowCnt));
        jcmb[6] = new JRtaComboBox(exemplare);
        builder.add(jcmb[6], cc.xy(5, rowCnt++));





        builder.addSeparator("Gemeinsame Einstellungen", cc.xyw(1, ++rowCnt, 5));
        rowCnt++;

        builder.addLabel("alle Ausdrucke", cc.xy(1, ++rowCnt));
        builder.add(rbut[2] = macheRadio("direkt zum Drucker leiten", "druckdirekt"), cc.xyw(3, rowCnt++, 3));
        rbut[2].setOpaque(false);
        bg2.add(rbut[2]);
        builder.add(rbut[3] = macheRadio("im OpenOffice-Writer öffnen", "druckoffice"), cc.xyw(3, ++rowCnt, 3));
        rbut[3].setOpaque(false);
        bg2.add(rbut[3]);
        rowCnt++;

        builder.addLabel("Vor dem Versand der  302-er Mail", cc.xy(1, ++rowCnt));
        builder.add(cbemail = new JRtaCheckBox("immer fragen"), cc.xyw(3, rowCnt, 3));
        cbemail.setOpaque(false);

        return builder.getPanel();
    }


    private JButton macheBut(String titel, String cmd) {
        JButton but = new JButton(titel);
        but.setActionCommand(cmd);
        but.addActionListener(this);
        return but;
    }

    private JRtaRadioButton macheRadio(String titel, String cmd) {
        JRtaRadioButton but = new JRtaRadioButton(titel);
        but.setActionCommand(cmd);
        but.addActionListener(this);
        return but;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("gkvrechnwahl".equals(cmd)) {
            doWaehlen(tf[0]);
            return;
        }
        if ("prirechnwahl".equals(cmd)) {
            doWaehlen(tf[1]);
            return;
        }
        if ("bgerechnwahl".equals(cmd)) {
            doWaehlen(tf[2]);
            return;
        }
        if ("nurbegleitzettel".equals(cmd)) {
            jcmb[2].setSelectedIndex(0);
            jcmb[2].setEnabled(false);
            return;
        }
        if ("beides".equals(cmd)) {
            jcmb[2].setEnabled(true);
            return;
        }
        if ("abbrechen".equals(cmd)) {
            SystemInit.abbrechen();
            return;
        }
        if ("speichern".equals(cmd)) {
            doSpeichern();
        }
    }

    private void doEinstellungen() {
        tf[0].setText(SystemConfig.hmAbrechnung.get("hmgkvformular"));
        jcmb[0].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrechnungdrucker"));
        jcmb[1].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvtaxierdrucker"));
        String wert = SystemConfig.hmAbrechnung.get("hmgkvrauchdrucken");
        if ("1".equals(wert)) {
            rbut[1].setSelected(true);
        } else {
            rbut[0].setSelected(true);
        }
        jcmb[2].setSelectedItem(SystemConfig.hmAbrechnung.get("hmgkvrexemplare"));
        /* PRI */
        tf[1].setText(SystemConfig.hmAbrechnung.get("hmpriformular"));
        jcmb[3].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpridrucker"));
        jcmb[4].setSelectedItem(SystemConfig.hmAbrechnung.get("hmpriexemplare"));
        /* BGE */
        tf[2].setText(SystemConfig.hmAbrechnung.get("hmbgeformular"));
        jcmb[5].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgedrucker"));
        jcmb[6].setSelectedItem(SystemConfig.hmAbrechnung.get("hmbgeexemplare"));
        wert = SystemConfig.hmAbrechnung.get("hmallinoffice");
        if ("1".equals(wert)) {
            rbut[3].setSelected(true);
        } else {
            rbut[2].setSelected(true);
        }
        wert = SystemConfig.hmAbrechnung.get("hmaskforemail");
        cbemail.setSelected("1".equals(wert));
        if ("1".equals(wert)) {
            cbemail.setSelected(true);
        } else {
            cbemail.setSelected(false);
        }
        wert = SystemConfig.hmAbrechnung.get("hmusePrinterFromTemplate");
        usePrinterFromTemplate = "1".equals(wert);
    }

    private void doSpeichern() {
        try {
            String wert;
            Settings inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/",
                    "abrechnung.ini");
            inif.setStringProperty("HMGKVRechnung", "Rformular", tf[0].getText()
                                                                      .trim(),
                    null);
            inif.setStringProperty("HMGKVRechnung", "Rdrucker", ((String) jcmb[0].getSelectedItem()).trim(), null);
            inif.setStringProperty("HMGKVRechnung", "Tdrucker", ((String) jcmb[1].getSelectedItem()).trim(), null);
            wert = rbut[1].isSelected() ? "1" : "0";
            inif.setStringProperty("HMGKVRechnung", "Rauchdrucken", wert, null);
            inif.setStringProperty("HMGKVRechnung", "Rexemplare", (String) jcmb[2].getSelectedItem(), null);
            inif.setStringProperty("HMGKVRechnung", "usePrinterFromTemplate",
                    usePrinterFromTemplate ? "1" : "0", null);

            inif.setStringProperty("HMPRIRechnung", "Pformular", tf[1].getText()
                                                                      .trim(),
                    null);
            inif.setStringProperty("HMPRIRechnung", "Pdrucker", ((String) jcmb[3].getSelectedItem()).trim(), null);
            inif.setStringProperty("HMPRIRechnung", "Pexemplare", (String) jcmb[4].getSelectedItem(), null);

            inif.setStringProperty("HMBGERechnung", "Bformular", tf[2].getText()
                                                                      .trim(),
                    null);
            inif.setStringProperty("HMBGERechnung", "Bdrucker", ((String) jcmb[5].getSelectedItem()).trim(), null);
            inif.setStringProperty("HMBGERechnung", "Bexemplare", (String) jcmb[6].getSelectedItem(), null);
            wert = rbut[3].isSelected() ? "1" : "0";
            inif.setStringProperty("GemeinsameParameter", "InOfficeStarten", wert, null);
            wert = cbemail.isSelected() ? "1" : "0";
            inif.setStringProperty("GemeinsameParameter", "FragenVorEmail", wert, null);
            // useTmplPrinter speichern
            INITool.saveIni(inif);
            JOptionPane.showMessageDialog(null, "Die Werte wurden erfolgreich in abrechung.ini gespeichert");
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    SystemConfig.AbrechnungParameter();
                    return null;
                }
            }.execute();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Fehler beim speichern in die abrechnung.ini!!!");
        }
    }

    private void doWaehlen(JRtaTextField tf) {
        String s = dateiDialog();
        tf.setText(s);
    }

    private String dateiDialog() {
        final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(Path.Instance.getProghome() + "/vorlagen/" + Betriebsumfeld.getAktIK());

        chooser.setCurrentDirectory(file);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())
                        || JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(e.getPropertyName())) {
                }
            }
        });
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();

            if ("".equals(inputVerzFile.getName()
                             .trim())) {
                return "";
            } else {
                return inputVerzFile.getName()
                                    .trim();
            }
        } else {
            return "";
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source == ChkUseTmplPrinter) {
            usePrinterFromTemplate = e.getStateChange() == ItemEvent.SELECTED;
            SystemConfig.hmAbrechnung.put("hmusePrinterFromTemplate", usePrinterFromTemplate ? "1" : "0");
        }
        setFields();
        validate();
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
