package abrechnung;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXFrame;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CommonTools.SqlInfo;
import sql.DatenquellenFactory;

public class RezeptGebuehrRechnungTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RezeptGebuehrRechnungTest.class);

    @Ignore
    @Test
    public void manuell() throws Exception {

        Connection conn = new DatenquellenFactory("123456789").createConnection();
        new SqlInfo().setConnection(conn);

        Map<String, String> hmRezgeb = new HashMap<>();

        hmRezgeb.put("<rgreznum>", "ER212");
        hmRezgeb.put("<rgdatum>", "2020-10-01");
        hmRezgeb.put("<rgbetrag>", "50");
        hmRezgeb.put("<rgpauschale>", "5");
        hmRezgeb.put("<rgbehandlung>", "mimimi");

        JXFrame owner = new JXFrame();
        RezeptGebuehrRechnung rgr = new RezeptGebuehrRechnung(owner, "titel", 0, hmRezgeb, false, new JPanel()) {
            @Override
            protected void showUserMessage(String message) {
                LOGGER.debug(message);
            }
        };
        rgr.start();

        rgr.setVisible(true);
    }

}
