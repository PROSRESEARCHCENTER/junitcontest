package org.freehep.util.parameterdatabase.editor;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTree;

import org.freehep.util.parameterdatabase.selector.Selector;

/**
 * This is a generic editor which allows all subclasses of the Selector class to
 * be edited in a table or tree with a JComboBox.
 */
public class SelectorEditor extends DefaultCellEditor {

    public SelectorEditor() {
        super(new JComboBox());
    }

    /**
     * This sets the initial value for the editor. We override this to ensure
     * that the underlying JComboBox has the correct values loaded into it.
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        // Ensure that the combobox is loaded with all of the possible
        // values.
        JComboBox comboBox = (JComboBox) getComponent();
        Selector selector = (Selector) value;
        selector.initialize(comboBox);

        // Let the superclass take care of the rest.
        return super.getTableCellEditorComponent(table, value, isSelected, row,
                column);
    }

    /**
     * This sets the initial value for the editor. We override this to ensure
     * that the underlying JComboBox has the correct values loaded into it.
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {

        // Ensure that the combobox is loaded with all of the possible
        // values.
        JComboBox comboBox = (JComboBox) getComponent();
        Selector selector = (Selector) value;
        selector.initialize(comboBox);

        // Let the superclass take care of the rest.
        return super.getTreeCellEditorComponent(tree, value, isSelected,
                expanded, leaf, row);
    }
}
