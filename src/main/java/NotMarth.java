import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * The main entry point for the NotMarth chatbot.
 */
public class NotMarth {
    private static final int MAX_TASKS = 100;
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskStorage.LoadResult loadResult = TaskStorage.load(MAX_TASKS);
        TaskList tasks = new TaskList(loadResult.getTasks(), MAX_TASKS);

        ui.showWelcome();
        if (loadResult.hasWarning()) {
            ui.showStartupWarning(loadResult.getWarning());
            return;
        }
        ui.showPrompt();

        while (ui.hasNextLine()) {
            String command = ui.readCommand();

            if (command.equals("bye")) {
                ui.showFarewell();
                break;
            }

            ui.showCommand(command);

            try {
                if (command.equals("sommie")) {
                    ui.showSommieMessage();
                } else if (command.equals("list")) {
                    ui.showTasks(tasks);
                } else if (isCommand(command, "on")) {
                    printTasksOnDate(command, tasks, ui);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks, ui);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks, ui);
                } else if (isCommand(command, "delete")) {
                    deleteTask(command, tasks, ui);
                } else if (isTaskCommand(command)) {
                    Task task = createTask(command);
                    tasks.add(task);
                    saveTasks(tasks, ui);
                    ui.showTaskAdded(task, tasks.size());
                } else if (command.isEmpty()) {
                    throw new NotMarthException("Please enter a command. Try todo, deadline, event, list, on, mark, unmark, or delete.");
                } else {
                    throw new NotMarthException("I don't recognize that command. Try todo, deadline, event, list, on, mark, unmark, or delete.");
                }
            } catch (NotMarthException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showSeparator();
        }
    }

    /**
     * Creates a task from a user command. Deadline values are parsed into
     * typed {@code java.time} values before the task is created.
     *
     * @param command the command entered by the user
     * @return the parsed task
     * @throws NotMarthException if the command is missing required information
     */
    private static Task createTask(String command) throws NotMarthException {
        if (isCommand(command, "todo")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new NotMarthException("A todo needs a description. Try: todo <description>");
            }
            return new ToDo(description);
        }

        if (isCommand(command, "deadline")) {
            String details = command.substring("deadline".length()).trim();
            int byMarker = details.indexOf("/by");
            if (byMarker > 0) {
                String description = details.substring(0, byMarker).trim();
                String by = details.substring(byMarker + "/by".length()).trim();
                if (!description.isEmpty() && !by.isEmpty()) {
                    try {
                        return new Deadline(description, by);
                    } catch (DateTimeParseException exception) {
                        throw new NotMarthException(
                                "That deadline date or time is not valid. Try yyyy-mm-dd or dd/MM/yyyy HHmm, for example: 2019-10-15 or 02/12/2019 1800",
                                exception);
                    }
                }
            }
            throw new NotMarthException("A deadline needs a description and a due time. Try: deadline <description> /by <date or time>");
        }

        if (isCommand(command, "event")) {
            String details = command.substring("event".length()).trim();
            int fromMarker = details.indexOf("/from");
            int toMarker = details.indexOf("/to");
            if (fromMarker > 0 && toMarker > fromMarker) {
                String description = details.substring(0, fromMarker).trim();
                String from = details.substring(fromMarker + "/from".length(), toMarker).trim();
                String to = details.substring(toMarker + "/to".length()).trim();
                if (!description.isEmpty() && !from.isEmpty() && !to.isEmpty()) {
                    try {
                        return new Event(description, from, to);
                    } catch (DateTimeParseException exception) {
                        throw new NotMarthException(
                                "That event date or time is not valid. Try yyyy-mm-dd or dd/MM/yyyy HHmm, for example: 2019-10-15 or 02/12/2019 1800",
                                exception);
                    } catch (IllegalArgumentException exception) {
                        throw new NotMarthException(
                                "An event cannot end before it starts. Check the /from and /to values.",
                                exception);
                    }
                }
            }
            throw new NotMarthException("An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>");
        }

        throw new NotMarthException("I don't recognize that task type.");
    }

    /**
     * Checks whether a command is exactly a keyword or starts with that keyword
     * followed by at least one space.
     *
     * @param command the complete command entered by the user
     * @param keyword the command keyword to look for
     * @return whether the command uses the keyword at its beginning
     */
    private static boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Checks whether the command is one of the supported task-creation commands.
     *
     * @param command the complete command entered by the user
     * @return whether the command starts with a supported task keyword
     */
    private static boolean isTaskCommand(String command) {
        return isCommand(command, "todo")
                || isCommand(command, "deadline")
                || isCommand(command, "event");
    }

    /**
     * Prints deadlines and events occurring on the date from an {@code on}
     * command. Original task numbers are retained so the results can still be
     * used with commands such as {@code mark} and {@code delete}.
     *
     * @param command the command containing the requested date
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the command has no valid date
     */
    private static void printTasksOnDate(String command, TaskList tasks, Ui ui) throws NotMarthException {
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new NotMarthException("The on command needs a date. Try: on <date>");
        }

        LocalDate date;
        try {
            date = DateTimeParser.parseDate(dateText);
        } catch (DateTimeParseException exception) {
            throw new NotMarthException(
                    "That date is not valid. Try yyyy-mm-dd or dd/MM/yyyy, for example: 2019-10-15 or 15/10/2019",
                    exception);
        }

        String displayDate = DateTimeParser.format(date.atStartOfDay(), false);
        boolean foundMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = task instanceof Deadline deadline && deadline.isDueOn(date)
                    || task instanceof Event event && event.occursOn(date);
            if (matches) {
                if (!foundMatch) {
                    ui.showDateTasksHeader(displayDate);
                    foundMatch = true;
                }
                ui.showNumberedTask(i + 1, task);
            }
        }
        if (!foundMatch) {
            ui.showNoDateTasks(displayDate);
        }
    }

    /**
     * Marks the task identified by a {@code mark n} command as completed.
     *
     * @param command the command containing the task number
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void markTask(String command, TaskList tasks, Ui ui) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "mark");

        Task task = tasks.mark(taskNumber);
        saveTasks(tasks, ui);
        ui.showTaskMarked(task);
    }

    /**
     * Parses a task number from a command that operates on a task.
     *
     * @param command the complete command entered by the user
     * @param commandName the command keyword used in the error message
     * @return the requested task number
     * @throws NotMarthException if the command does not contain an integer
     */
    private static int parseTaskNumber(String command, String commandName) throws NotMarthException {
        try {
            return Integer.parseInt(command.substring(commandName.length()).trim());
        } catch (NumberFormatException exception) {
            throw new NotMarthException(
                    commandName.substring(0, 1).toUpperCase() + commandName.substring(1)
                            + " needs a task number, for example: " + commandName + " 1",
                    exception);
        }
    }

    /**
     * Marks the task identified by an {@code unmark n} command as not completed.
     *
     * @param command the command containing the task number
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void unmarkTask(String command, TaskList tasks, Ui ui) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "unmark");

        Task task = tasks.unmark(taskNumber);
        saveTasks(tasks, ui);
        ui.showTaskUnmarked(task);
    }

    /**
     * Deletes the task identified by a {@code delete n} command and closes the
     * gap left behind so that the remaining tasks keep consecutive numbers.
     *
     * @param command the command containing the task number
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void deleteTask(String command, TaskList tasks, Ui ui) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "delete");

        Task deletedTask = tasks.delete(taskNumber);
        saveTasks(tasks, ui);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Persists a successful task-list change without interrupting the command
     * flow if the operating system temporarily refuses the write.
     *
     * @param tasks the changed task list
     */
    private static void saveTasks(TaskList tasks, Ui ui) {
        try {
            TaskStorage.save(tasks.asList());
        } catch (IOException exception) {
            ui.showError("I couldn't save the battle plan to disk. Your current session is still active.");
        }
    }

}
