package fr.viveris.xlsdiff.fxgui;

import java.util.Map;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CellFactory
implements
Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>> {
	@Override
	public TableCell<Map<String, String>, String> call(
			final TableColumn<Map<String, String>, String> param) {
		return new TextFieldTableCell();
	}
}
