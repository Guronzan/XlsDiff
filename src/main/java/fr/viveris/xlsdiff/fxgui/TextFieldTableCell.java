package fr.viveris.xlsdiff.fxgui;

import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

import fr.viveris.xlsdiff.fxgui.controler.SheetListControler;

public class TextFieldTableCell
		extends
		javafx.scene.control.cell.TextFieldTableCell<Map<String, String>, String> {
	private static final SheetListControler SHEET_LIST_CONTROLER = SheetListControler
			.getInstance();

	public TextFieldTableCell() {
		super(new StringConverter<String>() {
			@Override
			public String toString(final String value) {
				if (value != null) {
					return value.toString();
				} else {
					return null;
				}
			}

			@Override
			public String fromString(final String value) {
				return value;
			}

		});
	}

	@Override
	public void updateItem(final String item, final boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			// Get fancy and change color based on data
			// if (item.contains("-")) {
			// setTextFill(Color.RED);
			// setStyle("-fx-background-color: yellow");
			// } else {
			// setTextFill(Color.GREEN);
			// setStyle("");
			// }
			final int rowIndex = getIndex();
			final TableColumn<Map<String, String>, String> currentTableColumn = getTableColumn();

			// Searching the current Table Column in the available maps
			final Map<TableColumn<Map<String, String>, String>, Integer> newTableColumnsMap = SHEET_LIST_CONTROLER
					.getNewTableColumnsMap();
			final Map<TableColumn<Map<String, String>, String>, Integer> oldTableColumnsMap = SHEET_LIST_CONTROLER
					.getOldTableColumnsMap();
			final Integer newColumnId = newTableColumnsMap
					.get(currentTableColumn);
			final Integer oldColumnId = oldTableColumnsMap
					.get(currentTableColumn);
			if (newColumnId == null) {
				// means that we are in the OLD view table
				// Get the cell content from the other view
				boolean found = false;
				for (final Entry<TableColumn<Map<String, String>, String>, Integer> entry : newTableColumnsMap
						.entrySet()) {
					if (!entry.getValue().equals(oldColumnId)) {
						continue;
					}
					found = true;
					// OK so now we have the NEW TableColumn
					final TableColumn<Map<String, String>, String> newTableColumn = entry
							.getKey();
					final String newCellData = newTableColumn
							.getCellData(rowIndex);

					if (!StringUtils.equals(item, newCellData)) {
						// Contents are not equals
						setTextFill(Color.RED);
						setStyle("-fx-background-color: yellow");
					} else {
						// Contents are equals, no special style
						setTextFill(Color.GREEN);
						setStyle("");
					}
				}

				if (!found) {
					// If the column or row doesn't exist in the other view,
					// show it...
					setTextFill(Color.RED);
					setStyle("-fx-background-color: yellow");
				}
			} else {
				// means that we are in the the NEW view table and that
				// Get the cell content from the other view
				boolean found = false;
				for (final Entry<TableColumn<Map<String, String>, String>, Integer> entry : oldTableColumnsMap
						.entrySet()) {
					if (!entry.getValue().equals(newColumnId)) {
						continue;
					}
					found = true;
					// OK so now we have the OLD TableColumn
					final TableColumn<Map<String, String>, String> oldTableColumn = entry
							.getKey();
					final String oldCellData = oldTableColumn
							.getCellData(rowIndex);

					if (!StringUtils.equals(item, oldCellData)) {
						// Contents are not equals
						setTextFill(Color.RED);
						setStyle("-fx-background-color: yellow");
					} else {
						// Contents are equals, no special style
						setTextFill(Color.GREEN);
						setStyle("");
					}
				}
				if (!found) {
					// If the column or row doesn't exist in the other view,
					// show it...
					setTextFill(Color.RED);
					setStyle("-fx-background-color: yellow");
				}
			}
		}
	}
}
