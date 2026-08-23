import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean includesTime;

    /**
     * Creates an unfinished deadline task from a supported date or time text.
     *
     * @param description the task description
     * @param by the date or time by which the task should be completed
     * @throws DateTimeParseException if {@code by} is not a supported date
     *                                or date-time format
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        DateTimeParser.ParsedDateTime parsedDateTime = DateTimeParser.parse(by);
        this.by = parsedDateTime.value();
        this.includesTime = parsedDateTime.includesTime();
    }

    /**
     * Creates a deadline from an already parsed date and time.
     *
     * @param description the task description
     * @param by the deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
        this.includesTime = true;
    }

    /**
     * Creates a date-only deadline from an already parsed date.
     *
     * @param description the task description
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by.atStartOfDay();
        this.includesTime = false;
    }

    /**
     * Returns the typed deadline value.
     *
     * @return the deadline date and time, at midnight for a date-only deadline
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the unambiguous value used in the task archive.
     *
     * @return an ISO date or ISO local date-time
     */
    public String getByForStorage() {
        return DateTimeParser.formatForStorage(by, includesTime);
    }

    /**
     * Returns this task with its formatted deadline and type marker.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by, includesTime) + ")";
    }
}
