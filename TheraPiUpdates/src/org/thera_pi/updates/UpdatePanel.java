package org.thera_pi.updates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPFile;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import CommonTools.INIFile;
import environment.Path;
import sql.DatenquellenFactory;

public class UpdatePanel extends JXPanel {

    /**
     *
     */
    private static final long serialVersionUID = 8445390544969581869L;
    private static final String PROGHOME = Path.Instance.getProghome();
    TheraPiUpdates eltern = null;
    UpdateTab updateTab = null;

    UpdateTableModel tabmod = null;
    JXTable tab = null;

    public JProgressBar pbar = null;

    public JTextArea ta = null;
    public UpdateDlg abrdlg;

    DecimalFormat dcf = new DecimalFormat("##########0.00");
    SimpleDateFormat datumsFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss"); // Konv.
    SimpleDateFormat dformatter = new SimpleDateFormat("yyyy-MM-dd");
    Date currentTime = new Date();

    public static Vector<Vector<String>> updatefiles = new Vector<Vector<String>>();
    public static Vector<String[]> mandvec = new Vector<String[]>();

    public UpdateListSelectionHandler updateListener = null;

    DateFormat dataformat = DateFormat.getDateInstance(DateFormat.LONG);

    public static Vector<String> inupdatelist = new Vector<String>();

    FTPTools ftpt = null;

    Image imgkeinupdate = new ImageIcon(PROGHOME + "icons/clean.png").getImage()
                                                                     .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    Image imgupdate = new ImageIcon(PROGHOME + "icons/application-exit.png").getImage()
                                                                            .getScaledInstance(16, 16,
                                                                                    Image.SCALE_SMOOTH);
    ImageIcon icokeinupdate;
    ImageIcon icoupdate;
    public FTPFile[] ffile = null;


    public static boolean DbOk;

    private JFrame jFrame;

    private int xaktrow;

    // Verschluesseln man = Verschluesseln.getInstance();

    UpdatePanel(TheraPiUpdates xeltern, JFrame jFrame, UpdateTab xupdateTab) {

        super();
        eltern = xeltern;
        this.jFrame = jFrame;
        if (xupdateTab != null) {
            updateTab = xupdateTab;
        }
        setLayout(new BorderLayout());
        add(getHeader(), BorderLayout.NORTH);
        add(getContent(), BorderLayout.CENTER);
    }

    public void starteFTP() {
        doUpdateCheck();
    }

    public void doUpdateCheck() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    icokeinupdate = new ImageIcon(imgkeinupdate);
                    icoupdate = new ImageIcon(imgupdate);
                    doHoleUpdateConf();
                    updateCheck(PROGHOME + "update.files");
                    doFtpTest();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private JXHeader getHeader() {
        JXHeader head = new JXHeader();
        String titel = "<html><font size='5'><font color='e77817'>Thera-Pi</font>&nbsp;&nbsp; Update-Explorer (Vers. 2016-01)</font></html>";
        head.setTitle(titel);
        String description = "<html>Ein rotes " + "<img src='file:///" + PROGHOME
                + "icons/application-exit.png' width='16' height='16' align=\"bottom\">"
                + "signalisiert, daß die angezeigte Datei <b>neuer</b> ist als die Datei die sich auf Ihrem Rechner befindet.<br>"
                + "Wenn Sie in der Tabelle einen Doppelklick auf einer dieser Dateien ausführen, kopieren Sie diese Datei in Ihre Thera-Pi-Installation."
                + "<b><font color='aa0000'><br>Achtung:</font></b><br>Wenn INI-Dateien zum Update angeboten werden überschreiben Sie evtl. bestehende individuelle INI-Dateien. Bitte "
                + "machen Sie in diesem Fall vor dem Update eine <b>Sicherungskopie Ihres 'INI-Verzeichnisses'</b></html>";
        head.setDescription(description);
        head.setIcon(new ImageIcon(PROGHOME + "icons/TPorg.png"));
        return head;
    }

