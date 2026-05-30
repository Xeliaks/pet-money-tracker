package pet.money.tracker.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import pet.money.tracker.exception.ValidationException;

/** Utility methods for parsing and formatting dates. */
public final class DateUtils {

    /** Display format used in table output: dd/MM/yyyy. */
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtils() {
    }

    /**
     * Parses a date string in ISO format (yyyy-MM-dd).
     *
     * @param dateStr the string to parse
     * @return parsed LocalDate
     * @throws ValidationException if the string is not a valid ISO date
     */
    public static LocalDate parse(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date '" + dateStr + "'. Use yyyy-MM-dd format.", e);
        }
    }

    /**
     * Formats a LocalDate for display.
     *
     * @param date the date to format
     * @return date string in dd/MM/yyyy format
     */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * @return today's date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
}
