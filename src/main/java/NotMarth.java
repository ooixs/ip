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

            if (command.equals("list")) {
                printTasks(tasks, taskCount);
            } else if (isCommand(command, "mark")) {
                markTask(command, tasks, taskCount);
            } else if (isCommand(command, "unmark")) {
                unmarkTask(command, tasks, taskCount);
            } else if (isTaskCommand(command)) {
                Task task = createTask(command);
                if (task != null) {
                    if (taskCount == MAX_TASKS) {
                        printError("Your task list is full. Remove a task before adding another one.");
                    } else {
                        tasks[taskCount] = task;
                        taskCount++;
                        System.out.println("     Got it. I've added this task:");
                        System.out.println("       " + task);
                        System.out.println("     Now you have " + taskCount + " tasks in the list.");
                    }
                }
            } else if (command.isEmpty()) {
                printError("Please enter a command. Try todo, deadline, event, list, mark, or unmark.");
            } else {
                printError("I don't recognize that command. Try todo, deadline, event, list, mark, or unmark.");
            }

            System.out.println(separator);
        }
    }

    /**
     * Creates a task from a user command. Dates and times remain strings so
     * that the chatbot can display whatever format the user entered.
     *
     * @param command the command entered by the user
     * @return the parsed task, or {@code null} for an incomplete typed command
     */
    private static Task createTask(String command) {
        if (isCommand(command, "todo")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty()) {
                printError("A todo needs a description. Try: todo <description>");
                return null;
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
            printError("A deadline needs a description and a due time. Try: deadline <description> /by <date or time>");
            return null;
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
            printError("An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>");
            return null;
        }

        return null;
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
     */
    private static void markTask(String command, Task[] tasks, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(command.substring("mark".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                tasks[taskIndex].markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       " + tasks[taskIndex]);
            } else {
                printInvalidTaskNumber(taskCount);
            }
        } catch (NumberFormatException exception) {
            printError("Mark needs a task number, for example: mark 1");
        }
    }

    /**
     * Marks the task identified by an {@code unmark n} command as not completed.
     *
     * @param command the command containing the task number
     * @param tasks the array containing the stored tasks
     * @param taskCount the number of tasks currently stored
     */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(command.substring("unmark".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                tasks[taskIndex].markAsUndone();
                System.out.println("     OK, I've marked this task as not done yet:");
                System.out.println("       " + tasks[taskIndex]);
            } else {
                printInvalidTaskNumber(taskCount);
            }
        } catch (NumberFormatException exception) {
            printError("Unmark needs a task number, for example: unmark 1");
        }
    }

    /**
     * Explains why a task number cannot be used, including the empty-list case.
     *
     * @param taskCount the number of tasks currently stored
     */
    private static void printInvalidTaskNumber(int taskCount) {
        if (taskCount == 0) {
            printError("There are no tasks yet. Add a task before marking it.");
        } else {
            printError("That task number is not in your list. Use a number from 1 to " + taskCount + ".");
        }
    }

}
