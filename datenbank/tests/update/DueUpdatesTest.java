package update;

import static org.junit.Assert.fail;

import org.junit.Test;

import sql.DatenquellenFactory;

public class DueUpdatesTest {

    @Test
    public void falsePreconditionNoExecution() throws Exception {
        DueUpdates du = new DueUpdates(null);

        du.add(new Update() {

           @Override
            protected boolean postCondition(DatenquellenFactory dq) {
                return false;
            }

            @Override
            protected void execute(DatenquellenFactory dq) {
                fail("precondition not met, thus should not be executed");

            }

            @Override
            protected boolean preCondition(DatenquellenFactory dq) {
                return false;
            }

        });
        du.execute();

    }

}
