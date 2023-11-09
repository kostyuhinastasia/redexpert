package org.executequery.gui.browser.comparer;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databasemediators.spi.DefaultStatementExecutor;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.gui.browser.ComparerDBPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static org.executequery.databaseobjects.NamedObject.TABLE;

public class ErdComparer extends Comparer {

    public ErdComparer() {
        super();
    }

    /**
     * this code will throw java.lang.ClassCastException,
     * because parent.getAllComponentsVector() returns {@code Vector<ERDTable>}, but expected {@code Vector<NamedObjet>}
     * <p> todo convert Vector<ERDTable> into Vector<DefaultDatabaseTable> or learn Comparer to work with ERDTable instances
     *
     * @param newTables
     * @param connection
     * @return generated update DB SQL-script
     */
    public String getCompareErdTables(Vector<NamedObject> newTables, DatabaseConnection connection) {

        masterConnection = new DefaultStatementExecutor(connection, true);
        panel = new ComparerDBPanel();

        List<NamedObject> newTablesList = new ArrayList<>(newTables);
        List<NamedObject> oldTablesList = getMasterObjectsList(TABLE);

        List<NamedObject> createObjects = sortObjectsByDependency(createListObjects(TABLE, oldTablesList, newTablesList));
        List<NamedObject> dropObjects = sortObjectsByDependency(dropListObjects(TABLE, oldTablesList, newTablesList));
        Map<NamedObject, NamedObject> alterObjects = alterListObjects(TABLE, oldTablesList, newTablesList);

        dropConstraints(true, false, true, true);
        addCreateObjectsToScript(createObjects, TABLE, false);
        addDropObjectsToScript(dropObjects, TABLE, false);
        addAlterObjectsToScript(alterObjects, TABLE, false);
        createConstraints();
        createComputedFields();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getScript().size(); i++)
            result.append(getScript(i));

        return result.toString();
    }

}
