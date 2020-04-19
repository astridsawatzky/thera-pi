package org.therapi.reha.patient.neu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import mandant.IK;
import sql.DatenquellenFactory;
@RunWith(Parameterized.class)
public class PatientMapperTest {

    @Test
    public void testRoundtripDto() throws Exception {
        PatientDTO in = new PatientDTO();
        PatientDTO out = new PatientMapper(new IK("987654321")).of(new PatientMapper(new IK("987654321")).of(in));
        assertEquals(in.toString(), out.toString());
    }

    @Test
    public void testRoundtripPatient() throws Exception {
        String zusatz = null;
        String strasse = null;
        String plz = null;
        String ort = null;
        Patient in = new Patient(new Adresse(zusatz, strasse, plz, ort));
        Patient out = new PatientMapper(new IK("987654321")).of(new PatientMapper(new IK("987654321")).of(in));
        assertEquals(in.toString(), out.toString());
    }

    @Test
    public void testRoundtripwithDBData() throws Exception {
        Optional<PatientDTO> maybeDto = PatientDTO.findbyPat_intern("1", "987654321");
        assertTrue("there is a patintern = 1 patient in db",maybeDto.isPresent());

        PatientDTO dto =  maybeDto.get();

        PatientDTO out = new PatientMapper(new IK("987654321")).of(new PatientMapper(new IK("987654321")).of(dto));
        assertEquals(dto.toString(), out.toString());

    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { "2" ,1}, { "3",1 },{ "4",1 },{ "5" ,1},{ "8",1 },
                 { "9" ,1},{ "10",1 },{ "13",1 },{ "14",1 }
           });
    }
    
 


    @Parameter // first data value (0) is default
    public /* NOT private */ String patint;

    @Parameter(1)
    public /* NOT private */ int fExpected;
    
    @Test
    public void firstTenKrankenkassen() throws Exception {
//        String sql =  "select   kassenid, PAT_INTERN from pat5  group by pat5.kassenid order by id limit 10;";
//        DatenquellenFactory dqf = new DatenquellenFactory("987654321");
//        Connection con = dqf.createConnection();
//        Statement stmt = con.createStatement();
//        ResultSet rs = stmt.executeQuery(sql);
//        while(rs.next()) {
            String patIntern = patint;
            System.out.println(patIntern);
            Optional<PatientDTO> maybeDto = PatientDTO.findbyPat_intern(patIntern, "987654321");
            assertTrue("there is a patintern = "+ patIntern +" patient in db",maybeDto.isPresent());

            PatientDTO dto =  maybeDto.get();

            PatientDTO out = new PatientMapper(new IK("987654321")).of(new PatientMapper(new IK("987654321")).of(dto));
            assertEquals(patIntern , dto.toString(), out.toString());

//        }

    }




}
