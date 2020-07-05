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
        return NUMBER_INSTANCE.format(m.getValue()
                                       .doubleValue());
    }

    public Object stringToValue(String string) throws ParseException {
        try {
            return new Money(NUMBER_INSTANCE.parse(string)
                                            .doubleValue());
        } catch (Exception e) {

            return Money.ZERO;
        }
    }
}
