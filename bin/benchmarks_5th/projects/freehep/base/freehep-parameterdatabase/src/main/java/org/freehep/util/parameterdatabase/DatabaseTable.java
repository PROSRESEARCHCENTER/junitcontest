package org.freehep.util.parameterdatabase;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.freehep.swing.CellOrientatedTable;
import org.freehep.util.parameterdatabase.editor.DoubleRangeEditor;
import org.freehep.util.parameterdatabase.editor.SelectorEditor;
import org.freehep.util.parameterdatabase.selector.Selector;
import org.freehep.util.parameterdatabase.types.DoubleRange;

public class DatabaseTable implements TableModel, DatabaseListener {

    private JComponent table;

    /**
     * The object used as the key into the database. Actually here this will be
     * used to generate a ClassIterator which will be used to search the
     * database.
     */
    private Object key = null;

    /**
     * The flag indicating whether or not the local column is shown. The two
     * forms of the table are obtained with a trick--we just add one to the
     * column index if there is no local column.
     */
    private boolean local;

    /**
     * The ClassIterator derived from the key object.
     */
    private ClassIterator classIterator = null;

    /**
     * An array containing a sorted list of names.
     */
    private String[] names = null;

    /**
     * The database which will be searched for parameters.
     */
    ParameterDatabase database;

    /**
     * The list of TableModelListeners.
     */
    protected LinkedList tableModelListeners = new LinkedList();

    /**
     * Create a new table to view the parameters in the database.
     */
    public DatabaseTable(ParameterDatabase database) {
        this(database, true);
    }

    /**
     * Create a new table to view the parameters in the database. If local is
     * true, then this table will show a "local variable" column. If it is
     * false, then the "local variable" column is omitted.
     */
    public DatabaseTable(ParameterDatabase database, boolean local) {

        if (database == null)
            throw new IllegalArgumentException();
        this.local = local;
        this.database = database;
        table = makeForm();
    }

    /**
     * Return a reference to the JComponent which contains the table.
     */
    public JComponent getForm() {
        return table;
    }

    /**
     * Set the object used to search the parameter database.
     */
    public void setKeyObject(Object key) {
        if (this.key != key) {
            this.key = key;

            if (key != null) {

                // Make the ClassIterator for this object.
                classIterator = new ClassIterator(key);

                // Now we must remake the list of names.
                names = database.getCurrentParameterSet(classIterator);
            } else {
                classIterator = null;
                names = null;
            }
            fireTableChanged();
        }
    }

    /**
     * This is called when any parameter of the database is changed. It is
     * important when a parameter is updated programatically so that the user
     * interface reflects the current state of the database.
     */
    public void databaseUpdated() {
        fireTableChanged();
    }

    public void fireTableChanged() {

        // Create the table change event.
        TableModelEvent event = new TableModelEvent(this);

        // Notify all of the listeners.
        Iterator iterator = tableModelListeners.iterator();
        while (iterator.hasNext()) {
            TableModelListener listener = (TableModelListener) iterator.next();
            listener.tableChanged(event);
        }
    }

    private JComponent makeForm() {

        // Make an overall JPanel to hold everything.
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());

        // Make a table with this as its table model.
        JTable table = new CellOrientatedTable(this);
        table.setDefaultEditor(Selector.class, new SelectorEditor());
        table.setDefaultEditor(DoubleRange.class, new DoubleRangeEditor());

        JScrollPane scroll = new JScrollPane(table);
        main.add(scroll, BorderLayout.CENTER);

        return main;
    }

    public void addTableModelListener(TableModelListener listener) {
        if (listener != null)
            tableModelListeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        tableModelListeners.remove(listener);
    }

    public Class getColumnClass(int columnIndex) {

        if (!local)
            columnIndex++;

        Class columnClass;
        switch (columnIndex) {
        case (0):
            columnClass = Boolean.class;
            break;
        case (1):
            columnClass = String.class;
            break;
        case (2):
            columnClass = Object.class;
            break;
        default:
            columnClass = null;
            break;
        }
        return columnClass;
    }

    public int getColumnCount() {
        return (local) ? 3 : 2;
    }

    public String getColumnName(int columnIndex) {

        if (!local)
            columnIndex++;

        String columnName;
        switch (columnIndex) {
        case (0):
            columnName = "Local";
            break;
        case (1):
            columnName = "Parameter";
            break;
        case (2):
            columnName = "Value";
            break;
        default:
            columnName = null;
            break;
        }
        return columnName;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (local) {
            return (columnIndex == 0 || columnIndex == 2) ? true : false;
        } else {
            return (columnIndex == 1);
        }
    }

    public int getRowCount() {

        // The number of rows is just equal to the number of parameter names
        // in the list.
        return (names != null) ? names.length : 0;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        if (!local)
            columnIndex++;

        if (columnIndex == 0) {
            classIterator.reset();
            String name = names[rowIndex];
            if (database.isParameterLocal(name, classIterator)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } else if (columnIndex == 1) {
            return names[rowIndex];
        } else if (columnIndex == 2) {
            classIterator.reset();
            String name = names[rowIndex];
            return database.getParameter(name, classIterator);
        } else {
            return null;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        // We need to be a bit careful with this "local" column trick here. We
        // reuse the column index to pass back into getValueAt(), so we need
        // to keep a local copy of columnIndex.
        int cIndex = columnIndex;
        if (!local)
            cIndex++;

        if (cIndex == 0 && (aValue instanceof Boolean)) {
            boolean makeLocal = ((Boolean) aValue).booleanValue();

            if (makeLocal) {
                String name = names[rowIndex];
                Object value = getValueAt(rowIndex, 2);

                if (value != null) {

                    Object firstObject = null;
                    classIterator.reset();
                    if (classIterator.hasNext()) {
                        firstObject = classIterator.next();
                    }

                    PropertyChangeListener listener = null;
                    if (firstObject instanceof PropertyChangeListener) {
                        listener = (PropertyChangeListener) firstObject;
                    }

                    classIterator.reset();
                    database.addParameter(name, value, classIterator, listener);
                }
            } else {
                String name = names[rowIndex];
                classIterator.reset();
                database.removeParameter(name, classIterator);
            }
        }

        if (cIndex == 2) {

            String name = names[rowIndex];
            Object object = getValueAt(rowIndex, columnIndex);
            Class c = object.getClass();
            if (c.equals(aValue.getClass())) {

                // Check to see if the new value is of the same type as the
                // old value. If so, just set the parameter in the database.
                classIterator.reset();
                database.setParameter(name, aValue, classIterator);

            } else if (aValue instanceof String) {

                // Here we try to reconstruct the object itself from a
                // constructor which takes a String.

                String value = (String) aValue;

                if (object != null) {

                    Object[] parameters = new Object[1];
                    parameters[0] = value;
                    Class[] parameterTypes = new Class[1];
                    parameterTypes[0] = String.class;

                    try {
                        Class objectClass = object.getClass();
                        Constructor constructor = objectClass
                                .getConstructor(parameterTypes);

                        Object newValue = constructor.newInstance(parameters);

                        classIterator.reset();
                        database.setParameter(name, newValue, classIterator);

                    } catch (NoSuchMethodException nsme) {
                        nsme.printStackTrace();
                    } catch (InstantiationException ie) {
                        ie.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
