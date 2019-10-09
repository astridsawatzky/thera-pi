package converter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class DateConverter {
    /**
     * stolen from {@link java.sql.Date}
     * <p>
     * Obtains an instance of {@code Date} from a {@link LocalDate} object with the
     * same year, month and day of month value as the given {@code LocalDate}.
     * <p>
     * The provided {@code LocalDate} is interpreted as the local date in the local
     * time zone.
     *
     * @param date a {@code LocalDate} to convert
     * @return a {@code Date} object
     * @exception NullPointerException if {@code date} is null
     * @since 1.8
     */
    @SuppressWarnings("deprecation")
    public static Date valueOf(LocalDate date) {
        return new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth());
    }

    /**
     * stolen from {@link java.sql.Date}
     * <p>
     * Converts this {@code Date} object to a {@code LocalDate}
     * <p>
     * The conversion creates a {@code LocalDate} that represents the same date
     * value as this {@code Date} in local time zone
     *
     * @return a {@code LocalDate} object representing the same date value
     *
     * @since 1.8
     */
    @SuppressWarnings("deprecation")
    public static LocalDate valueOf(java.util.Date date) {
        return LocalDate.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }

    public static final String punktDatumPatternddmmyyyy = "dd.MM.yyyy";
    public static final SimpleDateFormat ddMMyyy = new SimpleDateFormat("dd.MM.yyyy");

}
