/*
 * ErdToolBarPalette.java
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

package org.executequery.gui.erd;

import org.executequery.EventMediator;
import org.executequery.GUIUtilities;
import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.datasource.ConnectionManager;
import org.executequery.event.ApplicationEvent;
import org.executequery.event.ConnectionEvent;
import org.executequery.event.ConnectionListener;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.GenerateErdPanel;
import org.executequery.gui.WidgetFactory;
import org.executequery.gui.browser.comparer.ErdComparer;
import org.executequery.gui.editor.QueryEditor;
import org.executequery.localization.Bundles;
import org.executequery.repository.DatabaseConnectionRepository;
import org.executequery.repository.RepositoryCache;
import org.underworldlabs.swing.DynamicComboBoxModel;
import org.underworldlabs.swing.RolloverButton;
import org.underworldlabs.swing.toolbar.PanelToolBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * @author Takis Diakoumis
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ErdToolBarPalette extends PanelToolBar
        implements ActionListener, ConnectionListener {

    private final ErdViewerPanel parent;
    private RolloverButton createTableButton;
    private RolloverButton addTableButton;
    private RolloverButton relationButton;
    private RolloverButton deleteRelationButton;
    private RolloverButton dropTableButton;
    private RolloverButton genScriptsButton;
    private RolloverButton fontStyleButton;
    private RolloverButton lineStyleButton;
    private RolloverButton canvasBgButton;
    private RolloverButton canvasFgButton;
    private RolloverButton erdTitleButton;

    private RolloverButton pushToDatabase;
    private RolloverButton updateFromDatabase;
    private RolloverButton rollbackFromDatabase;

    private JComboBox<DatabaseConnection> connectionsComboBox;

    /**
     * The zoom in button
     */
    private RolloverButton zoomInButton;
    /**
     * The zoom out button
     */
    private RolloverButton zoomOutButton;
    /**
     * The scale combo box
     */
    private JComboBox scaleCombo;
    private DynamicComboBoxModel connectionModel;

    public ErdToolBarPalette(ErdViewerPanel parent) {
        super();
        this.parent = parent;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() {

        connectionsComboBox = WidgetFactory.createComboBox();
        connectionModel = new DynamicComboBoxModel(new Vector<>(ConnectionManager.getActiveConnections()));
        connectionsComboBox.setModel(connectionModel);
        EventMediator.registerListener(this);

        //combosGroup = new TableSelectionCombosGroup(connectionsComboBox);

        List<DatabaseConnection> connections = ((DatabaseConnectionRepository) Objects.requireNonNull(
                RepositoryCache.load(DatabaseConnectionRepository.REPOSITORY_ID)
        )).findAll();

        connectionsComboBox = WidgetFactory.createComboBox();
        for (DatabaseConnection dc : connections)
            connectionsComboBox.addItem(dc);

        String[] scaleValues = ErdViewerPanel.scaleValues;
        scaleCombo = WidgetFactory.createComboBox(scaleValues);
        scaleCombo.setFont(new Font("dialog", Font.PLAIN, 10));
        scaleCombo.setPreferredSize(new Dimension(58, 20));
        scaleCombo.setLightWeightPopupEnabled(false);
        scaleCombo.setSelectedIndex(3);

        dropTableButton = new RolloverButton("/org/executequery/icons/DropTable16.png",
                bundleString("dropTable"));

        relationButton = new RolloverButton("/org/executequery/icons/TableRelationship16.png",
                bundleString("relation"));

        deleteRelationButton = new RolloverButton(
                "/org/executequery/icons/TableRelationshipDelete16.png",
                bundleString("deleteRelation"));

        genScriptsButton = new RolloverButton("/org/executequery/icons/CreateScripts16.png",
                bundleString("genScripts"));

        fontStyleButton = new RolloverButton("/org/executequery/icons/FontStyle16.png",
                bundleString("fontStyle"));

        lineStyleButton = new RolloverButton("/org/executequery/icons/LineStyle16.png",
                bundleString("lineStyle"));

        createTableButton = new RolloverButton("/org/executequery/icons/NewTable16.png",
                bundleString("createTable"));

        addTableButton = new RolloverButton("/org/executequery/icons/AddTable16.png",
                "Add tables from an existing schema");

        canvasBgButton = new RolloverButton("/org/executequery/icons/ErdBackground16.png",
                bundleString("canvasBg"));

        canvasFgButton = new RolloverButton("/org/executequery/icons/ErdForeground16.png",
                bundleString("canvasFg"));

        erdTitleButton = new RolloverButton("/org/executequery/icons/ErdTitle16.png",
                bundleString("erdTitle"));

        zoomInButton = new RolloverButton("/org/executequery/icons/ZoomIn16.png",
                bundleString("zoomIn"));

        zoomOutButton = new RolloverButton("/org/executequery/icons/ZoomOut16.png",
                bundleString("zoomOut"));

        updateFromDatabase = new RolloverButton("/org/executequery/icons/RecycleConnection16.png",
                bundleString("updateFromDatabase"));

        pushToDatabase = new RolloverButton("/org/executequery/icons/Commit16.png",
                bundleString("pushToDatabase"));

        rollbackFromDatabase = new RolloverButton("/org/executequery/icons/Rollback16.png",
                bundleString("rollbackFromDatabase"));

        genScriptsButton.addActionListener(this);
        canvasFgButton.addActionListener(this);
        canvasBgButton.addActionListener(this);
        addTableButton.addActionListener(this);
        createTableButton.addActionListener(this);
        dropTableButton.addActionListener(this);
        lineStyleButton.addActionListener(this);
        fontStyleButton.addActionListener(this);
        relationButton.addActionListener(this);
        deleteRelationButton.addActionListener(this);
        erdTitleButton.addActionListener(this);
        updateFromDatabase.addActionListener(this);
        pushToDatabase.addActionListener(this);
        rollbackFromDatabase.addActionListener(this);
        zoomInButton.addActionListener(this);
        zoomOutButton.addActionListener(this);

        addLabel(bundleString("ConnectionLabel"));
        addComboBox(connectionsComboBox);

        addSeparator();
        addButton(createTableButton);
        addButton(relationButton);
        addButton(deleteRelationButton);
        addButton(dropTableButton);
        addButton(genScriptsButton);

        addSeparator();
        addButton(erdTitleButton);
        addButton(fontStyleButton);
        addButton(lineStyleButton);
        addButton(canvasFgButton);
        addButton(canvasBgButton);

        addSeparator();
        addButton(zoomOutButton);
        addButton(zoomInButton);
        //addComboBox(scaleCombo);

        addSeparator();
        addButton(updateFromDatabase);
        addButton(pushToDatabase);
        addButton(rollbackFromDatabase);
    }

    private void setBackgroundColours(boolean forCanvas) {

        Color currentColour = forCanvas ? parent.getCanvasBackground() : parent.getTableBackground();

        boolean tablesSelected = false;
        ErdTable[] selectedTables = parent.getSelectedTablesArray();
        if (selectedTables != null) {
            tablesSelected = true;
            if (selectedTables.length == 1) {
                currentColour = selectedTables[0].getTableBackground();
            } else {
                // could be different colours in selected tables 
                // so null out the current colour
                currentColour = null;
            }
        }

        Color newColour = JColorChooser.showDialog(parent,
                Bundles.get("LocaleManager.ColorChooser.title"),
                currentColour);

        if (newColour == null) {
            return;
        }

        if (forCanvas) {
            parent.setCanvasBackground(newColour);
        } else {

            if (tablesSelected) {
                Arrays.stream(selectedTables).forEach(selectedTable -> selectedTable.setTableBackground(newColour));
                parent.repaintLayeredPane();
            } else {
                parent.setTableBackground(newColour);
            }
        }
    }

    public void incrementScaleCombo(int num) {

        int index = scaleCombo.getSelectedIndex() + num;

        if (index <= scaleCombo.getComponentCount() - 1) {

            setScaleComboIndex(index);
            parent.setPopupMenuScaleValue(index);
        }

    }

    public void setScaleComboIndex(int index) {

        if (index <= scaleCombo.getComponentCount() - 1) {

            scaleCombo.setSelectedIndex(index);
        }

    }

    public void setScaleComboValue(String value) {
        scaleCombo.setSelectedItem(value);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object btnObject = e.getSource();

        if (btnObject == lineStyleButton) {
            parent.showLineStyleDialog();

        } else if (btnObject == fontStyleButton) {
            parent.showFontStyleDialog();

        } else if (btnObject == createTableButton) {
            new ErdNewTableDialog(parent);

        } else if (btnObject == genScriptsButton) {

            Vector tables = parent.getAllComponentsVector();
            int v_size = tables.size();

            if (v_size == 0) {
                GUIUtilities.displayErrorMessage(bundleString("NoTablesError"));
                return;
            }

            Vector _tables = new Vector(v_size);
            for (int i = 0; i < v_size; i++) {
                _tables.add(tables.elementAt(i));
            }
            new ErdScriptGenerator(_tables, parent);

        } else if (btnObject == addTableButton) {
            new ErdSelectionDialog(parent);

        } else if (btnObject == dropTableButton) {
            parent.removeSelectedTables();

        } else if (btnObject == erdTitleButton) {

            ErdTitlePanel titlePanel = parent.getTitlePanel();
            if (titlePanel != null)
                titlePanel.doubleClicked(null);
            else
                new ErdTitlePanelDialog(parent);

        } else if (btnObject == relationButton) {

            if (parent.getAllComponentsVector().size() <= 1) {
                GUIUtilities.displayErrorMessage(Bundles.get("ErdPopupMenu.needMoreTablesError"));
                return;
            }

            new ErdNewRelationshipDialog(parent);

        } else if (btnObject == deleteRelationButton) {
            ErdTable[] tables = parent.getSelectedTablesArray();

            if (tables.length < 2) {
                return;
            } else if (tables.length > 2) {
                GUIUtilities.displayErrorMessage(bundleString("SelectOnlyTwoTablesError"));
                return;
            }
            new ErdDeleteRelationshipDialog(parent, tables);

        } else if (btnObject == canvasFgButton) {
            setBackgroundColours(false);

        } else if (btnObject == canvasBgButton) {
            setBackgroundColours(true);

        } else if (btnObject == zoomInButton) {
            parent.zoom(true);

        } else if (btnObject == zoomOutButton) {
            parent.zoom(false);

        } else if (btnObject == scaleCombo) {

            int index = scaleCombo.getSelectedIndex();
            switch (index) {
                case 0:
                    parent.setScaledView(0.25);
                    break;
                case 1:
                    parent.setScaledView(0.5);
                    break;
                case 2:
                    parent.setScaledView(0.75);
                    break;
                case 3:
                    parent.setScaledView(1.0);
                    break;
                case 4:
                    parent.setScaledView(1.25);
                    break;
                case 5:
                    parent.setScaledView(1.5);
                    break;
                case 6:
                    parent.setScaledView(1.75);
                    break;
                case 7:
                    parent.setScaledView(2.0);
                    break;
            }
            parent.setPopupMenuScaleValue(index);

        } else if (btnObject == updateFromDatabase) {
            updateFromDatabase();

        } else if (btnObject == pushToDatabase) {
            pushToDatabase();

        } else if (btnObject == rollbackFromDatabase) {
            rollbackFromDatabase();
        }
    }

    private void updateFromDatabase() {

        try {
            GUIUtilities.showWaitCursor();
            BaseDialog dialog = new BaseDialog(GenerateErdPanel.TITLE, false);
            dialog.addDisplayComponentWithEmptyBorder(new GenerateErdPanel(dialog, parent, getSelectedConnection()));
            dialog.setResizable(false);
            dialog.display();
        } finally {
            GUIUtilities.showNormalCursor();
        }

        parent.repaintLayeredPane();
    }

    private DatabaseConnection getSelectedConnection() {
        DatabaseConnection selectedConnection = (DatabaseConnection) connectionsComboBox.getSelectedItem();
        if (selectedConnection != null && !selectedConnection.isConnected())
            ConnectionManager.createDataSource(selectedConnection);

        return selectedConnection;
    }

    private void pushToDatabase() {

        String sqlScript = new ErdComparer().getCompareErdTables(parent.getAllComponentsVector(), getSelectedConnection());
        if (sqlScript == null || sqlScript.isEmpty()) {
            GUIUtilities.displayWarningMessage(bundleString("NothingToExecute"));
            return;
        }

        QueryEditor queryEditor = new QueryEditor(sqlScript);
        queryEditor.setSelectedConnection(getSelectedConnection());
        GUIUtilities.addCentralPane(
                QueryEditor.TITLE, QueryEditor.FRAME_ICON,
                queryEditor, null, true);

        /*
        // or execute in the background using something similar:

        org.underworldlabs.swing.util.SwingWorker worker = new SwingWorker("ERDChangesApply", this) {

            @Override
            public Object construct() {

                DefaultStatementExecutor executor = new DefaultStatementExecutor();
                executor.setDatabaseConnection(getSelectedConnection());
                executor.setKeepAlive(true);
                executor.setCommitMode(false);

                try {
                    executor.execute(sqlScript, true);
                    executor.getConnection().commit();
                    executor.releaseResources();

                } catch (SQLException e) {
                    Log.error(e.getMessage());
                }

                return null;
            }
        };
        worker.start();
        */

    }

    private void rollbackFromDatabase() {

        parent.removeAllTables();
        parent.cleanup();

        updateFromDatabase();
    }

    private String bundleString(String key) {
        return Bundles.get(ErdToolBarPalette.class, key);
    }

    @Override
    public boolean canHandleEvent(ApplicationEvent event) {
        return true;
    }

    @Override
    public void connected(ConnectionEvent connectionEvent) {
        connectionModel.addElement(connectionEvent.getDatabaseConnection());
    }

    @Override
    public void disconnected(ConnectionEvent connectionEvent) {
        connectionModel.removeElement(connectionEvent.getDatabaseConnection());
    }
}
