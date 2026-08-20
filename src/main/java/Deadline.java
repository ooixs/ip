/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param description the task description
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns this task with its deadline and type marker.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
