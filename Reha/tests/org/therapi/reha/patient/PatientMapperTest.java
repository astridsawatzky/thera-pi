package org.therapi.reha.patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.therapi.reha.patient.PatientDTO;
import org.therapi.reha.patient.PatientMapper;

import core.Adresse;
import core.Patient;
import mandant.IK;
import sql.DatenquellenFactory;

public class PatientMapperTest {

    @Test
    public void testRoundtripDto() throws Exception {
        PatientDTO in = new PatientDTO();
        PatientDTO out = new PatientMapper(new IK("123456789")).of(new PatientMapper(new IK("123456789")).of(in));
        assertEquals(in.toString(), out.toString());
    }

    @Test
    public void testRoundtripPatient() throws Exception {
        String zusatz = null;
        String strasse = null;
        String plz = null;
        String ort = null;
        Patient in = new Patient(new Adresse(zusatz, strasse, plz, ort));
        Patient out = new PatientMapper(new IK("123456789")).of(new PatientMapper(new IK("123456789")).of(in));
        assertEquals(in.toString(), out.toString());
    }

    @Ignore("Groß und Kleinschreibung in Patientenstamm")
    @Test
    public void testRoundtripwithDBData() throws Exception {
        Optional<PatientDTO> maybeDto = PatientDTO.findbyPat_intern("1", "123456789");
        assertTrue("there is a patintern = 1 patient in db", maybeDto.isPresent());

        PatientDTO dto = maybeDto.get();

        PatientDTO out = new PatientMapper(new IK("123456789")).of(new PatientMapper(new IK("123456789")).of(dto));
        assertEquals(dto.toString(), out.toString());

    }

    private String aktIK = "123456789";

    @Ignore("Groß und Kleinschreibung in Patientenstamm")
    @Test
    public void firstTenKrankenkassen() throws Exception {
        String sql = "select   kassenid, PAT_INTERN from pat5  group by pat5.kassenid order by id limit 10;";
        DatenquellenFactory dqf = new DatenquellenFactory("123456789");
        Connection con = dqf.createConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String patIntern = rs.getString("PAT_INTERN");
            System.out.println(patIntern);

            Optional<PatientDTO> maybeDto = PatientDTO.findbyPat_intern(patIntern, aktIK);
            assertTrue("there is a patintern = " + patIntern + " patient in db", maybeDto.isPresent());

            PatientDTO dto = maybeDto.get();

            PatientDTO out = new PatientMapper(new IK(aktIK)).of(new PatientMapper(new IK(aktIK)).of(dto));
            assertEquals(patIntern, dto.toString(), out.toString());

        }

    }

}
