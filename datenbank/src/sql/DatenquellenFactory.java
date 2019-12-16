package sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import mandant.IK;

public class DatenquellenFactory {
    private static final ConcurrentMap<String, Datenquelle> grube = new ConcurrentHashMap<>();
    private String ik;

    public DatenquellenFactory(String ik) {
        this.ik = ik;
    }



    public Connection createConnection() throws SQLException {
         grube.computeIfAbsent(ik,k -> new Datenquelle(ik));
            return grube.get(ik).connection();
     }

}


