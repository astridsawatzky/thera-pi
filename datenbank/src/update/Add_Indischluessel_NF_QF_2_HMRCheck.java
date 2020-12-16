package update;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import sql.DatenquellenFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Add_Indischluessel_NF_QF_2_HMRCheck extends Update {

    private Logger logger = LoggerFactory.getLogger(Add_Indischluessel_NF_QF_2_HMRCheck.class);

    @Override
    protected boolean postCondition(DatenquellenFactory dq) {

        return !tableContentIsOk(dq);
    }

    @Override
    protected void execute(DatenquellenFactory dq) {

        try (Connection conn = dq.createConnection(); Statement statement = conn.createStatement();) {
            statement.addBatch("INSERT IGNORE INTO `hmrcheck`\n"
                    + " (`indischluessel`, `gesamt`, `maxrezept`, `vorrangig`, `maxvorrangig`, `ergaenzend`, `maxergaenzend`, `id`)\n"
                    + " VALUES\n"
                    + " ('NF', '40', '6', '8001@8002@8003@8004@8005@8006', '6@6@6@6@6@6', NULL, NULL, NULL),\n"
                    + " ('QF', '40', '6', '8001@8002@8003@8004@8005@8006', '6@6@6@6@6@6', NULL, NULL, NULL);");
            int[] result = statement.executeBatch();

            logger.info("DB_Update_024 returned: " + Arrays.toString(result ));
        } catch (SQLException e) {
            logger.error("In DB_Update_024 execute:", e);
        }
    }

    @Override
    protected boolean preCondition(DatenquellenFactory dq) {
       return !tableContentIsOk(dq);
    }

    private boolean tableContentIsOk(DatenquellenFactory dq) {

        int count = 0;
        try (Connection conn = dq.createConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select count(*) as matchLines from hmrcheck where\n"
                        + "    indischluessel = 'NF' or indischluessel = 'QF';")) {
            rs.next();
            count = rs.getInt("matchLines");

        } catch (SQLException e) {
            logger.error("In DB_Update_024 preCondition:", e);
        }
        return !(count < 2);

    }
}
