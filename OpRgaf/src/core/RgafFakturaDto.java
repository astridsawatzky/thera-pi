package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import mandant.Mandant;
import opRgaf.rezept.Money;
import sql.DatenquellenFactory;

public class RgafFakturaDto {
    private Logger logger = LoggerFactory.getLogger(RgafFakturaDto.class);

    private static final String dbName = "RgafFaktura";

    private static final String SelectAllSql = "Select * from rgaffaktura";
    private Mandant mandant;
    private IK ik;

    public RgafFakturaDto(Mandant mand) {
        mandant = mand;
        ik = mandant.ik();
    }

    private RgafFaktura ofResultset(ResultSet rs) {
        RgafFaktura ret = new RgafFaktura();

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
                switch (field) {

                case "RNR":
                    ret.setrNr(rs.getString(field));
                    break;
                case "REZNR":
                    ret.setRezNr(rs.getString(field));
                    break;
                case "PAT_INTERN":
                    ret.setPatIntern(rs.getString(field));
                    break;
                case "RGESAMT":
                    ret.setrGesamt(new Money(rs.getString(field)));
                    break;
                case "ROFFEN":
                    ret.setrOffen(new Money(rs.getString(field)));
                    break;
                case "RGBETRAG":
                    ret.setrGBetrag(new Money(rs.getString(field)));
                    break;
                case "RPBETRAG":
                    ret.setrPBetrag(new Money(rs.getString(field)));
                    break;
                case "RDATUM":
                    ret.setrDatum(Optional.ofNullable(rs.getString(field)).map(LocalDate::parse).orElse(null));
                    break;
                case "RBEZDATUM":
                    ret.setrBezDatum(Optional.ofNullable(rs.getString(field)).map(LocalDate::parse).orElse(null));
                    break;
                case "RMAHNDAT1":
                    ret.setrMahndat1(Optional.ofNullable(rs.getString(field)).map(LocalDate::parse).orElse(null));
                    break;
                case "RMAHNDAT2":
                    ret.setrMahndat1(Optional.ofNullable(rs.getString(field)).map(LocalDate::parse).orElse(null));
                    break;
                case "ID":
                    ret.setId(rs.getInt(field));
                    break;
                case "IK":
                    ret.setIk(new IK(rs.getString(field)));
                    break;
                default:
                    logger.error("Unhandled field in rgaffaktura found: " + meta.getColumnLabel(o) + " at pos: " + o);
                }
                ;
            }
        } catch (SQLException e) {
            logger.error("Couldn't retrieve dataset in rgaffaktura",e);
        }

        return ret;
    }

    public boolean saveToDB(RgafFaktura rgaff) {
        String sql = "insert into " + dbName + " set " + "RNR='" + rgaff.getrNr() + "'," + "REZNR='" + rgaff.getRezNr()
                + "'," + "PAT_INTERN='" + rgaff.getPatIntern() + "'," + "RGESAMT='" + rgaff.getrGesamt() + "',"
                + "ROFFEN='" + rgaff.getrOffen() + "'," + "RGBETRAG='" + rgaff.getrGBetrag() + "'," + "RPBETRAG='"
                + rgaff.getrPBetrag() + "'," + "RDATUM='" + rgaff.getrDatum() + "'," + "RBEZDATUM='"
                + rgaff.getrBezDatum() + "'," + "RMAHNDAT1='" + rgaff.getrMahndat1() + "'," + "RMAHNDAT2='"
                + rgaff.getrMahndat2() + "'," + "ID='" + rgaff.getId() + "'," + "IK='" + rgaff.getIk() + "'";
        try {
            Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
            return conn.createStatement()
                             .execute(sql);
        } catch (SQLException e) {
            logger.error("Could not save to RgafFaktura " + rgaff.toString() + " to Database", e);
        }
        return false;
    }
    public List<RgafFaktura> all() {

        List<RgafFaktura> rgafFakturaListe = new LinkedList<RgafFaktura>();
        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllSql);
            while (rs.next()) {

                rgafFakturaListe.add(ofResultset(rs));

            }

            return rgafFakturaListe;
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
            return Collections.emptyList();
        }

    }

}
