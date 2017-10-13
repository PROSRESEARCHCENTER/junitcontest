package hep.aida.ref.plotter.style.editor;
/*
 * StylePropertyTableModel.java
 *
 * Created on June 13, 2005, 8:00 PM
 */

import hep.aida.IBaseStyle;
import hep.aida.ref.plotter.AbstractStyleParameter;
import hep.aida.ref.plotter.BaseStyle;
import hep.aida.ref.plotter.RevolvingStyleParameter;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.freehep.swing.ColorConverter;

import com.l2fprod.common.propertysheet.DefaultProperty;

/**
 *
 * @author  The FreeHEP team @ SLAC
 */
public class StyleProperty extends DefaultProperty {
    private boolean firstTime = true;
    private BaseStyle style;
    private Logger styleLogger;
    
    StyleProperty(IBaseStyle style, String parameterName) {
        super();
        this.style = (BaseStyle) style;
        styleLogger = Logger.getLogger("hep.aida.ref.plotter.style.editor");
        init(parameterName);
        firstTime = false;
    }
    
    
    // Service methods
    
    void init(String parameterName) {
        setName(parameterName);
        setEditable(true);
        readFromObject(style);
    }
    
    public boolean isParameterSet() {
        return isParameterSet(true);
    }
    public boolean isParameterSet(boolean recursive) {
        boolean ok = false;
        try {
            ok = style.isParameterSet(getName(), recursive);
        } catch (Exception e) { e.printStackTrace(); }
        return ok;
    }
    
    public String[] getAvailableValues() {
        return style.availableParameterOptions(getName());
    }
    
    public AbstractStyleParameter getParameter() {
        return style.parameter(getName());
    }
    
    // Overwrite Property methods
    public void readFromObject(Object object) {
        String parameterName = getName();
        setDisplayName(parameterName);
        
        Class t = style.parameter(parameterName).type();
        
        String val = null;
        if (style.isParameterSet(parameterName, false)) val = style.parameter(parameterName).parValue();
        else val = style.parameterValue(parameterName);
        String def = style.parameter(parameterName).defaultValue();
        String[] options = style.availableParameterOptions(parameterName);
        if (options != null && options.length > 0) {
            setCategory("preset");
        }
        
        int ci = -1;
        int di = -1;
        Object objValue = new StylePropertyState(ci, val, options, di, def, t);
        ((StylePropertyState) objValue).setIndex();
        styleLogger.fine("StyleProperty.readFromObject: Style="+style.name()+", parameter="+parameterName+", value="+val+", type="+t);
        
        if (getParameter() instanceof RevolvingStyleParameter) {
            setCategory("rotating");
        } else if (t == String.class)  {
            setCategory("string");
        } else if (t == Color.class) {
            setCategory("color");
        } else if (t == Font.class) {
            setCategory("font");
        } else if (t == Boolean.TYPE) {
            setCategory("boolean");
            objValue = Boolean.valueOf(val);
        } else if (t == Double.TYPE) {
            setCategory("double");
        } else if (t == Float.TYPE) {
            setCategory("float");
        } else if (t == Integer.TYPE) {
            setCategory("integer");
        } else throw new RuntimeException("**** Unknown type of parameter: name="+parameterName+", type="+t);
        
        setType(t);
        String tmpVal = (objValue == null) ? "null" : objValue.toString();
        String tmpClass = (objValue == null) ? "null" : objValue.getClass().getName();
        styleLogger.fine("ReadFromObject: "+ tmpVal +", Object="+tmpClass);
        setValue(objValue);
    }
    
    public void writeToObject(Object object) {
        styleLogger.fine("StyleProperty.writeToObject: Style="+style.name()+", Object="+object);
    }
    
    public void setValue(Object newValue) {
        Object oldValue = null; //getValue();
        String classString = (newValue == null) ? "null" : newValue.getClass().getName();
        styleLogger.fine("StyleProperty.setValue: Style="+style.name()+", parameter="+getName()+", newValue="+newValue+", oldValue="+oldValue+", Class "+classString);
        //System.out.println("StyleProperty.setValue: Style="+style.name()+", parameter="+getName()+", newValue="+newValue+", oldValue="+oldValue+", Class "+classString);
        
        if (!firstTime) fillStyle(newValue);
        super.setValue(newValue);
    }
    
    void fillStyle(Object newValue) {
        String parameterName = getName();
        Class t = getType();
        String[] options = null;
        
        String oldString = (newValue == null) ? "null" : newValue.getClass().getName();
        String val = null;
        String def = null;
        
        if (newValue == null) val = null;
        else if (newValue instanceof StylePropertyState) {
            val = (String) ((StylePropertyState) newValue).currentValue;
            def = (String) ((StylePropertyState) newValue).defaultValue;
            options = (String[]) ((StylePropertyState) newValue).values;
        } 
        else if (newValue instanceof Boolean) val = newValue.toString();
        else if (newValue instanceof Color)   val = ColorConverter.get((Color) newValue);
        else if (newValue instanceof Double)  val = newValue.toString();
        else if (newValue instanceof Integer) val = newValue.toString();
        else if (newValue instanceof String)  val = (String) newValue;
        else throw new RuntimeException("Unknown return type of parameter: name="+parameterName+", type="+newValue);
        
        String oldValue = style.parameterValue(parameterName);
        styleLogger.fine("StyleProperty.fillStyle: Style="+style.name()+", parameter="+parameterName+", newValue="+val+", oldValue="+oldValue);
        //System.out.println("StyleProperty.fillStyle: Style="+style.name()+", parameter="+parameterName+", newValue="+val+", oldValue="+oldValue);
        
        style.setParameterDefault(parameterName, def);
        if (newValue instanceof StylePropertyState) style.setParameter(parameterName, val, options);
        else style.setParameter(parameterName, val);
    }
    
}