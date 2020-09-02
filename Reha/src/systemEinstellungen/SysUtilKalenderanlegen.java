package systemEinstellungen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.DatFunk;
//import roogle.SuchenSeite;
import CommonTools.SqlInfo;
import CommonTools.StringTools;
import hauptFenster.AktiveFenster;
import hauptFenster.Reha;

public class SysUtilKalenderanlegen extends JXPanel implements KeyListener, ActionListener {

    private JButton btnNeuJahr = null;
    private JButton btnFeiertagImport = null;
    private JButton knopf3 = null;
    private JButton knopf4 = null;
    private JButton knopf5 = null;
    private JButton knopf6 = null;
    static JXLabel KalMake = null;
    private static JXLabel lblFeierTag = null;
    private static int anzahlLastDate = -1;

    private static String repairDateStart = "";
    private static String repairDateEnd = "";

    private JCheckBox AZPlan = null;

    private JScrollPane listscr = null;

    private static JProgressBar Fortschritt = null;
    private static JProgressBar Fortschritt2 = null;
    static boolean dblaeuft = false;
    static int progress = 0;

    private JComboBox<String> cmbNetz = null;
    private JComboBox<String> BuLand = null;
    private static JComboBox<Integer> FJahr = null;

    private static int speed = 100;

    private JXTable FreiTage = null;

    static JXLabel KalBis = null;

    private FeiertagTableModel ftm = null;

    static int kalTage;
    static Vector<Object> vecMasken = new Vector<Object>();

    public static boolean jahrOk = false;

    // static Vector<Object> aMaskenDaten = new Vector<Object>();

    private String[] laender = { "BW", "BY", "BE", "BB", "HB", "HH", "HE", "MV", "NI", "NW", "RP", "SL", "SN", "ST", "SH",
            "TH" };


