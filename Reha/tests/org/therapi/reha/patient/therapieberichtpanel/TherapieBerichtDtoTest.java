package org.therapi.reha.patient.therapieberichtpanel;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import mandant.IK;

public class TherapieBerichtDtoTest {

    @Test
    public void testName() throws Exception {
        TherapieBerichtDto tbdto = new TherapieBerichtDto(new IK("123456789"));


        List<TherapieBericht> result = tbdto.byPatIntern(350);
        assertFalse(result.isEmpty());

    }
    
    

}
