package org.therapi.reha.patient.neu;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class PatientDTOTest {

    @Test
    public final void testFindbyPat_intern() throws Exception {

      Optional<PatientDTO> result = PatientDTO.findbyPat_intern("1", "987654321");
       assertTrue(result.isPresent());
    }

    @Test
    public void allDtosAreCreatedEqual() throws Exception {
        assertEquals(new PatientDTO(), new PatientDTO());
    }

}
