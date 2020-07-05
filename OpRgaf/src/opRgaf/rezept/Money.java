package opRgaf.rezept;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class Money implements Comparable<Money> {
    private BigDecimal value = new BigDecimal(".00");
    private static final NumberFormat df = twoDecimalsRoundingDown();
    public static final Money ZERO = new Money();

    public Money() {

    }

    public Money(Money geld) {
        value = geld.getValue();
    }

    public Money(String value) {
        if (value != null && !value.isEmpty()) {

            this.value = new BigDecimal(value).setScale(2, RoundingMode.DOWN);
        }

    }

    public Money(double value) {
        this.value = new BigDecimal(df.format(value));
    }

    private static NumberFormat twoDecimalsRoundingDown() {

        DecimalFormatSymbols dezimalPunkt = DecimalFormatSymbols.getInstance();
        dezimalPunkt.setDecimalSeparator('.');

        NumberFormat df = new DecimalFormat("###0.##", dezimalPunkt);
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);

        df.setRoundingMode(RoundingMode.DOWN);

        return df;
    }

    public Money add(Money other) {
        Money m = new Money();
        m.value = this.getValue()
                      .add(other.getValue());
        return m;
    }

    public Money minus(Money eingang) {
        Money m = new Money();
        m.value = value.subtract(eingang.value);
        return m;
    }

    public String toPlainString() {
        return getValue().toPlainString();

    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int compareTo(Money o) {

        return this.getValue()
                   .compareTo(o.getValue());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (getValue() == null) {
            if (other.getValue() != null)
                return false;
        } else if (!getValue().equals(other.getValue()))
            return false;
        return true;
    }

    public boolean isMoreThan(Money other) {
        return getValue().compareTo(other.getValue()) > 0;
    }

    public boolean isLessThan(Money other) {
        return getValue().compareTo(other.getValue()) < 0;
    }

    public boolean hasSameValue(Money other) {
        if (other == null) {
            return false;
        }
        return getValue().compareTo(other.getValue()) == 0;
    }

    public BigDecimal getValue() {
        return value;
    }

}
