package gBriefe;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import CommonTools.ini.Settings;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import logging.Logging;
import office.OOTools;
import sql.DatenquellenFactory;

public class GBriefe implements WindowStateListener, WindowListener, ComponentListener, ContainerListener {


    public static String aktIK;
    public static String tempvz;
    public static String vorlagenvz;
    public static String hilfeserver;
    public static String hilfeftp;
    public static String hilfeuser;
    public static String hilfepasswd;
    public static String adsconnection;

    public static Connection conn = null;
    public static GBriefe thisClass = null;
    public static JFrame thisFrame = null;
    public JXFrame jFrame = null;

    public static boolean DbOk;
    public static Optional<IOfficeApplication> officeapplication;
    public static boolean warten = true;
    public JXPanel contpan = null;

    private String dblogin;
    public static RehaSockServer RehaSock = null;

    public static boolean testcase = false;

    public static void main(String[] args) throws SQLException {
        new Logging("gbriefe");




        String ikZiffern=args[1];
        conn= new DatenquellenFactory(ikZiffern).createConnection();
        String proghome = environment.Path.Instance.getProghome();
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
        Settings inif = null;


        /**************************/
        new Thread() {
            @Override
            public void run() {
                try {
                    // System.out.println("Starte SocketServer");
                    RehaSock = new RehaSockServer();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }.start();
        /**************************/

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", proghome + "RehaxSwing.jar");
        try {
            processBuilder.start();
        } catch (IOException e2) {

            e2.printStackTrace();
        }



        if (args.length > 0 || testcase) {
            System.out.println("Programmverzeichnis = " + proghome);



            tempvz = args[0] + "temp/" + args[1] + "/";
            vorlagenvz = args[0] + "vorlagen/" + args[1] + "/";


             aktIK = args[1];
          officeapplication =  OOTools.initOffice(args[0], aktIK);


        } else {

                JOptionPane.showMessageDialog(null,
                        "Keine Datenbankparameter übergeben!\nReha-Sql kann nicht gestartet werden");
                return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GBriefe application = new GBriefe();
                application.getJFrame();
                GBriefe.thisFrame.setIconImage(Toolkit.getDefaultToolkit()
                                                      .getImage(proghome + "icons/fragezeichen.png"));

            }
        });
        new SocketClient().setzeInitStand("Initialisiere Geburtstagsbriefe-Generator");

    }

    private JXFrame getJFrame() {
        if (jFrame == null) {
            jFrame = new JXFrame();

            jFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            thisClass = this;
            thisFrame = jFrame;
            jFrame.setTitle("Geburtstagsbriefe - Generator  [IK: " + aktIK + "] " + "[Server-IP: " + dblogin + "]");
            // jFrame.setTitle("Geburtstagsbriefe - Generator");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.addWindowListener(this);
            jFrame.addWindowStateListener(this);
            jFrame.addComponentListener(this);
            jFrame.addContainerListener(this);
            jFrame.setLayout(new BorderLayout());
            contpan = new JXPanel(new BorderLayout());
            SteuerPanel steuerPanel = new SteuerPanel();
            contpan.add(steuerPanel, BorderLayout.NORTH);


            JPanel jpan = new JPanel(new GridLayout());

            contpan.add(jpan, BorderLayout.CENTER);
            jFrame.setContentPane(contpan);
            jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
            jFrame.validate();
            jFrame.pack();
            steuerPanel.setOOoPanel(new OOoPanel(jpan));


            jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jFrame.setSize(800, 600);
                    jFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
                    jFrame.setVisible(true);
                    new SocketClient().setzeInitStand("INITENDE");

                }
            });

        }
        return jFrame;
    }

    @Override
    public void windowStateChanged(WindowEvent arg0) {

    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosed(WindowEvent arg0) {

        try {
            GBriefe.conn.close();
            System.out.println("Datenbankkontakt - geschlossen");
        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println("Programm Exit(0)");
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {

        try {
            GBriefe.conn.close();
            System.out.println("Datenbankkontakt - geschlossen");
        } catch (SQLException e) {

            e.printStackTrace();
        }
        if (OOoPanel.document != null) {
            try {
                OOoPanel.document.close();
            } catch (com.sun.star.lang.DisposedException dex) {
                System.exit(1);
            }
            System.out.println("OOWriter Dokument - geschlossen");
        }
        System.out.println("Programm Exit(0)");
        System.exit(0);
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {

    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {

    }

    @Override
    public void windowIconified(WindowEvent arg0) {

    }

    @Override
    public void windowOpened(WindowEvent arg0) {

    }

    @Override
    public void componentHidden(ComponentEvent arg0) {

    }

    @Override
    public void componentMoved(ComponentEvent arg0) {

    }

    @Override
    public void componentResized(ComponentEvent arg0) {

    }

    @Override
    public void componentShown(ComponentEvent arg0) {

    }

    @Override
    public void componentAdded(ContainerEvent arg0) {

    }

    @Override
    public void componentRemoved(ContainerEvent arg0) {

    }



}


class SocketClient {

    String stand = "";
    Socket server = null;

    public void setzeInitStand(String stand) {
        this.stand = new String(stand);
        run();
    }

    public void run() {
        try {
            serverStarten();
        } catch (IOException e) {

        }
    }

    private void serverStarten() throws IOException {
        this.server = new Socket("localhost", 1234);
        OutputStream output = server.getOutputStream();
        InputStream input = server.getInputStream();

        byte[] bytes = this.stand.getBytes();

        output.write(bytes);
        output.flush();
        int zahl = input.available();
        if (zahl > 0) {
            byte[] lesen = new byte[zahl];
            input.read(lesen);
        }

        server.close();
        input.close();
        output.close();
    }
}

/**************************/
class RehaSockServer {
    static ServerSocket serv = null;

    RehaSockServer() throws IOException {
        try {
            serv = new ServerSocket(1235);
            System.out.println("Reha SocketServer gestartet auf Port 1235");
        } catch (IOException e) {

            e.printStackTrace();
            // RehaxSwing.jDiag.dispose();
            return;
        }

        Socket client = null;

        while (true) {
            try {

                client = serv.accept();
            } catch (IOException e) {

                e.printStackTrace();
                break;
            }
            StringBuffer sb = new StringBuffer();
            InputStream input = client.getInputStream();
            OutputStream output = client.getOutputStream();
            int byteStream;
            String test = "";
            while ((byteStream = input.read()) > -1) {
                char b = (char) byteStream;

                sb.append(b);
            }

            test = new String(sb);
            System.out.println("Socket= " + test);
            final String xtest = new String(test);

            if (xtest.equals("INITENDE")) {
                byte[] schreib = "ok".getBytes();
                output.write(schreib);
                output.flush();
                output.close();
                input.close();
                serv.close();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                System.out.println("INITENDE-angekommen");
                GBriefe.warten = false;
                break;
            }
            byte[] schreib = "ok".getBytes();
            output.write(schreib);
            output.flush();
            output.close();
            input.close();

        }
        if (serv != null) {
            serv.close();
            serv = null;
            System.out.println("Socket wurde geschlossen");
        } else {
            System.out.println("Socket wurde geschlossen");
        }

        return;
    }
}
