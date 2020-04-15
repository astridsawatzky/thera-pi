package rezept;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import CommonTools.SqlInfo;
import mandant.IK;
import sql.DatenquellenFactory;
import stammDatenTools.RezTools;
import systemEinstellungen.SystemPreislisten;

public class RezeptTest {
    @Test
    public void reztools() throws Exception {
        IK ik = new IK("987654321");
        List<Rezept> rez = new RezeptDto(ik).allfromVerordn();
        SqlInfo sqlinf = new SqlInfo();
        Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
        sqlinf.setConnection(conn);
        SystemPreislisten.ladepreise("Ergo", ik.digitString());
        rez = rez.parallelStream()
                 .filter(r -> r.REZ_NR != null)
                 .collect(Collectors.toList());
        for (Rezept rezept : rez) {
            System.out.println(rezept);
            if (rezept.REZ_NR != null)

                assertEquals(rezept.REZ_NR,rezept.positionenundanzahl().toString(), RezTools.Y_holePosUndAnzahlAusRezept(rezept.REZ_NR).toString());
        }
    }

    @Test
    public void reztoolER1() throws Exception {
        IK ik = new IK("987654321");
        SqlInfo sqlinf = new SqlInfo();
        Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
        sqlinf.setConnection(conn);
        SystemPreislisten.ladepreise("Ergo", ik.digitString());
        Optional<Rezept> rez = new RezeptDto(ik).byRezeptNr("ER1");

        Rezept rezept = rez.get();
        System.out.println(rezept);
        if (rezept.REZ_NR != null)
//[[54210, 54103], [9, 1], [5, 13]]
            // [pos1,pos2],[anzahl1,anzahl2][artderbeh1,artderbeh2]
            assertEquals(rezept.positionenundanzahl().toString(), RezTools.Y_holePosUndAnzahlAusRezept(rezept.REZ_NR).toString());
    }
    @Test
    public void reztoolER1424() throws Exception {
        IK ik = new IK("987654321");
        SqlInfo sqlinf = new SqlInfo();
        Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
        sqlinf.setConnection(conn);
        SystemPreislisten.ladepreise("Ergo", ik.digitString());
        Optional<Rezept> rez = new RezeptDto(ik).byRezeptNr("ER1424");

        Rezept rezept = rez.get();
        System.out.println(rezept);
        if (rezept.REZ_NR != null)
//[[54210, 54103], [9, 1], [5, 13]]
            // [pos1,pos2],[anzahl1,anzahl2][artderbeh1,artderbeh2]
            assertEquals(rezept.positionenundanzahl().toString(), RezTools.Y_holePosUndAnzahlAusRezept(rezept.REZ_NR).toString());
    }
}
