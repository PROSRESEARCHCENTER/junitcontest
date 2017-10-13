package hep.aida.ref.remote.interfaces;

/**
 *
 *  User level interface to the Table
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
import hep.aida.IAnnotation;

public interface ITable {
    
    // Any string that is put in annotation with this key
    // will be displayed on top of the table panel
    public static String tableInfoKey = "tableinfokey";
    
    /**
     * Get the Table's title.
     * @return The Table's title.
     *
     */
    public String title();
    
    /**
     * Set the Table title.
     * @param title The title.
     * @throws      IllegalArgumentException If title cannot be changed.
     *
     */
    public void setTitle(String title) throws IllegalArgumentException;
    
    /**
     * Get the IAnnotation associated with the Table.
     * @return The IAnnotation.
     *
     */
    public IAnnotation annotation();
    
    public int columnCount();
    
    public int rowCount();
    
    public String columnName(int column);
    
    public Object valueAt(int row, int column);
    
    public void setValueAt(Object value, int row, int column);
    
}

