/*
 * RemoteHistogram1D.java
 *
 * Created on May 28, 2003, 5:38 PM
 */

package hep.aida.ref.remote;

import hep.aida.IPlottable;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.remote.interfaces.ITable;

/**
 * This is implementation of ITable that can not be modified
 * by the user. This is a simple implementation that does not guarantee
 * internal consistency. So extra care should be taken when setting
 * data for this class.
 * This Class is designed to work with the IDevMutableStore, but can
 * work with other Stores - overwrite makeSureDataIsValid() method.
 * Don't forget to call "setDataValid" after you fill new data, as
 * only this method fires events to notify AIDAListeners about change.
 *
 * @author  serbo
 */
public class RemoteTable extends RemoteManagedObject implements ITable, IPlottable {
    
    private Annotation annotation = null;
    private int columns;
    private int rows;
    private Object[][] values; // values[row][column]
    private String[] labels;
    
    /** Creates a new instance of RemoteTable */
    public RemoteTable(String name) {
        this(null, name);
    }
    
    public RemoteTable(IDevMutableStore store, String name) {
        this(store, name, name, 1, 1);
    }
    
    public RemoteTable(IDevMutableStore store, String name, String title, int rows, int columns) {
        super(name);
        aidaType = "hep.aida.ref.remote.interfaces.ITable";
        this.store = store;
        this.columns = columns;
        this.rows = rows;
        annotation = new Annotation();
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,title,true);
        annotation.setFillable(false);
        dataIsValid = false;        
    }
    
    
    // AIDAObservable methods
    protected java.util.EventObject createEvent()
    {
       return new RemoteTableEvent(this);
    }

    
    // Service methods
    
    public void setTreeFolder(String treeFolder) {
        super.setTreeFolder(treeFolder);
        
        boolean flbl = annotation.isFillable();
        if (!flbl) annotation.setFillable(true);
        if (annotation.hasKey(Annotation.fullPathKey)) {
            annotation.setValue(Annotation.fullPathKey, treePath);
        } else {
            annotation.addItem(Annotation.fullPathKey, treePath, true);
        }
        if (!flbl) annotation.setFillable(false);
    }
    
    public Object[][] getValues() {
        return values;
    }
    
    public String[] getLabels() {
        return labels;
    }
    
    public void setValues(Object[][] values) {
        this.values = values;
        this.rows = values.length;
        this.columns = values[0].length;
    }
    
    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    // ITable methods
    public hep.aida.IAnnotation annotation() {
        makeSureDataIsValid();
        return annotation;
    }
    
    public int columnCount() {
        makeSureDataIsValid();
        if (values == null) return 1;
        return columns;
    }    

    public int rowCount() {
        makeSureDataIsValid();
        if (values == null) return 1;
        return rows;
    }
    
    public String title() {
        //makeSureDataIsValid();
        return annotation.value(Annotation.titleKey);
    }
    
    public Object valueAt(int row, int column) {
        makeSureDataIsValid();
        if (values == null) return "No Data Available";
        return values[row][column];
    }
    
    public void setTitle(String title) throws IllegalArgumentException {
        if (!fillable) throw new ReadOnlyException();
        annotation.setFillable(true);
        annotation.setValue(Annotation.titleKey,title);        
        annotation.setFillable(false);
    }
    
    public String columnName(int column) {
        makeSureDataIsValid();
        if (labels == null) return String.valueOf(column);
        return labels[column];
    }
    
    public void setValueAt(Object value, int row, int column) {
        throw new ReadOnlyException();
    } 
}
