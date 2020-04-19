package org.therapi.reha.patient.neu;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PatientMapperTest {

    @Test
    public void testRoundtripDto() throws Exception {
        PatientDTO in = new PatientDTO();
        PatientDTO out = PatientMapper.of(PatientMapper.of(in));
        assertEquals(in.toString(), out.toString());
    }

    @Test
    public void testRoundtripPatient() throws Exception {
        String zusatz = null;
        String strasse = null;
        String plz = null;
        String ort = null;
        Patient in = new Patient(new Adresse(zusatz, strasse, plz, ort));
        Patient out = PatientMapper.of(PatientMapper.of(in));
        assertEquals(in.toString(), out.toString());
    }



}
