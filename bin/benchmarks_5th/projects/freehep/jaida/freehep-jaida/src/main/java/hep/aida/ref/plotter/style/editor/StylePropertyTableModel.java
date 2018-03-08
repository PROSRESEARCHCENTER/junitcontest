package hep.aida.ref.plotter.style.editor;
/*
 * StylePropertyTableModel.java
 *
 * Created on June 13, 2005, 8:00 PM
 */

import hep.aida.IBaseStyle;

import java.beans.PropertyChangeEvent;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;

/**
 *
 * @author  The FreeHEP team @ SLAC
 */
public class StylePropertyTableModel extends PropertySheetTableModel {
    public static final int EDIT_COLUMN = 2;
    public static final String EDIT_COLUMN_TEXT = "Edit...";
    private StylePropertyEditColumnType editColumnValue;
    private IBaseStyle style;
    
    public StylePropertyTableModel(IBaseStyle style) {
        super();
        this.style = style;
        this.editColumnValue = new StylePropertyEditColumnType(EDIT_COLUMN_TEXT);
        setProperties();
    }
    
    public IBaseStyle getStyle() { return style; }
    
    private void setProperties() {
        String[] pars = style.availableParameters();
        if (pars == null) return;
        
        Property[] props = new Property[pars.length];
        for (int i=0; i<pars.length; i++) {
            props[i] = new StyleProperty(style, pars[i]);
        }
        setProperties(props);
    }
    
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        String valueString = null;
        if (value instanceof StylePropertyState) valueString = ((StylePropertyState) value).toString(true);
        else if (value != null) valueString = value.toString()+", Class="+value.getClass().getName();
        //System.out.println("StylePropertyTableModel.setValueAt :: row="+rowIndex+", column="+columnIndex+", Value: "+valueString);
        
        super.setValueAt(value, rowIndex, columnIndex);
        //this.fireTableChanged(new TableModelEvent(this, rowIndex));
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("StylePropertyTableModel.propertyChange :: Name="+evt.getPropertyName()+", newValue="+evt.getNewValue()+", oldValue="+evt.getOldValue());
        super.propertyChange(evt);
    }
    
    
    // Change PropertySheetTableModel methods to work with three columns
    
    public int getColumnCount() {
        return NUM_COLUMNS+1;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        Item item = getPropertySheetElement(rowIndex);
        
        if (columnIndex == EDIT_COLUMN) {
            if (super.getValueAt(rowIndex, VALUE_COLUMN) instanceof StylePropertyState) result = editColumnValue;
            else result = null;
            return result;
        }
        if (item.isProperty()) {
            result = super.getValueAt(rowIndex, columnIndex);
        } else {
            result = item;
        }
        return result;
    }
    
}
