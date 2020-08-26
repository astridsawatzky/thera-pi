package org.therapi.reha.patient.neu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import org.junit.Test;
import org.therapi.reha.patient.ArztDto;

import sql.DatenquellenFactory;

public class ArztDtoTest {

    @Test
    public void roundtripDtoToArzt() throws Exception {

        ArztDto in = new ArztDto();
        ArztDto out = new ArztDto(in.toArzt());
        assertEquals(in.toString(), out.toString());

    }

    @Test
    public final void testFindbyId() throws Exception {

        Optional<ArztDto> result = ArztDto.findbyID("1", "123456789");
        assertTrue(result.isPresent());

    }

    @Test
    public void roundtripDtoToArztVonDB() throws Exception {

        ArztDto in = ArztDto.findbyID("1", "123456789")
                            .get();
        ArztDto out = new ArztDto(in.toArzt());
        assertEquals(in.toString(), out.toString());

    }

    @Test
    public void loadeachArzt() throws Exception {
        String sql = "select  id from arzt;";
        String ik = "123456789";
        DatenquellenFactory dqf = new DatenquellenFactory(ik);
        Connection con = dqf.createConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {

            ArztDto in = ArztDto.findbyID(rs.getString("iD"), ik)
                                .get();
            ArztDto out = new ArztDto(in.toArzt());
            assertEquals(in.toString(), out.toString());

        }
    }

}
