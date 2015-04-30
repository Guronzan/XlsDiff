package fr.viveris.xlsdiff.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class GridViewDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final Map<String, TableModel> modelMap = new HashMap<>();
	private final JPanel panel = new JPanel();
	private final JButton close = new JButton("Fermer");
	private final JSplitPane splitPane = new JSplitPane();
	private final JScrollPane sheetListPane = new JScrollPane();
	private final JScrollPane rightScrollPane = new JScrollPane();
	private final DefaultListModel<String> listModel = new DefaultListModel<>();
	// private final JList<String> sheetList = new JList<>(this.listModel);
	private final JTable table;

	private Workbook wk1;
	private Workbook wk2;

	private final JSplitPane dataDisplaySplitPane = new JSplitPane();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<String> sheetList = new JList<>(this.listModel);

	public GridViewDialog(final Boolean editable, final String[] args) {
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		this.setBounds(100, 100, 800, 600);
		GUIUtils.centerOnScreen(this);

		if (editable) {
			this.table = new JTable() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(final int row, final int column) {
					// final String virtualName = CONFIG.getTableFromRealName(
					// GridViewDialog.this.list.getSelectedValue())
					// .getVirtualName();
					// if (CHECKOUT_SERVICE.isCheckedOut(virtualName)) {
					// return false;
					// }
					if (column == 0
							|| column == this.columnModel.getColumnCount() - 1) {
						return false;
					}
					return true;
				}
			};
		} else {
			this.table = new JTable() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(final int row, final int column) {
					return false;
				}
			};
		}

		try {
			this.wk1 = openFile(args[0]);
			this.wk2 = openFile(args[1]);
		} catch (final IOException e) {
			log.error("Error while opening Workbook", e);
		}

		final List<Sheet> sheetList = new LinkedList<>();
		for (int sheetIndex = 0; sheetIndex < this.wk1.getNumberOfSheets(); ++sheetIndex) {
			sheetList.add(this.wk1.getSheetAt(sheetIndex));
		}

		Collections.sort(sheetList, new Comparator<Sheet>() {
			@Override
			public int compare(final Sheet o1, final Sheet o2) {
				return o1.getSheetName().compareToIgnoreCase(o2.getSheetName());
			}
		});

		for (final Sheet sheet : sheetList) {
			// this.listModel.addElement(computeSheetSize(sheet));
			this.listModel.addElement(sheet.getSheetName());
		}

		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(this.panel, BorderLayout.SOUTH);

		final GridBagLayout gblPanel = new GridBagLayout();
		gblPanel.columnWidths = new int[] { 0, 0 };
		gblPanel.rowHeights = new int[] { 0, 0, 0 };
		gblPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gblPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		this.panel.setLayout(gblPanel);
		this.sheetList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				GridViewDialog.this.load();
			}
		});

		final GridBagConstraints gbcClose = new GridBagConstraints();
		gbcClose.anchor = GridBagConstraints.EAST;
		gbcClose.gridx = 0;
		gbcClose.gridy = 1;
		this.panel.add(this.close, gbcClose);
		this.close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				GridViewDialog.this.dispose();
			}
		});

		getContentPane().add(this.splitPane, BorderLayout.CENTER);

		this.splitPane.setLeftComponent(this.sheetListPane);

		this.sheetListPane.setViewportView(this.sheetList);
		this.dataDisplaySplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		this.splitPane.setRightComponent(this.dataDisplaySplitPane);
		this.dataDisplaySplitPane.setLeftComponent(this.rightScrollPane);
		this.rightScrollPane.setViewportView(this.table);

		this.dataDisplaySplitPane.setRightComponent(this.scrollPane);

		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (!"tableCellEditor".equals(evt.getPropertyName())
						|| GridViewDialog.this.table.isEditing()) {
					return;
				}

				GridViewDialog.this.updateGridView();
			}
		});

	}

	// private String computeSheetSize(Sheet sheet) {
	// return sheet.getSheetName() + " ("
	// + countDataFromSheet(sheet) + ")";
	// }
	//
	// private int countDataFromSheet(Sheet sheet) {
	// for (final Row row : sheet) {
	// if(!nextLineIsValid(row)){
	// return row.getRowNum()-1;
	// }
	// }
	// }
	//
	// public boolean nextLineIsValid(final Row currentRow) {
	// int headerRowSize = 0;
	// for (final Cell cell : headerRow) {
	// if (StringUtils.isEmpty(getStringCellValue(cell))) {
	// break;
	// }
	// ++headerRowSize;
	// }
	// final Row currentRow = getRow(currentRowNum + 1);
	// if (currentRow == null) {
	// return false;
	// }
	//
	// for (int numColumn = 0; numColumn < headerRowSize; ++numColumn) {
	// if (currentRow.getCell(numColumn, Row.RETURN_BLANK_AS_NULL) == null) {
	// return false;
	// }
	// }
	// return true;
	// }

	private Workbook openFile(final String fileName)
			throws FileNotFoundException, IOException {
		if (fileName.endsWith(".xls")) {
			try (final InputStream stream = new FileInputStream(fileName)) {
				try (final HSSFWorkbook wk = new HSSFWorkbook(stream)) {
					return wk;
				}
			}
		} else {
			try (final InputStream stream = new FileInputStream(fileName)) {
				try (final XSSFWorkbook wk = new XSSFWorkbook(stream)) {
					return wk;
				}
			}
		}
	}

	public String getStringCellValue(final Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return String.valueOf(cell.getDateCellValue());
			} else {
				final String value = String.valueOf(cell.getNumericCellValue());
				if (value.endsWith(".0")) {
					return value.replaceAll("\\.0", "");
				} else {
					return value;
				}

			}
		case Cell.CELL_TYPE_FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return String.valueOf(cell.getDateCellValue());
				} else {
					final String value = String.valueOf(cell
							.getNumericCellValue());
					if (value.endsWith(".0")) {
						return value.replaceAll("\\.0", "");
					} else {
						return value;
					}
				}
			default:
				return cell.getStringCellValue();
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return cell.getStringCellValue();
		}
	}

	private void load() {
		final String sheetName = this.sheetList.getSelectedValue()
				.split(" \\(")[0];
		if (sheetName == null) {
			return;
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		if (this.modelMap.containsKey(sheetName)) {
			this.table.setModel(this.modelMap.get(sheetName));
		} else {
			final DefaultTableModel model = new DefaultTableModel();
			final Sheet sheet = this.wk1.getSheet(sheetName);
			final List<List<Object>> dataList = new LinkedList<>();
			for (final Row row : sheet) {
				if (row.getRowNum() == 0) {
					// Skip header
					continue;
				}
				final List<Object> rowData = new LinkedList<Object>();
				for (final Cell cell : row) {
					rowData.add(getStringCellValue(cell));
				}

				dataList.add(rowData);
			}

			final Row firstRow = sheet.getRow(0);
			for (final Cell cell : firstRow) {
				model.addColumn(getStringCellValue(cell));
			}

			// final SheetData sheet = DAO.select(tableName);
			//
			// for (final Column<?, ?, ?> column : sheet.getTable()) {
			// if (!column.getRealName().equals(
			// com.alstom.designStudio.sydt.db.DAO.LINE_NUMBER)) {
			// model.addColumn(column.getRealName());
			// }
			// }
			//
			// final List<List<Object>> dataList = new LinkedList<>();
			// for (final RowData row : sheet) {
			// final List<Object> rowDataList = new LinkedList<>();
			// for (final Column<?, ?, ?> column : sheet.getTable()) {
			// if (!column.getRealName().equals(
			// com.alstom.designStudio.sydt.db.DAO.LINE_NUMBER)) {
			// if (column.getType().getEnumType() == ColumnTypeEnum.dateType) {
			// final DateContent content = (DateContent) row.get(
			// column).getContent();
			// if (content.getValue() != null) {
			// final DateTime date = new DateTime(
			// DateUtil.getJavaDate(content.getValue()));
			// rowDataList.add(date
			// .toString("yyyy/MM/dd HH:mm:ss"));
			// } else {
			// rowDataList.add("");
			// }
			// } else {
			// rowDataList.add(row.get(column).getContent()
			// .getValue());
			// }
			// }
			// }
			// dataList.add(rowDataList);
			// }
			//
			// Collections.sort(dataList, RowComparator.INSTANCE);
			// for (final List<Object> rowDataList : dataList) {
			// model.addRow(rowDataList.toArray());
			// }
			//
			for (final List<Object> rowDataList : dataList) {
				model.addRow(rowDataList.toArray());
			}
			// model.addRow(dataList.get(0).toArray());
			// model.addRow(dataList.get(1).toArray());
			this.table.setModel(model);
			this.modelMap.put(sheetName, model);
		}
		setCursor(Cursor.getDefaultCursor());

	}

	private void updateGridView() {
		final int lineNumber = GridViewDialog.this.table.getEditingRow();
		final int columnNumber = GridViewDialog.this.table.getEditingColumn();

		final TableModel model = GridViewDialog.this.table.getModel();
		final String tableName = GridViewDialog.this.sheetList
				.getSelectedValue();
		final String columnName = model.getColumnName(columnNumber);
		final String lineNumberName = model.getColumnName(0);
		final Object lineNumberValue = model.getValueAt(lineNumber, 0);
		final Object value = model.getValueAt(lineNumber, columnNumber);

		if (value.toString().isEmpty()) {
			return;
		}

		// Object oldValue = null;
		// try {
		// oldValue = DAO.getValue(tableName, columnName, lineNumberValue);
		// final ColumnType<?, ?> type = CONFIG
		// .getTableFromRealName(tableName)
		// .getColumnFromRealName(columnName).getType();
		//
		// if (type.getEnumType() == ColumnTypeEnum.idRefType) {
		// DATA_BASE_CHECKER.checkIdRef(lineNumberValue, tableName,
		// columnName, value);
		// } else if (type.getEnumType() == ColumnTypeEnum.listType
		// && ((ListType) type).getSubTypeEnum() == ColumnTypeEnum.idRefType) {
		// DATA_BASE_CHECKER.checkListIdRef(lineNumberValue, tableName,
		// columnName, value);
		// }
		// DAO.update(tableName, columnName, value, lineNumberName,
		// lineNumberValue);
		// } catch (final XsdConstraintException | SQLException e) {
		// model.setValueAt(oldValue, lineNumber, columnNumber);
		// GUIUtils.error(e);
		// }
	}

	public static void main(final String[] args) {
		final GridViewDialog gridViewDialog = new GridViewDialog(false, args);
		gridViewDialog.setVisible(true);
	}
}
