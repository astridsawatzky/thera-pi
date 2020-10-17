package org.therapi.reha.patient.therapieberichtpanel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class TherapieBerichtDto {

    private Logger logger = LoggerFactory.getLogger(TherapieBerichtDto.class);

    private static final String dbTabellenName = "bericht1";

    private DatenquellenFactory datenquellenFactory;

    public TherapieBerichtDto(IK ik) {
        datenquellenFactory = new DatenquellenFactory(ik.digitString());
    }


    public List<TherapieBericht> byPatIntern(int id) {
        List<TherapieBericht> resultlist = new ArrayList<>();
        try (Connection con = datenquellenFactory.createConnection();
                Statement statmt = con.createStatement();
                ResultSet rs = statmt.executeQuery("select * from " + dbTabellenName + " where  PAT_INTERN = " + id)) {

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

    private TherapieBericht ofResultset(ResultSet rs) {
        TherapieBericht ret = new TherapieBericht();

        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();

            for (int o = 1; o <= meta.getColumnCount(); o++) {
                String field = meta.getColumnLabel(o)
                                   .toUpperCase();
                switch (field) {
                case "ID":
                    ret.setId(rs.getInt(field));
                    break;
                case "PAT_INTERN":
                    ret.setPatIntern(rs.getString(field));
                    break;
                case "BERICHTID":
                    ret.setBerichtId(rs.getInt(field));
                    break;
                case "ARZT_NUM":
                    ret.setArztNum(rs.getString(field));
                    break;
                case "ERSTELLDAT":
                    ret.setErstellDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "VERSANDART":
                    ret.setVersandArt(rs.getString(field));
                    break;
                case "VERSANDDAT":
                    ret.setVersandDat(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                case "BERTYP":
                    ret.setBerTyp(rs.getString(field));
                    break;
                case "BERSTAND":
                    ret.setBerStand(rs.getString(field));
                    break;
                case "BERBESO":
                    ret.setBerBeso(rs.getString(field));
                    break;
                case "BERPROG":
                    ret.setBerProg(rs.getString(field));
                    break;
                case "BERVORS":
                    ret.setBerVors(rs.getString(field));
                    break;
                case "DIAGNOSE":
                    ret.setDiagnose(rs.getString(field));
                    break;
                case "KRBILD":
                    ret.setKrBild(rs.getString(field));
                    break;
                case "VERFASSER":
                    ret.setVerfasser(rs.getString(field));
                    break;
                case "REZ_DATUM":
                    ret.setRezDatum(rs.getDate(field) == null ? null
                            : rs.getDate(field)
                                .toLocalDate());
                    break;
                default:
                    logger.error("Unhandled field in bericht1 found: " + meta.getColumnLabel(o) + " at pos: " + o);
                }
                ;
            }
        } catch (SQLException e) {
            logger.error("Couldn't retrieve dataset in Bericht1");
            logger.error("Error: " + e.getLocalizedMessage());
        }

        return ret;
    }

    public void saveToDB(TherapieBericht bericht) {
        String sql = "insert into " + dbTabellenName + " set " + "PAT_INTERN='" + bericht.getPatIntern() + "',"
                + "BERICHTID='" + bericht.getBerichtId() + "'," + "ARZT_NUM='" + bericht.getArztNum() + "',"
                + "ERSTELLDAT='" + bericht.getErstellDat() + "'," + "VERSANDART='" + bericht.getVersandArt() + "',"
                + "VERSANDDAT='" + bericht.getVersandDat() + "'," + "BERTYP='" + bericht.getBerTyp() + "',"
                + "BERSTAND='" + bericht.getBerStand() + "'," + "BERBESO='" + bericht.getBerBeso() + "'," + "BERPROG='"
                + bericht.getBerProg() + "'," + "BERVORS='" + bericht.getBerVors() + "'," + "DIAGNOSE='"
                + bericht.getDiagnose() + "'," + "KRBILD='" + bericht.getKrBild() + "'," + "VERFASSER='"
                + bericht.getVerfasser() + "'," + "REZ_DATUM='" + bericht.getRezDatum() + "'";
        try {

            Connection conn = datenquellenFactory.createConnection();
            conn.createStatement()
                .execute(sql);
        } catch (SQLException e) {
            logger.error("Could not save dataset " + bericht.toString() + " to Database, table bericht1", e);
        }
    }

}
