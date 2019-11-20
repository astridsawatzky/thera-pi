package suchen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.DatFunk;
import mandant.IK;
import sql.DatenquellenFactory;

public class PatMitAbgebrochenenVOs extends PatWithMatchingVo {
    private Logger logger = LoggerFactory.getLogger(PatMitVollenVOs.class);
    private String abgebrDatum = DatFunk.sDatInSQL(DatFunk.sDatPlusTage(DatFunk.sHeute(), -21));
    private String sstmt = "(SELECT p.n_name, p.v_name, DATE_FORMAT(p.geboren,'%d.%m.%Y') as geboren, v.pat_intern, v.rez_nr, "
            +       "v.termine, '' as LetzterTherapeut "
            + "FROM ("
            + "     SELECT v1.pat_intern, v1.rez_nr, v1.termine, v1.abschluss"
            + "     FROM verordn AS v1"
            + "     WHERE STR_TO_DATE(SUBSTRING(v1.termine FROM (character_length(v1.termine)-10)),'%Y-%m-%d') <= '" + abgebrDatum + "'" // Patienten mit abgebrochenen Rezepten (® by MSc)
            + "     ) AS v LEFT JOIN pat5 AS p ON (v.pat_intern = p.pat_intern) "
            + "WHERE !(v.termine='') AND !(v.termine is null) AND (v.abschluss='F')"
            + "ORDER BY SUBSTRING(v.termine FROM (character_length(v.termine)-10))"
            + ") UNION ("
            + "SELECT p.n_name, p.v_name, DATE_FORMAT(p.geboren,'%d.%m.%Y') AS geboren, v.pat_intern, rez_nr, "   // + nie angefangene Rezepte
            + "     '' AS termine, '' AS LetzterTherapeut "
            + "FROM ("
            + "     SELECT v1.pat_intern, v1.rez_nr, v1.termine, v1.abschluss "
            + "     FROM verordn AS v1"
            + "     WHERE v1.rez_datum  <= '" + abgebrDatum + "'"
            + "     ) AS v LEFT JOIN pat5 AS p ON (v.pat_intern = p.pat_intern) "
            + "WHERE (v.termine='') OR (v.termine is null)"
            + ") ORDER BY termine DESC, rez_nr";

       public PatMitAbgebrochenenVOs(IK ik) {
        super(ik);

        patientenListe = new LinkedList<PatWithMatchingVo>();
        try (Connection con = new DatenquellenFactory().with(ik)
                                                       .createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(sstmt);
            int spalten = rs.getMetaData()
                    .getColumnCount();
            int reihen = 0;
            while (rs.next()) {
                reihen++;
                patientenListe.add(ofResultset(rs));
            }

        } catch (SQLException e) {
            logger.error("could not retrieve clients with completed but unlocked VOs from Database", e);
        }
    }
    
    public static void main(String[] args) {
//      PatMitVollenVOs result = new PatMitAbgebrochenenVOs(new IK(Reha.getAktIK()));
      PatWithMatchingVo result = new PatMitAbgebrochenenVOs(new IK("441469326"));
  }

}
