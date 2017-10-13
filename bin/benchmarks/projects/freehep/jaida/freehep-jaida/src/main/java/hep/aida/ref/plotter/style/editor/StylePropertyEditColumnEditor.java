package hep.aida.ref.plotter.style.editor;

import hep.aida.ref.AidaUtils;
import hep.aida.ref.plotter.RevolvingColorStyleParameter;
import hep.aida.ref.plotter.RevolvingStyleParameter;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;

import org.freehep.application.studio.Studio;
import org.freehep.swing.ColorConverter;

public class StylePropertyEditColumnEditor  extends DefaultCellEditor {
    static int minTextFieldLength = 20;
    static int maxTextFieldLength = 80;
    
    protected Studio app;
    protected StylePropertyState oldValue;
    protected StylePropertyState currentValue;
    protected JButton button;
    protected JTextField valueField;
    protected JTextField defaultField;
    protected JTextField optionsField;
    protected JButton valColor;
    protected JButton defColor;
    private JColorChooser colorChooser;
    private JDialog colorChooserDialog;
    private OkListener okListener;
    private boolean addColor;
    
    protected JPanel panel;
    
    protected StylePropertyTable table;
    protected int row;
    protected int column;
    
    public StylePropertyEditColumnEditor(StylePropertyTable table, int row, int column) {
        super(new JCheckBox());
        this.app = (Studio) Studio.getApplication();
        this.table = table;
        this.row = row;
        this.column = column;
        this.currentValue = new StylePropertyState();
        setClickCountToStart(1);
        
        //System.out.println("Editor("+row+", "+column+")");
        
        //Must do this so that editing stops when appropriate.
        button = new JButton(StylePropertyTableModel.EDIT_COLUMN_TEXT);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editValue();
                fireEditingStopped();
            }
        });
        editorComponent = button;
    }
    
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
    
    public Object getCellEditorValue() {
        return "Edit...";
    }
    
    protected void createPanel() {
        if (panel != null) return;
        
        defaultField = new JTextField(minTextFieldLength);
        valueField = new JTextField(minTextFieldLength);
        optionsField = new JTextField(2*minTextFieldLength);
        
        valColor = new JButton("Color");
        valColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Class t = currentValue.type;
                if (t == RevolvingStyleParameter.class || t == RevolvingColorStyleParameter.class) {
                    addColor = true;
                    chooseColor(valueField);
                } else {
                    addColor = false;
                    chooseColor(valueField);
                }
            }
        });
        
        defColor = new JButton("Color");
        defColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Class t = currentValue.type;
                if (t == RevolvingStyleParameter.class || t == RevolvingColorStyleParameter.class) {
                    addColor = true;
                    chooseColor(defaultField);
                } else {
                    addColor = false;
                    chooseColor(defaultField);
                }
            }
        });
        
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        JPanel panel1 = new JPanel();
        panel1.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        panel1.add(new JLabel("Current Value: "), gbc);
        gbc.gridx=1;
        panel1.add(valueField, gbc);
        gbc.gridx=2;
        panel1.add(valColor, gbc);
        gbc.gridy=1; gbc.gridx=0;
        panel1.add(new JLabel("Default Value: "), gbc);
        gbc.gridx=1;
        panel1.add(defaultField, gbc);
        gbc.gridx=2;
        panel1.add(defColor, gbc);
        
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new java.awt.GridBagLayout());
        gbc.gridy=0; gbc.gridx=0;
        panel2.add(new JLabel("Possible Values: "), gbc);
        gbc.gridx=1;
        panel2.add(optionsField, gbc);
        
        panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(panel1);
        panel.add(panel2);
    }
    
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        return editorComponent;
    }
    
    
    // Get current value from the property
    public void getPropertyValue() {
        Object value = table.getValueAt(row, StylePropertyTableModel.VALUE_COLUMN);
        String valueString = null;
        if (value instanceof StylePropertyState) valueString = ((StylePropertyState) value).toString(true);
        else if (value != null) valueString = value.toString();
        //System.out.println("Editor.setValue :: equals="+currentValue.equals(value)+", newValue: "+valueString);
        
        oldValue = new StylePropertyState(currentValue);
        if (value == null) currentValue.clear();
        else currentValue = (StylePropertyState) value;
        
        Class t = currentValue.type;
        if (t == Color.class || t == RevolvingColorStyleParameter.class) {
            valColor.setEnabled(true);
            valColor.setVisible(true);
            defColor.setEnabled(true);
            defColor.setVisible(true);
            currentValue.values = null;
            optionsField.setEditable(false);
        } else if (t == RevolvingStyleParameter.class) {
            valColor.setEnabled(false);
            valColor.setVisible(false);
            defColor.setEnabled(false);
            defColor.setVisible(false);
            currentValue.values = null;
            optionsField.setEditable(false);
        } else {
            valColor.setEnabled(false);
            valColor.setVisible(false);
            defColor.setEnabled(false);
            defColor.setVisible(false);
            optionsField.setEditable(true);
        }
    }
    
    // Set modified value back in the table
    public void setPropertyValue() {
        //System.out.println("Editor.getValue :: "+currentValue.toString(true));
        table.setValueAt(new StylePropertyState(currentValue), row, StylePropertyTableModel.VALUE_COLUMN);
    }
    
    
    // Sets Swing components according to the new currentValue
    public void resetComponents() {
        String valueString = "";
        if (currentValue.values != null) {
            for (int i=0; i<currentValue.values.length; i++) {
                String val = "";
                Object tmp = currentValue.values[i];
                if (tmp instanceof String) val = (String) tmp;
                else if (tmp instanceof Color) val = ColorConverter.get((Color) tmp);
                else if (tmp != null) val = tmp.toString();
                else continue;
                if (val != null && !val.trim().equals("")) valueString += "\"" + val + "\", ";
            }
        }
        optionsField.setText(valueString.trim());
        if (valueString.length() > minTextFieldLength && valueString.length() < maxTextFieldLength) optionsField.setColumns(valueString.length() + 10);
        
        String val = "";
        String def = "";
        if (currentValue != null) {
            Object tmp = currentValue.currentValue;
            if (tmp instanceof String) val = (String) tmp;
            else if (tmp instanceof Color) val = ColorConverter.get((Color) tmp);
            else if (tmp != null) val = tmp.toString();
            
            tmp = currentValue.defaultValue;
            if (tmp instanceof String) def = (String) tmp;
            else if (tmp instanceof Color) def = ColorConverter.get((Color) tmp);
            else if (tmp != null) def = tmp.toString();
        }
        valueField.setText(val);
        defaultField.setText(def);
        if (val.length() > minTextFieldLength && val.length() < maxTextFieldLength) valueField.setColumns(val.length() + 10);
        if (def.length() > minTextFieldLength && def.length() < maxTextFieldLength) defaultField.setColumns(def.length() + 10);
    }
    
    // Sets currentValue after the user input
    public void resetValue() throws Exception {
        String val = null;
        String def = null;
        String[] opt = null;
        
        String tmp = defaultField.getText().trim();
        if (tmp.equals("")) {
            tmp = null;
        }
        def = tmp;
        
        tmp = optionsField.getText().trim();
        String[] strArray = null;
        if (tmp != null && !tmp.equals("")) {
            strArray = AidaUtils.parseString(tmp);
        }
        opt = strArray;
        
        tmp = valueField.getText().trim();
        if (tmp.equals("")) {
            tmp = null;
        }
        val = tmp;
        
        checkInput(val, def, opt);
        
        currentValue.values = opt;
        currentValue.defaultValue = def;
        currentValue.currentValue = val;
    }
    
    
    protected void editValue() {
        if (panel == null) createPanel();
        
        getPropertyValue();
        resetComponents();
        
        String title ="Edit Style Property";
        boolean wrongInput = true;
        while (wrongInput) {
            int reply = JOptionPane.showOptionDialog(button, panel, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
            try {
                if (reply == JOptionPane.YES_OPTION) {
                    oldValue = new StylePropertyState(currentValue);
                    resetValue();
                    resetComponents();
                    setPropertyValue();
                    
                } else {
                    resetComponents();
                }
                wrongInput = false;
            } catch (Exception e) {
                if (app != null) app.error("Wrong input: \n\t"+e.getMessage(), e);
                String message = "Error: "+e.getMessage();
                e.printStackTrace();
            }
            //((AbstractTableModel) table.getModel()).fireTableDataChanged();
        }
    }
    
    // Does type checking for string input
    protected void checkInput(String val, String def, String[] opt) throws Exception {
        Class t = currentValue.type;
        if (t == null || t == String.class || t == Boolean.TYPE) return;
        
        if (t == Double.TYPE) {
            double v, d, o;
            if (val != null) v = Double.parseDouble(val);
            if (def != null) d = Double.parseDouble(def);
            if (opt != null)
                for(int i=0; i<opt.length; i++) o = Double.parseDouble(opt[i]);
        } else if (t == Float.TYPE) {
            float v, d, o;
            if (val != null) v = Float.parseFloat(val);
            if (def != null) d = Float.parseFloat(def);
            if (opt != null)
                for(int i=0; i<opt.length; i++) o = Float.parseFloat(opt[i]);
        } else if (t == Integer.TYPE) {
            int v, d, o;
            if (val != null) v = Integer.parseInt(val);
            if (def != null) d = Integer.parseInt(def);
            if (opt != null)
                for(int i=0; i<opt.length; i++) o = Integer.parseInt(opt[i]);
        } else if (t == Color.class) {
            Color v, d, o;
            if (val != null) v = ColorConverter.get(val);
            if (def != null) d = ColorConverter.get(def);
            if (opt != null)
                for(int i=0; i<opt.length; i++) o = ColorConverter.get(opt[i]);
        }
    }
    
    // Color Chooser
    protected void chooseColor(JTextField field) {
        String title = "Choose Color";
        Color initialColor = null;
        
        if (okListener == null) {
            okListener = new OkListener(field);
        } else {
            okListener.setField(field);
        }
        
        if (colorChooserDialog == null) {
            colorChooser = ColorConverter.getColorChooser();
            colorChooserDialog = colorChooser.createDialog(panel, title, true, colorChooser, okListener, null);
            colorChooserDialog.pack();
        }
        colorChooserDialog.setVisible(true);
    }
    
    protected void okAction(JTextField field) {
        try {
            Color selectedColor = colorChooser.getColor();
            String colorString = ColorConverter.get(selectedColor);
            if (colorString != null && !colorString.trim().equals("")) {
                if (addColor) {
                    String tmp = field.getText();
                    if (tmp == null) tmp = "";
                    tmp += ", \"" + colorString + "\"";
                    field.setText(tmp);
                } else {
                    field.setText(colorString);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private class OkListener implements ActionListener {
        private JTextField field;
        
        OkListener(JTextField field) {
            this.field = field;
        }
        
        private void setField(JTextField field) {
            this.field = field;
        }
        
        public void actionPerformed(ActionEvent e) {
            okAction(field);
        }
    }
    
}
