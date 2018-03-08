package hep.aida.ref.plotter.style.editor;

import java.awt.Color;

import org.freehep.swing.ColorConverter;

public class StylePropertyState {
    Class type = null;
    int currentIndex = -1;
    int defaultIndex = -1;
    Object currentValue = null;
    Object defaultValue = null;
    Object[] values = null;
    
    StylePropertyState() {}
    
    StylePropertyState(Object cv, Object[] st) {
        currentValue = cv;
        values = st;
        setIndex();
    }
    StylePropertyState(int ci, Object cv, Object[] st) {
        this(ci, cv, st, -1, null);
    }
    StylePropertyState(int ci, Object cv, Object[] st, int di, Object dv) {
        this(ci, cv, st, di, dv, null);
    }
    StylePropertyState(int ci, Object cv, Object[] st, int di, Object dv, Class t) {
        type = t;
        currentIndex = ci;
        defaultIndex = di;
        currentValue = cv;
        defaultValue = dv;
        values = st;
    }
    
    StylePropertyState(StylePropertyState state) {
        type = state.type;
        currentIndex = state.currentIndex;
        defaultIndex = state.defaultIndex;
        currentValue = state.currentValue;
        defaultValue = state.defaultValue;
        values = state.values;
    }
    
    void setIndex() {
        int ci = -1;
        int di = -1;
        if (values != null && values.length > 0) {
            for (int i=0; i<values.length; i++) {
                if (currentValue != null && currentValue == values[i]) {
                    ci = i;
                }
                if (defaultValue != null && defaultValue == values[i]) {
                    di = i;
                }
            }
        }
        currentIndex = ci;
        defaultIndex = di;
    }
    
    public void clear() {
        type = null;
        currentIndex = -1;
        defaultIndex = -1;
        currentValue = null;
        defaultValue = null;
        values = null;
    }
    
    public boolean equals(Object state) {
        if (state == null) return false;
        else if (this == state) return true;
        else if (state instanceof StylePropertyState) return toString(true).equals(((StylePropertyState) state).toString(true));
        else return false;
    }
    
    public String toString() {
        return toString(false);
    }
    public String toString(boolean detailed) {
        String tmp = "";
        if (!detailed) {
            Object obj = currentValue;
            if (currentValue == null && defaultValue != null) obj = defaultValue;
            if (obj instanceof String) tmp = (String) obj;
            else if (obj instanceof Color) tmp = ColorConverter.get((Color) obj);
            else if (obj != null) tmp = obj.toString();
            return tmp;
        }
        tmp += "StylePropertyState: type="+type+"\n";
        tmp += "\tCurrentIndex="+currentIndex+"\t DefaultIndex="+defaultIndex+"\n";
        tmp += "\tCurrentValue="+currentValue+"\t DefaultValue="+defaultValue+"\n";
        if (values == null || values.length == 0) tmp += "\t\tValue is Empty\n";
        else for (int i=0; i<values.length; i++) tmp += "\t\tValue "+i+"\t"+values[i]+"\n";
        return tmp;
    }
}

