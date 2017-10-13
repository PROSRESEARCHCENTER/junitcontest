package hep.aida.ref.plotter.style.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.freehep.swing.ColorConverter;

import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.renderer.ColorCellRenderer;

public class StylePropertyTable extends PropertySheetTable {
    
    public StylePropertyTable(StylePropertyTableModel dm) {
        super(dm);
        this.getColumnModel().getColumn(StylePropertyTableModel.EDIT_COLUMN).setPreferredWidth(60);
        this.getColumnModel().getColumn(StylePropertyTableModel.EDIT_COLUMN).setMaxWidth(60);
    }
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer tcr = null;
        Object value = getValueAt(row, StylePropertyTableModel.VALUE_COLUMN);
        if (column == StylePropertyTableModel.EDIT_COLUMN) {
            if (value instanceof StylePropertyState) {
                DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
                cr.setBackground(Color.yellow);
                cr.setAlignmentX(0.5f);
                return cr;
            } else tcr =  super.getCellRenderer(row, column);
        } else if (column == StylePropertyTableModel.VALUE_COLUMN) {
            if (value instanceof StylePropertyState && ((StylePropertyState) value).type == Color.class) {
                tcr = new ColorCellRenderer() {
                    protected String convertToString(Object value) {
                        Color c = null;
                        if (value == null) {
                            return null;
                        } else if (value instanceof StylePropertyState) {
                            try {
                                Object val = ((StylePropertyState) value).currentValue;
                                if (val == null) val = ((StylePropertyState) value).defaultValue;
                                if (val == null) return null;
                                c = ColorConverter.get( (String) val);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            c = (Color)value;
                        }
                        return ColorConverter.get(c);
                    }
                    
                    protected Icon convertToIcon(Object value) {
                        if (value == null) {
                            return null;
                        } else if (value instanceof StylePropertyState) {
                            Color c = null;
                            try {
                                Object val = ((StylePropertyState) value).currentValue;
                                if (val == null) val = ((StylePropertyState) value).defaultValue;
                                if (val == null) return null;
                                c = ColorConverter.get( (String) val);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return super.convertToIcon(c);
                        } else return super.convertToIcon(value);
                    }
                    
                };
            } else tcr = super.getCellRenderer(row, column);
        } else if (column == StylePropertyTableModel.NAME_COLUMN) {
            tcr = super.getCellRenderer(row, column);
            return new StyleTableCellRenderer(tcr);
        }
        return tcr;
    }
    
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == StylePropertyTableModel.EDIT_COLUMN) {
            if (getValueAt(row, StylePropertyTableModel.VALUE_COLUMN) instanceof StylePropertyState)
                return new StylePropertyEditColumnEditor(this, row, column);
            else return null;
        }
        return super.getCellEditor(row, column);
    }
    
    class StyleTableCellRenderer implements TableCellRenderer {
        private TableCellRenderer tcr;
        StyleTableCellRenderer(TableCellRenderer tcr) {
            this.tcr = tcr;
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = tcr.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            Font font = c.getFont();
            String propertyName = null;
            if (font != null) {
                boolean normal = !font.isItalic();
                String name = font.getName();
                int size = font.getSize();
                int style = Font.PLAIN;
                boolean isSet = false;
                boolean isSetHere = false;
                StyleProperty sp = null;
                if (value instanceof StyleProperty) sp = (StyleProperty) value;
                else if (value instanceof Item) sp = (StyleProperty) ((Item) value).getProperty();
                
                if (sp != null) {
                    isSet = sp.isParameterSet(true);
                    isSetHere = sp.isParameterSet(false);
                    propertyName = sp.getName();
                }
                
                if (isSetHere) {
                    style = Font.PLAIN;
                    if (isSelected) c.setForeground(Color.white);
                    else c.setForeground(Color.black);
                } else if (isSet) {
                    style = Font.PLAIN;
                    if (isSelected) c.setForeground(Color.green);
                    else c.setForeground(Color.blue);
                } else {
                    style = Font.ITALIC;
                    if (isSelected) c.setForeground(Color.yellow);
                    else c.setForeground(Color.red);
                }
                //System.out.println("StyleTableCellRenderer :: name="+propertyName+", isSetHere="+isSetHere+", isSet="+isSet+", Color="+c.getForeground()+", Column="+column+", Value="+value);
                Font newFont = new Font(name, style, size);
                c.setFont(newFont);
            }
            return c;
        }
    }
    
}

