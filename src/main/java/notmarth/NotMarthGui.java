package notmarth;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import notmarth.command.Command;
import notmarth.exception.NotMarthException;
import notmarth.model.TaskList;
import notmarth.parser.Parser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Provides the command-processing backend for the NotMarth graphical interface. */
public final class NotMarthGui {
    private static final int MAX_TASKS = 100;
    private static final String DEFAULT_FILE_PATH = "data/notmarth.txt";
    private static final String WELCOME_MESSAGE = "Hello! I'm NotMarth, your tactical companion.\n"
            + "Enter a command below to update your battle plan.\n\n";

    private final Parser parser = new Parser();
    private final Storage storage = new Storage(DEFAULT_FILE_PATH);
    private final Ui ui = new Ui();
    private final TaskList tasks;
    private boolean isExitRequested;

    /** Creates the command-processing backend and loads the saved battle plan. */
    public NotMarthGui() {
        Storage.LoadResult loadResult = storage.load(MAX_TASKS);
        tasks = new TaskList(loadResult.getTasks(), MAX_TASKS);
    }

    /** Returns the opening message shown in the graphical conversation. */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /** Executes a command and returns the same response used by the console UI. */
    public String getResponse(String input) {
        isExitRequested = false;
        try {
            Command command = parser.parse(input);
            isExitRequested = command.isExit();
            return captureCommandOutput(command);
        } catch (NotMarthException exception) {
            return "I couldn't process that, Divine One: " + exception.getMessage();
        }
    }

    /** Returns whether the most recent command requested that the window close. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    private String captureCommandOutput(Command command) throws NotMarthException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            command.execute(tasks, ui, storage);
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
