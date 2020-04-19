package org.therapi.reha.patient.neu;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;


public class ArztDtoTest {

    @Test
    public void roundtripDtoToArzt() throws Exception {

        ArztDto in = new ArztDto();
        ArztDto out = new ArztDto(in.toArzt());
        assertEquals(in.toString(), out.toString());

    }
    @Test
    public final void testFindbyId() throws Exception {

      Optional<ArztDto> result = ArztDto.findbyID("1", "987654321");
       assertTrue(result.isPresent());
    }

}
