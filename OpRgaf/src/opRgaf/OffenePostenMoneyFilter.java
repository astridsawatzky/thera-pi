package opRgaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.rezept.Money;

public class OffenePostenMoneyFilter extends OffenePostenAbstractRowFilter {

    Strategy strategy = Strategy.gleich;
    private static final Logger logger = LoggerFactory.getLogger(OffenePostenMoneyFilter.class);
    public OffenePostenMoneyFilter(int offen,Strategy vergleich) {
        strategy = vergleich;
        columnIndex = offen;
    }


    @Override
    protected boolean validate(Object object) {
        if (getFiltertext().isEmpty()) {
            return true;
        } else {
            try {
                Money find = new Money(getFiltertext().replace(",", "."));
                Money value = (Money) object;
                System.out.println(find + "==" + value);
                return strategy. compare(value, find);
            } catch (Exception e) {
                return false;
            }
        }
    }

}
