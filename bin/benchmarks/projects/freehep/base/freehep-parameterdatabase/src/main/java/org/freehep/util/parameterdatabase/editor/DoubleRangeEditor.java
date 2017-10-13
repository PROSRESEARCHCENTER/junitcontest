package org.freehep.util.parameterdatabase.editor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freehep.util.parameterdatabase.types.DoubleRange;

/**
 * This allows a DoubleRange to be edited within a table.
 */
public class DoubleRangeEditor extends DefaultCellEditor implements
        ActionListener {

    private JTextField minField;

    private JTextField maxField;

    /**
     * This creates a cell editor for a DoubleRange object.
     */
    public DoubleRangeEditor() {
        super(new JCheckBox());

        editorComponent = makeForm();

        delegate = new EditorDelegate() {

            public void setValue(Object value) {
                DoubleRange range = null;
                if (value instanceof DoubleRange) {
                    range = (DoubleRange) value;
                }
                setState(range);
            }

            public Object getCellEditorValue() {
                return getState();
            }
        };
        minField.addActionListener(this);
        maxField.addActionListener(this);
    }

    public void setState(DoubleRange range) {

        if (range != null) {
            minField.setText(Double.toString(range.getMinimum()));
            maxField.setText(Double.toString(range.getMaximum()));
        } else {
            minField.setText(Double.toString(-Double.MAX_VALUE));
            maxField.setText(Double.toString(Double.MAX_VALUE));
        }
    }

    public DoubleRange getState() {

        DoubleRange retVal = null;

        // Get the strings which define the maximum and minimum values.
        String minString = minField.getText().trim();
        String maxString = maxField.getText().trim();

        try {

            // Now try to turn these strings into doubles.
            double min;
            if (!minString.equals("")) {
                min = Double.parseDouble(minString);
            } else {
                min = -Double.MAX_VALUE;
            }

            double max;
            if (!maxString.equals("")) {
                max = Double.parseDouble(maxString);
            } else {
                max = Double.MAX_VALUE;
            }

            // Everything went OK, so make a new value.
            retVal = new DoubleRange(min, max);

        } catch (NumberFormatException except) {
        }

        return retVal;
    }

    public void actionPerformed(ActionEvent event) {
        if (editorComponent != null && editorComponent instanceof DialogWrapper) {

            // Close the dialog window.
            DialogWrapper wrapper = (DialogWrapper) editorComponent;
            wrapper.hideDialog();
        }

        // Forward the action event to the delegate, so that the value can be
        // harvested and the editing stopped.
        delegate.actionPerformed(event);
    }

    private JComponent makeForm() {

        GridBagConstraints constraints = new GridBagConstraints();

        // Make the JPanel which will hold everything.
        JPanel main = new JPanel();
        main.setLayout(new GridBagLayout());

        // Make the input for the minimum value.
        JLabel label = new JLabel("min.:");
        minField = new JTextField();
        minField.setPreferredSize(new Dimension(50, 15));
        label.setLabelFor(minField);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 0.5;
        main.add(label, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.5;
        main.add(minField, constraints);

        // Make the input for the minimum value.
        label = new JLabel("max.:");
        maxField = new JTextField();
        maxField.setPreferredSize(new Dimension(50, 15));
        label.setLabelFor(maxField);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 0.5;
        main.add(label, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.5;
        main.add(maxField, constraints);

        return new DialogWrapper(main, this);
    }

    /**
     * This class creates a "fake" component for the editor to use. This
     * intercepts the critical method calls and does the appropriate thing for
     * the dialog.
     */
    private class DialogWrapper extends JComponent implements WindowListener {

        private JDialog dialog;

        private ActionListener parent;

        public DialogWrapper(JComponent component, ActionListener parent) {

            this.parent = parent;
            dialog = new JDialog();

            // We should make this a modal dialog. However, this forces the
            // user to click twice on the window to dismiss it. Wonder why?
            // dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(this);
            dialog.setContentPane(component);
        }

        public void validate() {
            super.validate();
            dialog.pack();
            dialog.setVisible(true);
            dialog.validate();
        }

        public void hideDialog() {
            dialog.setVisible(false);
        }

        public void windowClosing(WindowEvent e) {
            parent.actionPerformed(new ActionEvent(this, 0, "WindowClosed"));
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowOpened(WindowEvent e) {
        }

    }
}
