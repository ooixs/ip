import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Parses and formats the date and time values used by NotMarth tasks.
 */
final class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            formatter("d/M/uuuu HHmm"),
            formatter("d/M/uuuu H:mm"),
            formatter("uuuu-MM-dd HHmm"),
            formatter("uuuu-MM-dd H:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            formatter("d/M/uuuu"),
            formatter("uuuu-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE);

    private DateTimeParser() {
        // Utility class; do not create instances.
    }

    /**
     * Parses a date-only or date-time value.
     *
     * @param input the value entered by the user or loaded from storage
     * @return the parsed value and whether the input included a time
     * @throws DateTimeParseException if the value is not supported
     */
    static ParsedDateTime parse(String input) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDateTime.parse(input, formatter), true);
            } catch (DateTimeParseException exception) {
                // Try the next supported date/time format.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return new ParsedDateTime(LocalDate.parse(input, formatter).atStartOfDay(), false);
            } catch (DateTimeParseException exception) {
                // Try the next supported date-only format.
            }
        }
        throw new DateTimeParseException("Unsupported date or time format", input, 0);
    }

    /**
     * Formats a parsed value for the console.
     *
     * @param value the date and time to format
     * @param includesTime whether the original value included a time
     * @return the readable console representation
     */
    static String format(LocalDateTime value, boolean includesTime) {
        return (includesTime ? DISPLAY_DATE_TIME_FORMATTER : DISPLAY_DATE_FORMATTER).format(value);
    }

    /**
     * Formats a value for the unambiguous task archive.
     *
     * @param value the date and time to format
     * @param includesTime whether the original value included a time
     * @return an ISO date or ISO local date-time
     */
    static String formatForStorage(LocalDateTime value, boolean includesTime) {
        return includesTime ? value.toString() : value.toLocalDate().toString();
    }

    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /** Contains a parsed value and whether the source included a time. */
    record ParsedDateTime(LocalDateTime value, boolean includesTime) {
    }
}
