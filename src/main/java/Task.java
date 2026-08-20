/**
 * Represents a task in the chatbot's task list.
 */
public class Task {
    protected final String description;
    protected boolean isDone;
    private final TaskType type;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this(description, TaskType.GENERIC);
    }

    /**
     * Creates a task with a specific task type.
     *
     * @param description the task description
     * @param type the kind of task
     */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the icon representing this task's completion state.
     *
     * @return {@code X} when the task is done, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the task in the format used when displaying it.
     *
     * @return the optional type marker, status icon, and task description
     */
    @Override
    public String toString() {
        return type.getDisplayMarker() + "[" + getStatusIcon() + "] " + description;
    }
}
