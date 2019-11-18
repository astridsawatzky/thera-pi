package suchen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hauptFenster.Reha;
import mandant.IK;
import sql.DatenquellenFactory;

public class PatMitVollenVOs extends PatWithMatchingVo {
    private Logger logger = LoggerFactory.getLogger(PatMitVollenVOs.class);
    private String sstmt = "SELECT p.n_name, p.v_name, DATE_FORMAT(p.geboren,'%d.%m.%Y') AS geboren, "  // (® by Norbart/Astrid)
            + "v.pat_intern, v.rez_nr, r.termine, v.behandler "
            + "FROM (volle AS v left OUTER JOIN fertige AS f ON v.rez_nr = f.rez_nr "
            + "LEFT JOIN pat5 AS p ON v.pat_intern = p.pat_intern "
            + "LEFT JOIN verordn AS r ON v.rez_nr = r.rez_nr)  "
            + "WHERE f.rez_nr IS NULL ORDER BY v.behandler, v.rez_nr";

    public PatMitVollenVOs(IK ik) {
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

}
