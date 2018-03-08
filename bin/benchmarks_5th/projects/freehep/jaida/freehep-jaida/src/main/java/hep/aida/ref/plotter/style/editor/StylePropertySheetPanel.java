package hep.aida.ref.plotter.style.editor;
/*
 * StylePropertySheetPanel.java
 *
 * Created on June 16, 2005, 11:19 AM
 */

import hep.aida.IBaseStyle;
import hep.aida.ref.plotter.AbstractStyleParameter;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;


/**
 *
 * @author  serbo
 */
public class StylePropertySheetPanel extends PropertySheetPanel {
    private JButton editButton;
    private JPanel editPanel;
    
    private JComboBox parNames = new JComboBox();
    private JTextField parType = new JTextField();
    private JTextField parValue = new JTextField();
    private JComboBox parPossibleValues = new JComboBox();
    
    /** Creates a new instance of StylePropertySheetPanel */
    public StylePropertySheetPanel() {
        super();
        init();
    }
    
    public StylePropertySheetPanel(PropertySheetTable table) {
        super(table);
        init();
    }
    
    void init() {
        editPanel = new JPanel();
        editPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        gbc.gridy=0; gbc.gridx=0;
        editPanel.add(new JLabel("Name: "), gbc);
        gbc.gridx=1;
        editPanel.add(parNames, gbc);
        gbc.gridx=2;
        editPanel.add(new JLabel("Type: "), gbc);
        gbc.gridx=3;
        editPanel.add(parType, gbc);

        gbc.gridx=4;
        editPanel.add(new JLabel("Value: "), gbc);
        gbc.gridx=5;
        editPanel.add(parValue, gbc);

        gbc.gridx=6;
        editPanel.add(new JLabel("Possible Values: "), gbc);
        gbc.gridx=7;
        editPanel.add(parPossibleValues, gbc);

        editButton = new JButton("Edit");
        editButton.setToolTipText("Edit existing parameter or add a new one");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editParameter();
            }
        });
        
        JComponent toolsPanel = (JComponent) this.getComponent(0);
        toolsPanel.add(editButton);
    }
    
    public void editParameter() {
        TableModel tm = this.getTable().getModel();
        if ( !(tm instanceof StylePropertyTableModel) ) return;
        StylePropertyTableModel model = (StylePropertyTableModel) tm;
        
        IBaseStyle style = model.getStyle();
        setEditorPanel(style);
    }
    
    void setEditorPanel(IBaseStyle style) {
        
    }
    
    void setParameterEntry(AbstractStyleParameter par) {
        
    }

}
