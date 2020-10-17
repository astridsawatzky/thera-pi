package therapiHilfe;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;

import CommonTools.ini.INIFile;
import CommonTools.ini.Settings;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;
import environment.Path;
import logging.Logging;
import office.OOService;

public class piHelp  {
    public static String proghome;
    public static String dbtreiber;
    public static String dblogin;
    public static String dbuser;
    public static String dbpassword;
    public static String tempvz;
    public static String hilfeserver;
    public static String hilfeftp;
    public static String hilfeuser;
    public static String hilfepasswd;

    public static Connection conn = null;
    public static piHelp thisClass = null;
    public static JXFrame thisFrame = null;
    public JXFrame jFrame = null;

    public static boolean DbOk;
    public helpFenster hf = null;
    public static String OpenOfficePfad;
    public static String OfficeNativePfad;
    public static IOfficeApplication officeapplication;

    /**
     * @param args
     */
    public static void main(String[] args) {
        new Logging("pihelp");
        proghome = Path.Instance.getProghome();

        /************ Für die Entwicklung dieses Teil benutzen ********/
        // INIFile inif = new INIFile(piHelp.proghome+"pihelp.ini");

        Settings inif = new INIFile(piHelp.proghome + "ini/pihelp.ini");
        tempvz = new String(inif.getStringProperty("piHelp", "TempVZ"));

        OpenOfficePfad = new String(inif.getStringProperty("piHelp", "OOPfad"));
        OfficeNativePfad = new String(inif.getStringProperty("piHelp", "OONative"));
        starteOfficeApplication();

        hilfeserver = new String(inif.getStringProperty("piHelp", "HilfeServer"));
        hilfeftp = new String(inif.getStringProperty("piHelp", "HilfeFTP"));
        hilfeuser = new String(inif.getStringProperty("piHelp", "HilfeUser"));
        hilfepasswd = new String(inif.getStringProperty("piHelp", "HilfePasswd"));

        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (ClassNotFoundException e1) {

            e1.printStackTrace();
        } catch (InstantiationException e1) {

            e1.printStackTrace();
        } catch (IllegalAccessException e1) {

            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {

            e1.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                piHelp application = new piHelp();
                application.getJFrame();
                piHelp.thisFrame.setIconImage(Toolkit.getDefaultToolkit()
                                                     .getImage(proghome + "icons/fragezeichen.png"));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new WorkerGruppen().execute();
                    }
                });

            }
        });

    }

    private JXFrame getJFrame() {
        if (jFrame == null) {
            jFrame = new JXFrame();
            jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            thisClass = this;
            thisFrame = jFrame;
            jFrame.setTitle("pi-Hilfe - Generator");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.addWindowListener(wl);
            jFrame.setLayout(new BorderLayout());
            hf = new helpFenster();
            jFrame.setContentPane(hf);
            jFrame.pack();
            DatenbankStarten db = new DatenbankStarten();
            db.StarteDB();

            jFrame.setSize(800, 600);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrame.setVisible(true);
                    hf.starteOOO();
                }
            });

        }
        return jFrame;
    }

    WindowListener wl = new WindowAdapter() {




        @Override
        public void windowClosing(WindowEvent arg0) {

            try {
                piHelp.conn.close();
            } catch (SQLException e) {

                e.printStackTrace();
            }
            if (ooPanel.webdocument != null) {
                try {
                    if (ooPanel.webdocument.isOpen()) {
                        ooPanel.webdocument.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }

            }
            if (ooPanel.document != null) {
                try {
                    ooPanel.document.close();
                } catch (com.sun.star.lang.DisposedException dex) {
                    System.exit(1);
                }
            }

        }

    };

    public static void starteOfficeApplication() {

        try {

            OOService.setLibpath(OfficeNativePfad, OpenOfficePfad);
            officeapplication = new OOService().start();


            officeapplication.getDesktopService()
                             .addTerminateListener(new VetoTerminateListener() {
                                 @Override
                                 public void queryTermination(ITerminateEvent terminateEvent) {
                                     super.queryTermination(terminateEvent);
                                     try {
                                         IDocument[] docs = officeapplication.getDocumentService()
                                                                             .getCurrentDocuments();
                                         if (docs.length == 1) {
                                             docs[0].close();
                                         }
                                     } catch (DocumentException e) {
                                         e.printStackTrace();
                                     } catch (OfficeApplicationException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             });

        } catch (OfficeApplicationException | FileNotFoundException e) {

            e.printStackTrace();
        }
    }

}

final class DatenbankStarten implements Runnable {

    void StarteDB() {

        try {
            Class.forName("de.root1.jpmdbc.Driver");
        } catch (final Exception e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Laden des Datenbanktreibers für Preislisten-Server");
            return;
        }
        try {
            Properties connProperties = new Properties();
            connProperties.setProperty("user", "dbo336243054");
            connProperties.setProperty("password", "allepreise");
            connProperties.setProperty("host", "db2614.1und1.de");
            connProperties.setProperty("port", "3306");
            connProperties.setProperty("compression", "false");
            connProperties.setProperty("NO_DRIVER_INFO", "1");
            piHelp.conn = DriverManager.getConnection("jdbc:jpmdbc:http://www.thera-pi.org/jpmdbc.php?db336243054",
                    connProperties);

        } catch (final SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Fehler: Datenbankkontakt zum Preislisten-Server konnte nicht hergestellt werden.");
            return;
        }

    }

    @Override
    public void run() {
        StarteDB();
    }
}

final class WorkerGruppen extends SwingWorker<Void, Void> {

    @Override
    protected Void doInBackground() throws Exception {
        String[] combInhalt = holeGruppen();
        if (helpFenster.gruppenbox.getItemCount() > 0) {
            helpFenster.gruppenbox.removeAllItems();
        }
        for (int i = 0; i < combInhalt.length; i++) {
            helpFenster.gruppenbox.addItem(new String(combInhalt[i]));
        }
        return null;

    }

    private String[] holeGruppen() {
        String[] comboInhalt;
        try (Statement stmtx = piHelp.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rsCount = stmtx.executeQuery("select count(*) from hgroup");
                ResultSet hgruppen = stmtx.executeQuery("select gruppe from hgroup order by reihenfolge");

        ) {
            rsCount.next();
            comboInhalt = new String[rsCount.getInt(1)];
            int i = 0;
            while (hgruppen.next()) {
                comboInhalt[i] = hgruppen.getString(1);
                i++;
            }
        } catch (SQLException e) {
            comboInhalt = new String[0];
        }

        return comboInhalt;
    }
}
