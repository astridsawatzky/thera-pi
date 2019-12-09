package sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import mandant.IK;

public class DatenquellenFactory {
    private static final ConcurrentMap<IK, Datenquelle> grube = new ConcurrentHashMap<>();
    private IK ik;

    public DatenquellenFactory(IK ik) {
        this.ik = ik;
    }

     public Connection createConnection() throws SQLException {
         grube.computeIfAbsent(ik,k -> new Datenquelle(ik.digitString()));
            return grube.get(ik).connection();
     }

}


