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
     * @param taskCount the number of tasks currently stored
     */
    private static void printTasks(String[] tasks, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + ". " + tasks[i]);
        }
    }

}
