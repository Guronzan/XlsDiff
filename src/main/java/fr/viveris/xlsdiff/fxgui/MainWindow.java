package fr.viveris.xlsdiff.fxgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainWindow extends Application {
    private static MainWindow instance = null;
    private Stage primaryStage;

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public static MainWindow getInstance() {
        return instance;
    }

    @Override
    public void start(final Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        try {
            final Parent page = FXMLLoader.load(MainWindow.class
                    .getResource("/mainFrame.fxml"));
            final Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        // if(checkArgs()){
        launch(args);
        // }
    }

    // private static boolean checkArgs() {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //

}
