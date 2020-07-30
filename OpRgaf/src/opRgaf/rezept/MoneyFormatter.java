package opRgaf.rezept;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.DefaultFormatter;

public class MoneyFormatter extends DefaultFormatter {

    private static final NumberFormat NUMBER_INSTANCE = NumberFormat.getNumberInstance(Locale.GERMAN);
    static {
        NUMBER_INSTANCE.setMinimumFractionDigits(2);
    }

    public MoneyFormatter() {
        super();
    }

    public String valueToString(Object object) throws ParseException {
        Money m;
        if (object == null) {
            m = Money.ZERO;
        } else {
            m = (Money) object;
        }
        return NUMBER_INSTANCE.format(m.getValue());
    }

    public Object stringToValue(String string) throws ParseException {
        try {
            if (string.matches(".+\\.\\d{2}$")) {
                return new Money(Double.parseDouble(string));
            } else {

                Money money = new Money(NUMBER_INSTANCE.parse(string)
                                                       .doubleValue());
                return money;
            }
        } catch (Exception e) {
            return Money.ZERO;
        }
    }
}
