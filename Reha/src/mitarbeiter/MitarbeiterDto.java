package mitarbeiter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class MitarbeiterDto {

    private final IK ik;
    private Logger logger = LoggerFactory.getLogger(MitarbeiterDto.class);
    private String SelectAllSql = "SELECT * FROM kollegen2";
    private String SelectAllActiveSql = "SELECT * FROM kollegen2 WHERE NICHT_ZEIG NOT LIKE 'T'";

    public MitarbeiterDto(IK ik) {
        this.ik = ik;
    }

    public List<Mitarbeiter> all() {

        List<Mitarbeiter> mitarbeiterListe = new LinkedList<Mitarbeiter>();
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllSql);
            while (rs.next()) {

                mitarbeiterListe.add(ofResultset(rs));

            }

            return mitarbeiterListe;
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
            return Collections.emptyList();
        }

    }

    List<Mitarbeiter> allActive() {

        List<Mitarbeiter> mitarbeiterListe = new LinkedList<Mitarbeiter>();
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllActiveSql);
            while (rs.next()) {

                mitarbeiterListe.add(ofResultset(rs));

            }

            return mitarbeiterListe;
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
            return Collections.emptyList();
        }

    }

    public Optional<Mitarbeiter> byMatchcode(String matchcode) {
        String sql = "SELECT * FROM kollegen2 WHERE matchcode LIKE '" + matchcode + "';";
        Mitarbeiter mitarbeiter = null;
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();

                ResultSet rs = con.createStatement()
                                  .executeQuery(sql);) {
            if (rs.next()) {
                mitarbeiter = ofResultset(rs);

            }
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
        }

        return Optional.ofNullable(mitarbeiter);
    }

    private Mitarbeiter ofResultset(ResultSet rs) throws SQLException {
        Mitarbeiter ma = new Mitarbeiter();
        ma.anrede = rs.getString("ANREDE");
        ma.vorname = rs.getString("VORNAME");
        ma.nachname = Optional.ofNullable(rs.getString("NACHNAME"))
                              .orElse("");
        ma.strasse = rs.getString("STRASSE");
        ma.plz = rs.getString("PLZ");
        ma.ort = rs.getString("ORT");
        ma.telefon1 = rs.getString("TELEFON1");
        ma.telfon2 = rs.getString("TELFON2");

        ma.geboren = rs.getDate("GEBOREN") == null ? null
                : rs.getDate("GEBOREN")
                    .toLocalDate();

        ma.matchcode = Optional.ofNullable(rs.getString("matchcode"))
                               .orElse("");
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
        ma.isdirty = false;
        return ma;
    }

    public boolean delete(Mitarbeiter ma) {

        String sql = "DELETE FROM kollegen2 where id =" + ma.id + ";";

        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();
                Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("coud not delete Mitarbeiter " + ma, e);
            return false;
        }
        return true;
    }

    public boolean save(Mitarbeiter ma) {
        String sql;
        if (ma.isNew()) {
            sql = generateInsertSQL(ma);

        } else {
            sql = generateUpdateSQL(ma);
        }

        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();
                Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                ma.id = rs.getInt(1);
            }
            if (rs.getWarnings() != null) {
                logger.debug(String.valueOf(rs.getWarnings()));
            }
        } catch (SQLException e) {
            logger.error("coud not save Mitarbeiter " + ma, e);
            return false;
        }
        return true;

    }

    void save(List<Mitarbeiter> mitarbeiterListe) {

        Map<Boolean, List<Mitarbeiter>> mitarbeiterparts = mitarbeiterListe.stream()
                                                                           .filter(m -> m.isdirty)
                                                                           .collect(Collectors.partitioningBy(
                                                                                   Mitarbeiter::isNew));

        List<String> mitarbeiterUpdateSql = mitarbeiterparts.get(false)
                                                            .stream()
                                                            .map(m -> generateUpdateSQL(m))
                                                            .collect(Collectors.toList());
        logger.debug("updating " + mitarbeiterUpdateSql.size() + " Mitarbeiter in Database");

        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();
                Statement stmt = con.createStatement()) {

            for (String string : mitarbeiterUpdateSql) {
                stmt.addBatch(string);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Fehler beim Speichern der Mitarbeiter", e);
        }

        List<Mitarbeiter> list = mitarbeiterparts.get(true);
        logger.debug("inserting " + list.size() + " Mitarbeiter into Database");

        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();
                Statement statement = con.createStatement();) {

            for (Mitarbeiter mitarbeiter : list) {

                statement.executeUpdate(generateInsertSQL(mitarbeiter), Statement.RETURN_GENERATED_KEYS);

                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                mitarbeiter.id = id;
            }
        } catch (SQLException e) {
            logger.error("fehler beim einfügen neuer mitarbeiter", e);
        }

    }

    private String generateInsertSQL(Mitarbeiter ma) {
        String sqlStart = "INSERT INTO Kollegen2 ( ANREDE, VORNAME , NACHNAME, STRASSE , PLZ , ORT , TELEFON1 , TELFON2, GEBOREN , matchcode , ZTEXT , KAL_TEIL , PERS_NR , ASTUNDEN, NICHT_ZEIG , ABTEILUNG , DEFTAKT , KALZEILE ) VALUES (";
        String sql = new StringBuilder().append(sqlStart)
                                        .append(einklammern(ma.anrede))
                                        .append(",")
                                        .append(einklammern(ma.vorname))
                                        .append(",")
                                        .append(einklammern(ma.nachname))
                                        .append(",")
                                        .append(einklammern(ma.strasse))
                                        .append(",")
                                        .append(einklammern(ma.plz))
                                        .append(",")
                                        .append(einklammern(ma.ort))
                                        .append(",")
                                        .append(einklammern(ma.telefon1))
                                        .append(",")
                                        .append(einklammern(ma.telfon2))
                                        .append(",")
                                        .append(einklammern(ma.geboren==null?null:Date.valueOf(ma.geboren)))
                                        .append(",")
                                        .append(einklammern(ma.matchcode))
                                        .append(",")
                                        .append(einklammern(ma.ztext))
                                        .append(",")
                                        .append(einklammern(ma.kal_teil))
                                        .append(",")
                                        .append(einklammern(ma.pers_nr))
                                        .append(",")
                                        .append(ma.astunden)
                                        .append(",")
                                        .append((ma.nicht_zeig ? "'T'" : "'F'"))
                                        .append(",")
                                        .append(einklammern(ma.abteilung))
                                        .append(",")
                                        .append(ma.deftakt)
                                        .append(",")
                                        .append(ma.kalzeile)
                                        .append(")")
                                        .toString();

        return sql;
    }

    private String generateUpdateSQL(Mitarbeiter ma) {
        String sql = new StringBuilder().append("UPDATE Kollegen2")
                                        .append(" SET ANREDE = ")
                                        .append(einklammern(ma.anrede))
                                        .append(", VORNAME = ")
                                        .append(einklammern(ma.vorname))
                                        .append(", NACHNAME = ")
                                        .append(einklammern(ma.nachname))
                                        .append(", STRASSE = ")
                                        .append(einklammern(ma.strasse))
                                        .append(", PLZ = ")
                                        .append(einklammern(ma.plz))
                                        .append(", ORT = ")
                                        .append(einklammern(ma.ort))
                                        .append(", TELEFON1 = ")
                                        .append(einklammern(ma.telefon1))
                                        .append(", TELFON2 = ")
                                        .append(einklammern(ma.telfon2))
                                        .append(", GEBOREN = ")
                                        .append(einklammern(ma.geboren==null?null:Date.valueOf(ma.geboren)))
                                        .append(", matchcode = ")
                                        .append(einklammern(ma.matchcode))
                                        .append(", ZTEXT = ")
                                        .append(einklammern(ma.ztext))
                                        .append(", KAL_TEIL = ")
                                        .append(einklammern(ma.kal_teil))
                                        .append(", PERS_NR = ")
                                        .append(einklammern(ma.pers_nr))
                                        .append(", ASTUNDEN = ")
                                        .append(ma.astunden)
                                        .append(", NICHT_ZEIG = ")
                                        .append((ma.nicht_zeig ? "'T'" : "'F'"))
                                        .append(", ABTEILUNG = ")
                                        .append(einklammern(ma.abteilung))
                                        .append(", DEFTAKT = ")
                                        .append(ma.deftakt)
                                        .append(", KALZEILE = ")
                                        .append(ma.kalzeile)
                                        .append(" WHERE ID = ")
                                        .append(ma.id)
                                        .toString();
        return sql;
    }

    private String einklammern(Date value) {
        return value == null ? null : "'" + String.valueOf(value) + "'";
    }

    private Integer einklammern(int invalue) {

        return invalue == 0 ? null : Integer.valueOf(invalue);
    }

    private String einklammern(String value) {
        return value == null ? null : "'" + value + "'";
    }

    public int findgap() {
        String sql = "SELECT  ko.KALZEILE + 1 as gap , mi.KALZEILE  FROM    kollegen2 ko LEFT JOIN  kollegen2 mi on ko.KALZEILE+1=mi.KALZEILE WHERE mi.KALZEILE IS  NULL  order by gap;";
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(sql);
            while (rs.next()) {

                return rs.getInt("gap");

            }

        } catch (SQLException e) {
            logger.error("could not retrieve kalenzeilengap from Database", e);

        }
        return 0;
    }

}
