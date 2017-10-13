package hep.aida.ref.plotter.style.editor;

import hep.aida.IPlotterStyle;
import hep.aida.ref.xml.AidaStyleXMLWriter;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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

import org.freehep.application.RecentItemTextField;

public class SaveAidaStylePanel extends JPanel {
    private Component parent;
    private RecentItemTextField outputFile = new RecentItemTextField("hep.aida.ref.plotter.style.editor.SaveAidaStylePanel.OutputFile", 15, false);
    private JCheckBox writeSetParameters = new JCheckBox("Write Only Set Parameters");
    private JFileChooser outputChooser = new JFileChooser();
    private JButton outputBrowse = new JButton("Browse...");
    private JPanel thisPanel;
    private IPlotterStyle style;
    
    public SaveAidaStylePanel() {
        this(null, null);
    }
    public SaveAidaStylePanel(Component parent) {
        this(parent, null);
    }
    public SaveAidaStylePanel(IPlotterStyle style) {
        this(null, style);
    }
    public SaveAidaStylePanel(Component parent, IPlotterStyle style) {
        super();
        this.parent = parent;
        this.style = style;
        thisPanel = this;
        initComponents();
    }
    
    private void initComponents() {
        outputFile.setMinWidth(20);

        outputBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = outputChooser.showOpenDialog(thisPanel);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = outputChooser.getSelectedFile();
                    String fileName = file.getAbsolutePath();
                    outputFile.setText(fileName);
                } else {
                    
                }
            }
        });
        
        writeSetParameters.setSelected(true);
        this.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new Insets(2, 2, 2, 2);
        
        gbc.gridy=0; gbc.gridx=0;
        this.add(new JLabel("Output File: "), gbc);
        gbc.gridx=1;
        this.add(outputFile, gbc);
        gbc.gridx=2;
        this.add(outputBrowse, gbc);
        
        gbc.gridy=1; gbc.gridx=1;
        this.add(writeSetParameters, gbc);
        
    }
    
    public void setStyle(IPlotterStyle style) { this.style = style; }
    
    public IPlotterStyle getStyle() { return style; }
    
    public void saveStyle() throws IOException {
        String title = "Select Output File if You Want to Save Style";
        Component comp = parent;
        if (comp == null) comp = (Component) SwingUtilities.getAncestorOfClass(Frame.class, this);
        int reply = JOptionPane.showOptionDialog(comp, this, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
        if (reply == JOptionPane.YES_OPTION) {
            String fileName = outputFile.getText();
            boolean writeAll = !writeSetParameters.isSelected();
            
            AidaStyleXMLWriter.writeToFile(fileName, style, writeAll);
            outputFile.saveState();
        } else {
            return;
        }
    }

}
