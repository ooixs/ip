import java.util.Scanner;

/**
 * The main entry point for the NotMarth chatbot.
 */
public class NotMarth {
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
            System.out.println(getResponse(command));
            System.out.println(separator);
        }
    }

    /**
     * Returns NotMarth's response to a command.
     *
     * @param command the command entered by the user
     * @return a tactical response for the command
     */
    private static String getResponse(String command) {
        return switch (command) {
        case "help" -> "Available commands: help, engage, status, marth, sombron, bye";
        case "engage" -> "NotMarth and Marth, engage!";
        case "status" -> "Current status: awake, determined, and still not Marth.";
        case "marth" -> "Marth is currently fighting alongside me. Please stop confusing us.";
        case "sombron" -> "Sombron detected. Tactical response recommended.";
        default -> "That command is not in my battle plan.";
        };
    }
}
