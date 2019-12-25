package org.therapi.reha.patient.neu;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class PatientDTOTest {

    @Test
    public final void testFindbyPat_intern() throws Exception {


      Optional<PatientDTO> result = PatientDTO.findbyPat_intern("1", "987654321");
       assertTrue(result.isPresent());




    }

}
