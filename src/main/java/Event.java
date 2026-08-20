/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an unfinished event task.
     *
     * @param description the task description
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this task with its event range and type marker.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
