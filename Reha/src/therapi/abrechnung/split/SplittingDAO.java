package therapi.abrechnung.split;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Disziplin;
import mandant.IK;
import rezept.Rezeptnummer;
import sql.DatenquellenFactory;

public class SplittingDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplittingDAO.class);
    private final IK ik;

    public SplittingDAO(IK ik) {
        this.ik = ik;

    }

    public void load() {
        try (
                Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
                Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet result = stmt.executeQuery("select disziplin, rezeptnummer from hmv_splitter;"))

        {
            Splitting rezepte = new Splitting();
            while (result.next()) {

                try {
                    rezepte.add(new Rezeptnummer(Disziplin.valueOf(result.getString("disziplin")),
                            result.getInt("rezeptnummer")));
                } catch (Exception e) {
                    LOGGER.error(result.getInt("id") + "could not be added",e);
                }

            }

        } catch (Exception e) {
            LOGGER.debug(" could not be added",e);
        }

    }

}
