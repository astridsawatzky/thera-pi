package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jdesktop.swingx.JXPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JRtaCheckBox;
import CommonTools.JRtaComboBox;
import CommonTools.JRtaTextField;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import hauptFenster.Reha;
import umfeld.Betriebsumfeld;

public class SysUtilRezepte extends JXPanel implements KeyListener, ActionListener, SysInitCommon_If {

    JButton[] button = { null, null, null, null, null, null, null };
    JRtaCheckBox[] heilmittel = { null, null, null, null, null, null, null, null };
    JRtaCheckBox angelegtVonUser = null;
    JRtaCheckBox warnungBeiRezGeb = null;
    JRtaComboBox voreinstellung = null;
    JComboBox druckername = null;
    JComboBox barcodedrucker = null;
    PrintService[] services = null;
    String[] drucker = null;
    // JXTable vorlagen = null;
    JLabel datLabel = null;
    JRtaTextField vorlage = null;
    MyVorlagenTableModel modvorl = null;

    SysUtilVorlagen vorlagen = null;
    boolean formok = true;

    public SysUtilRezepte() {

        super(new BorderLayout());
        // System.out.println("Aufruf SysUtilRezepte");
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 20));
        /****/
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        /****/
        services = PrintServiceLookup.lookupPrintServices(null, null);
        drucker = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            drucker[i] = services[i].getName();
        }
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
//      add(getKnopfPanel(),BorderLayout.SOUTH);
        AbbruchOderSpeichern footer = new AbbruchOderSpeichern(this);
        this.add(footer.getPanel(), BorderLayout.SOUTH);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                fuelleMitWerten();
                datLabel = new JLabel();
                vorlage = new JRtaTextField("NIX", true);
                return null;
            }

        }.execute();

        return;
    }

    private void fuelleMitWerten() {
        int aktiv;
        Settings inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "rezept.ini");
        if (SystemConfig.mitRs) {
            for (int i = 0; i < 8; i++) {
                aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv" + Integer.valueOf(i + 1)
                                                                                        .toString());
                if (aktiv > 0) {
                    heilmittel[i].setSelected(true);
                } else {
                    heilmittel[i].setSelected(false);
                }

            }
        } else {
            for (int i = 0; i < 6; i++) {
                aktiv = inif.getIntegerProperty("RezeptKlassen", "KlasseAktiv" + Integer.valueOf(i + 1)
                                                                                        .toString());
                if (aktiv > 0) {
                    heilmittel[i].setSelected(true);
                } else {
                    heilmittel[i].setSelected(false);
                }
            }
            heilmittel[6].setEnabled(false);
            heilmittel[7].setEnabled(false);

        }

        angelegtVonUser.setSelected(SystemConfig.AngelegtVonUser);

        warnungBeiRezGeb.setSelected(SystemConfig.RezGebWarnung);

        voreinstellung.setSelectedItem(SystemConfig.initRezeptKlasse);

        /*
         * int forms = inif.getIntegerProperty("Formulare", "RezeptFormulareAnzahl");
         * Vector<String> vec = new Vector<String>(); for (int i = 1; i <= forms; i++) {
         * vec.clear(); vec.add(inif.getStringProperty("Formulare", "RFormularText" +
         * i)); vec.add(inif.getStringProperty("Formulare", "RFormularName" + i));
         * modvorl.addRow((Vector) vec.clone()); } if (modvorl.getRowCount() > 0) {
         * vorlagen.setRowSelectionInterval(0, 0); } vorlagen.validate();
         */
        vorlagen.readFromIni();
    }

    /**************
     * Beginn der Methode f�r die Objekterstellung und -platzierung
     *********/
    private JPanel getVorlagenSeite() {

        for (int i = 0; i < 8; i++) {
            heilmittel[i] = new JRtaCheckBox();
        }
        voreinstellung = new JRtaComboBox(SystemConfig.rezeptKlassen);

        // 1. 2. 3. 4. 5. 6. 7.
        FormLayout lay = new FormLayout("right:max(120dlu;p), 20dlu:g, 40dlu, 70dlu, 4dlu, 10dlu,15dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20.
                "p, 2dlu, p,  2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p  ,2dlu, p  ,2dlu, p  ,10dlu, p,  10dlu,"
                        // 21. 22. 23. 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40
                        + "p, 10dlu, p, 10dlu, p,  10dlu, p, 10dlu, p, 10dlu,p, 10dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 10dlu");

        PanelBuilder builder = new PanelBuilder(lay);
        // PanelBuilder builder = new PanelBuilder(lay, new FormDebugPanel()); // debug
        // mode
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();
        builder.addLabel("Kreuzen Sie an welche Praxisart(en) Sie betreiben", cc.xyw(1, 1, 6));
        builder.addLabel("Physio-Praxis", cc.xyw(4, 3, 2));
        builder.add(heilmittel[0], cc.xy(6, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Massage-Praxis", cc.xyw(4, 5, 2));
        builder.add(heilmittel[1], cc.xy(6, 5, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Ergo-Praxis", cc.xyw(4, 7, 2));
        builder.add(heilmittel[2], cc.xy(6, 7, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Logo-Praxis", cc.xyw(4, 9, 2));
        builder.add(heilmittel[3], cc.xy(6, 9, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Reha-Zentrum", cc.xyw(4, 11, 2));
        builder.add(heilmittel[4], cc.xy(6, 11, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Podologische-Praxis", cc.xyw(4, 13, 2));
        builder.add(heilmittel[5], cc.xy(6, 13, CellConstraints.RIGHT, CellConstraints.BOTTOM));

        builder.addLabel("Rehasport", cc.xyw(4, 15, 2));
        builder.add(heilmittel[6], cc.xy(6, 15, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.addLabel("Funktionstraining", cc.xyw(4, 17, 2));
        builder.add(heilmittel[7], cc.xy(6, 17, CellConstraints.RIGHT, CellConstraints.BOTTOM));

        builder.addSeparator("Voreinstellung bei Rezeptanlage", cc.xyw(1, 19, 6));

        builder.addLabel("Rezeptklasse", cc.xy(1, 21));
        builder.add(voreinstellung, cc.xyw(3, 21, 4));

        builder.addSeparator("Quittungsdrucker f. Rezeptgebühren", cc.xyw(1, 23, 6));

        builder.addLabel("Drucker auswählen", cc.xy(1, 25));
        druckername = new JComboBox(drucker);
        if (SystemConfig.rezGebDrucker.trim()
                                      .equals("")) {
            druckername.setSelectedIndex(0);
        } else {
            druckername.setSelectedItem(SystemConfig.rezGebDrucker.trim());
        }
        builder.add(druckername, cc.xyw(3, 25, 4));

        builder.addSeparator("Barcodedrucker (nur sofern Sie Barcode verwenden)", cc.xyw(1, 27, 6));
        builder.addLabel("Drucker auswählen", cc.xy(1, 29));
        barcodedrucker = new JComboBox(drucker);
        if (SystemConfig.rezBarcodeDrucker.trim()
                                          .equals("")) {
            barcodedrucker.setSelectedIndex(0);
        } else {
            barcodedrucker.setSelectedItem(SystemConfig.rezBarcodeDrucker.trim());
        }
        builder.add(barcodedrucker, cc.xyw(3, 29, 4));
        /*
         * builder.addSeparator("Vorlagen - Verwaltung", cc.xyw(1, 31, 6));
         * 
         * modvorl = new MyVorlagenTableModel(); modvorl.setColumnIdentifiers(new
         * String[] { "Titel der Vorlage", "Vorlagendatei" }); vorlagen = new
         * JXTable(modvorl); vorlagen.getColumn(0) .setCellEditor(new TitelEditor());
         * vorlagen.setSortable(false); vorlagen.addMouseListener(new MouseAdapter() {
         * 
         * @Override public void mouseClicked(MouseEvent arg0) {
         * 
         * if (arg0.getClickCount() == 2 && arg0.getButton() == 1) { int row =
         * vorlagen.getSelectedRow(); row = vorlagen.convertRowIndexToModel(row); int
         * col = vorlagen.getSelectedColumn(); if (col == 1) {
         * setCursor(Cursors.wartenCursor); String svorlage =
         * dateiDialog(Path.Instance.getProghome() + "vorlagen/" + Reha.getAktIK()); if
         * (svorlage.equals("")) { return; } modvorl.setValueAt(svorlage, row, col);
         * vorlagen.validate(); } } } });
         * 
         * JScrollPane jscr = JCompTools.getTransparentScrollPane(vorlagen);
         * jscr.validate(); builder.add(jscr, cc.xyw(1, 33, 6));
         * 
         * JPanel butPan = JCompTools.getEmptyJXPanel(); butPan.setOpaque(false);
         * FormLayout lay2 = new
         * FormLayout("fill:0:grow(1.0),right:max(120dlu;p),4dlu,right:40dlu",
         * "p,2dlu,p"); butPan.setLayout(lay2); CellConstraints cc2 = new
         * CellConstraints(); button[1] = new JButton("entfernen");
         * button[1].setActionCommand("entfernen"); button[1].addActionListener(this);
         * button[2] = new JButton("hinzufügen");
         * button[2].setActionCommand("vorlagenneu"); button[2].addActionListener(this);
         * butPan.add(new JLabel("aus Liste entfernen"), cc2.xy(2, 1));
         * butPan.add(button[1], cc2.xy(4, 1)); butPan.add(new
         * JLabel("neue Vorlagendatei hinzufügen"), cc2.xy(2, 3)); butPan.add(button[2],
         * cc2.xy(4, 3)); butPan.validate(); builder.add(butPan, cc.xyw(1, 37, 6));
         */
        vorlagen = new SysUtilVorlagen(this);
        vorlagen.setVPfad(Path.Instance.getProghome() + "vorlagen/" + Betriebsumfeld.getAktIK());

        vorlagen.setIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK(), "rezept.ini");
        vorlagen.setLabels("Formulare", "RezeptFormulareAnzahl", "RFormular");
        vorlagen.activateEditing();

        builder.add(vorlagen.getPanel(), cc.xyw(1, 31, 7));

        builder.addSeparator("Sonstiges", cc.xyw(1, 35, 6));
        builder.addLabel("Angelegt von = aktueller User", cc.xy(1, 37));

        angelegtVonUser = new JRtaCheckBox();
        builder.add(angelegtVonUser, cc.xy(6, 37));

        builder.addLabel("Signalton bei nicht bezahlten Rezeptgebühren", cc.xy(1, 39));
        warnungBeiRezGeb = new JRtaCheckBox();
        builder.add(warnungBeiRezGeb, cc.xy(6, 39));

        return builder.getPanel();
    }

    /*
     * private JPanel getKnopfPanel() { button[5] = new JButton("abbrechen");
     * button[5].setActionCommand("abbrechen"); button[5].addActionListener(this);
     * button[6] = new JButton("speichern");
     * button[6].setActionCommand("speichern"); button[6].addActionListener(this);
     * // 1. 2. 3. 4. 5. 6. 7. 8. 9. FormLayout jpanlay = new
     * FormLayout("right:max(126dlu;p), 60dlu, 40dlu, 4dlu, 40dlu", // 1. 2. 3. 4.
     * 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21. // 22. 23.
     * "p, 10dlu, p");
     * 
     * PanelBuilder jpan = new PanelBuilder(jpanlay); jpan.getPanel()
     * .setOpaque(false); CellConstraints jpancc = new CellConstraints();
     * 
     * jpan.addSeparator("", jpancc.xyw(1, 1, 5)); jpan.add(button[5], jpancc.xy(3,
     * 3)); jpan.add(button[6], jpancc.xy(5, 3));
     * jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1, 3));
     * 
     * return jpan.getPanel(); }
     */
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
        /*
         * if (cmd.equals("vorlagenneu")) { setCursor(Cursors.wartenCursor); String
         * svorlage = dateiDialog(Path.Instance.getProghome() + "vorlagen/" +
         * Reha.getAktIK()); if (!svorlage.equals("")) { datLabel.setText(svorlage); }
         * else { datLabel.setText(""); return; }
         * 
         * if (vorlage.getText() .equals("") || datLabel.getText() .equals("")) {
         * JOptionPane.showMessageDialog(null,
         * "Geben Sie jetzt einen Titel für die neue Text-Vorlage ein"); // return; }
         * Vector vec = new Vector(); vec.add(""); vec.add(datLabel.getText());
         * modvorl.addRow((Vector) vec.clone()); vorlagen.validate(); int rows =
         * modvorl.getRowCount();
         * 
         * // vorlagen.setCellSelectionEnabled(true); final int xrows = rows - 1;
         * SwingUtilities.invokeLater(new Runnable() {
         * 
         * @Override public void run() { vorlagen.requestFocus();
         * vorlagen.setRowSelectionInterval(xrows, xrows); startCellEditing(xrows); }
         * });
         * 
         * // vorlage.setText(""); // datLabel.setText(""); return; } if
         * (cmd.equals("entfernen")) { int row = vorlagen.getSelectedRow(); int frage =
         * JOptionPane.showConfirmDialog(null,
         * "Wollen Sie die ausgewählte Tabellenzeile wirklich löschen?",
         * "Wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION); if (frage ==
         * JOptionPane.NO_OPTION) { return; } if (row >= 0) {
         * TableTool.loescheRow(vorlagen, row); } return; }
         */
    }

    /*
     * private void startCellEditing(int row) { final int xrows = row;
     * SwingUtilities.invokeLater(new Runnable() {
     * 
     * @Override public void run() { vorlagen.editCellAt(xrows, 0); } }); }
     * 
     * private String dateiDialog(String pfad) { String sret = ""; final
     * JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
     * chooser.setDialogType(JFileChooser.OPEN_DIALOG);
     * chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); final File
     * file = new File(pfad);
     * 
     * chooser.setCurrentDirectory(file);
     * 
     * chooser.addPropertyChangeListener(new PropertyChangeListener() {
     * 
     * @Override public void propertyChange(PropertyChangeEvent e) { if
     * (e.getPropertyName() .equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) ||
     * e.getPropertyName() .equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) { final
     * File f = (File) e.getNewValue(); } } }); chooser.setVisible(true);
     * setCursor(Cursors.normalCursor); final int result =
     * chooser.showOpenDialog(null);
     * 
     * if (result == JFileChooser.APPROVE_OPTION) { File inputVerzFile =
     * chooser.getSelectedFile(); // String inputVerzStr = inputVerzFile.getPath();
     * 
     * if (inputVerzFile.getName() .trim() .equals("")) { sret = ""; } else { sret =
     * inputVerzFile.getName() .trim(); } } else { sret = ""; //
     * vorlagenname.setText(SystemConfig.oTerminListe.NameTemplate); }
     * chooser.setVisible(false);
     * 
     * return sret; }
     */

    private void doSpeichern() {
        try {
            String wert = "";
            int iwert;
            // INIFile inif =
            // INITool.openIni(Path.Instance.getProghome()+"ini/"+Reha.getAktIK()+"/",
            // "rezept.ini");
            Settings inif = vorlagen.getInif();
            inif.setStringProperty("RezeptKlassen", "InitKlasse", (String) voreinstellung.getSelectedItem(), null);
            /*
             * if (!SystemConfig.mitRs) { for (int i = 0; i < 6; i++) { iwert =
             * (heilmittel[i].isSelected() ? 1 : 0);
             * inif.setIntegerProperty("RezeptKlassen", "KlasseAktiv" + Integer.valueOf(i +
             * 1) .toString(), iwert, null);
             * 
             * } } else { for (int i = 0; i < 8; i++) { iwert = (heilmittel[i].isSelected()
             * ? 1 : 0); inif.setIntegerProperty("RezeptKlassen", "KlasseAktiv" +
             * Integer.valueOf(i + 1) .toString(), iwert, null);
             * 
             * } }
             */
            int nbOfClasses = 6;
            if (SystemConfig.mitRs) {
                nbOfClasses = 8;
            }
            for (int i = 0; i < nbOfClasses; i++) {
                iwert = (heilmittel[i].isSelected() ? 1 : 0);
                inif.setIntegerProperty("RezeptKlassen", "KlasseAktiv" + Integer.valueOf(i + 1)
                                                                                .toString(),
                        iwert, null);

            }

            SystemConfig.rezGebDrucker = (String) druckername.getSelectedItem();
            inif.setStringProperty("DruckOptionen", "RezGebDrucker", (String) druckername.getSelectedItem(), null);

            SystemConfig.rezBarcodeDrucker = (String) barcodedrucker.getSelectedItem();
            inif.setStringProperty("DruckOptionen", "BarCodeDrucker", (String) barcodedrucker.getSelectedItem(), null);

            formok = vorlagen.saveToIni();
            /*
             * int rows = vorlagen.getRowCount();
             * 
             * boolean formok = true; for (int i = 0; i < rows; i++) { String test =
             * (String) vorlagen.getValueAt(i, 0); if (test.equals("")) { String datei =
             * (String) vorlagen.getValueAt(i, 1); String msg = "Für Vorlagendatei " + datei
             * +
             * " wurde kein Titel eingegeben!\nDie Vorlagen werden nicht(!!!) gespeichert.";
             * JOptionPane.showMessageDialog(null, msg); formok = false; break; } else {
             * formok = true; } } if (formok) { inif.setStringProperty("Formulare",
             * "RezeptFormulareAnzahl", Integer.valueOf(rows) .toString(), null); for (int i
             * = 0; i < rows; i++) { inif.setStringProperty("Formulare", "RFormularText" +
             * (i + 1), (String) vorlagen.getValueAt(i, 0), null);
             * inif.setStringProperty("Formulare", "RFormularName" + (i + 1), (String)
             * vorlagen.getValueAt(i, 1), null); } }
             */
            SystemConfig.AngelegtVonUser = angelegtVonUser.isSelected();
            inif.setStringProperty("Sonstiges", "AngelegtVonUser", (angelegtVonUser.isSelected() ? "1" : "0"), null);

            SystemConfig.RezGebWarnung = warnungBeiRezGeb.isSelected();
            inif.setStringProperty("Sonstiges", "RezGebWarnung", (warnungBeiRezGeb.isSelected() ? "1" : "0"), null);

            INITool.saveIni(inif);
            SystemConfig.RezeptInit();

            JOptionPane.showMessageDialog(null, "Konfiguration in rezept.ini erfolgreich gespeichert");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Speichern der Konfiguration in rezept.ini fehlgeschlagen");
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
        // TODO Auto-generated method stub

    }

    @Override
    public void RemoveEntry(int instanceNb) {
        // TODO Auto-generated method stub

    }

}

class MyVorlagenTableModel extends DefaultTableModel {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return true;
        } else {
            return false;
        }
    }

}

/**********************************************/
class TitelEditor extends AbstractCellEditor implements TableCellEditor {
    Object value;
    JComponent component = new JFormattedTextField();

    public TitelEditor() {
        // component = new JRtaTextField("NIX",true);
        // System.out.println("editor-Component wurde initialisiert");
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                // System.out.println("********Button in KeyPressed*********");
                if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    arg0.consume();
                    stopCellEditing();
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ((JFormattedTextField) component).setText((String) value);
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
            MouseEvent me = (MouseEvent) anEvent;
            if (me.getClickCount() != 2) {
                return false;
            }
        }
        // System.out.println("isCellEditable");
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        // System.out.println("in schouldCellSelect"+anEvent);
        return super.shouldSelectCell(anEvent);
    }

    @Override
    public boolean stopCellEditing() {
        value = ((JFormattedTextField) component).getText();
        // System.out.println("in stopCellediting");
        super.stopCellEditing();
        return true;
    }

    public boolean startCellEditing() {
        return false;// super.startCellEditing();//false;
    }

}
