package gBriefe;

import java.sql.SQLException;

public class GBriefeTest {

    public static void main(String[] args) throws SQLException {
        GBriefe.main(new String[] {environment.Path.Instance.getProghome(), "987654321"});
    }

}
