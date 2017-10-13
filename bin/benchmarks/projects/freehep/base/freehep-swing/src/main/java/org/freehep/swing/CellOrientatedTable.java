package org.freehep.swing;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * This class extends JTable by modifying the lookup of the CellEditors and
 * CellRenderers.  Normally this is done based on the declared class of the
 * column.  However, this behavior is not very convient when the types in a
 * column can vary from row to row--as happens, for example, with property
 * sheets. 
 *
 * @version $Id: CellOrientatedTable.java 8584 2006-08-10 23:06:37Z duns $
 * @author Charles Loomis */
public class CellOrientatedTable 
    extends JTable {

    private boolean tryInterfaces;

    /**
     * Create a CellOrientatedTable which doesn't check the class'
     * interfaces. */     
    public CellOrientatedTable(TableModel dm) {
        this(dm,false);
    }
    
    /**
     * Create a CellOrientatedTable which optionally checks the class'
     * interfaces.  The class hierarchy is checked first, is no more specific
     * editor or renderer is found than one for Object, then the class'
     * implemented interfaces are tried. */
    public CellOrientatedTable(TableModel dm, boolean tryInterfaces) {
        super(dm);
        this.tryInterfaces = tryInterfaces;
    }
    
    public TableCellEditor getCellEditor(int row, int column) {
        Class c = this.getValueAt(row,column).getClass();
        if (c==null) return null;

        // Now try to find one which is more specific.  First do what is
        // normally done and just search the class hierarchy.
        TableCellEditor editor = getDefaultEditor(c);

        // Now try to find one based on the implemented interfaces if this has
        // been requested and the class hierarchy didn't find a more specific
        // one. 
        if (tryInterfaces && editor==getDefaultEditor(Object.class)) {
            Class[] interfaces = c.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                TableCellEditor newEditor = getDefaultEditor(interfaces[i]);
                if (newEditor!=null) {
                    editor = newEditor;
                    break;
                }
            }
        }
        return editor;
    }
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        Class c = this.getValueAt(row,column).getClass();
        if (c==null) return null;

        // Now try to find one which is more specific.  First do what is
        // normally done and just search the class hierarchy.
        TableCellRenderer renderer = getDefaultRenderer(c);

        // Now try to find one based on the implemented interfaces if this has
        // been requested and the class hierarchy didn't find a more specific
        // one. 
        if (tryInterfaces && renderer==getDefaultRenderer(Object.class)) {
            Class[] interfaces = c.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                TableCellRenderer newRenderer = 
                    getDefaultRenderer(interfaces[i]);
                if (newRenderer!=null) {
                    renderer = newRenderer;
                    break;
                }
            }
        }
        return renderer;
    }    
}