    private JXPanel getContent() {
        JXPanel jpan = new JXPanel();
        String xwerte = "5dlu,p:g,5dlu";
        // 1 2 3 4 5 6 7 8 9 10
        String ywerte = "5dlu,0dlu,0dlu,100dlu,5dlu,p,2dlu,0dlu:g,5dlu,p,5dlu";
        FormLayout lay = new FormLayout(xwerte, ywerte);
        CellConstraints cc = new CellConstraints();
        jpan.setLayout(lay);
        tabmod = new UpdateTableModel();
        tabmod.setColumnIdentifiers(new String[] { "Update-Datei", "Dateidatum/Uhrzeit", "Größe in Bytes", "aktuell" });

        tab = new JXTable(tabmod);
        tab.setSortable(false);
        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if (!TheraPiUpdates.updateallowed) {
                        JOptionPane.showMessageDialog(null,
                                "Keine gültigen Zugangsdaten eingegeben!\nUpdates können nicht heruntergeladen werden!");
                        // jFrame.dispose();
                        // System.exit(0);
                        return;
                    }
                    testeObUpdate(tab.getSelectedRow());
                }
            }
        });
        updateListener = new UpdateListSelectionHandler();
        tab.getSelectionModel()
           .addListSelectionListener(updateListener);
        JScrollPane jscr = JCompTools.getTransparentScrollPane(tab);
        jscr.validate();
        jpan.add(jscr, cc.xy(2, 4));
        pbar = new JProgressBar();
        pbar.setStringPainted(false);
        jpan.add(pbar, cc.xy(2, 6));

        ta = new JTextArea();
        ta.setFont(new Font("Courier", Font.PLAIN, 12));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setForeground(Color.BLUE);
        jscr = JCompTools.getTransparentScrollPane(ta);
        jscr.validate();
        jpan.add(jscr, cc.xy(2, 8, CellConstraints.FILL, CellConstraints.FILL));

        JButton but = new JButton("Update-Explorer beenden");
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (TheraPiUpdates.starteTheraPi) {
                    int anfrage = JOptionPane.showConfirmDialog(null, "Wollen Sie Thera-Pi 1.0 jetzt starten?",
                            "Thera-Pi starten?", JOptionPane.YES_NO_OPTION);
                    if (anfrage == JOptionPane.YES_OPTION) {
                        try {
                            Runtime.getRuntime()
                                   .exec("java -jar " + PROGHOME + "TheraPi.jar");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.exit(0);
                } else {
                    System.exit(0);
                }
            }
        });
        jpan.add(but, cc.xy(2, 10, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        jpan.validate();

        return jpan;
    }

    private void testeObUpdate(int row) {
        String upddatei = tab.getValueAt(row, 0)
                             .toString();
        Long gross = Long.parseLong(tab.getValueAt(row, 2)
                                       .toString());
        if (tab.getValueAt(row, 3)
               .equals(this.icokeinupdate)) {
            JOptionPane.showMessageDialog(null,
                    "Die Datei --> " + upddatei + " <-- ist bereits auf dem neuesten Stand. Update nicht erforderlich");
            return;
        }
        boolean ok = true;
        for (int i = 0; i < updatefiles.size(); i++) {
            if (updatefiles.get(i)
                           .get(0)
                           .equals(upddatei)) {
                if (upddatei.equals("TabellenUpdate.sql")) {
                    int anfrage = JOptionPane.showConfirmDialog(null,
                            "Soll der Tabellen-Update jetzt durchgeführt werden?", "Achtung wichtige Benutzeranfrage",
                            JOptionPane.YES_NO_OPTION);
                    if (anfrage == JOptionPane.YES_OPTION) {
                        doTabellenUpdate();
                        tabmod.setValueAt(icokeinupdate, row, 3);
                    }
                    break;
                }
                if (upddatei.equals("ProgrammAusfuehren.sql")) {
                    int anfrage = JOptionPane.showConfirmDialog(null,
                            "Soll das im Change-Log aufgeführte Programm jetzt gestartet werden?",
                            "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                    if (anfrage == JOptionPane.YES_OPTION) {
                        doProgExecute();
                        tabmod.setValueAt(icokeinupdate, row, 3);
                    }
                    break;
                }
                String cmd = "<html>Wollen Sie die Update-Datei <b>" + updatefiles.get(i)
                                                                                  .get(0)
                        + "</b> nach<br>" + "<b>" + updatefiles.get(i)
                                                               .get(1)
                        + "</b> kopieren</html>";
                int anfrage = JOptionPane.showConfirmDialog(null, cmd, "Achtung wichtige Benutzeranfrage",
                        JOptionPane.YES_NO_OPTION);
                if (anfrage == JOptionPane.YES_OPTION) {
                    pbar.setStringPainted(true);
                    pbar.getParent()
                        .validate();
                    final int ix = i;
                    final String xupdate = upddatei;
                    final long xgross = gross;
                    final int xrow = row;

                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {

                            try {
                                pbar.setValue(0);
                                String nurvz = updatefiles.get(ix)
                                                          .get(1)
                                                          .substring(0, updatefiles.get(ix)
                                                                                   .get(1)
                                                                                   .lastIndexOf("/")
                                                                  + 1);
                                System.out.println(updatefiles.get(ix)
                                                              .get(1));
                                System.out.println(updatefiles.get(ix)
                                                              .get(0));
                                System.out.println(nurvz);
                                try {
                                    if (updatefiles.get(ix)
                                                   .get(1)
                                                   .endsWith(".ini")) {
                                        File f = new File(updatefiles.get(ix)
                                                                     .get(1));
                                        f.renameTo(new File(updatefiles.get(ix)
                                                                       .get(1)
                                                                       .replace(".ini", ".bak")));
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null,
                                            "Fehler beim Umbenennen der Datei " + updatefiles.get(ix)
                                                                                             .get(1));
                                }
                                System.out.println("hole Datei -> " + updatefiles.get(ix)
                                                                                 .get(1)
                                        + " Schleifendurchlauf -> " + ix);
                                xaktrow = xrow;
                                try {
                                    ftpt = new FTPTools();
                                    ftpt.holeDatei(xupdate, nurvz, true, getInstance(), xgross);
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Bezug der Datei " + xupdate
                                            + " fehlgeschlagen!\nBitte starten Sie einen neuen Versuch");
                                    ftpt = null;
                                    pbar.setValue(0);
                                    // return null;
                                    return null;
                                }
                                ftpt = null;
                                // pbar.setValue(0);
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*
                                         * for(int i = 0; i < 6;i++){
                                         * System.out.println(i+" Divisionsrest = "+(eltern.userdaten.get(i).length() %
                                         * 8)); }
                                         */
                                        try {
                                            if (eltern.userdaten != null) {
                                                // System.out.println(eltern.userdaten);
                                                String stmt = "insert into updated set ik='" + eltern.userdaten.get(0)
                                                        + "', datei='" + xupdate + "', userid='"
                                                        + eltern.userdaten.get(6) + "', datum='"
                                                        + dformatter.format(currentTime) + "', " + "mac='"
                                                        + eltern.strMACAdr + "'";
                                                try {
                                                    SqlInfo.sqlAusfuehren(TheraPiUpdates.conn, stmt);
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } catch (Exception ex) {

                                        }

                                    }
                                    // TheraPiUpdates.RunAjax("http://www.thera-pi.org/html/updates.php",
                                    // "updated.txt", xupdate);

                                });

                                // tabmod.setValueAt((ImageIcon) icokeinupdate,xrow, 3);
                                // x
                            } catch (Exception ex2) {

                            }

                            return null;
                        }

                    }.execute();

                } else {
                    ok = false;
                }
            }
        }
        if (ok) {
            // tabmod.setValueAt((ImageIcon) this.icokeinupdate,row, 3);
        }

    }

    public void setDoneIcon() {
        tabmod.setValueAt(icokeinupdate, xaktrow, 3);
    }

    private UpdatePanel getInstance() {
        return this;
    }

    private void doHoleUpdateConf() {
        try {
            ftpt = new FTPTools();
            boolean geklappt = ftpt.holeDatei("update.files", PROGHOME, false, getInstance(), -1);
            if (!geklappt) {
                JOptionPane.showMessageDialog(null, "Fehler beim Bezug der Steuerdatei update.files");
            }
            ftpt = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doFtpTest() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    ftpt = new FTPTools();

                    ffile = ftpt.holeDatNamen();
                    Vector<Object> vec = new Vector<Object>();
                    for (int i = 0; i < ffile.length; i++) {
                        if ((!ffile[i].getName()
                                      .toString()
                                      .trim()
                                      .equals("."))
                                && (!ffile[i].getName()
                                             .toString()
                                             .trim()
                                             .equals(".."))
                                && (!ffile[i].getName()
                                             .toString()
                                             .startsWith("update."))) {
                            vec.clear();
                            vec.add(ffile[i].getName()
                                            .toString());
                            vec.add(datumsFormat.format(ffile[i].getTimestamp()
                                                                .getTime()
                                                                .getTime()));
                            vec.add(Long.valueOf(ffile[i].getSize())
                                        .toString());
                            if (mussUpdaten(ffile[i].getName()
                                                    .toString()
                                                    .trim(),
                                    ffile[i].getTimestamp()
                                            .getTime()
                                            .getTime())) {
                                vec.add(icoupdate);
                                tabmod.addRow((Vector<?>) vec.clone());
                            }
                            // System.out.println(ffile[i].getName());
                            // System.out.println(ffile[i].getTimestamp().getTime().getTime());
                            // System.out.println(ffile[i].getSize() + " Bytes");
                        } else {

                        }
                    }
                    if (tabmod.getRowCount() > 0) {
                        tab.setRowSelectionInterval(0, 0);
                    } else {
                        JOptionPane.showMessageDialog(null, "Ihre Thera-Pi-Installation ist auf dem aktuellen Stand.");
                    }
                    System.out.println("\n*********************************");
                    System.out.println("   Insgesamt getestete Files: " + ffile.length);
                    System.out.println("Davon muessen updated werden: " + tabmod.getRowCount());
                    System.out.println("*********************************\n");
                    /*
                     * System.out.println("Insgesamt = "+ffile.length+" Dateien"); File file = new
                     * File("C:/RehaVerwaltung/Reha.jar");
                     * System.out.println("Größe Originaldatei  = "+file.length()+" Bytes");
                     * System.out.println(file.lastModified()); ftpt.holeDatei("update.files",
                     * "C:/ftptests/", true, getInstance(),-1); Thread.sleep(100); pbar.setValue(0);
                     * ftpt.holeDatei("Reha.jar", "C:/ftptests/", true, getInstance(),-1);
                     */
                    ftpt = null;
                    jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ftpt = null;
                }
                return null;
            }

        }.execute();

    }

    private boolean mussUpdaten(String datei, Long datum) {
        try {
            File f = null;
            for (int i = 0; i < updatefiles.size(); i++) {
                if (updatefiles.get(i)
                               .get(0)
                               .equals(datei)) {
                    f = new File(updatefiles.get(i)
                                            .get(1));
                    if (!f.exists()) {
                        testeDirectory(f);
                        return true;
                    }
                    System.out.println("\nDatei: -----> " + f.getName()
                                                             .toString()
                            + "\n       Dateidatum lokal: " + usingDateFormatterWithTimeZone(f.lastModified())
                            + "\nDateidatum Updateserver: " + usingDateFormatterWithTimeZone(datum));
                    if (f.lastModified() < datum) {
                        System.out.println(
                                "lokale Datei ist aelter -> muss updated werden!!!!!" + "\n****************");
                        // System.out.println("Zeitunterschied = "+(f.lastModified()-datum));
                        // System.out.println(datumsFormat.format(f.lastModified()));
                        // System.out.println(datumsFormat.format(datum));
                        return true;
                    } else {
                        System.out.println(
                                "lokale Datei ist juenger -> darf nicht updated werden" + "\n****************");
                    }
                }
            }
            // System.out.println("Zeitunterschied = "+(f.lastModified()-datum));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void testeDirectory(File f) {
        String abspfad = f.getAbsolutePath()
                          .replace("\\", "/");
        String dir = abspfad.substring(0, abspfad.lastIndexOf("/"));
        File d = new File(dir);
        if (!d.exists()) {
            System.out.println("Erstelle Verzeichnis " + d.getAbsolutePath());
            try {
                d.mkdir();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Verzeichnis " + d.getAbsolutePath()
                        + " konnte nicht erstellt werden\n"
                        + "Bitte erstellen die das Verzeichnis von Hand und starten danach den Update-Explorer erneut");
            }
        }

    }

    private String usingDateFormatterWithTimeZone(long input) {
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss z");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);

    }

    /************************************************************************/
    private static void updateCheck(String xupdatefile) {
        String updatedir = "";
        String zeile = "";
        FileReader reader = null;
        BufferedReader in = null;

        /************************/
        INIFile inif = new INIFile(PROGHOME + "ini/mandanten.ini");
        int AnzahlMandanten = inif.getIntegerProperty("TheraPiMandanten", "AnzahlMandanten");
        for (int i = 0; i < AnzahlMandanten; i++) {
            String[] mand = { null, null };
            mand[0] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-IK" + (i + 1)));
            mand[1] = new String(inif.getStringProperty("TheraPiMandanten", "MAND-NAME" + (i + 1)));
            mandvec.add(mand);
        }

        /************************/

        try {
            Vector<String> dummy = new Vector<String>();
            zeile = "";
            reader = new FileReader(xupdatefile);
            in = new BufferedReader(reader);
            // String pfad = "";
            String[] sourceAndTarget = { null, null };
            Vector<Object> targetvec = new Vector<Object>();
            inupdatelist.clear();
            while ((zeile = in.readLine()) != null) {
                if (!zeile.startsWith("#")) {
                    if (zeile.length() > 5) {
                        sourceAndTarget = zeile.split("@");
                        // System.out.println(zeile);
                        // System.out.println(sourceAndTarget[0].trim());
                        // System.out.println(sourceAndTarget[1].trim().replace("%proghome%",
                        // proghome));
                        // System.out.println(sourceAndTarget.length);
                        if (sourceAndTarget.length == 2) {
                            // nur dann Dateien eintrgen
                            // pfad = zeile;
                            if (sourceAndTarget[1].contains("%proghome%")) {
                                dummy.clear();
                                dummy.add(updatedir + sourceAndTarget[0].trim());
                                dummy.add(sourceAndTarget[1].trim()
                                                            .replace("%proghome%", PROGHOME)
                                                            .replace("//", "/"));
                                if (!targetvec.contains(dummy.get(1))) {
                                    targetvec.add(new String(dummy.get(1)));
                                    updatefiles.add(new Vector<String>(dummy));
                                }
                            } else if (sourceAndTarget[1].contains("%userdir%")) {
                                String home = sourceAndTarget[1].trim()
                                                                .replace("%userdir%", PROGHOME)
                                                                .replace("//", "/");

                                for (int i = 0; i < mandvec.size(); i++) {
                                    dummy.clear();
                                    dummy.add(updatedir + sourceAndTarget[0].trim());
                                    dummy.add(home.replace("%mandantik%", mandvec.get(i)[0]));
                                    if (!targetvec.contains(dummy.get(1))) {
                                        updatefiles.add(new Vector<String>(dummy));
                                    }
                                    targetvec.add(String.valueOf(home.replace("%mandantik%", mandvec.get(i)[0])));
                                }
                            }
                            // Ende nur dann Dateien eintragen
                        }
                    }
                }
            }
            in.close();
            reader.close();
            if (updatefiles.size() > 0) {
                // System.out.println(updatefiles);
                // System.out.println("Anzahl Update-Dateien = "+updatefiles.size());
                // kopiereUpdate();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Fehler beim Bezug der Update-Informationen.\nBitte informieren Sie den Administrator umgehend");
        }
    }

    /*******************************************************/
    private void doProgExecute() {
        ftpt = new FTPTools();
        Long xgross = Long.parseLong(tab.getValueAt(tab.getSelectedRow(), 2)
                                        .toString());
        ftpt.holeDatei("ProgrammAusfuehren.sql", PROGHOME, true, getInstance(), xgross);
        ftpt = null;
        pbar.setValue(0);
        File file = null;
        FileReader freader = null;
        LineNumberReader lnreader = null;
        Vector<String> vecstmt = new Vector<String>();
        try {
            file = new File(PROGHOME + "ProgrammAusfuehren.sql");
            freader = new FileReader(file);
            lnreader = new LineNumberReader(freader);
            String line = "";
            while ((line = lnreader.readLine()) != null) {
                if (!String.valueOf(line)
                           .trim()
                           .equals("")) {
                    vecstmt.add(String.valueOf(line));
                }
                System.out.println("Statement = " + line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                freader.close();
                lnreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (vecstmt.size() > 0) {
            String cmd = vecstmt.get(0);
            cmd = cmd.replace("@proghome/", PROGHOME);
            final String command = String.valueOf(cmd);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Runtime.getRuntime()
                           .exec(command);
                    return null;
                }

            }.execute();
        } else {
            JOptionPane.showMessageDialog(null, "Keine Programm zur Ausführung gefunden");
        }

    }

    public static String Escaped(String string) {
        String escaped = string.replaceAll("\'", "\\\\'");
        escaped = escaped.replaceAll("\"", "\\\\\"");
        return escaped;
    }

    private void doTabellenUpdate() {
        String ik = "";
        ftpt = new FTPTools();
        Long xgross = Long.parseLong(tab.getValueAt(tab.getSelectedRow(), 2)
                                        .toString());
        ftpt.holeDatei("TabellenUpdate.sql", PROGHOME, true, getInstance(), xgross);
        ftpt = null;
        pbar.setValue(0);
        File file = null;
        FileReader freader = null;
        LineNumberReader lnreader = null;
        Vector<String> vecstmt = new Vector<String>();
        try {
            file = new File(PROGHOME + "TabellenUpdate.sql");
            freader = new FileReader(file);
            lnreader = new LineNumberReader(freader);
            String line = "";
            while ((line = lnreader.readLine()) != null) {
                if (!String.valueOf(line)
                           .trim()
                           .equals("")) {
                    // vecstmt.add(String.valueOf(Escaped(line)));
                    vecstmt.add(String.valueOf(line));
                }
                System.out.println("Statement = " + line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                freader.close();
                lnreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (vecstmt.size() > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                }
            });

            for (int i = 0; i < mandvec.size(); i++) {
                ik = mandvec.get(i)[0];
                int frage = JOptionPane.showConfirmDialog(null,
                        "Wollen Sie die Tabellen der Datenbank für IK ->" + ik + " jetzt anpassen",
                        "Achtung wichtige Benutzeranfrage", JOptionPane.YES_NO_OPTION);
                if (frage == JOptionPane.YES_OPTION) {
                    try {

                       Connection  conn = new DatenquellenFactory(ik).createConnection();
                        for (int x = 0; x < vecstmt.size(); x++) {
                            try {
                                System.out.println("Execute = " + vecstmt.get(x));
                                SqlInfo.sqlAusfuehren(conn, vecstmt.get(x));
                                System.out.println("Execute = " + vecstmt.get(x));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        conn.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Fehler beim öffnen der Datenbank\nBetroffene IK: " + ik
                                + "\nFehlertext: " + (ex.getMessage() == null ? "nicht verfügbar" : ex.getMessage()));
                    }
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

        } else {
            JOptionPane.showMessageDialog(null, "Keine Statements für Tabellen-Update gefunden");
        }
    }


    class UpdateTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) {
                return ImageIcon.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {

            return false;
        }

    }

    private boolean testeObLogVorhanden(String datei) {
        for (int i = 0; i < ffile.length; i++) {
            if (ffile[i].getName()
                        .equals(datei + ".log")) {
                return true;
            }
        }
        return false;
    }

    private String holeLogText(String logDatei) {
        FTPTools ftp = new FTPTools();
        return ftp.holeLogDateiSilent(logDatei);
    }

    class UpdateListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            boolean isAdjusting = e.getValueIsAdjusting();
            if (isAdjusting) {
                return;
            }
            if (lsm.isSelectionEmpty()) {

            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        final int ix = i;

                        new SwingWorker<Void, Void>() {

                            @Override
                            protected Void doInBackground() throws Exception {
                                try {
                                    int modi = tab.convertRowIndexToModel(ix);
                                    pbar.setValue(0);
                                    if (testeObLogVorhanden(tabmod.getValueAt(modi, 0)
                                                                  .toString())) {
                                        jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                        final int imodi = modi;
                                        new SwingWorker<Void, Void>() {

                                            @Override
                                            protected Void doInBackground() throws Exception {
                                                // System.out.println("Starte AbrDlg");
                                                abrdlg = new UpdateDlg();
                                                abrdlg.setzeLabel("<html>Beziehe Log-Datei für "
                                                        + tabmod.getValueAt(imodi, 0)
                                                                .toString()
                                                        + "<br><br>Bitte warten....<br></html>");
                                                abrdlg.setModal(true);
                                                // abrdlg.setPreferredSize(new Dimension(200,200));
                                                abrdlg.setAlwaysOnTop(true);

                                                abrdlg.pack();
                                                abrdlg.setLocationRelativeTo(null);
                                                abrdlg.setVisible(true);
                                                return null;
                                            }

                                        }.execute();

                                        try {
                                            ta.setForeground(Color.BLUE);
                                            ta.setText(holeLogText(tabmod.getValueAt(modi, 0)
                                                                         .toString()
                                                    + ".log"));
                                            if (ta.getText()
                                                  .equals("Fehler beim Bezug der Log-Datei, ftpClient == nicht connected\nBitte starten Sie einen neuen Versuch")) {
                                                tab.clearSelection();
                                                tab.setRowSelectionInterval(modi, modi);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            ta.setText("Fehler beim Bezug der Log-Datei");
                                        }
                                        jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                                    } else {
                                        ta.setForeground(Color.RED);
                                        ta.setText("Für diese Update-Datei ist kein ChangeLog verfügbar");

                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                // System.out.println("Beende AbrDlg");
                                if (abrdlg != null) {
                                    abrdlg.setVisible(false);
                                    abrdlg = null;
                                }
                                return null;
                            }

                        }.execute();

                        break;
                    }
                }
            }
        }
    }

}
