package hmv;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.StringConverter;

public class SixNumbersConverter extends StringConverter<LocalDate> {

    private static final DecimalFormatSymbols alldots = new DecimalFormatSymbols(Locale.GERMAN);
    private static final DecimalFormat formatter = new DecimalFormat("000,000.##", alldots);
    private StringConverter<LocalDate> filling;
    static {
        alldots.setDecimalSeparator('.');
        alldots.setGroupingSeparator('.');
        formatter.setGroupingSize(2);
    }

    public SixNumbersConverter(StringConverter<LocalDate> filling) {
        this.filling = filling;
    }

    @Override
    public String toString(LocalDate object) {
        return filling.toString(object);
    }

    @Override
    public LocalDate fromString(String string) {
        Pattern pattern = Pattern.compile("\\d{6}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);

        if (matcher.matches()) {
            string = formatter.format(Integer.valueOf(string));
        }
        return filling.fromString(string);
    }
}
