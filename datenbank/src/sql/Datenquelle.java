package sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.ini4j.Ini;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import crypt.Verschluesseln;

public class Datenquelle {

    private static final String DATEN_BANK = "DatenBank";
    MysqlDataSource dataSource;

    Datenquelle(String digitString) {
        initialize(digitString);

    }

    private void initialize(String digitString) {
        File f = new File(environment.Path.Instance.getProghome() + "ini/" + digitString + "/rehajava.ini");
        Ini ini;
        try {
            ini = new Ini(f);
            ini.load();
        } catch (IOException e) {
            throw new IllegalArgumentException(f.getPath());
        }
        dataSource = new MysqlDataSource();
        dataSource.setUrl(ini.get(DATEN_BANK, "DBKontakt1"));
        dataSource.setUser(ini.get(DATEN_BANK, "DBBenutzer1"));
        String pw = Verschluesseln.getInstance()
                                  .decrypt(ini.get(DATEN_BANK, "DBPasswort1"));
        dataSource.setPassword(pw);
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }

}
