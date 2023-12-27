package org.executequery.gui.browser.comparer;

import org.executequery.databasemediators.DatabaseConnection;
import org.executequery.databaseobjects.NamedObject;
import org.executequery.databaseobjects.impl.DefaultDatabaseHost;
import org.executequery.gui.browser.ColumnConstraint;
import org.executequery.gui.erd.ErdTable;

import java.util.*;
import java.util.stream.Collectors;

import static org.executequery.databaseobjects.NamedObject.*;

public class ErdComparer extends Comparer {

    private List<ColumnConstraint> columnConstraints;
    private Vector<ErdTable> newTables;

    public ErdComparer(DatabaseConnection connection) {
        super(null, connection, new boolean[]{true, true, true, true}, false, false, false);
        columnConstraints = new ArrayList<>();
    }

    public String getCompareErdTables(Vector<ErdTable> newTables) {

        this.newTables=newTables;
        List<NamedObject> newTablesList = newTables.stream().map(e -> e.toNamedObject(new DefaultDatabaseHost(compareConnection))).collect(Collectors.toList());
        List<NamedObject> oldTablesList = getObjects(compareConnection, TABLE);

        for (ErdTable table : newTables)
            columnConstraints.addAll(Arrays.asList(table.getConstraints()));

        List<NamedObject> createObjects = sortObjectsByDependency(createListObjects(oldTablesList, newTablesList, TABLE, false));
        List<NamedObject> dropObjects = sortObjectsByDependency(dropListObjects(oldTablesList, newTablesList, TABLE));
        Map<NamedObject, NamedObject> alterObjects = alterListObjects(oldTablesList, newTablesList, TABLE);

        dropConstraints(true, false, true, true);
        addCreateObjectsToScript(createObjects, TABLE, false);
        addDropObjectsToScript(dropObjects, TABLE);
        addAlterObjectsToScript(alterObjects, TABLE, false);
        createConstraints();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getScript().size(); i++) {
            result.append(getScript(i));
           // result.append((newTables.get(i).getAddConstraintsScript()));
        }

        return result.toString();
    }

    @Override
    public void createConstraints() {

        StringBuilder pKeys = new StringBuilder();
        StringBuilder fKeys = new StringBuilder();

        for (ColumnConstraint cc : columnConstraints) {

            StringBuilder template = new StringBuilder()
                    .append("ALTER TABLE ");

            if(cc.getTable() != null)
                template.append(cc.getTable());
            else{
                for(ErdTable table : newTables){
                    for (ColumnConstraint constraint : table.getConstraints())
                        if(constraint.getName()==cc.getName())
                            template.append(table.getName());
                }
            }

            int type = cc.getType();

            if(type == PRIMARY_KEY){
                pKeys.append(template)
                        .append(" ALTER COLUMN ").append(cc.getColumn())
                        .append(" SET NOT NULL,")
                        .append(" ADD CONSTRAINT ").append(cc.getName())
                        .append(" ").append(cc.getTypeName())
                        .append(" KEY (").append(cc.getColumn()).append(");\n");
            }
            if (type == FOREIGN_KEY) {

                fKeys.append(template)
                        .append(" ADD CONSTRAINT ").append(cc.getName())
                        .append(" ").append(cc.getTypeName())
                        .append(" KEY (").append(cc.getColumn()).append(")")
                        .append(" REFERENCES ").append(cc.getRefTable())
                        .append("(").append(cc.getRefColumn()).append(");\n");
            }
        }
            if (pKeys.length() > 0)
                getScript().add("\n\n--- primary keys ---\n\n" + pKeys);
            if (fKeys.length() > 0)
                getScript().add("\n\n--- foreign keys ---\n\n" + fKeys);

        }

    }
