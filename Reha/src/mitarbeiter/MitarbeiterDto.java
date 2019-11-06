package mitarbeiter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class MitarbeiterDto {

    private final IK ik;
    private Logger logger = LoggerFactory.getLogger(MitarbeiterDto.class);
    private String sql = "SELECT * FROM kollegen2";

    public MitarbeiterDto(IK ik) {
        this.ik = ik;
    }

    List<Mitarbeiter> all() {

        List<Mitarbeiter> mitarbeiterListe = new LinkedList<Mitarbeiter>();
        try (Connection con = new DatenquellenFactory().with(ik)
                                                       .createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(sql);
            while (rs.next()) {

                mitarbeiterListe.add(ofResultset(rs));

            }
            return mitarbeiterListe;
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
            return Collections.EMPTY_LIST;
        }

    }

    private Mitarbeiter ofResultset(ResultSet rs) throws SQLException {
        Mitarbeiter ma = new Mitarbeiter();
        ma.anrede = rs.getString("ANREDE");
        ma.vorname = rs.getString("VORNAME");
        ma.nachname = rs.getString("NACHNAME");
        ma.strasse = rs.getString("STRASSE");
        ma.plz = rs.getString("PLZ");
        ma.ort = rs.getString("ORT");
        ma.telefon1 = rs.getString("TELEFON1");
        ma.telfon2 = rs.getString("TELFON2");
        ma.geboren = rs.getDate("GEBOREN")==null?null:rs.getDate("GEBOREN")
                       .toLocalDate();
        ma.matchcode = rs.getString("matchcode");
        ma.ztext = rs.getString("ZTEXT");
        ma.kal_teil = rs.getInt("KAL_TEIL");
        ma.pers_nr = rs.getInt("PERS_NR");
        ma.astunden = rs.getDouble("ASTUNDEN");
        ma.nicht_zeig = Optional.ofNullable(rs.getString("NICHT_ZEIG"))
                                .orElse("")
                                .equals("T");
        ma.abteilung = rs.getString("ABTEILUNG");
        ma.deftakt = rs.getInt("DEFTAKT");
        ma.kalzeile = rs.getInt("KALZEILE");
        ma.id = rs.getInt("ID");

        return ma;
    }

    public static void main(String[] args) {
        List<Mitarbeiter> result = new MitarbeiterDto(new IK("987654321")).all();

    }
}
