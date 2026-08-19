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
        System.out.println("Hello! I'm NotMarth.");
        System.out.println("What can I do for you?");
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
            System.out.println(separator);
        }
    }
}
