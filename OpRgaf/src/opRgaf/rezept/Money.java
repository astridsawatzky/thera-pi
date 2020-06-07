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
        value = geld.value;
    }

    public Money(String value) {
        if (value != null) {

            this.value = new BigDecimal(value).setScale(2, RoundingMode.DOWN);
        }

    }

    Money(double value) {
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
        m.value = this.value.add(other.value);
        return m;
    }

    public String toPlainString() {
        return value.toPlainString();

    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(Money o) {

        return this.value.compareTo(o.value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public boolean isMoreThan(Money other) {
        return value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Money other) {
        return value.compareTo(other.value) < 0;
    }

    public boolean hasSameValue(Money other) {
        return value.compareTo(other.value) == 0;
    }

}
