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
        String[] tasks = new String[MAX_TASKS];
        boolean[] completedTasks = new boolean[MAX_TASKS];
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
                printTasks(tasks, completedTasks, taskCount);
            } else if (command.startsWith("mark ")) {
                markTask(command, tasks, completedTasks, taskCount);
            } else if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks, completedTasks, taskCount);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("     added: " + command);
            }

            System.out.println(separator);
        }
    }

    /**
     * Prints all tasks in the order in which they were entered.
     *
     * @param tasks the array containing the stored tasks
     * @param completedTasks the array containing each task's completion state
     * @param taskCount the number of tasks currently stored
     */
    private static void printTasks(String[] tasks, boolean[] completedTasks, int taskCount) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            String status = completedTasks[i] ? "X" : " ";
            System.out.println("     " + (i + 1) + ".[" + status + "] " + tasks[i]);
        }
    }

    /**
     * Marks the task identified by a {@code mark n} command as completed.
     *
     * @param command the command containing the task number
     * @param tasks the array containing the stored tasks
     * @param completedTasks the array containing each task's completion state
     * @param taskCount the number of tasks currently stored
     */
    private static void markTask(String command, String[] tasks, boolean[] completedTasks, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(command.substring("mark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                completedTasks[taskIndex] = true;
                System.out.println("     Nice! I've marked this task as done:");
                System.out.println("       [X] " + tasks[taskIndex]);
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
     * @param completedTasks the array containing each task's completion state
     * @param taskCount the number of tasks currently stored
     */
    private static void unmarkTask(String command, String[] tasks, boolean[] completedTasks, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(command.substring("unmark ".length()).trim());
            int taskIndex = taskNumber - 1;

            if (taskIndex >= 0 && taskIndex < taskCount) {
                completedTasks[taskIndex] = false;
                System.out.println("     OK, I've marked this task as not done yet:");
                System.out.println("       [ ] " + tasks[taskIndex]);
            }
        } catch (NumberFormatException exception) {
            // Ignore malformed unmark commands instead of terminating the chatbot.
        }
    }

}
