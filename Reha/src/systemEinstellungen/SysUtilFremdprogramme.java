package systemEinstellungen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.JCompTools;
import CommonTools.ini.INITool;
import CommonTools.ini.Settings;
import environment.Path;
import gui.Cursors;
import hauptFenster.Reha;
import office.OOService;
import umfeld.Betriebsumfeld;

public class SysUtilFremdprogramme extends JXPanel implements KeyListener, ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JXTable progtab = null;
    MyProgTableModel modprog = new MyProgTableModel();

    JButton[] button = { null, null, null, null, null, null, null, null, null, null, null, null, null };
    JTextField oopfad = null;
    JTextField adobepfad = null;
    JTextField grafpfad = null;
    String lpfad = null;

    public SysUtilFremdprogramme() {

        super(new BorderLayout());
        // System.out.println("Aufruf SysUtilFremdprogramme");
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        /****/
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        /****/
        add(getVorlagenSeite(), BorderLayout.CENTER);
        add(getKnopfPanel(), BorderLayout.SOUTH);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                validate();
            }
        });

        return;
    }

    JButton abbruch = null;
    JButton speichern = null;

    private JPanel getKnopfPanel() {

        abbruch = new JButton("abbrechen");
        abbruch.setActionCommand("abbrechen");
        abbruch.addActionListener(this);
        speichern = new JButton("speichern");
        speichern.setActionCommand("speichern");
        speichern.addActionListener(this);

        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout jpanlay = new FormLayout("right:max(150dlu;p), 60dlu:g, 60dlu, 4dlu, 60dlu,0dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "p, 10dlu, p");

        PanelBuilder jpan = new PanelBuilder(jpanlay);
        jpan.getPanel()
            .setOpaque(false);
        CellConstraints jpancc = new CellConstraints();

        jpan.addSeparator("", jpancc.xyw(1, 1, 5));
        jpan.add(abbruch, jpancc.xy(3, 3));
        jpan.add(speichern, jpancc.xy(5, 3));
        jpan.addLabel("Änderungen übernehmen?", jpancc.xy(1, 3));

        jpan.getPanel()
            .validate();
        return jpan.getPanel();
    }

    /**************
     * Beginn der Methode f�r die Objekterstellung und -platzierung
     *********/
    private JPanel getVorlagenSeite() {

        button[0] = new JButton("entfernen");
        button[0].setActionCommand("entfernen");
        button[0].addActionListener(this);
        button[1] = new JButton("hinzufügen");
        button[1].setActionCommand("hinzufuegen");
        button[1].addActionListener(this);
        button[2] = new JButton("auswählen");
        button[2].setActionCommand("oopfad");
        button[2].addActionListener(this);
        button[3] = new JButton("auswählen");
        button[3].setActionCommand("adobepfad");
        button[3].addActionListener(this);
        button[4] = new JButton("auswählen");
        button[4].setActionCommand("grafpfad");
        button[4].addActionListener(this);

        // progtab = new JXTable();
        oopfad = new JTextField();
        oopfad.setText(OOService.OpenOfficePfad);
        adobepfad = new JTextField();
        adobepfad.setText(SystemConfig.hmFremdProgs.get("AcrobatReader"));
        grafpfad = new JTextField();
        grafpfad.setText(SystemConfig.hmFremdProgs.get("GrafikProg"));

        oopfad.setEditable(false);
        adobepfad.setEditable(false);
        grafpfad.setEditable(false);
        // FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 180dlu, 4dlu,
        // 60dlu",
        // 1. 2. 3. 4. 5. 6. 7. 8. 9.
        FormLayout lay = new FormLayout("right:max(80dlu;p), 4dlu, 160dlu:g, 4dlu, 60dlu,5dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18. 19. 20. 21.
                // 22. 23.
                "120dlu, 2dlu,p,2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu,p, 10dlu");

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        modprog.setColumnIdentifiers(new String[] { "Name d. Programmes", "Kompletter Pfad" });
        progtab = new JXTable(modprog);
        progtab.getColumn(0)
               .setMinWidth(175);
        progtab.getColumn(0)
               .setMaxWidth(250);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    doTabelleFuellen();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return null;
            }

        }.execute();
        // progtab.getColumn(0).setCellEditor(new TitelEditor());
        progtab.setSortable(false);
        JScrollPane jscrProg = JCompTools.getTransparentScrollPane(progtab);
        jscrProg.validate();
        builder.add(jscrProg, cc.xyw(1, 1, 5));

        builder.addLabel("markiertes Programm aus Liste entfernen",
                cc.xyw(1, 3, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.add(button[0], cc.xy(5, 3));
        builder.addLabel("Programm zu Liste hinzufügen",
                cc.xyw(1, 5, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        builder.add(button[1], cc.xy(5, 5));
        builder.addSeparator("systemrelevante Programme / Pfade", cc.xyw(1, 7, 5));
        builder.addLabel("Pfad zu OpenOffice", cc.xy(1, 9));
        builder.add(oopfad, cc.xy(3, 9));
        builder.add(button[2], cc.xy(5, 9));
        builder.addLabel("PDF-Reader auswählen (z.B. AcrobatReader)", cc.xy(1, 11));
        builder.add(adobepfad, cc.xy(3, 11));
        builder.add(button[3], cc.xy(5, 11));
        builder.addLabel("Graphikbearbeitung auswählen", cc.xy(1, 13));
        builder.add(grafpfad, cc.xy(3, 13));
        builder.add(button[4], cc.xy(5, 13));
        builder.getPanel()
               .validate();
        return builder.getPanel();
    }

    public void doTabelleFuellen() {
        for (int i = 0; i < SystemConfig.vFremdProgs.size(); i++) {
            modprog.addRow(SystemConfig.vFremdProgs.get(i));
        }
        if (modprog.getRowCount() > 0) {
            progtab.setRowSelectionInterval(0, 0);
        }
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
        if (cmd.equals("oopfad")) {
            String pfad = progWaehlen(0);
            if (pfad.trim()
                    .length() > 0) {
                oopfad.setText(pfad.replaceAll("\\\\", "/"));
            }
            return;
        }
        if (cmd.equals("adobepfad")) {
            String pfad = progWaehlen(1);
            if (pfad.trim()
                    .length() > 0) {
                adobepfad.setText(pfad.replaceAll("\\\\", "/"));
            }
            return;
        }
        if (cmd.equals("grafpfad")) {
            String pfad = progWaehlen(1);
            if (pfad.trim()
                    .length() > 0) {
                grafpfad.setText(pfad.replaceAll("\\\\", "/"));
            }
            return;
        }
        if (cmd.equals("hinzufuegen")) {
            String svorlage = progWaehlen(1);
            if (svorlage.trim()
                        .equals("")) {
                return;
            }
            Vector<String> vec = new Vector<String>();
            vec.add("");
            vec.add(svorlage);
            modprog.addRow((Vector<?>) vec.clone());
            progtab.validate();
            int rows = modprog.getRowCount();
            final int xrows = rows - 1;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progtab.requestFocus();
                    progtab.setRowSelectionInterval(xrows, xrows);
                    startCellEditing(progtab, xrows);
                }
            });

        }
        if (cmd.equals("entfernen")) {
            int row = progtab.getSelectedRow();
            if (row < 0) {
                return;
            }
            int frage = JOptionPane.showConfirmDialog(null,
                    "Wollen Sie die ausgewählte Tabellenzeile wirklich löschen?", "Wichtige Benutzeranfrage",
                    JOptionPane.YES_NO_OPTION);
            if (frage == JOptionPane.NO_OPTION) {
                return;
            }
            modprog.removeRow(row);
            progtab.validate();
        }
        if (cmd.equals("abbrechen")) {
            SystemInit.abbrechen();
            // SystemUtil.thisClass.parameterScroll.requestFocus();
        }
        if (cmd.equals("speichern")) {
            doSpeichern();
        }
    }

    private void startCellEditing(JXTable table, int row) {
        final int xrows = row;
        final JXTable xtable = table;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                xtable.scrollRowToVisible(xrows);
                xtable.editCellAt(xrows, 0);
            }
        });
    }

    private void doSpeichern() {
        String wert = "";
        Settings inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "fremdprog.ini");
        wert = adobepfad.getText()
                        .trim();
        inif.setStringProperty("FestProg", "FestProgPfad1", wert, null);
        // SystemConfig.hmFremdProgs.put("AcrobatReader",wert);
        wert = grafpfad.getText()
                       .trim();
        inif.setStringProperty("FestProg", "FestProgPfad2", wert, null);
        // SystemConfig.hmFremdProgs.put("GrafikProg",wert);
        /***** hier noch den Tabelleninhalt speichern ****/
        int rows = progtab.getRowCount();
        inif.setIntegerProperty("FremdProgramme", "FremdProgrammeAnzahl", rows, null);
        for (int i = 0; i < rows; i++) {
            inif.setStringProperty("FremdProgramme", "FremdProgrammName" + (i + 1), (String) modprog.getValueAt(i, 0),
                    null);
            inif.setStringProperty("FremdProgramme", "FremdProgrammPfad" + (i + 1), (String) modprog.getValueAt(i, 1),
                    null);
        }
        INITool.saveIni(inif);

        inif = INITool.openIni(Path.Instance.getProghome() + "ini/" + Betriebsumfeld.getAktIK() + "/", "rehajava.ini");
        wert = oopfad.getText()
                     .trim();
        inif.setStringProperty("OpenOffice.org", "OfficePfad", wert, null);
        OOService.OpenOfficePfad = wert;
        wert = Path.Instance.getProghome() + "Libraries/lib/openofficeorg";
        inif.setStringProperty("OpenOffice.org", "OfficeNativePfad", wert, null);
        OOService.OpenOfficeNativePfad = wert;
        INITool.saveIni(inif);
        SystemConfig.FremdProgs();
        JOptionPane.showMessageDialog(null, "Einstellungen Fremdprogramme wurden erfolgreich gespeichert");
    }

    public String progWaehlen(int welchesProg) {
        String sret = "";
        JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //
        if (lpfad == null) {
            lpfad = Path.Instance.getProghome();
        }
        File file = new File(lpfad);
        chooser.setCurrentDirectory(file);
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName()
                     .equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
                        || e.getPropertyName()
                            .equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                    // final File f = (File) e.getNewValue();
                }
            }
        });
        chooser.setVisible(true);
        setCursor(Cursors.normalCursor);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            String inputVerzStr = inputVerzFile.getPath();
            lpfad = inputVerzStr;

            if (inputVerzFile.getName()
                             .trim()
                             .equals("")) {
                sret = inputVerzStr;
            } else {
                sret = inputVerzFile.getAbsolutePath();
            }
        } else {
            sret = "";
        }
        chooser.setVisible(false);
        chooser.removeAll();
        chooser = null;
        return sret;
    }

}

class MyProgTableModel extends DefaultTableModel {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {

        return true;
    }

}
