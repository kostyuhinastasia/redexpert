package org.executequery.gui.browser.comparer;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.databaseobjects.impl.DefaultDatabaseHost;
import org.executequery.gui.erd.ErdTable;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import static org.executequery.databaseobjects.NamedObject.TABLE;

public class ErdComparer extends Comparer {

    public ErdComparer(DatabaseConnection connection) {
        super(null, connection, new boolean[]{true, true, false, false}, false, false, false);
    }

    public String getCompareErdTables(Vector<ErdTable> newTables) {

        List<NamedObject> newTablesList = newTables.stream().map(e -> e.toNamedObject(new DefaultDatabaseHost(compareConnection))).collect(Collectors.toList());
        List<NamedObject> oldTablesList = getObjects(compareConnection, TABLE);

        List<NamedObject> createObjects = sortObjectsByDependency(createListObjects(oldTablesList, newTablesList, TABLE, false));
        List<NamedObject> dropObjects = sortObjectsByDependency(dropListObjects(oldTablesList, newTablesList, TABLE));
        Map<NamedObject, NamedObject> alterObjects = alterListObjects(oldTablesList, newTablesList, TABLE);

        dropConstraints(true, false, true, true);
        addCreateObjectsToScript(createObjects, TABLE, false);
        addDropObjectsToScript(dropObjects, TABLE);
        addAlterObjectsToScript(alterObjects, TABLE, false);
        createConstraints();
        createComputedFields();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getScript().size(); i++)
            result.append(getScript(i));

        return result.toString();
    }

}
