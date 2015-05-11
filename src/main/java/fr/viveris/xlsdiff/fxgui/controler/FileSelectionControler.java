package fr.viveris.xlsdiff.fxgui.controler;

import java.io.File;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import fr.viveris.xlsdiff.fxgui.MainWindow;

@Slf4j
public class FileSelectionControler {
	@FXML
	private Button buttonCancel;
	@FXML
	private Button buttonRunCompare;
	@FXML
	private Button buttonFile1;
	@FXML
	private Button buttonFile2;
	@FXML
	private TextField fieldFile1;
	@FXML
	private TextField fieldFile2;

	private final MainWindow mainInstance = MainWindow.getInstance();
	private FXMLLoader mainFxmlLoader;

	public void initialize() {
		this.buttonRunCompare.setOnAction(event -> {
			run();
		});

		this.buttonCancel.setOnAction(event -> {
			this.mainInstance.getFileSelectionStage().close();
		});

		this.buttonFile1.setOnAction(event -> {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File 1");
			final File file1 = fileChooser.showOpenDialog(this.mainInstance
					.getFileSelectionStage());
			this.fieldFile1.setText(file1.getAbsolutePath());
		});
		this.buttonFile2.setOnAction(event -> {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select File 2");
			final File file2 = fileChooser.showOpenDialog(this.mainInstance
					.getFileSelectionStage());
			this.fieldFile2.setText(file2.getAbsolutePath());
		});
		this.mainFxmlLoader = new FXMLLoader(
				MainWindow.class.getResource("/mainFrame.fxml"));
		try {
			final Parent page = this.mainFxmlLoader.load();
			final Stage primaryStage = this.mainInstance.getPrimaryStage();
			final Scene scene = new Scene(page);
			primaryStage.setScene(scene);
		} catch (final Exception e) {
			log.error("Error while opening MainWindow", e);
		}
	}

	private void run() {
		final File file1 = new File(this.fieldFile1.getText());
		final File file2 = new File(this.fieldFile2.getText());
		final Stage fileSelectionStage = this.mainInstance
				.getFileSelectionStage();
		if (file1.exists() && file2.exists()) {
			final Stage primaryStage = this.mainInstance.getPrimaryStage();

			primaryStage.show();
			fileSelectionStage.close();
			final SheetListControler sheetListControler = this.mainFxmlLoader
					.getController();

			sheetListControler.process(this.fieldFile1.getText(),
					this.fieldFile2.getText());
		} else {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid files");
			alert.setHeaderText(null);
			alert.setContentText("At least one of the selected files doesn't exist, please check their existence before launch compare");

			alert.showAndWait();
		}
	}
}
