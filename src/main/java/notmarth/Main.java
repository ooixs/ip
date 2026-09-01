package notmarth;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import notmarth.ui.MainWindow;

/** The JavaFX entry point for the NotMarth tactical assistant. */
public final class Main extends Application {
    private final NotMarthGui notMarth = new NotMarthGui();

    /** Loads and displays the FXML-defined chat window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setNotMarth(notMarth);

            stage.setTitle("NotMarth — Tactical Assistant");
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            stage.setScene(new Scene(mainWindow));
            stage.getScene().getStylesheets().add(
                    Main.class.getResource("/css/main.css").toExternalForm());
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
