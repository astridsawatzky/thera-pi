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
    public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
        if (getFiltertext().isEmpty()) {
            return true;
        } else {
            try {
                Money find = new Money(getFiltertext().replace(",", "."));
                Money value = (Money) entry.getValue(columnIndex);
                System.out.println(find + "==" + value);
                return strategy. compare(value, find);
            } catch (Exception e) {
                return false;
            }
        }
    }

}
