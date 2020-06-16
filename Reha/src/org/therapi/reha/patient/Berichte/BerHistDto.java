package org.therapi.reha.patient.Berichte;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class BerHistDto {

    private Logger logger = LoggerFactory.getLogger(BerHistDto.class);

    private static final String dbName = "berhist";

    private static final String SelectAllSql = "Select * from berhist";
    private static final String SelectAllTheraSql = "select * from berhist where UPPER(bertitel) not LIKE '%REHA%' AND  UPPER(bertitel) not LIKE '%ARZT%';";

    private IK ik;

    public BerHistDto(IK ik) {
        this.ik = ik;
    }

    private BerHist ofResultset(ResultSet rs) {
        BerHist ret = new BerHist();

        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();
        } catch (SQLException e) {
            logger.error("Could not retrieve metaData", e);
            return null;
        }
        try {
            for (int o = 1; o <= meta.getColumnCount(); o++) {
                String field = meta.getColumnLabel(o)
                                   .toUpperCase();
                // logger.debug("Checking: " + field + " in " + o);
                switch (field) {

                case "PAT_INTERN":
                    ret.setPatIntern(rs.getString(field));
                    break;
                case "BERICHTID":
                    ret.setBerichtId(rs.getInt(field));
                    break;
                case "BERICHTTYP":
                    ret.setBerichtTyp(rs.getString(field));
                    break;
                case "VERFASSER":
                    ret.setVerfasser(rs.getString(field));
                    break;
                case "EMPFAENGER":
                    ret.setEmpfaenger(rs.getString(field));
                    break;
                case "BERTITEL":
                    ret.setBerTitel(rs.getString(field));
                    break;
                case "ERSTELLDAT":
                    ret.setErstellDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "EDITDAT":
                    ret.setEditDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "VERSANDDAT":
                    ret.setVersandDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "DATEINAME":
                    ret.setDateiname(rs.getString(field));
                    break;
                case "EMPFID":
                    ret.setEmpfId(rs.getInt(field));
                    break;
                case "ID":
                    ret.setId(rs.getInt(field));
                    break;
                default:
                    logger.error("Unhandled field in berhist found: " + meta.getColumnLabel(o) + " at pos: " + o);
                }
                ;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Couldn't retrieve dataset in Berhist");
            logger.error("Error: " + e.getLocalizedMessage());
        }

        return ret;
    }

    public void saveToDB(BerHist dataset) {
        String sql = "insert into " + dbName + " set " + "PAT_INTERN='" + dataset.getPatIntern() + "'," + "BERICHTID='"
                + dataset.getBerichtId() + "'," + "BERICHTTYP='" + dataset.getBerichtTyp() + "'," + "VERFASSER='"
                + dataset.getVerfasser() + "'," + "EMPFAENGER='" + dataset.getEmpfaenger() + "'," + "BERTITEL='"
                + dataset.getBerTitel() + "'," + "ERSTELLDAT='" + dataset.getErstellDat() + "'," + "EDITDAT='"
                + dataset.getEditDat() + "'," + "VERSANDDAT='" + dataset.getVersandDat() + "'," + "DATEINAME='"
                + dataset.getDateiname() + "'," + "EMPFID='" + dataset.getEmpfId() + "'," + "ID='" + dataset.getId()
                + "'";
        try {
            Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
            conn.createStatement()
                             .executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Could not save dataset " + dataset.toString() + " to Database, table berhist", e);
        }
    }

    List<BerHist> all() {

        List<BerHist> berichtHistorie = new LinkedList<>();
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllSql);
            while (rs.next()) {

                berichtHistorie.add(ofResultset(rs));

            }

            return berichtHistorie;
        } catch (SQLException e) {
            logger.error("could not retrieve OffenePosten from Database", e);
            return Collections.emptyList();
        }

    }

    List<BerHist> allTherapieBerichte() {
        List<BerHist> berichtHistorie = new LinkedList<>();
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllTheraSql);
            while (rs.next()) {

                berichtHistorie.add(ofResultset(rs));

            }

            return berichtHistorie;
        } catch (SQLException e) {
            logger.error("could not retrieve OffenePosten from Database", e);
            return Collections.emptyList();
        }
    }

}
