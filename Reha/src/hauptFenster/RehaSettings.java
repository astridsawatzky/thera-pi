package hauptFenster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.ini4j.Ini;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import CommonTools.Verschluesseln;
import mandant.Mandant;

public class RehaSettings {

    private Ini ini;
    private DataSource ds;

    public RehaSettings(Mandant mainMandant) throws IOException {
        String iniPath = environment.Path.Instance.getProghome()+"ini/"+ mainMandant.ik()+"/rehajava.ini";
        Path inifile = Paths.get(iniPath);
        if (Files.exists(inifile)) {
            ini = new Ini(inifile.toFile());
        } else {
            throw new FileNotFoundException(iniPath);
        }
        ds =  extractservername();
    }

    private DataSource extractservername() {

        String connectionUrl = ini.get("DatenBank", "DBKontakt1");

        MysqlDataSource mds = new MysqlDataSource();
        mds.setUrl(connectionUrl);
        mds.setUser(ini.get("DatenBank", "DBBenutzer1"));
        mds.setPassword(Verschluesseln.getInstance().decrypt(ini.get("DatenBank", "DBPasswort1")));
        return mds;
    }

    public DataSource datasource() {
        return ds;

    }



}

