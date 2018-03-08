package hep.aida.ref.plotter.style.editor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;

import org.freehep.swing.ColorConverter;

import com.l2fprod.common.beans.editor.ColorPropertyEditor;
import com.l2fprod.common.swing.renderer.ColorCellRenderer;

/**
 * Extends l2fprod ColorPropertyEditor to use FreeHEP
 * color chooser - it has extrat tab to choose color "By Name"
 */

public class ColorStylePropertyEditor extends ColorPropertyEditor {
    private JColorChooser colorChooser;
    private JDialog colorChooserDialog;
    private ActionListener okListener;
    
    public ColorStylePropertyEditor() {
         super();
    }
    
    

    protected void selectColor() {
        if (okListener == null) {
            okListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okAction();
                }
            };
        }
        if (colorChooserDialog == null) {
            String title = "Choose Color";
            colorChooser = ColorConverter.getColorChooser();
            colorChooserDialog = colorChooser.createDialog(editor, title, true, colorChooser, okListener, null);
            colorChooserDialog.pack();
        }
        colorChooserDialog.setVisible(true);
    }
    

    private void okAction() {
        try {
            Color selectedColor = colorChooser.getColor();
            if (selectedColor != null) {
                Color oldColor = (Color) getValue();
                Color newColor = selectedColor;
                setValue(newColor);
                firePropertyChange(oldColor, newColor);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private class ColorStyleCellRenderer extends ColorCellRenderer {
        
        protected String convertToString(Object value) {
            String tmp = null;
            if (value == null) return null;
            
            if (value instanceof Color) tmp = ColorConverter.get((Color) value);
            else tmp = super.convertToString(value);
            return tmp;
        }
    }
}
