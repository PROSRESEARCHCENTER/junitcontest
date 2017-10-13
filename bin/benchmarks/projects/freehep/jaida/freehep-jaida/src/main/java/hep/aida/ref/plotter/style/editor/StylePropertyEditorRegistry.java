package hep.aida.ref.plotter.style.editor;
/*
 * StylePropertyEditorRegistry.java
 *
 * Created on June 14, 2005, 7:50 PM
 */

import hep.aida.ref.plotter.RevolvingColorStyleParameter;
import hep.aida.ref.plotter.RevolvingStyleParameter;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.util.logging.Logger;

import org.freehep.application.studio.Studio;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;

public class StylePropertyEditorRegistry extends PropertyEditorRegistry {
    private Logger styleLogger;
    
    /** Creates a new instance of StylePropertyEditorRegistry */
    public StylePropertyEditorRegistry() {
        super();
        styleLogger = Logger.getLogger("hep.aida.ref.plotter.style.editor");
    }
    
    public synchronized PropertyEditor getEditor(Property property) {
        styleLogger.finest("StylePropertyEditorRegistry.getEditor for property: "+property.getName());
        Studio app = (Studio) Studio.getApplication();
        PropertyEditor editor = null;
        if (property instanceof StyleProperty) {
            Object val = ((StyleProperty) property).getValue();
            Object[] options = null;
            if (val instanceof StylePropertyState) options = ((StylePropertyState) val).values;
            
            Class t = ((StyleProperty) property).getType();            
            boolean customEditor = (t == Double.TYPE || t== Float.TYPE || t == Integer.TYPE);
            customEditor = (customEditor || t == Color.class || t == String.class || t == Font.class);
            customEditor = (customEditor || t == RevolvingStyleParameter.class || t == RevolvingColorStyleParameter.class);
            if (customEditor) {
                if (options == null || options.length == 0) {
                    editor = new StringStylePropertyEditor();
                } else {
                    editor = new ComboBoxStylePropertyEditor(); 
                }
            }
        }
        if (editor == null) editor = super.getEditor(property);
        //System.out.println("StylePropertyEditorRegistry.getEditor for property: :::::: Name="+property.getName()+", Type="+property.getType()+", editor="+editor);
        return editor;
    }
}
