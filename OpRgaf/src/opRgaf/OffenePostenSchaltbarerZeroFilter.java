package opRgaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opRgaf.rezept.Money;

public class OffenePostenSchaltbarerZeroFilter extends OffenePostenAbstractRowFilter {

     private   Strategy strategy = Strategy.ungleich;

        private static final Logger logger = LoggerFactory.getLogger(OffenePostenSchaltbarerZeroFilter.class);


        public OffenePostenSchaltbarerZeroFilter(int column) {
            columnIndex = column;
        }

        public OffenePostenSchaltbarerZeroFilter(int column,Strategy vergleich) {
            this(column);
            strategy = vergleich;
        }



        @Override
        protected boolean validate(Object object) {

                try {
                    Money find = Money.ZERO;
                    Money value = (Money) object;
                    return strategy. compare(value, find);
                } catch (Exception e) {
                    return false;
                }
            }
        }

