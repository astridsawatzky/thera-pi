package opRgaf;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.CommonTools.DateTimeFormatters;

public class OffenePostenDatumFilter extends OffenePostenAbstractRowFilter {


        Strategy strategy = Strategy.gleich;
        private static final Logger logger = LoggerFactory.getLogger(OffenePostenMoneyFilter.class);
        public OffenePostenDatumFilter(int offen,Strategy vergleich) {
            strategy = vergleich;
            columnIndex = offen;
        }

        @Override
        public boolean include(Entry<? extends OffenePostenTableModel, ? extends Integer> entry) {
            if (getFiltertext().isEmpty()) {
                return true;
            } else if(!Pattern.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}",getFiltertext())) {
                return true;

            }else{
                try {
                    LocalDate find =  LocalDate.parse(getFiltertext(),DateTimeFormatters.dMYYYYmitPunkt);
                    LocalDate value = (LocalDate) entry.getValue(columnIndex);
                    System.out.println(find + "==" + value);
                    return strategy. compare(value, find);
                } catch (Exception e) {
                    logger.error("mist",e);
                    return false;
                }
            }
        }




}
