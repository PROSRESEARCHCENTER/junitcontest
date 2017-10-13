package hep.aida.ref.plotter.style.editor;

import com.l2fprod.common.beans.editor.StringPropertyEditor;

public class StringStylePropertyEditor extends StringPropertyEditor {
    protected Class valueType;
    protected StylePropertyState oldValue;
    protected StylePropertyState currentValue;
    
    public StringStylePropertyEditor() {
        this(null);
    }
    public StringStylePropertyEditor(Class valueType) {
        super();
        this.valueType = valueType;
        currentValue = new StylePropertyState();
    }
    
    
    public Object getValue() {
        String value = (String) super.getValue();
        if (value != null && value.trim().equals("")) value = null;
        //System.out.println("StringStylePropertyEditor.getValue :: value: "+value);
        currentValue.currentValue = value;
        return new StylePropertyState(currentValue);
    }
    
    public void setValue(Object value) {
        String valueString = null;
        if (value instanceof StylePropertyState) valueString = ((StylePropertyState) value).toString(true);
        else if (value != null) valueString = value.toString();
        //System.out.println("StringStylePropertyEditor.setValue :: equals="+currentValue.equals(value)+", newValue: "+valueString);
        
        if (currentValue.equals(value)) return;
        oldValue = new StylePropertyState(currentValue);
        if (value == null) currentValue.clear();
        else currentValue = (StylePropertyState) value;
        
        super.setValue(currentValue.currentValue);
    }   

}
