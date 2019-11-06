package Suchen;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;

import gui.LaF;
import logging.Logging;
import mandant.IK;
import sql.DatenquellenFactory;

public class ICDrahmen implements Runnable {

    private JFrame jFrame;
    Connection conn;

    public static void main(String[] args) throws SQLException {
        LaF.setPlastic();
        new Logging("icd");
        IK ik;
        if (args.length > 0) {
            ik = new IK(args[0]);
        } else {
            ik = new IK("123456789");
        }
        Connection conn = new DatenquellenFactory().with(ik).createConnection();
        System.out.println(conn.isClosed());
        ICDrahmen icd = new ICDrahmen(conn);
        icd.getJFrame();

    }

    public ICDrahmen(Connection conn) {
        this.conn = conn;

    }

    @Override
    public void run() {
        getJFrame();

    }

    public JFrame getJFrame() {

        jFrame = new JFrame();
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(800, 600));
        jFrame.setTitle("ICD-Suche");
        jFrame.setContentPane(new ICDoberflaeche(new SqlInfo(conn)));
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }

}
