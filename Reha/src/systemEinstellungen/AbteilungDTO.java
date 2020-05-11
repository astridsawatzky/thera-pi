package systemEinstellungen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class AbteilungDTO {


    private IK ik;
    private Logger logger = LoggerFactory.getLogger(AbteilungDTO.class);

    AbteilungDTO(IK ik) {
        this.ik = ik;

    }

    List<Abteilung> all() {
        Set<Abteilung> set = new HashSet<>();
        set.add(new Abteilung(" "));
        set.add(new Abteilung("KG"));
        set.add(new Abteilung("MA"));
        set.add(new Abteilung("ER"));
        set.add(new Abteilung("LO"));
        set.add(new Abteilung("SP"));
        try {
            set.addAll(SystemConfig.oGruppen.gruppenNamen.stream()
                                                           .map(Abteilung::new)
                                                           .collect(Collectors.toList()));
        } catch (Exception e) {
            // gruppe has not been loaded yet, happens only in tests
        }

        set.addAll(fromDB());
        LinkedList<Abteilung> liste = new LinkedList<Abteilung>();
        liste.addAll(set);
        liste.sort(new Comparator<Abteilung>() {

            @Override
            public int compare(Abteilung o1, Abteilung o2) {
                return o1.bezeichnung.compareTo(o2.bezeichnung);
            }

        });
        return liste;

    }

    private Collection<? extends Abteilung> fromDB() {
        List<Abteilung> abteilungsListe = new LinkedList<>();
        String SelectAllSql = "SELECT DISTINCT ABTEILUNG FROM kollegen2;";

        try (Connection con = new DatenquellenFactory(ik.digitString()).createConnection();) {
            ResultSet rs = con.createStatement()
                              .executeQuery(SelectAllSql);
            while (rs.next()) {

                abteilungsListe.add(new Abteilung(rs.getString("ABTEILUNG")));

            }

            return abteilungsListe;
        } catch (SQLException e) {
            logger.error("could not retrieve Mitarbeiter from Database", e);
            return Collections.emptyList();
        }
    }

}
