package org.therapi.reha.patient.Berichte;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class BerHistDto {

    private Logger logger = LoggerFactory.getLogger(BerHistDto.class);

    private static final String dbTabellenName = "berhist";

    private static final String SelectAllSql = "Select * from berhist";
    private static final String SelectAllTheraSql = "select * from berhist where UPPER(bertitel) not LIKE '%REHA%' AND  UPPER(bertitel) not LIKE '%ARZT%' ";

    private IK ik;

    private DatenquellenFactory datenquellenFactory;

    public BerHistDto(IK ik) {
            datenquellenFactory = new DatenquellenFactory(ik.digitString());
    }

    private BerHist ofResultset(ResultSet rs) {
        BerHist berichthistorie = new BerHist();

        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();

            for (int o = 1; o <= meta.getColumnCount(); o++) {
                String field = meta.getColumnLabel(o)
                                   .toUpperCase();
                switch (field) {

                case "PAT_INTERN":
                    berichthistorie.setPatIntern(rs.getString(field));
                    break;
                case "BERICHTID":
                    berichthistorie.setBerichtId(rs.getInt(field));
                    break;
                case "BERICHTTYP":
                    berichthistorie.setBerichtTyp(rs.getString(field));
                    break;
                case "VERFASSER":
                    berichthistorie.setVerfasser(rs.getString(field));
                    break;
                case "EMPFAENGER":
                    berichthistorie.setEmpfaenger(rs.getString(field));
                    break;
                case "BERTITEL":
                    berichthistorie.setBerTitel(rs.getString(field));
                    break;
                case "ERSTELLDAT":
                    berichthistorie.setErstellDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "EDITDAT":
                    berichthistorie.setEditDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "VERSANDDAT":
                    berichthistorie.setVersandDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "DATEINAME":
                    berichthistorie.setDateiname(rs.getString(field));
                    break;
                case "EMPFID":
                    berichthistorie.setEmpfId(rs.getInt(field));
                    break;
                case "ID":
                    berichthistorie.setId(rs.getInt(field));
                    break;
                default:
                    logger.error("Unhandled field in berhist found: " + meta.getColumnLabel(o) + " at pos: " + o);
                }
                ;
            }
        } catch (SQLException e) {
            logger.error("Couldn't retrieve dataset in Berhist",e);
            return null;
        }

        return berichthistorie;
    }

    public void saveToDB(BerHist dataset) {
        String sql = "insert into " + dbTabellenName + " set " + "PAT_INTERN='" + dataset.getPatIntern() + "'," + "BERICHTID='"
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
    public List<BerHist> byPatIntern(int id) {
        List<BerHist> resultlist = new ArrayList<>();
        try (Connection con = datenquellenFactory.createConnection();
                Statement statmt = con.createStatement();
                ResultSet rs = statmt.executeQuery(SelectAllSql + " AND  PAT_INTERN = " + id)) {

            while (rs.next()) {

                try {
                    resultlist.add(ofResultset(rs));
                } catch (Exception e) {
                    logger.error("fehler beim laden von therapieberichten fuer PatIntern = " + id, e);
                }
            }
        } catch (SQLException e) {
            logger.error("fehler beim laden von therapieberichten fuer PatIntern = " + id, e);
        }

        return resultlist;
}

    public List<BerHist> therapieBerichtByPatIntern(int id) {
            List<BerHist> resultlist = new ArrayList<>();
            try (Connection con = datenquellenFactory.createConnection();
                    Statement statmt = con.createStatement();
                    ResultSet rs = statmt.executeQuery(SelectAllTheraSql + " AND  PAT_INTERN = " + id)) {

                while (rs.next()) {

                    try {
                        resultlist.add(ofResultset(rs));
                    } catch (Exception e) {
                        logger.error("fehler beim laden von therapieberichten fuer PatIntern = " + id, e);
                    }
                }
            } catch (SQLException e) {
                logger.error("fehler beim laden von therapieberichten fuer PatIntern = " + id, e);
            }

            return resultlist;
    }

}
