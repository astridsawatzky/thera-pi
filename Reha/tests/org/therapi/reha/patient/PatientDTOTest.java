package org.therapi.reha.patient;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;
import org.therapi.reha.patient.PatientDTO;

public class PatientDTOTest {

    @Test
    public final void testFindbyPat_intern() throws Exception {

      Optional<PatientDTO> result = PatientDTO.findbyPat_intern("1", "123456789");
       assertTrue(result.isPresent());
    }

    @Test
    public void allDtosAreCreatedEqual() throws Exception {
        assertEquals(new PatientDTO(), new PatientDTO());
    }

}
