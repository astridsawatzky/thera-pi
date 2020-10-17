package rehaBillEdit.Tools;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rehaBillEdit.RehaBillEdit;

public class SqlInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlInfo.class);

    public static Vector<String> holeSatz(String tabelle, String felder, String kriterium, List<String> ausschliessen) {
        Vector<String> retvec = new Vector<>();

        String sstmt = "select " + felder + " from " + tabelle + " where " + kriterium + " LIMIT 1";
        try (Statement stmt = RehaBillEdit.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sstmt))

        {
            if (rs.next()) {
                ResultSetMetaData rsMetaData = rs.getMetaData();


                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {

                        if (!ausschliessen.contains(rsMetaData.getColumnName(i))) {
                            if (rs.getString(i) == null) {
                                retvec.add("");
                            } else {
                                retvec.add(rs.getString(i));
                            }
                        }

                }
            }
        } catch (SQLException ev) {
            LOGGER.error("Tabelle:" + tabelle + ", Felder: " + felder + ", Kriterium" + kriterium + ", Ausschliessen:"
                    + ausschliessen, ev);
        }
        return retvec;
    }

    public static Vector<Vector<String>> holeFelder(String xstmt) {
        final Vector<Vector<String>> retkomplett = new Vector<>();
        final String sstmt = xstmt;

        try (Statement stmt = RehaBillEdit.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(sstmt))
        {
            while (rs.next()) {
                final Vector<String> retvec = new Vector<>();

                ResultSetMetaData rsMetaData = rs.getMetaData();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    if (rs.getString(i) == null) {
                        retvec.add("");
                    } else {
                        retvec.add(rs.getString(i));
                    }
                }
                retkomplett.add(retvec);
            }
        } catch (SQLException ev) {
            LOGGER.error(xstmt, ev);
        }

        return retkomplett;
    }

    public static void sqlAusfuehren(String sstmt) {
        try (Statement stmt = RehaBillEdit.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)) {
            stmt.execute(sstmt);
        } catch (SQLException ev) {
            LOGGER.error(sstmt, ev);
        }
    }

    public static String holeEinzelFeld(String xstmt) {
        String sstmt = xstmt;
        StringBuilder ret = new StringBuilder();
        try (Statement stmt = RehaBillEdit.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sstmt)) {
            if (rs.next()) {
                String value = rs.getString(1);
                if (value != null) {
                    ret.append(value.trim());
                }
            }
            return ret.toString();
        } catch (SQLException ev) {
            LOGGER.error(xstmt, ev);
            return "";
        }
    }
}
