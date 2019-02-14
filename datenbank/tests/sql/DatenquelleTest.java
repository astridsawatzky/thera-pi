package sql;

import static org.junit.Assert.assertFalse;

import java.sql.SQLException;

import org.junit.Test;

import mandant.IK;

public class DatenquelleTest {
    
    @Test
    public void constructor() throws SQLException {
        
        IK ik = new IK("123456789");
        Datenquelle dq = new Datenquelle(ik );
        assertFalse(dq.connection().isClosed());
        dq.connection().close();
        assertFalse(dq.connection().isClosed());
        
    }

}
