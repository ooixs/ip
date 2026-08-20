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
            String command = scanner.nextLine();

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
            } else if (command.startsWith("mark ")) {
                markTask(command, tasks, taskCount);
            } else if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks, taskCount);
            } else if (taskCount < MAX_TASKS) {
                Task task = createTask(command);
                if (task != null) {
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println("     Got it. I've added this task:");
                    System.out.println("       " + task);
                    System.out.println("     Now you have " + taskCount + " tasks in the list.");
                }
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
        if (command.startsWith("todo ")) {
            String description = command.substring("todo ".length()).trim();
            return description.isEmpty() ? null : new ToDo(description);
        }

        if (command.startsWith("deadline ")) {
            String details = command.substring("deadline ".length()).trim();
            int byMarker = details.indexOf("/by");
            if (byMarker > 0) {
                String description = details.substring(0, byMarker).trim();
                String by = details.substring(byMarker + "/by".length()).trim();
                if (!description.isEmpty() && !by.isEmpty()) {
                    return new Deadline(description, by);
                }
            }
            return null;
        }

        if (command.startsWith("event ")) {
            String details = command.substring("event ".length()).trim();
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
            return null;
        }

        // Keep the original behaviour for commands that are not typed tasks.
        return new Task(command);
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
            int taskNumber = Integer.parseInt(command.substring("mark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                tasks[taskIndex].markAsDone();
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       " + tasks[taskIndex]);
            }
        } catch (NumberFormatException exception) {
            // Ignore malformed mark commands instead of terminating the chatbot.
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
            int taskNumber = Integer.parseInt(command.substring("unmark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                tasks[taskIndex].markAsUndone();
                System.out.println("     OK, I've marked this task as not done yet:");
                System.out.println("       " + tasks[taskIndex]);
            }
        } catch (NumberFormatException exception) {
            // Ignore malformed unmark commands instead of terminating the chatbot.
        }
    }

}
