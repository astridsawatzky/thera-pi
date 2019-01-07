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


/**
 * [DatenBank]
AnzahlConnections = 1
DBType1 = mysql
DBTreiber1 = com.mysql.jdbc.Driver
DBPort1 = 3306
DBKontakt1 = jdbc:mysql://127.0.0.1:3306/therapi
DBName1 = therapi
DBBenutzer1 = therapi
DBPasswort1 = E2fyCxAPQkqXjpVUetngA3H8rkblqjY8

 [HauptFenster]
Hintergrundbild = C:\\MeinWorkspace\\pics\\rta.gif
Bildgroesse = 659,413
FensterFarbeRGB = 83,124,83
FensterTitel = Reha-Verwaltung  -  (Java Version)
LookAndFeel = com.jgoodies.looks.plastic.PlasticXPLookAndFeel
HorizontalTeilen = 1
TP1Offen = 0
TP2Offen = 0
TP3Offen = 1
TP4Offen = 0
TP5Offen = 0
TP6Offen = 0
Divider1 = 1350
Divider2 = 250
TP7Offen = 1

[OpenOffice.org]
OfficePfad = C:/Program Files (x86)/OpenOffice 4
OfficeNativePfad = C:/Rehaverwaltung/Libraries/lib/openofficeorg

[Formulare]
PDFFormularPfad = L:\\projekte\\rta\\formulare

[Verzeichnisse]
Programmverzeichnis = c:\RehaVerwaltung\
Vorlagen = c:\RehaVerwaltung\vorlagen\
Icons = c:\RehaVerwaltung\icons\
Temp = c:\RehaVerwaltung\temp\
Ini = c:\RehaVerwaltung\ini\
Rehaplaner = c:\\
Fahrdienstliste = L:\\projekte\\rta\\dbf\\
Fahrdienstrohdatei = C:\\

;*************************Systemvariablen wie Kalender-Timer etc.
[SystemIntern]
AktJahr = 2018
Testlog = 0
KalenderLog = 1
BenutzerLog = 1
ZeitgeberAufruf = 60
Sound =
MinMemSize = 128m
MaxMemSize = 256m
VLog = 0
ALog = 0

**/

