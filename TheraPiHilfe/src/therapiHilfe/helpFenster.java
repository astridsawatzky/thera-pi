package therapiHilfe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
//import com.mysql.jdbc.PreparedStatement;

import CommonTools.DatFunk;
import ftpTools.FTPTools;
import wartenFenster.WartenFenster;

public class helpFenster extends JXPanel implements
         ActionListener {
    UIFSplitPane splitOU = null;
    UIFSplitPane splitORL = null;
    JXPanel obenLinks = null;
    JXPanel obenRechts = null;
    public static JComboBox<String> gruppenbox;
    boolean neuertext = false;
    JFormattedTextField stitel = null;
    String tempname = null;
    public JButton abbrechen = null;
    public JButton[] buts = { null, null, null, null, null, null };
    static JXPanel ooPan = null;
    public static helpFenster thisClass = null;
    public Vector htmlDaten = null;

    public JXTable tblThemen = null;
    public DefaultTableModel themenDtblm = null;
    public JXLabel lblstand = null;
    public JProgressBar jprogress = null;
    public JCheckBox chkweb = null;
    public Vector<String> bilder = new Vector<String>();

    public static String hilfeDatei = "";
    public static String absolutDatei = "";
    public WartenFenster wf = null;

    public boolean sqlfertig;
    public boolean ftpfertig;
    public String initvz = piHelp.proghome + "ScreenShots/";

    helpFenster() {
        super();
        this.setLayout(new BorderLayout());
        this.add(grundFlaeche(), BorderLayout.CENTER);
        thisClass = this;
        new WorkerGruppen().execute();
    }

    private JXPanel grundFlaeche() {
        JXPanel jGrund = new JXPanel();
        jGrund.setLayout(new BorderLayout());
        splitOU = UIFSplitPane.createStrippedSplitPane(JSplitPane.VERTICAL_SPLIT, getTop(), getBottom());
        splitOU.setDividerSize(7);
        splitOU.setDividerBorderVisible(true);
        splitOU.setName("BrowserSplitLinksRechts");
        splitOU.setOneTouchExpandable(true);
        splitOU.setDividerLocation(250);
        splitOU.validate();
        jGrund.add(splitOU, BorderLayout.CENTER);
        jGrund.validate();
        return jGrund;
    }

    private JPanel getBottom() {
        ooPan = new JXPanel();
        ooPan.setLayout(new GridLayout());
        ooPan.setBackground(Color.WHITE);
        ooPan.validate();
        return ooPan;
    }

    private JPanel getTop() {
        JPanel jpan = new JPanel();
        jpan.setLayout(new BorderLayout());
        jpan.setBackground(Color.WHITE);
        jpan.add(linksRechts(), BorderLayout.CENTER);
        validate();
        return jpan;
    }

    private JXPanel linksRechts() {
        JXPanel jlr = new JXPanel(new BorderLayout());
        splitORL = UIFSplitPane.createStrippedSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(getScrollOL()),
                new JScrollPane(getScrollOR()));
        splitORL.setDividerSize(7);
        splitORL.setDividerBorderVisible(true);
        splitORL.setName("BrowserSplitLinksRechts");
        splitORL.setOneTouchExpandable(true);
        splitORL.setDividerLocation(300);
        splitORL.validate();
        jlr.add(splitORL, BorderLayout.CENTER);
        return jlr;
    }

    private JScrollPane getScrollOL() {
        FormLayout lay = new FormLayout("10dlu,80dlu,10dlu,80dlu,10dlu",
                "10dlu,p,2dlu,p,10dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,5dlu,p,3dlu,p");
        CellConstraints cc = new CellConstraints();
        JXPanel jpan = new JXPanel(lay);
        jpan.setBackground(Color.WHITE);

        buts[0] = new JButton("HilfeText neu...");
        buts[0].setActionCommand("neuertext");
        buts[0].addActionListener(this);
        jpan.add(buts[0], cc.xyw(2, 2, 3));

        stitel = new JFormattedTextField();
        stitel.setEnabled(false);
        jpan.add(stitel, cc.xyw(2, 4, 3));

        buts[1] = new JButton("HilfeText speichern");
        buts[1].setActionCommand("speicherntext");
        buts[1].addActionListener(this);
        buts[1].setEnabled(false);
        jpan.add(buts[1], cc.xyw(2, 6, 3));

        chkweb = new JCheckBox("SWEB?");
        chkweb.setOpaque(false);

        buts[2] = new JButton("HilfeText abholen");
        buts[2].setActionCommand("abholentext");

        buts[2].addActionListener(this);
        buts[2].add(chkweb);
        jpan.add(buts[2], cc.xyw(2, 8, 3));

        buts[3] = new JButton("Aktion abbrechen");
        buts[3].setActionCommand("abbrechen");
        buts[3].setEnabled(false);
        buts[3].addActionListener(this);
        jpan.add(buts[3], cc.xyw(2, 12, 3));

        buts[5] = new JButton("Dateien (z.B. PDF) auf Server hochladen");
        buts[5].setActionCommand("hilfespeichern");
        buts[5].addActionListener(this);
        jpan.add(buts[5], cc.xyw(2, 14, 3));

        lblstand = new JXLabel(" ");
        jpan.add(lblstand, cc.xyw(2, 16, 3));

        jprogress = new JProgressBar();
        jpan.add(jprogress, cc.xyw(2, 18, 3));

        JScrollPane jscr = new JScrollPane(jpan);
        jscr.setOpaque(false);
        jscr.validate();
        return jscr;
    }

    private JScrollPane getScrollOR() {
        FormLayout lay = new FormLayout("20dlu,100dlu,200dlu,right:max(100dlu;p),10dlu",
                "10dlu,p,2dlu,80dlu:g,10dlu,p,10dlu");
        CellConstraints cc = new CellConstraints();
        JXPanel jpan = new JXPanel(lay);
        gruppenbox = new JComboBox();
        gruppenbox.setActionCommand("gruppen");
        gruppenbox.addActionListener(this);
        jpan.add(gruppenbox, cc.xy(2, 2));

        themenDtblm = new MyDefaultTableModel();
        String[] column = { "Titel", "HTML-Datei", "letzte Änderung", "ID" };
        themenDtblm.setColumnIdentifiers(column);

        tblThemen = new JXTable(themenDtblm);
        tblThemen.setDoubleBuffered(true);
        tblThemen.setHighlighters(HighlighterFactory.createSimpleStriping(new Color(204, 255, 255)));
        tblThemen.getColumn(0)
                 .setMinWidth(100);
        tblThemen.getColumn(1)
                 .setMinWidth(150);
        tblThemen.getColumn(1)
                 .setMaxWidth(150);
        tblThemen.getColumn(2)
                 .setMinWidth(0);
        tblThemen.getColumn(2)
                 .setMaxWidth(0);
        tblThemen.getColumn(3)
                 .setMinWidth(30);
        tblThemen.getColumn(3)
                 .setMaxWidth(30);
        ListSelectionModel listSelectionModel = tblThemen.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        tblThemen.setSelectionModel(listSelectionModel);

        JScrollPane themenScroll = new JScrollPane();
        themenScroll.setViewportView(tblThemen);
        jpan.add(themenScroll, cc.xyw(2, 4, 3));

        buts[4] = new JButton("Thema löschen");
        buts[4].setActionCommand("loeschen");
        buts[4].addActionListener(this);
        jpan.add(buts[4], cc.xy(4, 2));

        JScrollPane jscr = new JScrollPane(jpan);
        jscr.validate();
        return jscr;

    }

    public void starteOOO() {
        new ooPanel(ooPan);
    }

    public void setzeTitelTabelle(Vector vec) {
        while (tblThemen.getRowCount() > 0) {
            themenDtblm.removeRow(0);
        }
        tblThemen.validate();
        tblThemen.repaint();
        int i = 0, lang = vec.size();
        for (i = 0; i < lang; i++) {
            themenDtblm.addRow((Vector) vec.get(i));
        }
        if (tblThemen.getRowCount() <= 0) {
            buts[2].setEnabled(false);
            buts[4].setEnabled(false);
        } else {
            buts[2].setEnabled(true);
            buts[4].setEnabled(true);
        }
        tblThemen.validate();
    }







    @Override
    public void actionPerformed(ActionEvent arg0) {

        String command = arg0.getActionCommand();

        if (command.equals("neuertext")) {
            ooPanel.schliesseText();
            ooPanel.neuesNoaPanel();
            // ooPanel.starteNeuenText();
            buts[3].setEnabled(true);
            buts[0].setEnabled(false);
            buts[2].setEnabled(false);
            buts[1].setEnabled(true);
            stitel.setEnabled(true);
            chkweb.setSelected(false);
            chkweb.setEnabled(false);
            stitel.setText("");
            stitel.requestFocus();
            neuertext = true;
            tblThemen.setEnabled(false);
        }
        if (command.equals("speicherntext")) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (stitel.getText()
                              .trim()
                              .equals("")) {
                        JOptionPane.showMessageDialog(null, "Sie haben keinen Titel eingegeben");
                        stitel.requestFocus();
                        return null;
                    }
                    buts[3].setEnabled(false);
                    buts[0].setEnabled(true);
                    buts[2].setEnabled(true);
                    buts[1].setEnabled(false);
                    stitel.setEnabled(false);
                    chkweb.setEnabled(true);
                    speichern();
                    neuertext = false;
                    tblThemen.setEnabled(true);
                    return null;
                }

            }.execute();

        }
        if (command.equals("abholentext")) {
            int id = new Integer((String) tblThemen.getValueAt(tblThemen.getSelectedRow(), 3));
            if (id > 0) {
                HtmlHolen hhol = new HtmlHolen();
                hhol.init(id);
                buts[0].setEnabled(false);
                buts[2].setEnabled(false);
                buts[1].setEnabled(true);
                buts[3].setEnabled(true);
                chkweb.setEnabled(false);
                stitel.setEnabled(false);
                neuertext = false;
                tblThemen.setEnabled(false);

            }

        }
        if (command.equals("gruppen")) {
            WorkerTitel wt = new WorkerTitel();
            wt.init((String) gruppenbox.getSelectedItem());
            // System.out.println(arg0);
        }
        if (command.equals("abbrechen")) {
            doAbbrechen();
        }
        if (command.equals("loeschen")) {
            String stext = "\nDie Löschen-Funktion ist passwortgeschützt!\n\n"
                    + "Wenn Sie über kein Passwort für diesen Vorgang verfügen, senden Sie\n"
                    + "Bitte eine Email an -> j.steinhilber@rta.de <- und geben Sie bitte\n"
                    + "Die 'ID' des Beitrages an den Sie löschen wollen\n"
                    + "(Die 'ID' des Beitrages steht in der Liste oben ganz rechts)\n\n";
            String s = JOptionPane.showInputDialog(this, stext, "Hilfe-Thema löschen", JOptionPane.OK_CANCEL_OPTION);

            if ((s != null) && (s.length() >= 0)) {
                if (s.equals("therapis1b2rta")) {
                    int row = tblThemen.getSelectedRow();
                    if (row < 0) {
                        return;
                    }
                    String deleteid = tblThemen.getValueAt(row, 3)
                                               .toString();
                    String cmd = "delete from htitel where id = '" + deleteid + "' LIMIT 1";
                    sqlAusfuehren(cmd);
                    this.themenDtblm.removeRow(tblThemen.convertRowIndexToModel(row));
                } else {
                    JOptionPane.showMessageDialog(null, "Passwort für Löschvorgang wurde nicht akzeptiert!");
                }

                return;
            }

        }
        if (command.equals("hilfespeichern")) {
            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {

                    try {
                        String[] sret = transferiereDatei();
                        if (sret.length > 0) {
                            piHelp.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            wf = new WartenFenster();
                            wf.setLocationRelativeTo(null);
                            wf.setVisible(true);
                            WartenFenster.setStand("Transferiere Datei: " + sret[0]);

                            FTPTools ftpt = new FTPTools();
                            lblstand.setText("Datei uploaden: " + sret[0]);
                            ftpt.ftpTransferX(sret[0], sret[1], new Long(sret[2]), jprogress);
                            lblstand.setText("Upload beendet");
                            piHelp.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            wf.dispose();
                            ftpt = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

            }.execute();
        }

    }

    public void doAbbrechen() {
        buts[3].setEnabled(false);
        buts[0].setEnabled(true);
        buts[2].setEnabled(true);
        buts[1].setEnabled(false);
        chkweb.setEnabled(true);
        ooPanel.schliesseText();
        ooPanel.neuesNoaPanel();
        neuertext = false;
        tblThemen.setEnabled(true);
        helpFenster.thisClass.stitel.setText((String) tblThemen.getValueAt(tblThemen.getSelectedRow(), 0));

        stitel.setEnabled(false);
    }

    public static void sqlAusfuehren(String sstmt) {
        Statement stmt = null;
        try {
            stmt = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Fehler bei der Ausführung des Statements\nMethode:sqlAusfuehren(" + sstmt + ")");
        }
        try {
            stmt.execute(sstmt);
        } catch (SQLException ev) {
            // System.out.println("SQLException: " + ev.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException sqlEx) { // ignore }
                    stmt = null;
                }
            }
        }
        return;
    }

    private String[] transferiereDatei() {
        String[] sret = {};
        piHelp.thisFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        final File file = new File(initvz);
        chooser.setCurrentDirectory(file);
        chooser.setVisible(true);
        piHelp.thisFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            String inputVerzStr = inputVerzFile.getPath();
            String inputDatei = inputVerzFile.getName();

            File fgr = new File(inputVerzStr);
            long length = fgr.length();
            initvz = String.valueOf(inputVerzStr);
            sret = new String[] { inputDatei, inputVerzStr, new Long(length).toString() };
        }
        chooser.setVisible(false);

        return sret;
    }

    private static boolean inTransaktion() {
        try {
            if (thisClass.ftpfertig && thisClass.sqlfertig) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private static void statusFensterSchliessen() {
        thisClass.wf.dispose();
        thisClass.wf = null;
    }

    private void speichern() {
        sqlfertig = false;
        ftpfertig = false;
        wf = new WartenFenster();
        wf.setLocationRelativeTo(null);
        wf.setVisible(true);
        WartenFenster.setStand("Überprüfe Dateien");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                long zeit = System.currentTimeMillis();
                while (inTransaktion()) {
                    try {
                        helpFenster.thisClass.wf.toFront();
                        Thread.sleep(50);
                        // System.out.println("Warte auf fertig....");
                        if (System.currentTimeMillis() - zeit > 50000) {
                            // break;
                        }

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                // System.out.println("**********dispose() abgesetzt");
                helpFenster.statusFensterSchliessen();
                doAbbrechen();

                return null;
            }

        }.execute();

        String test = "'" + stitel.getText() + "' AND gruppe='" + gruppenbox.getSelectedItem() + "'";
        testObVorhanden vorhanden = new testObVorhanden();
        /* 1. In Datenbank nachsehen ob bereits vorhanden */

        if (neuertext) {
            if (vorhanden.init(test)) {
                JOptionPane.showMessageDialog(null, "Dieser Titel ist bereits vorhanden");
                sqlfertig = true;
                ftpfertig = true;
                return;
            }
            absolutDatei = new Long(System.currentTimeMillis()).toString() + ".html";
        }

        /* 2. Wenn nicht in temp-Verzeichnis speichern */

        System.out.println("Ist FTP-Fertig -> " + ftpfertig);
        System.out.println("Ist Sql-Fertig -> " + sqlfertig);
        tempname = new String(absolutDatei);

        helpFenster.hilfeDatei = piHelp.tempvz + new String(tempname);
        WartenFenster.setStand("Aktuelle Datei wird gespeichert");
        /********** OOo-Speichern **********/
        String tempdat = ooPanel.speichernText(helpFenster.hilfeDatei, neuertext);
        System.out.println("Ist FTP-Fertig -> " + ftpfertig);
        System.out.println("Ist Sql-Fertig -> " + sqlfertig);

        ooPanel.schliesseText();

        /************** Einheit gehört zusammen ************/
        extrahiereBilder(helpFenster.hilfeDatei);
        // System.out.println(helpFenster.thisClass.bilder);
        /************** Einheit gehört zusammen ************/

        ooPanel.neuesNoaPanel();

        ftpfertig = false;

        String falt = helpFenster.hilfeDatei + ".html";
        String fneu = new String(falt).substring(0, falt.length() - 5);
        new File(fneu).delete();
        new File(falt).renameTo(new File(fneu));
        // System.out.println("Alter Dateiname = "+falt);
        // System.out.println("Neuer Dateiname = "+fneu);

        ooPanel.starteDatei(fneu, helpFenster.thisClass.chkweb.isSelected());

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                /*
                 * WartenFenster wf = new WartenFenster(); wf.setLocationRelativeTo(null);
                 * wf.setVisible(true);
                 */
                try {
                    FTPTools ftpt = new FTPTools();
                    Vector<String> ar = new Vector<String>();
                    ar = FTPTools.getInstance()
                                 .holeDatNamen();

                    for (int i = 0; i < helpFenster.thisClass.bilder.size(); i++) {
                        if (!ar.contains(helpFenster.thisClass.bilder.get(i))) {
                            // System.out.println("jetzt wird der FTP zum speichern angeschmissen");
                            WartenFenster.setStand("Übertrage Grafik: " + helpFenster.thisClass.bilder.get(i));
                            long gross = new File(piHelp.tempvz + helpFenster.thisClass.bilder.get(i)).length();
                            System.out.println(
                                    "Scheibe Bild " + helpFenster.thisClass.bilder.get(i) + " " + gross + " Bytes");
                            lblstand.setText(helpFenster.thisClass.bilder.get(i));
                            ftpt.schreibeDatei(helpFenster.thisClass.bilder.get(i), helpFenster.thisClass.bilder.get(i),
                                    gross, helpFenster.thisClass.jprogress);
                            Thread.sleep(50);

                        }
                    }
                    WartenFenster.setStand("Übertrage Datei: " + helpFenster.absolutDatei);
                    String htmldat = helpFenster.absolutDatei;
                    long gross = new File(helpFenster.hilfeDatei).length();
                    System.out.println("Scheibe HTML-Datei " + helpFenster.hilfeDatei + " " + gross + " Bytes");
                    lblstand.setText(htmldat);
                    ftpt.schreibeDatei(htmldat, htmldat, new File(helpFenster.hilfeDatei).length(),
                            helpFenster.thisClass.jprogress);

                    ftpt = null;

                    // System.out.println("ftp = fertig");

                    ftpfertig = true;
                    System.out.println("Ist FTP-Fertig -> " + ftpfertig);
                    System.out.println("Ist Sql-Fertig -> " + sqlfertig);

                } catch (Exception ex) {
                    ftpfertig = true;
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();

        /*
         * 3. Dann Tempdateien HTML - in Datenbank transferieren 4. Nachsehen ob
         * Bilddateien eingebunden sind 5. Falls ja die Bilddateien in ArrayEinlesen 6.
         * jede Bilddatei in ByteArray Verwandeln und in Datenbank transferieren.
         *
         */

        while (!ftpfertig) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        htmlDaten = new Vector();
        htmlDaten.add(stitel.getText()
                            .trim());
        htmlDaten.add(0);
        htmlDaten.add(gruppenbox.getSelectedItem());
        try {
            if (!tempdat.contains(".html")) {
                tempdat = tempdat + ".html";
            }
            BufferedReader in = null;
            in = new BufferedReader(new FileReader(piHelp.tempvz + absolutDatei));

            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line)
                      .append("\n");
            }
            in.close();
            String escapen = buffer.toString();
            escapen = escapen.replace("\\", "\\\\");
            escapen = escapen.replace("'", "\\'");

            // htmlDaten.add( readFileToByteArray(helpFenster.hilfeDatei));
            htmlDaten.add(escapen.getBytes());
            htmlDaten.add(absolutDatei);

        } catch (IOException e1) {

            e1.printStackTrace();
        }

        if (!neuertext) {
            htmlDaten.add(tblThemen.getValueAt(tblThemen.getSelectedRow(), 3));
            // System.out.println("Id für Update =
            // "+tblThemen.getValueAt(tblThemen.getSelectedRow(),3) );
        }
        try {
            if (wf != null) {
                WartenFenster.setStand("Übertrage Dateien in Datenbank......");
            }
            sqlfertig = doHtmlSpeichern((Vector) htmlDaten.clone());
            /*
             * HtmlSpeichern hms = new HtmlSpeichern(); hms.init((Vector)htmlDaten.clone());
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("Ist FTP-Fertig -> " + ftpfertig);
        System.out.println("Ist Sql-Fertig -> " + sqlfertig);
        System.out.println("Übertrag in DB -> " + sqlfertig);
        sqlfertig = true;

    }

    public static byte[] readFileToByteArray(String originalFilePath) throws IOException {

        FileInputStream fis = new FileInputStream(originalFilePath);
        byte[] buf = new byte[1024 * 10];
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((numRead = fis.read(buf)) > 0) {
            baos.write(buf, 0, numRead);
            baos.flush();

        }
        fis.close();
        byte[] returnVal = baos.toByteArray();
        baos.close();
        return returnVal.clone();
    }

    public static ByteArrayOutputStream readFileToOutputStream(String originalFilePath) throws IOException {

        FileInputStream fis = new FileInputStream(originalFilePath);
        byte[] buf = new byte[1024 * 10];
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((numRead = fis.read(buf)) > 0) {
            baos.write(buf, 0, numRead);
            baos.flush();

        }
        fis.close();
        baos.close();
        return baos;
    }

    public static String readFileToString(String originalFilePath) throws IOException {

        FileInputStream fis = new FileInputStream(originalFilePath);
        byte[] buf = new byte[1024 * 10];
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((numRead = fis.read(buf)) > 0) {
            baos.write(buf, 0, numRead);
            baos.flush();

        }
        fis.close();
        byte[] returnVal = baos.toByteArray();
        baos.close();
        return new String(returnVal);
    }

    /****************************************************************/
    public static void erzeugeAusByteArray(byte[] bhtml, String datei, boolean alsweb) {
        FileOutputStream fileOut;
        String indatei = datei;
        if (!indatei.contains(".html")) {
            indatei = indatei + ".html";
        } else {
            // System.out.println("erzeugeAusByteArray -> Dateiname korrekt = "+datei);
        }
        try {
            fileOut = new FileOutputStream(indatei);
            fileOut.write(bhtml);
            fileOut.flush();
            fileOut.close();
            try {
                extrahiereBilder(datei);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(null, "Die Hilfe-Datei konnte nicht erzeugt werden");
            e1.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Die Hilfe-Datei konnte nicht erzeugt werden");
            e.printStackTrace();
        }
    }

    /***********************************************/
    public static void extrahiereBilder(String url) {
        //// System.out.println("helpFenster -> Beginn extrahiereBilder aus "+url);
        helpFenster.thisClass.bilder.clear();
        BufferedReader infile = null;
        BufferedWriter outfile = null;

        try {
            // System.out.println("Extrahiere Bilder aus Dateiname: "+url);

            infile = new BufferedReader(new FileReader(url));
            outfile = new BufferedWriter(new FileWriter(url + ".html"));
            String str;
            while ((str = infile.readLine()) != null) {
                if (str.contains("IMG SRC=")) {
                    outfile.write(testeString(new String(str), "/") + "\n");
                    outfile.flush();
                } else {
                    outfile.write(new String(str) + "\n");
                    outfile.flush();
                }
            }
            outfile.flush();
            outfile.close();
            infile.close();

        } catch (IOException e) {

            e.printStackTrace();
            // System.exit(1);
        }
    }

    /***********************************************/
    public static String testeString(String webstring, String trenner) {
        int aktuell = 0;
        int wo = 0;

        String meinweb = new String(webstring);
        String ssret = "";
        int lang = meinweb.length();

        while ((wo = webstring.indexOf("IMG SRC=\"", aktuell)) > 0) {
            String nurBild = "";
            boolean start = false;
            boolean austritt = false;
            int iende = 0;
            int istart = 0;
            for (int i = wo; i < lang; i++) {
                for (int d = 0; d < 1; d++) {
                    if ((meinweb.substring(i, i + 1)
                                .equals("\""))
                            && (!start)) {
                        i++;
                        istart = i;
                        start = true;
                        break;
                    }
                    if ((meinweb.substring(i, i + 1)
                                .equals("\""))
                            && (start)) {
                        start = false;
                        iende = i;
                        austritt = true;
                        break;
                    }
                }
                if (austritt) {
                    break;
                }
                if (start) {
                    nurBild = nurBild + meinweb.substring(i, i + 1);
                }
            }
            int ergebnis = nurBild.lastIndexOf(trenner);
            String sret = "";
            if (ergebnis > -1) {
                sret = new String(nurBild.substring(ergebnis + 1));
                String salt = meinweb.substring(istart, iende);
                ssret = new String(meinweb.replaceAll(salt, sret));
                helpFenster.thisClass.bilder.add(sret);

            } else {
                // String salt = meinweb.substring(istart,iende);
                sret = nurBild;
                ssret = new String(meinweb);
                helpFenster.thisClass.bilder.add(nurBild);
            }
            aktuell = new Integer(iende);
        }

        return ssret;

    }



    class SharedListSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();


            if (!lsm.isSelectionEmpty()) {

                int minIndex = lsm.getMinSelectionIndex();

                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        helpFenster.thisClass.stitel.setText((String) helpFenster.thisClass.tblThemen.getValueAt(i, 0));
                    }
                }
            }
        }

    }

    /******************************************************/
    public boolean doHtmlSpeichern(Vector htmlvec) {
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        /*
         * WartenFenster wf = new WartenFenster(); wf.setLocationRelativeTo(null);
         * wf.setVisible(true);
         */
        try {

            if (htmlvec.size() < 6) {
                // System.out.println("In neuem Text speichern");
                System.out.println("Neuer Text wird eingefügt");
                WartenFenster.setStand("Übertrage Internetseite");

                stmt = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                String select = "Insert into htitel set titel = ? , bilder = ?, gruppe = ?, inhalt = ?, datei = ?, lastedit = ?;";

                ps = piHelp.conn.prepareStatement(select);
                ps.setBytes(1, ((String) htmlvec.get(0)).getBytes());
                ps.setInt(2, (new Integer((Integer) htmlvec.get(1))));
                ps.setBytes(3, ((String) htmlvec.get(2)).getBytes());
                ps.setBytes(4, (byte[]) htmlvec.get(3));
                ps.setString(5, (String) htmlvec.get(4));
                ps.setString(6, DatFunk.sDatInSQL(DatFunk.sHeute()));
                // ps.setString(2, "vorschau");
                ps.execute();

                String neuid = "select max(id) from htitel";
                rs = stmt.executeQuery(neuid);
                rs.next();
                int ineuid = rs.getInt(1);
                Vector vec = new Vector();
                vec.add(htmlvec.get(0));
                vec.add(htmlvec.get(4));
                vec.add("");
                vec.add(new Integer(ineuid).toString());
                helpFenster.thisClass.themenDtblm.addRow((Vector) vec.clone());
                helpFenster.thisClass.tblThemen.setRowSelectionInterval(
                        helpFenster.thisClass.tblThemen.getRowCount() - 1,
                        helpFenster.thisClass.tblThemen.getRowCount() - 1);
            } else {
                System.out.println("Bestehender Text wird Updated");

                WartenFenster.setStand("Übertrage Internetseite");
                /*
                 * stmt = (Statement)
                 * piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                 * ResultSet.CONCUR_UPDATABLE );
                 */

                // String sid = new Integer( ((Integer)htmlvec.get(4)) ).toString();
                String select = "Update htitel set titel = ? , bilder = ?, gruppe = ?, inhalt = ? ,lastedit = ? where id= ?;";

                ps = piHelp.conn.prepareStatement(select);

                ps.setBytes(1, ((String) htmlvec.get(0)).getBytes());

                ps.setInt(2, new Integer(htmlvec.get(1)
                                                .toString()));

                ps.setBytes(3, ((String) htmlvec.get(2)).getBytes());

                ps.setBytes(4, (byte[]) htmlvec.get(3));

                ps.setString(5, DatFunk.sDatInSQL(DatFunk.sHeute()));

                ps.setInt(6, new Integer(htmlvec.get(5)
                                                .toString()));

                ps.execute();

            }
            System.out.println("Daten wurden erfolgreich in DB gespeichert");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Abspeichern in die MySql-Tabelle");
            e.printStackTrace();
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) { // ignore }
                rs = null;
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx) { // ignore }
                stmt = null;
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException sqlEx) { // ignore }
                stmt = null;
            }
        }

        // System.out.println("FTP = fertig");
        // System.out.println("Die Größe des Arrays = "+htmlvec.size());

        return true;

    }
    /******************************************************/

}


class MyDefaultTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    @Override
    public Class getColumnClass(int columnIndex) {
            return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

}

final class WorkerTitel extends SwingWorker<Void, Void> {
    String gruppe;

    public void init(String sgruppe) {
        this.gruppe = sgruppe;
        execute();
    }


    @Override
    protected Void doInBackground() throws Exception {
        Vector combInhalt = holeGruppen();

        helpFenster.thisClass.setzeTitelTabelle((Vector) combInhalt.clone());
        if (combInhalt.size() > 0) {
            helpFenster.thisClass.tblThemen.setRowSelectionInterval(0, 0);
        }
        return null;

    }

    private Vector holeGruppen() {
        Statement stmtx = null;
        ResultSet rsx = null;
        Vector comboInhalt = null;
        Vector gesamtVec = null;
        try {
            stmtx = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        } catch (SQLException e) {

            e.printStackTrace();
        }
        try {
            comboInhalt = new Vector();
            rsx = stmtx.executeQuery("select titel,datei,lastedit,id from htitel where gruppe='" + this.gruppe + "'");
            gesamtVec = new Vector();
            while (rsx.next()) {
                comboInhalt = new Vector();
                comboInhalt.add(rsx.getString("titel"));
                comboInhalt.add(rsx.getString("datei"));
                comboInhalt.add(rsx.getString("lastedit"));
                comboInhalt.add(rsx.getString("id"));
                gesamtVec.add(comboInhalt.clone());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rsx != null) {
            try {
                rsx.close();
            } catch (SQLException sqlEx) {
                rsx = null;
            }
        }
        try {
            stmtx.close();
        } catch (SQLException sqlEx) {
            stmtx = null;
        }
        return (Vector) gesamtVec.clone();

    }

}




final class HtmlHolen extends SwingWorker<Void, Void> {
    int id;

