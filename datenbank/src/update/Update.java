package update;

import sql.DatenquellenFactory;

public abstract class Update {

    protected final boolean consider(DatenquellenFactory dq) {
        if (preCondition(dq)) {
            execute(dq);
            return postCondition(dq);
        }
        return true;
    }

    protected abstract boolean postCondition(DatenquellenFactory dq);

    protected abstract void execute(DatenquellenFactory dq);

    protected abstract boolean preCondition(DatenquellenFactory dq);;


}
