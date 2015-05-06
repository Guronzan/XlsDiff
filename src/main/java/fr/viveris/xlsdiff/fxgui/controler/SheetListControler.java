package fr.viveris.xlsdiff.fxgui.controler;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.application.Application.Parameters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.viveris.xlsdiff.fxgui.MainWindow;
import fr.viveris.xlsdiff.utils.XlsUtils;

@Slf4j
public class SheetListControler implements Initializable {
    private Workbook                       oldWorkBook;
    private Workbook                       newWorkBook;
    private final Map<Integer, String>     columnMapKeys = new TreeMap<>();

    @FXML
    private ListView<String>               sheetList;

    @FXML
    private TableView<Map<String, String>> oldTableView;
    @FXML
    private TableView<Map<String, String>> newTableView;

    public SheetListControler() {
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        assert this.sheetList != null : "fx:id=\"sheetList\" was not injected: check your FXML file 'mainFrame.fxml'.";

        this.sheetList.setOnMouseClicked(event -> load());

        final Parameters parameters = MainWindow.getInstance().getParameters();
        final Map<String, String> namedParameters = parameters.getNamed();
        final String oldFileName = namedParameters.get("oldFile");
        final String newFileName = namedParameters.get("newFile");

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

    private void load() {
        final String sheetName = this.sheetList.getSelectionModel()
                .getSelectedItem();
        if (sheetName == null) {
            return;
        }
        this.oldTableView.getColumns().clear();
        this.newTableView.getColumns().clear();

        final Sheet oldSheet = this.oldWorkBook.getSheet(sheetName);
        final Sheet newSheet = this.newWorkBook.getSheet(sheetName);

        if (oldSheet != null) {
            final Row firstRow = oldSheet.getRow(0);
            final Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>> cellFactoryForMap = p -> new TextFieldTableCell<>(
                    new StringConverter() {
                        @Override
                        public String toString(final Object t) {
                            if (t != null) {
                                return t.toString();
                            } else {
                                return null;
                            }
                        }

                        @Override
                        public Object fromString(final String string) {
                            return string;
                        }
                    });
            for (final Cell cell : firstRow) {

                final String cellValue = XlsUtils.getStringCellValue(cell);
                final TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(
                        cellValue);
                tableColumn.setCellValueFactory(new MapValueFactory(cellValue));
                tableColumn.setCellFactory(cellFactoryForMap);
                this.oldTableView.getColumns().add(tableColumn);
                this.columnMapKeys.put(cell.getColumnIndex(), cellValue);
            }

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
                    dataRow.put(this.columnMapKeys.get(i),
                            XlsUtils.getStringCellValue(cell));
                }
                data.add(dataRow);
            }
            this.oldTableView.setItems(data);
        }

        if (newSheet != null) {
            final Row firstRow = newSheet.getRow(0);
            final Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>> cellFactoryForMap = p -> new TextFieldTableCell<>(
                    new StringConverter() {
                        @Override
                        public String toString(final Object t) {
                            if (t != null) {
                                return t.toString();
                            } else {
                                return null;
                            }
                        }

                        @Override
                        public Object fromString(final String string) {
                            return string;
                        }
                    });

            for (final Cell cell : firstRow) {

                final String cellValue = XlsUtils.getStringCellValue(cell);
                final TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(
                        cellValue);
                tableColumn.setCellValueFactory(new MapValueFactory(cellValue));
                tableColumn.setCellFactory(cellFactoryForMap);
                this.newTableView.getColumns().add(tableColumn);
                this.columnMapKeys.put(cell.getColumnIndex(), cellValue);
            }

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
                    dataRow.put(this.columnMapKeys.get(i),
                            XlsUtils.getStringCellValue(cell));
                }
                data.add(dataRow);
            }
            this.newTableView.setItems(data);
        }
    }
}
