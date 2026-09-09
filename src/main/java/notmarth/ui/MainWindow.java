package notmarth.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import notmarth.NotMarthGui;

/** Controller for the FXML-defined NotMarth chat window. */
public final class MainWindow extends AnchorPane {
    private static final String ERROR_MESSAGE_PREFIX = "I couldn't process that";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream("/images/sommie.png"));
    private final Image notMarthImage = new Image(
            MainWindow.class.getResourceAsStream("/images/alear.png"));
    private NotMarthGui notMarth;

    /** Keeps the conversation scrolled to its latest message. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /** Injects the command-processing backend used by the graphical interface. */
    public void setNotMarth(NotMarthGui notMarth) {
        this.notMarth = notMarth;
        dialogContainer.getChildren().add(
                DialogBox.getNotMarthDialog(notMarth.getWelcomeMessage(), notMarthImage));
    }

    /** Adds the user's command and NotMarth's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = notMarth.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                isErrorResponse(response)
                        ? DialogBox.getErrorDialog(response, notMarthImage)
                        : DialogBox.getNotMarthDialog(response, notMarthImage));
        userInput.clear();
        if (notMarth.isExitRequested()) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        }
    }

    private boolean isErrorResponse(String response) {
        return response.startsWith(ERROR_MESSAGE_PREFIX);
    }
}