    public void init(int id) {
        this.id = id;
        helpFenster.thisClass.bilder.clear();
        execute();
    }

    @Override
    protected Void doInBackground() throws Exception {
        Statement stmtx = null;
        ResultSet rsx = null;
        try {
            stmtx = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        } catch (SQLException e) {

            e.printStackTrace();
        }
        try {
            WartenFenster wf = new WartenFenster();
            wf.setLocationRelativeTo(null);
            wf.setVisible(true);
            rsx = stmtx.executeQuery("select inhalt,datei from htitel where id='" + this.id + "'");
            while (rsx.next()) {
                helpFenster.absolutDatei = rsx.getString("datei");
                helpFenster.hilfeDatei = piHelp.tempvz + rsx.getString("datei");
                if (rsx.getBytes("inhalt").length <= 10) {
                    new copyFile(piHelp.tempvz + "dummy.html", helpFenster.hilfeDatei);

                } else {
                    WartenFenster.setStand("Erzeuge HTML-Datei");
                    helpFenster.erzeugeAusByteArray(rsx.getBytes("inhalt"), helpFenster.hilfeDatei,
                            helpFenster.thisClass.chkweb.isSelected());
                }
                if (helpFenster.thisClass.bilder.size() > 0) {
                    FTPTools ftpt = new FTPTools();
                    for (int i = 0; i < helpFenster.thisClass.bilder.size(); i++) {
                        File f = new File(piHelp.tempvz + helpFenster.thisClass.bilder.get(i));
                        if (f.exists()) {
                            WartenFenster.setStand("Bereits vorhanden: " + (i + 1) + " von "
                                    + helpFenster.thisClass.bilder.size() + " " + helpFenster.thisClass.bilder.get(i));
                            helpFenster.thisClass.lblstand.setText(
                                    "Bereits vorhanden: " + helpFenster.thisClass.bilder.get(i));
                        } else {
                            WartenFenster.setStand("Hole Datei: " + (i + 1) + " von "
                                    + helpFenster.thisClass.bilder.size() + " " + helpFenster.thisClass.bilder.get(i));
                            helpFenster.thisClass.lblstand.setText(
                                    "Hole Datei: " + helpFenster.thisClass.bilder.get(i));
                            ftpt.holeDatei(helpFenster.thisClass.bilder.get(i), helpFenster.thisClass.bilder.get(i),
                                    new Long(0), helpFenster.thisClass.jprogress);
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                    ftpt = null;
                    helpFenster.thisClass.lblstand.setText("FTP-Transfer beendet");
                    helpFenster.thisClass.lblstand.setText("FTP-Status: warten...");

                }
                String falt = helpFenster.hilfeDatei + ".html";
                String fneu = helpFenster.hilfeDatei.substring(0, falt.length() - 5);
                new File(fneu).delete();
                new File(falt).renameTo(new File(fneu));
                ooPanel.starteDatei(fneu, helpFenster.thisClass.chkweb.isSelected());

            }
            WartenFenster.setStand("Datentransfer beendet ");
            wf.dispose();
            wf = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rsx != null) {
            try {
                rsx.close();
            } catch (SQLException sqlEx) {
                rsx = null;
            }
        }
        try {
            stmtx.close();
        } catch (SQLException sqlEx) {
            stmtx = null;
        }
        return null;
    }
}

class copyFile {
    public copyFile(String fin, String fout) {
        try {
            FileInputStream in = new FileInputStream(fin);
            FileOutputStream out = new FileOutputStream(fout);
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }

    }

}
