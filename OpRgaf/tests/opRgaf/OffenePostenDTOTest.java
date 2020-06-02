package opRgaf;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import mandant.Mandant;

public class OffenePostenDTOTest {
    @Test
    public void testall() throws Exception {
        Mandant m = new mandant.Mandant("123456789", "blabla");
       List<OffenePosten> result = new OffenePostenDTO(m.ik()).all();
       assertFalse(result.isEmpty());
    }

}
