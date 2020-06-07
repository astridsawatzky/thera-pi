package opRgaf;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.CommonTools.DateTimeFormatters;

public class OffenePostenDatumFilter extends OffenePostenAbstractRowFilter {

    Strategy strategy = Strategy.gleich;
    private static final Logger logger = LoggerFactory.getLogger(OffenePostenDatumFilter.class);

    public OffenePostenDatumFilter(int offen, Strategy vergleich) {
        strategy = vergleich;
        columnIndex = offen;
    }

    @Override
    protected boolean validate(Object object) {
        LocalDate value = (LocalDate) object;
        if (getFiltertext().isEmpty()) {
            return true;
        } else if (!Pattern.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}", getFiltertext())) {
            return true;

        } else {
            try {
                LocalDate find = LocalDate.parse(getFiltertext(), DateTimeFormatters.dMYYYYmitPunkt);
                return strategy.compare(value, find);
            } catch (Exception e) {
                logger.error("mist", e);
                return false;
            }
        }
    }

}
