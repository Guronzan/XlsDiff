package fr.viveris.xlsdiff.fxgui.controler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.viveris.xlsdiff.fxgui.CellFactory;
import fr.viveris.xlsdiff.fxgui.MainWindow;
import fr.viveris.xlsdiff.utils.XlsUtils;

@Slf4j
public class SheetListControler {
	private Workbook oldWorkBook;
	private Workbook newWorkBook;
	private final Map<String, ObservableList<Map<String, String>>> oldWkData = new TreeMap<>();
	private final Map<String, ObservableList<Map<String, String>>> newWkData = new TreeMap<>();
	private final Map<TableColumn<Map<String, String>, String>, Integer> oldTableColumnsMap = new HashMap<>();
	private final Map<TableColumn<Map<String, String>, String>, Integer> newTableColumnsMap = new HashMap<>();
	private final CellFactory cellFactory = new CellFactory();

	private final MainWindow mainInstance = MainWindow.getInstance();
	private static SheetListControler instance;

	public static SheetListControler getInstance() {
		return instance;
	}

	public SheetListControler() {
		instance = this;
	}

	@FXML
	private ListView<String> sheetList;

	@FXML
	private TableView<Map<String, String>> oldTableView;
	@FXML
	private TableView<Map<String, String>> newTableView;

	@FXML
	private MenuItem menuChooseFiles;
	@FXML
	private MenuItem menuSearchDiff;

	public void initialize() {
		assert this.sheetList != null : "fx:id=\"sheetList\" was not injected: check your FXML file 'mainFrame.fxml'.";
		this.sheetList.setOnMouseClicked(event -> load());
		this.menuChooseFiles.setOnAction(event -> {
			this.mainInstance.getPrimaryStage().hide();
			this.mainInstance.getFileSelectionStage().showAndWait();
		});

		this.menuSearchDiff.setOnAction(event -> {
			performDiffs();
		});
	}

	private void performDiffs() {
		final ObservableList<Map<String, String>> oldItems = this.oldTableView
				.getItems();
		final ObservableList<Map<String, String>> newitems = this.newTableView
				.getItems();

		for (int i = 0; i < oldItems.size(); ++i) {
			final Map<String, String> oldMap = oldItems.get(i);
			final Map<String, String> newMap = newitems.get(i);

		}
	}

	private void load() {
		final String sheetName = this.sheetList.getSelectionModel()
				.getSelectedItem();
		if (sheetName == null) {
			return;
		}
		this.oldTableView.getColumns().clear();
		this.newTableView.getColumns().clear();
		this.newTableColumnsMap.clear();
		this.oldTableColumnsMap.clear();

		final Sheet oldSheet = this.oldWorkBook.getSheet(sheetName);
		final Sheet newSheet = this.newWorkBook.getSheet(sheetName);

		if (oldSheet != null) {
			final Map<Integer, String> columnMapKeys = new TreeMap<>();

			final Row firstRow = oldSheet.getRow(0);
			for (final Cell cell : firstRow) {

				final String cellValue = XlsUtils.getStringCellValue(cell);
				final TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(
						cellValue);
				tableColumn.setCellValueFactory(new MapValueFactory(cellValue));
				tableColumn.setCellFactory(this.cellFactory);
				this.oldTableView.getColumns().add(tableColumn);
				columnMapKeys.put(cell.getColumnIndex(), cellValue);
				this.oldTableColumnsMap.put(tableColumn, cell.getColumnIndex());
			}

			if (this.oldWkData.containsKey(oldSheet.getSheetName())) {
				this.oldTableView.setItems(this.oldWkData.get(oldSheet
						.getSheetName()));
			} else {

				final ObservableList<Map<String, String>> data = FXCollections
						.observableArrayList();
				for (final Row sheetRow : oldSheet) {
					if (sheetRow.getRowNum() == 0) {
						// Skip header
						continue;
					}

					final Map<String, String> dataRow = new HashMap<>();
					for (int i = 0; i < sheetRow.getLastCellNum(); ++i) {

						final Cell cell = sheetRow.getCell(i);
						dataRow.put(columnMapKeys.get(i),
								XlsUtils.getStringCellValue(cell));
					}
					data.add(dataRow);
				}

				this.oldTableView.setItems(data);
				this.oldWkData.put(sheetName, data);
			}
		}

		if (newSheet != null) {
			final Map<Integer, String> columnMapKeys = new TreeMap<>();
			final Row firstRow = newSheet.getRow(0);

			for (final Cell cell : firstRow) {

				final String cellValue = XlsUtils.getStringCellValue(cell);
				final TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(
						cellValue);
				tableColumn.setCellValueFactory(new MapValueFactory(cellValue));
				tableColumn.setCellFactory(this.cellFactory);
				this.newTableView.getColumns().add(tableColumn);
				columnMapKeys.put(cell.getColumnIndex(), cellValue);
				this.newTableColumnsMap.put(tableColumn, cell.getColumnIndex());
			}

			if (this.newWkData.containsKey(newSheet.getSheetName())) {
				this.newTableView.setItems(this.newWkData.get(newSheet
						.getSheetName()));
			} else {
				final ObservableList<Map<String, String>> data = FXCollections
						.observableArrayList();
				for (final Row sheetRow : newSheet) {
					if (sheetRow.getRowNum() == 0) {
						// Skip header
						continue;
					}
					final Map<String, String> dataRow = new HashMap<>();
					for (int i = 0; i < sheetRow.getLastCellNum(); ++i) {
						final Cell cell = sheetRow.getCell(i);
						dataRow.put(columnMapKeys.get(i),
								XlsUtils.getStringCellValue(cell));
					}
					data.add(dataRow);
				}
				this.newTableView.setItems(data);
				this.newWkData.put(sheetName, data);
			}
		}
	}

	public void process(final String oldFileName, final String newFileName) {

		try {
			this.oldWorkBook = XlsUtils.openFile(oldFileName);
			this.newWorkBook = XlsUtils.openFile(newFileName);
		} catch (final IOException e) {
			log.error("Error while opening Workbook", e);
			return;
		}

		final List<String> sheetList = new LinkedList<>();
		for (int sheetIndex = 0; sheetIndex < this.newWorkBook
				.getNumberOfSheets(); ++sheetIndex) {
			sheetList.add(this.newWorkBook.getSheetAt(sheetIndex)
					.getSheetName());
		}

		for (int sheetIndex = 0; sheetIndex < this.oldWorkBook
				.getNumberOfSheets(); ++sheetIndex) {
			final Sheet sheet = this.oldWorkBook.getSheetAt(sheetIndex);
			final String oldSheetName = sheet.getSheetName();
			if (!sheetList.contains(oldSheetName)) {
				sheetList.add(oldSheetName);
			}
		}

		Collections.sort(sheetList, (o1, o2) -> o1.compareToIgnoreCase(o2));

		final ObservableList<String> list = FXCollections
				.observableArrayList(sheetList.toArray(new String[sheetList
						.size()]));
		this.sheetList.setItems(list);
	}

	public Map<TableColumn<Map<String, String>, String>, Integer> getOldTableColumnsMap() {
		return this.oldTableColumnsMap;
	}

	public Map<TableColumn<Map<String, String>, String>, Integer> getNewTableColumnsMap() {
		return this.newTableColumnsMap;
	}
}
