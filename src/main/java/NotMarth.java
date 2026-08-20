import java.util.Scanner;

/**
 * The main entry point for the NotMarth chatbot.
 */
public class NotMarth {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "_".repeat(60);
        String banner = " _   _  ___ _____ __  __    _    ____ _____ _   _\n"
                + "| \\ | |/ _ \\_   _|  \\/  |  / \\  |  _ \\_   _| | | |\n"
                + "|  \\| | | | || | | |\\/| | / _ \\ | |_) || | | |_| |\n"
                + "| |\\  | |_| || | | |  | |/ ___ \\|  _ < | | |  _  |\n"
                + "|_| \\_|\\___/ |_| |_|  |_/_/   \\_\\_| \\_\\|_| |_| |_|\n";

        System.out.println(separator);
        System.out.print(banner);
        System.out.println("Hello! I'm NotMarth, the definitely-not-Marth Divine Dragon.");
        System.out.println("What tactical command can I assist with?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();

            if (command.equals("bye")) {
                System.out.println(separator);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            System.out.println(separator);
            System.out.println("     " + command);

            try {
                if (command.equals("list")) {
                    printTasks(tasks, taskCount);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks, taskCount);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks, taskCount);
                } else if (isCommand(command, "delete")) {
                    taskCount = deleteTask(command, tasks, taskCount);
                } else if (isTaskCommand(command)) {
                    Task task = createTask(command);
                    if (taskCount == MAX_TASKS) {
                        throw new NotMarthException("Your task list is full. Remove a task before adding another one.");
                    } else {
                        tasks[taskCount] = task;
                        taskCount++;
                        System.out.println("     Got it. I've added this task:");
                        System.out.println("       " + task);
                        System.out.println("     Now you have " + taskCount + " tasks in the list.");
                    }
                } else if (command.isEmpty()) {
                    throw new NotMarthException("Please enter a command. Try todo, deadline, event, list, mark, unmark, or delete.");
                } else {
                    throw new NotMarthException("I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or delete.");
                }
            } catch (NotMarthException exception) {
                printError(exception.getMessage());
            }

            System.out.println(separator);
        }
    }

    /**
     * Creates a task from a user command. Dates and times remain strings so
     * that the chatbot can display whatever format the user entered.
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
                    return new Deadline(description, by);
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
                    return new Event(description, from, to);
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
     * Prints a consistent, user-facing error message.
     *
     * @param message the explanation of what went wrong
     */
    private static void printError(String message) {
        System.out.println("     I couldn't process that: " + message);
    }

    /**
     * Prints all tasks in the order in which they were entered.
     *
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Marks the task identified by a {@code mark n} command as completed.
     *
     * @param command the command containing the task number
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void markTask(String command, Task[] tasks, int taskCount) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "mark");
        validateTaskNumber(taskNumber, taskCount, "marking");

        tasks[taskNumber - 1].markAsDone();
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       " + tasks[taskNumber - 1]);
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
     * Ensures that a requested task number identifies an existing task.
     *
     * @param taskNumber the requested one-based task number
     * @param taskCount the number of tasks currently stored
     * @throws NotMarthException if there are no tasks or the number is out of range
     */
    private static void validateTaskNumber(int taskNumber, int taskCount, String action) throws NotMarthException {
        if (taskCount == 0) {
            throw new NotMarthException("There are no tasks yet. Add a task before " + action + " it.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NotMarthException("That task number is not in your list. Use a number from 1 to " + taskCount + ".");
        }
    }

    /**
     * Marks the task identified by an {@code unmark n} command as not completed.
     *
     * @param command the command containing the task number
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "unmark");
        validateTaskNumber(taskNumber, taskCount, "unmarking");

        tasks[taskNumber - 1].markAsUndone();
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + tasks[taskNumber - 1]);
    }

    /**
     * Deletes the task identified by a {@code delete n} command and closes the
     * gap left behind so that the remaining tasks keep consecutive numbers.
     *
     * @param command the command containing the task number
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     * @return the updated number of stored tasks
     * @throws NotMarthException if the task number is invalid or out of range
     */
    private static int deleteTask(String command, Task[] tasks, int taskCount) throws NotMarthException {
        int taskNumber = parseTaskNumber(command, "delete");
        validateTaskNumber(taskNumber, taskCount, "deleting");

        Task deletedTask = tasks[taskNumber - 1];
        for (int i = taskNumber - 1; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        taskCount--;

        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + deletedTask);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

}
