package notmarth;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import notmarth.command.Command;
import notmarth.exception.NotMarthException;
import notmarth.model.TaskList;
import notmarth.parser.Parser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Provides a graphical interface for entering NotMarth mission orders. */
public final class NotMarthGui extends Application {
    private static final int MAX_TASKS = 100;
    private static final String DEFAULT_FILE_PATH = "data/notmarth.txt";
    private static final String WINDOW_TITLE = "NotMarth — Tactical Assistant";

    private final Parser parser = new Parser();
    private final Storage storage = new Storage(DEFAULT_FILE_PATH);
    private final Ui ui = new Ui();
    private TaskList tasks;
    private TextArea conversation;
    private TextField commandInput;

    /** Creates the graphical application and loads its saved battle plan. */
    public NotMarthGui() {
        Storage.LoadResult loadResult = storage.load(MAX_TASKS);
        tasks = new TaskList(loadResult.getTasks(), MAX_TASKS);
    }

    /** Builds and displays the NotMarth command window. */
    @Override
    public void start(Stage stage) {
        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setText("Hello! I'm NotMarth, your tactical companion.\n"
                + "Enter a command below to update your battle plan.\n\n");

        commandInput = new TextField();
        commandInput.setPromptText("Enter a mission order, e.g. todo review notes");
        commandInput.setOnAction(event -> executeCommand(stage));

        Button engageButton = new Button("Engage");
        engageButton.setDefaultButton(true);
        engageButton.setOnAction(event -> executeCommand(stage));

        HBox commandBar = new HBox(8, commandInput, engageButton);
        commandBar.setPadding(new Insets(10));
        HBox.setHgrow(commandInput, javafx.scene.layout.Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(new Label("  NotMarth — Your battle plan awaits"));
        root.setCenter(conversation);
        root.setBottom(commandBar);
        BorderPane.setMargin(root.getTop(), new Insets(10, 10, 0, 10));

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root, 720, 480));
        stage.show();
        commandInput.requestFocus();
    }

    private void executeCommand(Stage stage) {
        String fullCommand = commandInput.getText().trim();
        if (fullCommand.isEmpty()) {
            return;
        }

        conversation.appendText("> " + fullCommand + "\n");
        commandInput.clear();
        try {
            Command command = parser.parse(fullCommand);
            String output = captureCommandOutput(command);
            conversation.appendText(output);
            if (command.isExit()) {
                stage.close();
            }
        } catch (NotMarthException exception) {
            conversation.appendText("I couldn't process that, Divine One: " + exception.getMessage() + "\n");
        }
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