    SysUtilKalenderanlegen() {
        super(new GridLayout(1, 1));
        //// System.out.println("Aufruf SysUtilKalenderanlagen");
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
        /****/
        setBackgroundPainter(Reha.instance.compoundPainter.get("SystemInit"));
        /****/
        JScrollPane jscr = new JScrollPane();
        jscr.setBorder(null);
        jscr.setOpaque(false);
        jscr.getViewport()
            .setOpaque(false);
        jscr.setViewportView(getAnlegenSeite());
        jscr.getVerticalScrollBar()
            .setUnitIncrement(15);
        jscr.validate();

        add(jscr);
        HoleMaxDatum hmd = new HoleMaxDatum();
        hmd.setzeStatement("select max(DATUM) from flexkc");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                doSucheNachFeiertagen();
                return null;
            }

        }.execute();
        return;
    }

    private String askUserForRange(String datumString, String datum) {
        Object ret = JOptionPane.showInputDialog(null, "<html>Geben Sie bitte das neue <b>" + datumString + "</b> ein: </html>",
                datum) ;
        if (ret == null) {
            return datum;
        }
        return (String) ret;
    }

    /**************
     * Beginn der Methode für die Objekterstellung und -platzierung
     *********/
    private JPanel getAnlegenSeite() {

        btnNeuJahr = new JButton("los"); // neues Jahr in Datenbank anlegen
        btnNeuJahr.setPreferredSize(new Dimension(70, 20));
        btnNeuJahr.addActionListener(this);
        btnNeuJahr.setActionCommand("NeuJahr");
        btnNeuJahr.addKeyListener(this);

        btnFeiertagImport = new JButton("los"); // Feiertage importieren
        btnFeiertagImport.setPreferredSize(new Dimension(70, 20));
        btnFeiertagImport.addActionListener(this);
        btnFeiertagImport.setActionCommand("FTimport");
        btnFeiertagImport.addKeyListener(this);

        knopf3 = new JButton("hinzufügen"); // Feiertage oder Betr.-Ferien in Tabelle zuf�gen
        knopf3.setPreferredSize(new Dimension(70, 20));
        knopf3.addActionListener(this);
        knopf3.setActionCommand("add");
        knopf3.addKeyListener(this);

        knopf4 = new JButton("entfernen"); // Feiertage oder Betr.-Ferien in Tabelle entfernen
        knopf4.setPreferredSize(new Dimension(70, 20));
        knopf4.addActionListener(this);
        knopf4.setActionCommand("delete");
        knopf4.addKeyListener(this);

        knopf5 = new JButton("FERTIG"); // Feiertage oder Betr.-Ferien in Datenbank schreiben
        knopf5.setPreferredSize(new Dimension(70, 20));
        knopf5.addActionListener(this);
        knopf5.setActionCommand("take");
        knopf5.addKeyListener(this);

        knopf6 = new JButton("speichern"); // Feiertage oder Betr.-Ferien in Datenbank schreiben
        knopf6.setPreferredSize(new Dimension(70, 20));
        knopf6.addActionListener(this);
        knopf6.setActionCommand("indbspeichern");
        knopf6.addKeyListener(this);

        FreiTage = new JXTable();

        KalBis = new JXLabel("");
        KalBis.setName("KalBis");


        KalBis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    repairDateStart = askUserForRange("Startdatum", repairDateStart);
                }
            }
        });
        KalMake = new JXLabel("");
        KalMake.setName("KalMake");
        KalMake.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    repairDateEnd = askUserForRange("Enddatum",repairDateEnd);
                }
            }

        });

        AZPlan = new JCheckBox("");
        AZPlan.setOpaque(false);

        // 1. 2. 3. 4 5.
        FormLayout lay = new FormLayout("8dlu, p:g, 130dlu, 40dlu,20dlu",
                // 1. 2. 3. 4. 5. 6. 7. 8. 9. 10. 11. 12. 13. 14. 15. 16. 17. 18 19 20 21 22 23
                // 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49
                // 50 51 52 53 54 55 56 57 58 59 60 61 62
                "p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 15dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, 80dlu, 2dlu, p, 10dlu, p, 10dlu, p,2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 10dlu, p, 10dlu");

        PanelBuilder builder = new PanelBuilder(lay);
        builder.setDefaultDialogBorder();
        builder.getPanel()
               .setOpaque(false);
        CellConstraints cc = new CellConstraints();

        builder.addSeparator("0. Voreinstellung", cc.xyw(1, 1, 4));
         cmbNetz = new JComboBox<>(new String[] { "DSL", "LAN" });
        cmbNetz.setActionCommand("netz");
        cmbNetz.addActionListener(this);
        builder.addLabel("Art der Netzwerkverbindung", cc.xyw(2, 3, 2));
        builder.add(cmbNetz, cc.xy(4, 3));
        builder.addLabel("Sollte Ihr Rechner mit der Datenbank in einem lokalen", cc.xyw(2, 5, 2));
        builder.addLabel("Netzwerk verbunden sein, können Sie die Einstellung LAN", cc.xyw(2, 7, 2));
        builder.addLabel("wählen.", cc.xy(2, 9));
        builder.addLabel("Bei jeder anderen Art der Verbindung sind Sie mit DSL", cc.xyw(2, 11, 2));
        builder.addLabel("auf der SICHEREN Seite!", cc.xyw(2, 13, 2));

        builder.addSeparator("1. Kalenderdatenbank anlegen", cc.xyw(1, 15, 4));
        builder.addLabel("Daten sind aktuell vorhanden bis zum Jahr ", cc.xyw(2, 17, 2));
        KalBis.setForeground(Color.RED);
        KalBis.setFont(new Font("Arial", Font.BOLD, 11));
        KalBis.setText("");
        builder.add(KalBis, cc.xy(4, 17));

        builder.addLabel("Arbeitszeitpläne berücksichtigen? (empfohlen)", cc.xyw(2, 19, 2));
        AZPlan.setSelected(true);
        builder.add(AZPlan, cc.xy(4, 19));
        AZPlan.setEnabled(false);

        builder.addLabel("Datenbank wird angelegt für das Jahr ", cc.xyw(2, 21, 2));
        KalMake.setText("");
        KalMake.setFont(new Font("Arial", Font.BOLD, 11));
        KalMake.setForeground(Color.RED);
        builder.add(KalMake, cc.xy(4, 21));

        builder.addLabel("Datenbank anlegen", cc.xyw(2, 23, 2));
        builder.add(btnNeuJahr, cc.xy(4, 23));

        builder.addSeparator("Prozessfortschritt / Datenbank", cc.xyw(2, 25, 3));

        Fortschritt = new JProgressBar();
        builder.add(Fortschritt, cc.xyw(2, 27, 3));

        builder.addSeparator("2. Feiertage importieren", cc.xyw(1, 29, 4));

        builder.addLabel("Bundesland auswählen", cc.xyw(2, 31, 2));
        String[] bula = { "Baden-Württemberg", "Bayern", "Berlin", "Brandenburg", "Bremen", "Hamburg", "Hessen",
                "Meckl.-Vorpommern", "Niedersachsen", "Rheinland-Pfalz", "Saarlan", "Sachsen", "Sachs.-Anhalt",
                "Schleswig-Holstein", "Thüringen" };
        // BuLand = new JComboBox(bula);
        BuLand = new JComboBox(laender);
        BuLand.setSelectedIndex(0);
        builder.add(BuLand, cc.xy(4, 31));

        builder.addLabel("Kalenderjahr auswählen", cc.xyw(2, 33, 2));
        String[] jahr = { "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018",
                "2019", "2020" };

        FJahr = new JComboBox<>();
        LocalDate ld = LocalDate.now();
        for (int year = ld.getYear() - 1; year <= ld.getYear() + 5; year++) {
            FJahr.addItem(year);
        }
        FJahr.setSelectedIndex(0);
        FJahr.setActionCommand("jahr");
        FJahr.addActionListener(this);
        builder.add(FJahr, cc.xy(4, 33));

        builder.addLabel("Feiertage einlesen", cc.xyw(2, 35, 2));
        builder.add(btnFeiertagImport, cc.xy(4, 35));

        builder.addSeparator("3. Feiertagsliste / Betriebsferien bearbeiten", cc.xyw(1, 37, 4));
        ftm = new FeiertagTableModel();
        String[] dat = { "Datum", "Feiertag/Ferien", "Bundesland" };
        ftm.setColumnIdentifiers(getColVector(dat));
        // klm.setDataVector((Vector) vkollegen.clone(), getColVector(kcolumn));
        // ftm.setDataVector((Vector)vec.clone(),getColVector(dat));
        // ftm.setDataVector((Vector)vec.clone(),getColVector(dat));
        FreiTage.setModel(ftm);
        FreiTage.validate();
        listscr = new JScrollPane(FreiTage);
        // builder.add(FreiTage, cc.xyw(2, 39,3));
        builder.add(listscr, cc.xyw(2, 39, 3));

        // FormLayout lay = new FormLayout("8dlu, p:g, 130dlu, 40dlu,20dlu",
        /******** hier neues JXPanel einbauen *******************/
        JXPanel butpan = new JXPanel();
        butpan.setOpaque(false);
        FormLayout lay2 = new FormLayout("8dlu, p:g, 86dlu, 40dlu,4dlu,40dlu,20dlu", "p");
        CellConstraints cc2 = new CellConstraints();
        butpan.setLayout(lay2);
        butpan.add(knopf3, cc2.xy(2, 1));
        butpan.add(knopf6, cc2.xy(4, 1));
        butpan.add(knopf4, cc2.xy(6, 1));
        butpan.validate();
        builder.add(butpan, cc.xyw(1, 41, 5));
        // builder.add(knopf3, cc.xy(2, 41));
        // builder.add(knopf4, cc.xy(4,41));

        builder.addSeparator("4. Daten in Kalender übernehmen", cc.xyw(1, 43, 4));

        builder.addLabel("Die Daten aus der Liste werden in den Kalender eingetragen.", cc.xyw(2, 45, 2));
        // builder.addLabel("", cc.xyw(2, 47, 2));
        builder.addLabel("Änderungen können danach nur noch manuell im Terminplan ", cc.xyw(2, 51, 2));
        builder.addLabel("oder über den Arbeitszeitplan vorgenommen werden.", cc.xyw(2, 53, 2));
        // builder.addLabel("", cc.xyw(2, 55, 2));

        lblFeierTag = new JXLabel("Feiertagsliste für das Jahr 9999 eintragen.");
        builder.add(lblFeierTag, cc.xyw(2, 57, 2));
        builder.add(knopf5, cc.xy(4, 57));

        builder.addSeparator("Prozessfortschritt / Feiertage eintragen", cc.xyw(2, 59, 3));

        Fortschritt2 = new JProgressBar();
        builder.add(Fortschritt2, cc.xyw(2, 61, 3));

        return builder.getPanel();
    }

    private Vector getColVector(String[] cols) {
        Vector col = new Vector();
        for (int i = 0; i < cols.length; i++) {
            col.add(cols[i]);
        }
        return col;
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

    }

    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        int i;
        for (i = 0; i < 1; i++) {
            if (arg0.getActionCommand()
                    .equals("FTimport")) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        starteSession((String) BuLand.getSelectedItem(), FJahr.getItemAt(FJahr.getSelectedIndex()));
                        lblFeierTag.setText(
                                "Feiertagsliste für das Jahr -> " + FJahr.getSelectedItem() + " <- eintragen.");
                    }
                });
                break;
            }

            if (arg0.getActionCommand()
                    .equals("NeuJahr")) {
                JComponent termin = AktiveFenster.getFensterAlle("TerminFenster");
                if (termin != null) {
                    JOptionPane.showMessageDialog(null,
                            "Achtung!!!!! \n\nWährend der Anlage eines Kalenderjahres\n"
                                    + "darf der Terminkalender aus Sicherheitsgründen nicht geöffnet sein.\n"
                                    + "Beenden Sie den Terminkalender und rufen Sie diese Funktion erneut auf.\n\n");
                    break;
                }
                String frage = "Bitte beachten Sie!\n\n"
                        + "1. Stellen Sie sicher, dass Sie zum Zeitpunkt der Kalenderanlage möglichst der einzige Benutzer im Netzwerk sind\n"
                        + "2. Wurde die Kalenderanlage gestartet, brechen Sie den Vorgang bitte keinesfalls ab\n"
                        + "3. Die Kalenderanlage kann einige Zeit in Anspruch nehmen. Sie sehen den Fortschritt anhand des 'Laufbalkens'\n"
                        + "4. Verlassen Sie diese Seite nicht, bis das Kalenderjahr vollständig angelegt wurde\n\n"
                        + "Wollen Sie jetzt das Kalenderjahr wie folgt anlegen:\n" + "angelegt wird das Jahr -> "
                        + KalMake.getText() + " <- \n" + "automatische Übernahme der Wochenarbeitszeit -> "
                        + (AZPlan.isSelected() ? "JA" : "NEIN") + " <-";
                int anfrage = JOptionPane.showConfirmDialog(null, frage, "Achtung wichtige Benutzeranfrage!!!!",
                        JOptionPane.YES_NO_OPTION);
                if (anfrage == JOptionPane.YES_OPTION) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new Thread() {
                                @Override
                                public void run() {
                                    knopfGedoense(false);
                                }
                            }.start();

                            starteKraftakt();
                        }
                    });
                    break;
                } else {
                    break;
                }
            }
            if (arg0.getActionCommand()
                    .equals("add")) {
                String[] inhalt = { "", "", "" };
                ftm.addRow(new Vector(Arrays.asList(inhalt)));
                int aktrow = FreiTage.getRowCount() - 1;
                FreiTage.setRowSelectionInterval(aktrow, aktrow);
                FreiTage.validate();
                Rectangle re = FreiTage.getCellRect(aktrow + 1, 1, false);
                JViewport vp = listscr.getViewport();
                vp.setView(FreiTage);
                vp.setViewPosition(re.getLocation());
                // FreiTage.changeSelection(FreiTage.getRowCount()-1, 0, false, false);

                // FreiTage.setEditingRow(FreiTage.getRowCount()-1);
                break;
            }
            if (arg0.getActionCommand()
                    .equals("delete")) {

                while (FreiTage.getSelectedRows().length > 0) {
                    int[] select = FreiTage.getSelectedRows();
                    doAusDbLoeschen(select[0]);
                    // ftm.removeRow(FreiTage.convertRowIndexToModel(select[0]));
                    ftm.removeRow(select[0]);
                }
                break;
            }
            if (arg0.getActionCommand()
                    .equals("take")) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        knopfGedoense(false);
                        starteFTEintragen();
                    }
                });
                break;
            }
            if (arg0.getActionCommand()
                    .equals("jahr")) {
                setLabelAndSearchForFeiertage();
                break;
            }
            if (arg0.getActionCommand()
                    .equals("netz")) {
                if (arg0.getSource() instanceof JComboBox) {
                    if (((JComboBox) arg0.getSource()).getSelectedItem()
                                                      .equals("DSL")) {
                        speed = 100;
                    } else {
                        speed = 40;
                    }
                    //// System.out.println("Dauer der Pause = "+speed+" Millisekunden");
                }
            }
            if (arg0.getActionCommand()
                    .equals("indbspeichern")) {
                doInDbSpeichern();
            }

        }
    }

    private void setLabelAndSearchForFeiertage() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (FJahr != null) {
                    lblFeierTag.setText("Feiertagsliste für das Jahr -> " + FJahr.getSelectedItem() + " <- eintragen.");
                    doSucheNachFeiertagen();
                }
            }
        });
    }

    private void doInDbSpeichern() {
        if (FreiTage.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(null, "Keine Feiertage zum Speichern vorhanden");
            return;
        }
        String ftjahr = FreiTage.getValueAt(0, 0)
                                .toString()
                                .trim()
                                .substring(6);
        // Erst alle bisherigen löschen;
        String cmd = "delete from feiertage where jahr='" + ftjahr + "'";
        SqlInfo.sqlAusfuehren(cmd);
        String deutschdat = "", sqldat = "", feiertag = "", buland = "";
        for (int i = 0; i < FreiTage.getRowCount(); i++) {
            try {
                deutschdat = FreiTage.getValueAt(i, 0)
                                     .toString();
                sqldat = DatFunk.sDatInSQL(deutschdat);
                feiertag = FreiTage.getValueAt(i, 1)
                                   .toString()
                                   .trim();
                buland = FreiTage.getValueAt(i, 2)
                                 .toString()
                                 .trim();
                cmd = "insert into feiertage set datdeutsch='" + deutschdat + "', datsql='" + sqldat + "', "
                        + "feiertag='" + feiertag + "', buland='" + buland + "', jahr='" + ftjahr + "'";
                SqlInfo.sqlAusfuehren(cmd);
                // System.out.println(cmd);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei der Anlage des Feiertages -> " + feiertag);
            }
        }
        JOptionPane.showMessageDialog(null, "Feiertage für das Jahr " + ftjahr + " wurden gespeichert!");
    }

    private void doSucheNachFeiertagen() {
        long zeit = System.currentTimeMillis();
        while (!SysUtilKalenderanlegen.jahrOk) {
            try {
                Thread.sleep(50);
                if (System.currentTimeMillis() - zeit > 5000) {
                    JOptionPane.showMessageDialog(null, "Fehler bei der Datenbank-Recherche nach Feiertagen");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        doErmittleFeiertage(FJahr.getSelectedItem()
                                 .toString()
                                 .trim());
    }

    private void doErmittleFeiertage(String jahr) {
        String cmd = "select * from feiertage where jahr='" + jahr + "' order by datsql";
        Vector<Vector<String>> vec = SqlInfo.holeFelder(cmd);
        ftm.setRowCount(0);
        Vector<String> dummyvec = new Vector<String>();
        for (int i = 0; i < vec.size(); i++) {
            dummyvec.clear();
            dummyvec.add(vec.get(i)
                            .get(0));
            dummyvec.add(vec.get(i)
                            .get(2));
            dummyvec.add(vec.get(i)
                            .get(3));
            ftm.addRow((Vector<?>) dummyvec.clone());
        }
        FreiTage.validate();
    }

    private void doAusDbLoeschen(int row) {
        if (row < 0) {
            // JOptionPane.showMessageDialog(null,"Kein Feiertag zum Löschen ausgewählt");
            return;
        }
        String datum = ftm.getValueAt(FreiTage.convertRowIndexToModel(row), 0)
                          .toString();
        String feiertag = ftm.getValueAt(FreiTage.convertRowIndexToModel(row), 1)
                             .toString();
        String cmd = "delete from feiertage where datdeutsch='" + datum + "', and feiertag='" + feiertag + "' LIMIT 1";
        SqlInfo.sqlAusfuehren(cmd);
    }

    private void starteFTEintragen() {
        int max = FreiTage.getRowCount();
        int i;
        long zeit1 = System.currentTimeMillis();
        String fdatum = null, ftext = null, sret = null, sqldat = null;
        // StringBuffer sbuf = new StringBuffer();
        if (max == 0) {
            knopfGedoense(true);
            return;
        }

        ftext = (String) FreiTage.getValueAt(0, 1);
        if (ftext.trim()
                 .equals("")) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie eine Bezeichnung für den Feiertag/Betriebsurlaub an");
            return;
        }

        Fortschritt2.setMinimum(0);
        Fortschritt2.setMaximum(max - 1);
        Fortschritt2.setStringPainted(true);
        dblaeuft = true;
        progress = 0;
        ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt2);
        pv.execute();
        String tstart = TKSettings.KalenderUmfang[0];
        String tend = TKSettings.KalenderUmfang[1];
        String tdauer = new Long(TKSettings.KalenderMilli[1] - TKSettings.KalenderMilli[0]).toString();

        for (i = 0; i < max; i++) {
            progress = i;
            fdatum = (String) FreiTage.getValueAt(i, 0);
            ftext = (String) FreiTage.getValueAt(i, 1);

            if (!fdatum.trim()
                       .equals("")) {
                sqldat = DatFunk.sDatInSQL(fdatum);
                sret = "Update flexkc set ";
                sret = sret + "T1='" + "FTG " + ftext.trim()
                                                     .toUpperCase()
                        + "', N1='@FREI', TS1='" + tstart + "', TD1='" + tdauer + "', TE1='" + tend + "',";
                sret = sret + "BELEGT='1' Where DATUM='" + sqldat + "'";
                //// System.out.println(sret);
                SchreibeNeuenKalender snk = new SchreibeNeuenKalender();
                snk.setzeStatement(String.valueOf(sret));
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }
        JOptionPane.showMessageDialog(null,
                "Feiertagübernahme beendet nach " + ((System.currentTimeMillis() - zeit1)) + " Millisekunden");
        dblaeuft = false;
        knopfGedoense(true);
    }

    private void starteKraftakt() {

        int i;
        if (AZPlan.isSelected()) {
            Fortschritt.setMinimum(1);
            Fortschritt.setMaximum(99);
            dblaeuft = true;
            progress = 1;
            ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt);
            pv.execute();
            for (i = 1; i < 100; i++) {

                String sbehandler = (i < 10 ? "0" + Integer.valueOf(i)
                                                           .toString()
                        + "BEHANDLER"
                        : Integer.valueOf(i)
                                 .toString()
                                + "BEHANDLER");
                String stmtmaske = "select * from masken where behandler = '" + sbehandler + "' ORDER BY art";
                new HoleMasken(stmtmaske);
                try {
                    Thread.sleep(15);
                    progress = i;
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            dblaeuft = false;
        }
        new Thread() {
            @Override
            public void run() {
                // ab hier geht's dann zur Sache
                starteDbAppend();
            }
        }.start();

        // xxxx
    }

    private void knopfGedoense(boolean einschalten) {
        if (einschalten) {
            btnNeuJahr.setEnabled(false);
        } else {
            btnNeuJahr.setEnabled(false);
        }
        btnFeiertagImport.setEnabled(einschalten);
        knopf3.setEnabled(einschalten);
        knopf4.setEnabled(einschalten);
        knopf5.setEnabled(einschalten);
        AZPlan.setEnabled(einschalten);
        BuLand.setEnabled(einschalten);
        FJahr.setEnabled(einschalten);

    }

    private void starteDbAppend() {
        int durchgang = 0;
        long zeit1 = System.currentTimeMillis();
        dblaeuft = true;
        if (KalMake.getText()
                   .equals("")) {
            dblaeuft = false;
            return;
        }

        String starttag = "01.01." + KalMake.getText();
        String stoptag = "31.12." + KalMake.getText();

        if (repairDateStart.trim()
                           .length() == 10
                && repairDateEnd.trim()
                                .length() == 10) {
            int frage = JOptionPane.showConfirmDialog(null,
                    "Vermutlich wollen Sie einen vorhandenen Kalender reparieren\n"
                            + "Der von Ihnen festgelegte Starttag = " + repairDateStart + "\n"
                            + "Der von Ihnen festgelegte Endtag = " + repairDateEnd + "\n"
                            + "Wollen Sie mit diesen Parametern starten?",
                    "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_CANCEL_OPTION);
            if (frage == JOptionPane.YES_OPTION) {
                starttag = repairDateStart;
                stoptag = repairDateEnd;
                KalMake.setText(stoptag.substring(6));
            } else if (frage == JOptionPane.CANCEL_OPTION) {
                dblaeuft = false;
                return;
            }
        }

        kalTage = Year.of(Integer.valueOf(KalMake.getText()))
                      .length();
        Fortschritt.setMinimum(1);
        Fortschritt.setMaximum(kalTage * 99);
        Fortschritt.setStringPainted(true);

        // String stoptag = "02.01."+KalMake.getText();
        String akttag = String.valueOf(starttag);
        progress = 0;
        int i;
        ProgressVerarbeiten pv = new ProgressVerarbeiten(Fortschritt);
        pv.execute();
        String stmt = null;
        while (DatFunk.DatumsWert(akttag) <= DatFunk.DatumsWert(stoptag)) {
            for (i = 1; i < 100; i++) {
                durchgang++;
                if (durchgang > 200) {
                    durchgang = 0;
                    Runtime r = Runtime.getRuntime();
                    r.gc();
                }

                stmt = macheStatement(DatFunk.sDatInSQL(akttag),
                        (ArrayList) ((Vector) vecMasken.get(i - 1)).get(DatFunk.TagDerWoche(akttag) - 1),
                        (i < 10 ? "0" + Integer.valueOf(i)
                                               .toString()
                                + "BEHANDLER"
                                : Integer.valueOf(i)
                                         .toString()
                                        + "BEHANDLER"),
                        AZPlan.isSelected());
                SqlInfo.sqlAusfuehren(stmt);
                try {
                    Thread.sleep(speed);
                    ++progress;
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            akttag = DatFunk.sDatPlusTage(akttag, 1);
        }
        dblaeuft = false;
        JOptionPane.showMessageDialog(null,
                "Kalenderanlegen beendet nach " + ((System.currentTimeMillis() - zeit1) / 1000) + " Sekunden\n\n"
                        + "Kalender wird jetzt auf Integrität geprüft!");

        knopfGedoense(true);
        Vector<Vector<String>> vec = SqlInfo.holeFelder("select min(datum),max(datum) from flexkc");
        Reha.kalMin = DatFunk.sDatInDeutsch(((String) ((Vector) vec.get(0)).get(0)));
        Reha.kalMax = DatFunk.sDatInDeutsch(((String) ((Vector) vec.get(0)).get(1)));
        int testanzahl = 0;
        try {

            String sstmt = "select count(*) from flexkc where datum >='"
                    + DatFunk.sDatInSQL("01.01." + KalMake.getText()) + "' and datum <= '"
                    + DatFunk.sDatInSQL("31.12." + KalMake.getText()) + "'";
            String sanzahl = SqlInfo.holeEinzelFeld(sstmt);
            testanzahl = Integer.parseInt(sanzahl);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SysUtilKalenderanlegen.anzahlLastDate = Integer.parseInt(SqlInfo.holeEinzelFeld(
                "select count(*) from flexkc where datum = '" + ((String) ((Vector) vec.get(0)).get(1)) + "'"));
        if ((!Reha.kalMax.startsWith("31.12.")) || (SysUtilKalenderanlegen.anzahlLastDate != 99)
                || (testanzahl != kalTage * 99)) {
            JOptionPane.showMessageDialog(null,
                    "Achtung Ihr Kalender wurde nicht korrekt angelegt!\nBitte melden Sie sich im Anwenderforum und fragen Sie nach Unterstützung");
        } else {
            JOptionPane.showMessageDialog(null,
                    "Der Kalender wurde korrekt angelegt, frohes Schaffen und gute Geschäfte.");
        }

    }

    private String macheStatement(String sqldat, ArrayList list, String sBehandler, boolean mitmaske) {
        String sret = null;
        int i, j;
        int bloecke = Integer.valueOf((String) ((Vector) list.get(5)).get(0));
        String nummer;
        if (mitmaske) {
            sret = "Insert into flexkc set ";
            for (i = 0; i < bloecke; i++) {
                if (((String) ((Vector) list.get(1)).get(i)).contains("\\")) {
                    String replace = ((String) ((Vector) list.get(1)).get(i));
                    String[] split = { null, null };
                    split = replace.split("\\\\");
                    nummer = split[0] + "\\\\" + split[1];

                } else {
                    nummer = ((String) ((Vector) list.get(1)).get(i));
                }

                sret = sret + "T" + (i + 1) + "='" + StringTools.Escaped((String) ((Vector) list.get(0)).get(i))
                        + "', ";
                sret = sret + "N" + (i + 1) + "='" + StringTools.Escaped(nummer) + "', ";
                sret = sret + "TS" + (i + 1) + "='" + ((Vector) list.get(2)).get(i) + "', ";
                sret = sret + "TD" + (i + 1) + "='" + ((Vector) list.get(3)).get(i) + "', ";
                sret = sret + "TE" + (i + 1) + "='" + ((Vector) list.get(4)).get(i) + "', ";
            }
            sret = sret + "BELEGT='" + Integer.valueOf(bloecke)
                                              .toString()
                    + "', DATUM='" + sqldat + "' , BEHANDLER='" + sBehandler + "'";
        } else {
            //
            String tstart = TKSettings.KalenderUmfang[0];
            String tend = TKSettings.KalenderUmfang[1];
            String tdauer = new Long(TKSettings.KalenderMilli[1] - TKSettings.KalenderMilli[0]).toString();
            sret = "Insert into flexkc set ";
            sret = sret + "T1='', N1='@FREI', TS1='" + tstart + "', TD1='" + tdauer + "', TE1='" + tend + "',";
            sret = sret + "BELEGT='1', DATUM='" + sqldat + "' , BEHANDLER='" + sBehandler + "'";
        }
        return sret;
    }

    private void starteSession(String land, Integer jahr) {
        String urltext = null;
        try {
            urltext = "https://www.feiertage.net/csvfile.php?state=" + land + "&year=" + jahr + "&type=csv";
            String text = null;
            ftm.setRowCount(0);
            FreiTage.validate();
            URL url = new URL(urltext);

            URLConnection conn = url.openConnection();

            BufferedReader inS = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            int durchlauf = 0;
            while ((text = inS.readLine()) != null) {
                String s = makeUTF8(text);
                String[] spl = s.split(";");
                if (durchlauf > 0) {
                    Vector reihe = new Vector(Arrays.asList(spl));
                    ftm.addRow((Vector) reihe.clone());
                    FreiTage.setRowSelectionInterval(0, 0);
                }
                ++durchlauf;
            }
            inS.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Fehler bei der Auswertung von " + urltext + "\nFehlertext: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public static String makeUTF8(final String toConvert) {
        try {
            return new String(toConvert.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // und dann zum testen vielleicht

    public static void setJahr(String item) {
        FJahr.setSelectedItem(item);
        String ftext = "Feiertagsliste für das Jahr " + item + " eintragen";
        lblFeierTag.setText(ftext);
        // doErmittleFeiertage(item);
    }

}

/***********************************/
class FeiertagTableModel extends DefaultTableModel {
    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        return true;
    }

}

/***********************************/
class HoleMaxDatum extends Thread {

    private String statement;

    public void setzeStatement(String statement) {
        this.statement = statement;
        start();
    }

    @Override
    public void run() {

        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

                ResultSet rs = stmt.executeQuery(this.statement);) {
            if (rs.next()) {
                if (rs.getString(1) != null) {
                    String datum = rs.getString(1);
                    int altjahr = Integer.valueOf(datum.substring(0, 4));
                    SysUtilKalenderanlegen.KalBis.setText(Integer.valueOf(altjahr)
                                                                 .toString());
                    SysUtilKalenderanlegen.KalMake.setText(Integer.valueOf(altjahr + 1)
                                                                  .toString());
                    // SysUtilKalenderanlegen.setJahr(Integer.valueOf(altjahr+1).toString());
                    SysUtilKalenderanlegen.setJahr(Integer.valueOf(altjahr + 1)
                                                          .toString());
                    SysUtilKalenderanlegen.jahrOk = true;

                } else {
                    String datum = DatFunk.sHeute()
                                          .substring(6);
                    SysUtilKalenderanlegen.KalBis.setText("leer");
                    SysUtilKalenderanlegen.KalMake.setText(datum);
                    SysUtilKalenderanlegen.setJahr(datum);
                    SysUtilKalenderanlegen.jahrOk = true;
                }
            }

        } catch (SQLException ex) {
            // System.out.println("von stmt -SQLState: " + ex.getSQLState());
        }

    }

}

class HoleMasken {
    private static Logger logger = LoggerFactory.getLogger(HoleMasken.class);

    HoleMasken(String sstmt) {

        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sstmt);) {
            int i = 0;
            int durchlauf = 0;
            int maxbehandler = 7;

            int maxblock = 0;
            int aktbehandler = 1;
            ArrayList<Object> aKalList = new ArrayList<Object>();
            Vector<Object> aMaskenDaten = new Vector<Object>();
            // SysUtilKalenderanlegen.aMaskenDaten.clear();
            // *******************SysUtilKalenderanlegen.aMaskenDaten.clear();
            while ((rs.next())) {
                Vector<String> v1 = new Vector<String>();
                Vector<String> v2 = new Vector<String>();
                Vector<String> v3 = new Vector<String>();
                Vector<String> v4 = new Vector<String>();
                Vector<String> v5 = new Vector<String>();
                Vector<String> v6 = new Vector<String>();

                /* in Spalte 301 steht die Anzahl der belegten Bl�cke */
                int belegt = rs.getInt(226);
                /* letzte zu durchsuchende Spalte festlegen */
                int ende = (5 * belegt);
                maxblock = maxblock + (ende + 5);
                durchlauf = 1;

                for (i = 1; i < ende; i = i + 5) {
                    int durchlauf1 = durchlauf;
                    v1.addElement(rs.getString(i) != null ? rs.getString(i) : "");
                    v2.addElement(rs.getString(i + 1) != null ? rs.getString(i + 1) : "");
                    v3.addElement(rs.getString(i + 2));
                    v4.addElement(rs.getString(i + 3));
                    v5.addElement(rs.getString(i + 4));
                    durchlauf1 = durchlauf1 + 1;
                }

                v6.addElement(rs.getString(226)); // Anzahl
                v6.addElement(rs.getString(227)); // Art
                v6.addElement(rs.getString(228)); // Behandler
                v6.addElement(rs.getString(229)); // MEMO
                v6.addElement(rs.getString(230)); // Datum

                aKalList.add(v1.clone());
                aKalList.add(v2.clone());
                aKalList.add(v3.clone());
                aKalList.add(v4.clone());
                aKalList.add(v5.clone());
                aKalList.add(v6.clone());
                aMaskenDaten.add(aKalList.clone());
                aKalList.clear();
                aktbehandler++;
            }
            SysUtilKalenderanlegen.vecMasken.add(aMaskenDaten.clone());

        } catch (SQLException ex) {
            logger.error("", ex);
        }
    }
}

class TesteKalender {
    TesteKalender(String sstmt) {

        int tage = 0;

        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

                ResultSet rs = stmt.executeQuery(sstmt);) {
            if (rs.next()) {
                tage = rs.getInt(1);
                if (tage != (SysUtilKalenderanlegen.kalTage * 60)) {
                    JOptionPane.showMessageDialog(null,
                            "Fehler!!!!! ---- Der Kalender wurde unvollständig angelegt!\n\n"
                                    + "Zur Sicherheit wird der fehlerhafte Kalender wieder gelöscht\n\n"
                                    + "Stellen Sie bei einem neuen Versuch die Einstellung\n"
                                    + "'Art der Netzwerkverbindung' auf -> DSL");
                } else {
                    JOptionPane.showMessageDialog(null, "Kalender wurde perfekt angelegt");
                }
            }

        } catch (SQLException ex) {
            // System.out.println("von stmt -SQLState: " + ex.getSQLState());
        }
    }
}

/******************************/

class ProgressVerarbeiten extends SwingWorker<Void, JComponent> {
    private JProgressBar jpb = null;

    ProgressVerarbeiten(JComponent laufbalken) {
        jpb = (JProgressBar) laufbalken;
    }

    @Override
    protected synchronized Void doInBackground() throws Exception {
        while (SysUtilKalenderanlegen.dblaeuft) {
            /*
             * SysUtilKalenderanlegen.Fortschritt.setValue(SysUtilKalenderanlegen.progress);
             * SysUtilKalenderanlegen.Fortschritt.repaint();
             */
            jpb.setValue(SysUtilKalenderanlegen.progress);
            jpb.repaint();

            Thread.sleep(10);
        }
        return null;

    }
}

class SchreibeNeuenKalender extends Thread {
    private String statement;

    public void setzeStatement(String statement) {
        this.statement = statement;
        start();
    }

    @Override
    public synchronized void run() {
        try (Statement stmt = Reha.instance.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);) {
            stmt.execute(this.statement);

        } catch (SQLException ex) {
            Logger logger = LoggerFactory.getLogger(SysUtilKalenderanlegen.class);
            logger.error("sqlfehler beim Anlegen des kalenders", ex);
        }


    }

}
