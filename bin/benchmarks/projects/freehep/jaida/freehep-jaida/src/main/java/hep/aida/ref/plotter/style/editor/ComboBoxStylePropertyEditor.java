package hep.aida.ref.plotter.style.editor;

import java.awt.Color;

import javax.swing.JComboBox;

import org.freehep.swing.ColorConverter;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ComboBoxStylePropertyEditor extends ComboBoxPropertyEditor {
    protected Object oldValue;
    protected StylePropertyState currentValue;
    
    public ComboBoxStylePropertyEditor() {
        super();
        currentValue = new StylePropertyState();
    }

    
    public Object getValue() {
        String value = null;
        Object tmp = super.getValue();
        if (tmp instanceof String) value = (String) tmp;
        else if (tmp instanceof Color) value = ColorConverter.get((Color) tmp);
        else if (tmp != null) value = tmp.toString();
        if (value != null && value.trim().equals("")) value = null;
        //System.out.println("ComboBoxStylePropertyEditor.getValue :: value: "+value);
        currentValue.currentValue = value;
        return new StylePropertyState(currentValue);
    }
    
    public void setValue(Object value) {
        String valueString = null;
        if (value instanceof StylePropertyState) valueString = ((StylePropertyState) value).toString(true);
        else if (value != null) valueString = value.toString();
        //System.out.println("ComboBoxStylePropertyEditor.setValue :: equals="+currentValue.equals(value)+", newValue: "+valueString);
        
        oldValue = new StylePropertyState(currentValue);
        if (value == null) currentValue.clear();
        else currentValue = (StylePropertyState) value;
        
        JComboBox combo = (JComboBox) editor;
        combo.removeAllItems();
        for (int i=0; i<currentValue.values.length; i++) combo.addItem(currentValue.values[i]);
        super.setValue(currentValue.currentValue);
   }
    
}
