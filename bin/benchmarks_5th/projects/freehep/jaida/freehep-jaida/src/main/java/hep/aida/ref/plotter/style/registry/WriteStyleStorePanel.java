package hep.aida.ref.plotter.style.registry;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.freehep.application.RecentItemTextField;
import org.freehep.application.studio.Studio;

public class WriteStyleStorePanel extends JPanel {
    private Component parent;
    private String title;
    private JPanel thisPanel;
    private RecentItemTextField outputFile = new RecentItemTextField("hep.aida.ref.plotter.style.registry.WriteStyleStorePanel.OutputFile", 15, false);
    private JFileChooser outputChooser = new JFileChooser();
    private JButton browse;
    private JCheckBox writeSetParameters = new JCheckBox("Write Only Set Parameters");
    
    public WriteStyleStorePanel() {
        this(null, "Write Style Store to XML File");
    }
    public WriteStyleStorePanel(Component parent) {
        this(parent, "Add Style Store Entry");
    }
    public WriteStyleStorePanel(String title) {
        this(null, title);
    }
    public WriteStyleStorePanel(Component parent, String title) {
        super();
        this.parent = parent;
        this.title = title;
        thisPanel = this;
        initComponents();
    }
    
    private void initComponents() {
        outputFile.setMinWidth(20);
        
        browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String fileName = outputFile.getText();
                int returnVal = outputChooser.showOpenDialog(thisPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = outputChooser.getSelectedFile();
                    fileName = file.getAbsolutePath();
                    outputFile.setText(fileName);
                } else {  }
            }
        });
        
        writeSetParameters.setSelected(true);
        
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        this.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        this.add(new JLabel(" Output File: "), gbc);
        gbc.gridx=1;
        this.add(outputFile, gbc);
        gbc.gridx=2;
        this.add(browse, gbc);
        gbc.gridy=1; gbc.gridx=1;
        this.add(writeSetParameters, gbc);
    }
    
    public void writeStore(IStyleStore store) throws IOException, org.jdom.JDOMException {
        Component comp = parent;
        if (comp == null) comp = (Component) SwingUtilities.getAncestorOfClass(Frame.class, this);
        boolean wrongInput = true;
        while (wrongInput) {
            int reply = JOptionPane.showOptionDialog(comp, this, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
            try {
                if (reply == JOptionPane.YES_OPTION) {
                    String fileName = outputFile.getText();
                    boolean writeAll = !writeSetParameters.isSelected();
                    StyleStoreXMLWriter.writeToFile(fileName, store, writeAll);
                    outputFile.saveState();
                }
                wrongInput = false;
            } catch (Exception e) {
                String message = "Error: "+e.getMessage();
                if (parent != null && parent instanceof Studio) ((Studio) parent).error(this, message, e);
                e.printStackTrace();
            }
        }
    }
}
