package fr.viveris.xlsdiff.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import fr.viveris.xlsdiff.utils.XlsUtils;

@Slf4j
public class GridViewDialog extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Map<String, TableModel> oldModelMap = new HashMap<>();
    private final Map<String, TableModel> newModelMap = new HashMap<>();
    private final JPanel panel = new JPanel();
    private final JButton close = new JButton("Fermer");
    private final JSplitPane splitPane = new JSplitPane();
    private final JScrollPane sheetListPane = new JScrollPane();
    private final JScrollPane rightScrollPane = new JScrollPane();
    private final DefaultListModel<String> sheetListModel = new DefaultListModel<>();
    private final JTable oldTable;
    private final JTable newTable;

    private Workbook oldWorkBook;
    private Workbook newWorkBook;

    private final JSplitPane dataDisplaySplitPane = new JSplitPane();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JList<String> sheetList = new JList<>(this.sheetListModel);

    public GridViewDialog(final Boolean editable, final String[] args) {
        this.setBounds(100, 100, 1400, 1000);
        GUIUtils.centerOnScreen(this);

        // if (editable) {
        // this.newTable = new JTable() {
        // private static final long serialVersionUID = 1L;
        //
        // @Override
        // public boolean isCellEditable(final int row, final int column) {
        // // final String virtualName = CONFIG.getTableFromRealName(
        // // GridViewDialog.this.list.getSelectedValue())
        // // .getVirtualName();
        // // if (CHECKOUT_SERVICE.isCheckedOut(virtualName)) {
        // // return false;
        // // }
        // if (column == 0
        // || column == this.columnModel.getColumnCount() - 1) {
        // return false;
        // }
        // return true;
        // }
        // };
        // } else {
        this.oldTable = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        this.newTable = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(final int row, final int column) {
                return false;
            }
        };
        // }

        try {
            this.oldWorkBook = XlsUtils.openFile(args[0]);
            this.newWorkBook = XlsUtils.openFile(args[1]);
        } catch (final IOException e) {
            log.error("Error while opening Workbook", e);
            return;
        }

        final List<Sheet> sheetList = new LinkedList<>();
        for (int sheetIndex = 0; sheetIndex < this.newWorkBook
                .getNumberOfSheets(); ++sheetIndex) {
            sheetList.add(this.newWorkBook.getSheetAt(sheetIndex));
        }

        Collections.sort(sheetList, new Comparator<Sheet>() {
            @Override
            public int compare(final Sheet o1, final Sheet o2) {
                return o1.getSheetName().compareToIgnoreCase(o2.getSheetName());
            }
        });

        for (final Sheet sheet : sheetList) {
            this.sheetListModel.addElement(sheet.getSheetName());
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
        this.rightScrollPane.setViewportView(this.oldTable);

        this.dataDisplaySplitPane.setRightComponent(this.scrollPane);

        this.scrollPane.setViewportView(this.newTable);

        this.oldTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.oldTable.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (!"tableCellEditor".equals(evt.getPropertyName())
                        || GridViewDialog.this.oldTable.isEditing()) {
                    return;
                }

                GridViewDialog.this.updateGridView();
            }
        });
        this.newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.newTable.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (!"tableCellEditor".equals(evt.getPropertyName())
                        || GridViewDialog.this.oldTable.isEditing()) {
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

    private void load() {
        final String sheetName = this.sheetList.getSelectedValue()
                .split(" \\(")[0];
        if (sheetName == null) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (this.oldModelMap.containsKey(sheetName)) {
            this.oldTable.setModel(this.oldModelMap.get(sheetName));
        } else {
            final DefaultTableModel oldModel = new DefaultTableModel();
            final Sheet oldSheet = this.oldWorkBook.getSheet(sheetName);
            final List<List<Object>> dataList = new LinkedList<>();
            if (oldSheet != null) {
                for (final Row row : oldSheet) {
                    if (row.getRowNum() == 0) {
                        // Skip header
                        continue;
                    }
                    final List<Object> rowData = new LinkedList<>();
                    for (final Cell cell : row) {
                        rowData.add(XlsUtils.getStringCellValue(cell));
                    }

                    dataList.add(rowData);
                }

                final Row firstRow = oldSheet.getRow(0);
                for (final Cell cell : firstRow) {
                    oldModel.addColumn(XlsUtils.getStringCellValue(cell));
                }
                for (final List<Object> rowDataList : dataList) {
                    oldModel.addRow(rowDataList.toArray());
                }
                this.oldTable.setModel(oldModel);
                this.oldModelMap.put(sheetName, oldModel);
            } else {
                this.oldTable.setModel(new DefaultTableModel());
            }
        }

        if (this.newModelMap.containsKey(sheetName)) {
            this.newTable.setModel(this.newModelMap.get(sheetName));
        } else {
            final DefaultTableModel newModel = new DefaultTableModel();
            final Sheet newSheet = this.newWorkBook.getSheet(sheetName);
            final List<List<Object>> dataList = new LinkedList<>();
            for (final Row row : newSheet) {
                if (row.getRowNum() == 0) {
                    // Skip header
                    continue;
                }
                final List<Object> rowData = new LinkedList<>();
                for (final Cell cell : row) {
                    rowData.add(XlsUtils.getStringCellValue(cell));
                }

                dataList.add(rowData);
            }

            final Row firstRow = newSheet.getRow(0);
            for (final Cell cell : firstRow) {
                newModel.addColumn(XlsUtils.getStringCellValue(cell));
            }
            for (final List<Object> rowDataList : dataList) {
                newModel.addRow(rowDataList.toArray());
            }
            this.newTable.setModel(newModel);
            this.newModelMap.put(sheetName, newModel);
        }
        setCursor(Cursor.getDefaultCursor());

    }

    private void updateGridView() {
        final int lineNumber = GridViewDialog.this.oldTable.getEditingRow();
        final int columnNumber = GridViewDialog.this.oldTable
                .getEditingColumn();

        final TableModel model = GridViewDialog.this.oldTable.getModel();
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
