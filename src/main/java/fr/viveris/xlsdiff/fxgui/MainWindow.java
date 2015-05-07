package fr.viveris.xlsdiff.fxgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainWindow extends Application {
    private static MainWindow instance = null;
    private Stage primaryStage;
    final Stage fileSelectionStage = new Stage();

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public Stage getFileSelectionStage() {
        return this.fileSelectionStage;
    }

    public static MainWindow getInstance() {
        return instance;
    }

    @Override
    public void start(final Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/fileSelectionFrame.fxml"));
            final AnchorPane pane = fxmlLoader.load();
            final Scene fileSelectionLogin = new Scene(pane);
            // fileSelectionLogin.getStylesheets()
            // .add(getClass().getResource("/Application.css")
            // .toExternalForm());
            this.fileSelectionStage.setTitle("Second Stage");
            this.fileSelectionStage.setScene(fileSelectionLogin);
            this.fileSelectionStage.show();

        } catch (final Exception e) {
            log.error("Error while opening mainFrame.fxml", e);
        }
    }

    public static void main(final String[] args) {

        // final String[] args2 = {
        // "--oldFile=C:\\Travail\\Tools\\XlsDiff\\src\\test\\resources\\Donnees_BPL_V2_I.xls",
        // "--newFile=C:\\Travail\\Tools\\XlsDiff\\src\\test\\resources\\Donnees_BPL_V2_I_KO_pour_diff.xls"
        // };

        launch();
    }
}
