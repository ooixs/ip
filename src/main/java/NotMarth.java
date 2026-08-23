import java.io.IOException;
import java.time.LocalDate;

/**
 * The main entry point for the NotMarth chatbot.
 */
public class NotMarth {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("data/notmarth.txt");
        Storage.LoadResult loadResult = storage.load(MAX_TASKS);
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
                } else if (parser.isCommand(command, "on")) {
                    printTasksOnDate(command, tasks, ui, parser);
                } else if (parser.isCommand(command, "mark")) {
                    markTask(command, tasks, ui, parser, storage);
                } else if (parser.isCommand(command, "unmark")) {
                    unmarkTask(command, tasks, ui, parser, storage);
                } else if (parser.isCommand(command, "delete")) {
                    deleteTask(command, tasks, ui, parser, storage);
                } else if (parser.isTaskCommand(command)) {
                    Task task = parser.createTask(command);
                    tasks.add(task);
                    saveTasks(tasks, ui, storage);
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
     * Prints deadlines and events occurring on the date from an {@code on}
     * command. Original task numbers are retained so the results can still be
     * used with commands such as {@code mark} and {@code delete}.
     *
     * @param command the command containing the requested date
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the command has no valid date
     */
    private static void printTasksOnDate(String command, TaskList tasks, Ui ui, Parser parser)
            throws NotMarthException {
        LocalDate date = parser.parseOnDate(command);

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
    private static void markTask(String command, TaskList tasks, Ui ui, Parser parser, Storage storage)
            throws NotMarthException {
        int taskNumber = parser.parseTaskNumber(command, "mark");

        Task task = tasks.mark(taskNumber);
        saveTasks(tasks, ui, storage);
        ui.showTaskMarked(task);
    }

    /**
     * Marks the task identified by an {@code unmark n} command as not completed.
     *
     * @param command the command containing the task number
     * @param tasks the collection containing the stored tasks
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void unmarkTask(String command, TaskList tasks, Ui ui, Parser parser, Storage storage)
            throws NotMarthException {
        int taskNumber = parser.parseTaskNumber(command, "unmark");

        Task task = tasks.unmark(taskNumber);
        saveTasks(tasks, ui, storage);
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
    private static void deleteTask(String command, TaskList tasks, Ui ui, Parser parser, Storage storage)
            throws NotMarthException {
        int taskNumber = parser.parseTaskNumber(command, "delete");

        Task deletedTask = tasks.delete(taskNumber);
        saveTasks(tasks, ui, storage);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Persists a successful task-list change without interrupting the command
     * flow if the operating system temporarily refuses the write.
     *
     * @param tasks the changed task list
     */
    private static void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.asList());
        } catch (IOException exception) {
            ui.showError("I couldn't save the battle plan to disk. Your current session is still active.");
        }
    }

}
