package hep.aida.ref.pdf;

import java.util.ArrayList;

import org.freehep.util.Value;

/**
 * The base class for a Variable object.
 * Variables can be dependent, parameters and Functions.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class Variable implements HasName, HasValue, HasUnits {
    
    public static int FUNCTION = 0;
    public static int DEPENDENT = 1;
    public static int PARAMETER = 2;
    
    static int NAME_CHANGING  = 0;
    static int NAME_CHANGED   = 1;
    static int UNITS_CHANGING = 2;
    static int UNITS_CHANGED  = 3;
    static int VALUE_CHANGING = 4;
    static int VALUE_CHANGED  = 5;
    static int RANGE_CHANGED  = 6;
    
    private String name;
    private Units units;
    private int type;
    private Value v = new Value();
    
    private ArrayList listeners = new ArrayList();
    
    public Variable(String name, int type) {
        this(name, type, "");
    }
    
    public Variable(String name, int type, String units) {
        this(name, type, new Units(units) );
    }
    
    public Variable(String name, int type, Units units) {
        if ( type != FUNCTION && type != DEPENDENT && type != PARAMETER )
            throw new IllegalArgumentException("Unknown type "+type);
        this.type = type;
        setName(name);
        setUnits(units);
    }
    
    public void addVariableListener(VariableListener listener) {
        listeners.add(listener);
    }
    
    public void removeVariableListener(VariableListener listener) {
        listeners.remove(listener);
    }
    
    public int type() {
        return type;
    }
    
    public String name() {
        return name;
    }
    
    public void setName(String name) {
        if ( notifyVariableChanging(NAME_CHANGING, name) ) {
            this.name = name;
            notifyVariableChanged(NAME_CHANGED);
        }
        else
            throw new IllegalArgumentException("Cannot change the variable name to "+name);
    }
    
    public Units units() {
        return units;
    }
    
    public void setUnits(Units units) {
        if ( notifyVariableChanging(UNITS_CHANGING, units) ) {
            this.units = units;
            notifyVariableChanged(UNITS_CHANGED);
        }
        else
            throw new IllegalArgumentException("Cannot change the variable units to "+units);
    }
    
    public abstract double value();
    
    public void setValue(double value) {
        v.set(value);
        if ( notifyVariableChanging(VALUE_CHANGING, v) ) {
            setVariableValue(value);
            notifyVariableChanged(VALUE_CHANGED);
        }
        else
            throw new IllegalArgumentException("Cannot change the variable value to "+value);
    }
    
    protected abstract void setVariableValue(double value);

    boolean notifyVariableChanging(int type, Object change) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            VariableListener l = (VariableListener) listeners.get(i);
            if ( type == NAME_CHANGING ) {
                if ( ! l.variableChangingName(this, (String) change) )
                    return false;
            }
            else if ( type == VALUE_CHANGING ) {
                    if ( ! l.variableChangingValue(this, ( (Value) change ).getDouble()) )
                        return false;
            }                         
            else if ( type == UNITS_CHANGING )
                if ( ! l.variableChangingUnits(this, (Units) change) )
                    return false;
        }
        return true;
    }
    
    void notifyVariableChanged(int type) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            VariableListener l = (VariableListener) listeners.get(i);
            if ( type == NAME_CHANGED )
                l.variableChangedName(this);
            else if ( type == VALUE_CHANGED )
                l.variableChangedValue(this);
            else if ( type == UNITS_CHANGED )
                l.variableChangedUnits(this);
            else if ( type == RANGE_CHANGED )
                l.variableChangedRange(this);
        }
    }
    
}
