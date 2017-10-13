/*
 * RmiHist1DData.java
 *
 * Created on October 26, 2003, 9:15 PM
 */

package hep.aida.ref.remote.rmi.data;

import java.io.Serializable;

/**
 * This class contains data for ITable
 *
 * @author  serbo
 */

public class RmiTableData implements Serializable {
    
    static final long serialVersionUID = 3538945661122585653L;
    private RmiAnnotationItem[] items = null;
    private Object[][] values = null; // values[row][column]
    private String[] labels = null;
    
    /** Creates a new instance of RmiHist1DData */
    public RmiTableData() {
    }
    
    public RmiTableData(RmiAnnotationItem[] items, Object[][] values, String[] labels) {
        this.items = items;
        this.values = values;
        this.labels = labels;
    }   
    
    // Setters and getters 
    
    public void setAnnotationItems(RmiAnnotationItem[] items) { this.items = items; }
    public RmiAnnotationItem[] getAnnotationItems() { return items; }

    public void setValues(Object[][] values) { this.values = values; }
    public Object[][] getValues() { return values; }
       
    public void setLabels(String[] labels) { this.labels = labels; }
    public String[] getLabels() { return labels; }
       
}
