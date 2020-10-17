package CommonTools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class ExUndHop extends Thread {
    private String statement;

    public static boolean processdone = false;

    public void setzeStatement(String statement) {
        processdone = false;
        this.statement = statement;
        start();
    }

    @Override
    public synchronized void run() {

        try
        (
                ResultSet rs = null;
                Statement stmt = SqlInfo.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        )
        {
               stmt.execute(this.statement);


        } catch (SQLException ex) {
            System.out.println("von stmt -SQLState: " + ex.getSQLState());
            new FireRehaError(RehaEvent.ERROR_EVENT, "Datenbankfehler!",
                    new String[] { "Datenabankfehler, Fehlertext:", ex.getMessage() });
        }


        processdone = true;
    }

    public static int erzeugeNummerMitMax(String nummer, int max) {
        int reznr = -1;
        /* Zunächst eine neue Rezeptnummer holen */
        Vector<String> numvec = null;
        try {
            SqlInfo.conn.setAutoCommit(false);

            numvec = SqlInfo.holeFeldForUpdate("nummern", nummer + ",id", " FOR UPDATE");

        if (!numvec.isEmpty()) {
            reznr = Integer.parseInt(numvec.get(0));
            if (reznr + 1 > max) {
                reznr = 1;
            }
            String cmd = "update nummern set " + nummer + "='" + (reznr + 1) + "' where id='" + numvec.get(1) + "'";
            new ExUndHop().setzeStatement(cmd);
            try {
                SqlInfo.conn.setAutoCommit(true);
            } catch (SQLException e) {
                SqlInfo.logger.error("something bad happens here", e);
            }
        } else {
            try {
                SqlInfo.conn.rollback();
                SqlInfo.conn.setAutoCommit(true);
            } catch (SQLException e) {
                SqlInfo.logger.error("something bad happens here", e);
            }
        }
        } catch (SQLException e) {
            SqlInfo.logger.error("something bad happens here", e);
        }
        numvec = null;
        return reznr;
    }

}
