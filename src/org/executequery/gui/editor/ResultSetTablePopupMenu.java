/*
 * ResultSetTablePopupMenu.java
 *
 * Copyright (C) 2002-2017 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.editor;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.UserPreferencesManager;
import org.executequery.databaseobjects.DatabaseTableObject;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.resultset.*;
import org.executequery.localization.Bundles;
import org.executequery.print.PrintingSupport;
import org.executequery.print.TablePrinter;
import org.underworldlabs.swing.actions.ActionBuilder;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.underworldlabs.swing.menu.MenuItemFactory;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.util.SystemProperties;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;

public class ResultSetTablePopupMenu extends JPopupMenu implements MouseListener {

    private Point lastPopupPoint;

    private final ReflectiveAction reflectiveAction;

    private final ResultSetTable table;

    private final ResultSetTableContainer resultSetTableContainer;

    private boolean doubleClickCellOpensDialog;

    DatabaseTableObject tableObject;

    public ResultSetTablePopupMenu(ResultSetTable table,
                                   ResultSetTableContainer resultSetTableContainer) {
        this(table, resultSetTableContainer, null);
    }

    public ResultSetTablePopupMenu(ResultSetTable table,
                                   ResultSetTableContainer resultSetTableContainer, DatabaseTableObject tableObject) {

        this.tableObject = tableObject;
        this.table = table;
        this.resultSetTableContainer = resultSetTableContainer;

        doubleClickCellOpensDialog = doubleClickCellOpensDialog();
        reflectiveAction = new ReflectiveAction(this);

        // the print sub-menu
        JMenu printMenu = MenuItemFactory.createMenu(bundleString("Print"));
        create(printMenu, bundleString("PrintSelection"), "printSelection");
        create(printMenu, bundleString("PrintTable"), "printTable");

        JCheckBoxMenuItem cellOpensDialog =
                MenuItemFactory.createCheckBoxMenuItem(reflectiveAction);
        cellOpensDialog.setText(bundleString("Double-ClickOpensItemView"));
        cellOpensDialog.setSelected(doubleClickCellOpensDialog());
        cellOpensDialog.setActionCommand("cellOpensDialog");

        add(create(bundleString("CopySelectedCells"), "copySelectedCells"));
        add(create(bundleString("CopySelectedCells-CommaSeparated"), "copySelectedCellsAsCSV"));
        add(create(bundleString("CopySelectedCells-CommaSeparatedWithNames"), "copySelectedCellsAsCSVWithNames"));
        add(create(bundleString("CopySelectedCells-CommaSeparatedAndQuoted"), "copySelectedCellsAsCSVQuoted"));
        add(create(bundleString("CopySelectedCells-CommaSeparatedAndQuotedWithNames"), "copySelectedCellsAsCSVQuotedWithNames"));
        addSeparator();
        add(create(bundleString("SelectRow"), "selectRow"));
        add(create(bundleString("SelectColumn"), "selectColumn"));
        JMenuItem menuItem = create(bundleString("AutoWidthForCols"), "autoWidthForCols");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
        add(menuItem);
        if (resultSetTableContainer != null && resultSetTableContainer.isTransposeAvailable()) {

            add(create(bundleString("TransposeRow"), "transposeRow"));
        }
        addSeparator();
        add(create(bundleString("SetNull"), "setNull"));
        addSeparator();
        add(create(bundleString("ExportSelection"), "exportSelection"));
        add(create(bundleString("ExportTable"), "exportTable"));
        addSeparator();
        if (resultSetTableContainer != null && resultSetTableContainer.isTransposeAvailable()) {
            add(createFromAction("editor-show-hide-rs-columns-command", "Show/hide result set columns"));
            addSeparator();
        }
        add(create(bundleString("View"), "openDataItemViewer"));
        add(printMenu);
        addSeparator();
        add(cellOpensDialog);

    }

    private String bundleString(String key){
        return Bundles.get(ResultSetTablePopupMenu.class,key);
    }

    public void setLastPopupPoint(Point lastPopupPoint) {

        this.lastPopupPoint = lastPopupPoint;
    }

    public void autoWidthForCols(ActionEvent e) {
        if(table.getAutoResizeMode()!=JTable.AUTO_RESIZE_ALL_COLUMNS) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            ((JMenuItem)e.getSource()).setText(bundleString("ColumnWidthByContent"));
        }
        else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            ((JMenuItem)e.getSource()).setText(bundleString("AutoWidthForCols"));
        }

    }

    public void setNull(ActionEvent e) {

        setNullEvent(lastPopupPoint);
    }

    private void setNullEvent(Point point) {

        if(table.hasMultipleColumnAndRowSelections()) {

            int[] selectedColumns = table.getSelectedCellsColumnsIndexes();

            int[] selectedRows = table.getSelectedCellsRowsIndexes();

            for (int i = 0; i < selectedRows.length; i++) {

                for (int j = 0; j < selectedColumns.length; j++) {

                    table.setValueAt(null, selectedRows[i], selectedColumns[j]);
                }
            }
        }
        else{

            table.setValueAt(null, table.getSelectedRow(),table.getSelectedColumn());
        }
    }

    private boolean doubleClickCellOpensDialog() {

        return UserPreferencesManager.doubleClickOpenItemView();
    }

    private JMenuItem create(JMenu menu, String text, String actionCommand) {

        JMenuItem menuItem = create(text, actionCommand);
        menu.add(menuItem);

        return menuItem;
    }

    private JMenuItem createFromAction(String actionId, String toolTipText) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(ActionBuilder.get(actionId));
        menuItem.setToolTipText(toolTipText);
        menuItem.setIcon(null);

        return menuItem;
    }

    private JMenuItem create(String text, String actionCommand) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(reflectiveAction);
        menuItem.setActionCommand(actionCommand);
        menuItem.setText(text);

        return menuItem;
    }

    private RecordDataItem tableCellDataAtPoint(Point point) {

        Object value = table.valueAtPoint(point);
        if (value instanceof RecordDataItem) {

            return (RecordDataItem) value;
        }

        return null;
    }

    private void showViewerForValueAt(Point point) {

        RecordDataItem recordDataItem = tableCellDataAtPoint(point);
        if (recordDataItem != null && !recordDataItem.isDisplayValueNull()) {

            if (recordDataItem instanceof SimpleRecordDataItem) {

                showSimpleRecordDataItemDialog(recordDataItem);

            } else if (recordDataItem instanceof LobRecordDataItem) {

                showLobRecordDataItemDialog(recordDataItem);
            }

        } else if (recordDataItem instanceof LobRecordDataItem) {

            showLobRecordDataItemDialog(recordDataItem);
        }

    }

    private void showSimpleRecordDataItemDialog(RecordDataItem recordDataItem) {

        BaseDialog dialog = new BaseDialog(bundleString("RecordDataItemViewer"), true);
        dialog.addDisplayComponentWithEmptyBorder(
                new SimpleDataItemViewerPanel(dialog, (SimpleRecordDataItem) recordDataItem));
        dialog.display();
    }

    private void showLobRecordDataItemDialog(RecordDataItem recordDataItem) {

        BaseDialog dialog = new BaseDialog(bundleString("LOBRecordDataItemViewer"), true);
        dialog.addDisplayComponentWithEmptyBorder(
                new LobDataItemViewerPanel(dialog, (LobRecordDataItem) recordDataItem, tableObject, ((ResultSetTableModel) ((TableSorter) table.getModel()).getTableModel()).getRowDataForRow(table.getSelectedRow())));
        dialog.display();
    }

    public void cellOpensDialog(ActionEvent e) {

        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();

        doubleClickCellOpensDialog = menuItem.isSelected();
        resultSetTableModel().setCellsEditable(!doubleClickCellOpensDialog);

        SystemProperties.setBooleanProperty(
                Constants.USER_PROPERTIES_KEY,
                "results.table.double-click.record.dialog", doubleClickCellOpensDialog);

        UserPreferencesManager.fireUserPreferencesChanged();
    }

    private ResultSetTableModel resultSetTableModel() {

        TableSorter tableSorter = (TableSorter) table.getModel();

        return (ResultSetTableModel) tableSorter.getReferencedTableModel();
    }

    public void exportSelection(ActionEvent e) {

        TableModel selected = table.selectedCellsAsTableModel();
        if (selected != null) {
            if (tableObject != null)
                new QueryEditorResultsExporter(selected, tableObject.getName());
            else new QueryEditorResultsExporter(selected, null);
        }
    }

    public void transposeRow(ActionEvent e) {

        if (resultSetTableContainer != null) {

            table.selectRow(lastPopupPoint);

            int selectedRow = table.getSelectedRow();

            TableSorter model = (TableSorter) table.getModel();
            resultSetTableContainer.transposeRow(model.getTableModel(), selectedRow);
        }

    }

    public void selectColumn(ActionEvent e) {

        table.selectColumn(lastPopupPoint);
    }

    public void selectRow(ActionEvent e) {

        table.selectRow(lastPopupPoint);
    }

    public void copySelectedCells(ActionEvent e) {

        table.copySelectedCells();
    }

    public void copySelectedCellsAsCSV(ActionEvent e) {

        table.copySelectedCellsAsCSV();
    }

    public void copySelectedCellsAsCSVWithNames(ActionEvent e) {

        table.copySelectedCellsAsCSVWithNames();
    }

    public void copySelectedCellsAsCSVQuoted(ActionEvent e) {

        table.copySelectedCellsAsCSVQuoted();
    }

    public void copySelectedCellsAsCSVQuotedWithNames(ActionEvent e) {

        table.copySelectedCellsAsCSVQuotedWithNames();
    }

    public void exportTable(ActionEvent e) {
        if (tableObject != null)
            new QueryEditorResultsExporter(resultSetTableModel(), tableObject.getName(), tableObject.getColumns());
        else new QueryEditorResultsExporter(resultSetTableModel(), null);
    }

    public void printSelection(ActionEvent e) {

        printResultSet(true);
    }

    public void printTable(ActionEvent e) {

        printResultSet(false);
    }

    public void openDataItemViewer(ActionEvent e) {

        try {

            GUIUtilities.showWaitCursor();
            showViewerForValueAt(lastPopupPoint);

        } finally {

            GUIUtilities.showNormalCursor();
        }
    }

    private void printResultSet(boolean printSelection) {

        JTable printTable = null;

        if (printSelection) {

            TableModel model = table.selectedCellsAsTableModel();

            if (model != null) {

                printTable = new JTable(model);

            } else {

                return;
            }

        } else {

            printTable = table;
        }

        Printable printable = new TablePrinter(printTable, null);
        new PrintingSupport().print(printable, "Red Expert - table");
    }

    public void mousePressed(MouseEvent e) {

        maybeShowPopup(e);
    }

    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() >= 2 && (doubleClickCellOpensDialog || table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()) instanceof LobRecordDataItem)) {

            lastPopupPoint = e.getPoint();
            openDataItemViewer(null);
        }
    }

    public void mouseReleased(MouseEvent e) {

        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {

            lastPopupPoint = e.getPoint();

            if (!table.hasMultipleColumnAndRowSelections()) {

                table.selectCellAtPoint(lastPopupPoint);
            }

            show(e.getComponent(), lastPopupPoint.x, lastPopupPoint.y);

        } else {

            // re-enable cell selection
            table.setColumnSelectionAllowed(true);
            table.setRowSelectionAllowed(true);
        }

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}