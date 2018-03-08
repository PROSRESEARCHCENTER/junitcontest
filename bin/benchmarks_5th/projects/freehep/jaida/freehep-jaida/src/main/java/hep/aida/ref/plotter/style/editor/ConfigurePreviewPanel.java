package hep.aida.ref.plotter.style.editor;
/*
 * ConfigurePreviewPanel.java
 *
 * Created on June 22, 2005, 11:06 AM
 */

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.freehep.swing.JSpinBox;

/**
 *
 * @author  serbo
 */
public class ConfigurePreviewPanel extends JPanel {
    
    protected Logger styleLogger;
    
    int previewWidth = 300;
    int previewHeight = 200;
    boolean previewFitRegion = true;
    
    int nEvents1D = 1000;
    int nx1D = 50;
    double xMin1D = 1;
    double xMax1D = 101;
    
    int nEvents2D = 1000;
    int nx2D = 50;
    double xMin2D = 1;
    double xMax2D = 101;
    int ny2D = 50;
    double yMin2D = 1;
    double yMax2D = 101;
    
    int nEventsDps2D = 20;
    double xMinDps2D = 1;
    double xMaxDps2D = 21;
    double yMinDps2D = 10;
    double yMaxDps2D = 50;
    
    
    private JButton advancedButton;
    private JTextField previewWidthField;
    private JTextField previewHeightField;
    private JCheckBox previewFitRegionBox;

    private JPanel previewPanel;
    private JPanel advancedPanel;
    private JPanel hist1DPanel;
    private JPanel hist2DPanel;
    private JPanel dps2DPanel;
    
    private JSpinBox nEvents1DBox;
    private JSpinBox nx1DBox;
    private JTextField xMin1DField;
    private JTextField xMax1DField;
    
    private JSpinBox nEvents2DBox;
    private JSpinBox nx2DBox;
    private JSpinBox ny2DBox;
    private JTextField xMin2DField;
    private JTextField xMax2DField;
    private JTextField yMin2DField;
    private JTextField yMax2DField;
    
    private JSpinBox nEventsDps2DBox;
    private JTextField xMinDps2DField;
    private JTextField xMaxDps2DField;
    private JTextField yMinDps2DField;
    private JTextField yMaxDps2DField;
    

    /** Creates a new instance of ConfigurePreviewPanel */
    public ConfigurePreviewPanel() {
        this(null);
    }
    
    public ConfigurePreviewPanel(StyleEditorPanel editorPanel) {
        super();
        styleLogger = Logger.getLogger("hep.aida.ref.plotter.style.editor");
        init(editorPanel);
    }
    
    public void init(StyleEditorPanel editorPanel) {
        if (advancedPanel == null) makePanel();
        resetPanel(); 
    }
    
    public void validateInput() throws NumberFormatException {
        int i = Integer.parseInt(previewWidthField.getText());
        i = Integer.parseInt(previewHeightField.getText());

        double d = Double.parseDouble(xMin1DField.getText());
        d = Double.parseDouble(xMax1DField.getText());
        
        d = Double.parseDouble(xMin2DField.getText());
        d = Double.parseDouble(xMax2DField.getText());        
        d = Double.parseDouble(yMin2DField.getText());
        d = Double.parseDouble(yMax2DField.getText());
        
        d = Double.parseDouble(xMinDps2DField.getText());
        d = Double.parseDouble(xMaxDps2DField.getText());        
        d = Double.parseDouble(yMinDps2DField.getText());
        d = Double.parseDouble(yMaxDps2DField.getText());
    }
        
    public void readInput() throws NumberFormatException {
        previewWidth = Integer.parseInt(previewWidthField.getText());
        previewHeight = Integer.parseInt(previewHeightField.getText());
        previewFitRegion = previewFitRegionBox.isSelected();

        nEvents1D = nEvents1DBox.getValue();
        nx1D = nx1DBox.getValue();
        xMin1D = Double.parseDouble(xMin1DField.getText());
        xMax1D = Double.parseDouble(xMax1DField.getText());
        
        nEvents2D = nEvents2DBox.getValue();
        nx2D = nx2DBox.getValue();
        ny2D = ny2DBox.getValue();
        xMin2D = Double.parseDouble(xMin2DField.getText());
        xMax2D = Double.parseDouble(xMax2DField.getText());        
        yMin2D = Double.parseDouble(yMin2DField.getText());
        yMax2D = Double.parseDouble(yMax2DField.getText());
        
        nEventsDps2D = nEventsDps2DBox.getValue();
        xMinDps2D = Double.parseDouble(xMinDps2DField.getText());
        xMaxDps2D = Double.parseDouble(xMaxDps2DField.getText());        
        yMinDps2D = Double.parseDouble(yMinDps2DField.getText());
        yMaxDps2D = Double.parseDouble(yMaxDps2DField.getText());        
    }
    
    public void resetPanel() {
        previewWidthField.setText(String.valueOf(previewWidth));
        previewHeightField.setText(String.valueOf(previewHeight));
        previewFitRegionBox.setSelected(previewFitRegion);
                
        previewWidthField.setEnabled(!previewFitRegion);
        previewHeightField.setEnabled(!previewFitRegion);
        
        nEvents1DBox.setValue(nEvents1D);
        nx1DBox.setValue(nx1D);
        xMin1DField.setText(String.valueOf(xMin1D));
        xMax1DField.setText(String.valueOf(xMax1D));

        nEvents2DBox.setValue(nEvents2D);
        nx2DBox.setValue(nx2D);
        ny2DBox.setValue(ny2D);
        xMin2DField.setText(String.valueOf(xMin2D));
        xMax2DField.setText(String.valueOf(xMax2D));
        yMin2DField.setText(String.valueOf(yMin2D));
        yMax2DField.setText(String.valueOf(yMax2D));
        
        nEventsDps2DBox.setValue(nEventsDps2D);
        xMinDps2DField.setText(String.valueOf(xMinDps2D));
        xMaxDps2DField.setText(String.valueOf(xMaxDps2D));
        yMinDps2DField.setText(String.valueOf(yMinDps2D));
        yMaxDps2DField.setText(String.valueOf(yMaxDps2D));
    }
    
    void previewFitRegionAction() {
        boolean fit = previewFitRegionBox.isSelected();
        previewWidthField.setEnabled(!fit);
        previewHeightField.setEnabled(!fit);
    }
    
    private void makePanel() {
        advancedButton = new JButton("Advanced");
        advancedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, advancedButton);
                String title = "Advanced Preview Settings";
                boolean wrong = true;
                while (wrong) {                    
                    int reply = JOptionPane.showOptionDialog(frame, advancedPanel, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
                    if (reply == JOptionPane.YES_OPTION) {
                        try {
                            validateInput();
                            wrong = false;
                        } catch (Exception ex) {
                            String message = "Wrong input! Please correct: ";
                            message += "\n"+ex.getMessage();
                            JOptionPane.showMessageDialog(frame, message);
                        }                        
                    } else {
                        resetPanel();
                        wrong = false;
                    }
                }
            }
        });
     
        previewWidthField = new JTextField(4);
        previewHeightField = new JTextField(4);
        previewFitRegionBox = new JCheckBox("Fit the Preview Region");
        previewFitRegionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                previewFitRegionAction();
            }
        });

        nEvents1DBox = new JSpinBox(nEvents1D, 0, 1000000);
        nx1DBox = new JSpinBox(nx1D, 0, 1000);
        xMin1DField = new JTextField(4);
        xMax1DField = new JTextField(4);
        
        nEvents2DBox = new JSpinBox(nEvents2D, 0, 1000000);
        nx2DBox = new JSpinBox(nx2D, 0, 1000);
        ny2DBox = new JSpinBox(ny2D, 0, 1000);
        xMin2DField = new JTextField(4);
        xMax2DField = new JTextField(4);
        yMin2DField = new JTextField(4);
        yMax2DField = new JTextField(4);
        
        nEventsDps2DBox = new JSpinBox(nEventsDps2D, 0, 1000000);
        xMinDps2DField = new JTextField(4);
        xMaxDps2DField = new JTextField(4);
        yMinDps2DField = new JTextField(4);
        yMaxDps2DField = new JTextField(4);
        
        packPanel();
    }
    
    private void packPanel() {
        advancedPanel = new JPanel();
        advancedPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.Y_AXIS));
        
        hist1DPanel = new JPanel();
        hist1DPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "IHistogram1D"));
        hist1DPanel.setLayout(new java.awt.GridBagLayout());
                
        hist2DPanel = new JPanel();
        hist2DPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "IHistogram2D"));
        hist2DPanel.setLayout(new java.awt.GridBagLayout());
                
        dps2DPanel = new JPanel();
        dps2DPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "IDataPointSet"));
        dps2DPanel.setLayout(new java.awt.GridBagLayout());
                
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        // Histogram 1D
        gbc.gridy=0; gbc.gridx=0;
        gbc.anchor = GridBagConstraints.WEST;
        hist1DPanel.add(new JLabel("N Events"), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        hist1DPanel.add(nEvents1DBox, gbc);
       
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy=1; gbc.gridx=0;
        hist1DPanel.add(new JLabel("N Bins: "), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        hist1DPanel.add(nx1DBox, gbc);
        gbc.gridx=2;
        gbc.anchor = GridBagConstraints.CENTER;
        hist1DPanel.add(new JLabel("X Min:"), gbc);
        gbc.gridx=3;
        hist1DPanel.add(xMin1DField, gbc);
        gbc.gridx=4;
        hist1DPanel.add(new JLabel("X Max:"), gbc);
        gbc.gridx=5;
        hist1DPanel.add(xMax1DField, gbc);
        
        // Histogram 2D
        gbc.gridy=0; gbc.gridx=0;
        gbc.anchor = GridBagConstraints.WEST;
        hist2DPanel.add(new JLabel("N Events"), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        hist2DPanel.add(nEvents2DBox, gbc);
        
        gbc.gridy=1; gbc.gridx=0;
        gbc.anchor = GridBagConstraints.WEST;
        hist2DPanel.add(new JLabel("X:  N Bins: "), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        hist2DPanel.add(nx2DBox, gbc);
        gbc.gridx=2;
        gbc.anchor = GridBagConstraints.CENTER;
        hist2DPanel.add(new JLabel("X Min:"), gbc);
        gbc.gridx=3;
        hist2DPanel.add(xMin2DField, gbc);
        gbc.gridx=4;
        hist2DPanel.add(new JLabel("X Max:"), gbc);
        gbc.gridx=5;
        hist2DPanel.add(xMax2DField, gbc);
        
        gbc.gridy=2; gbc.gridx=0;
        gbc.anchor = GridBagConstraints.WEST;
        hist2DPanel.add(new JLabel("Y:  N Bins: "), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        hist2DPanel.add(ny2DBox, gbc);
        gbc.gridx=2;
        gbc.anchor = GridBagConstraints.CENTER;
        hist2DPanel.add(new JLabel("Y Min:"), gbc);
        gbc.gridx=3;
        hist2DPanel.add(yMin2DField, gbc);
        gbc.gridx=4;
        hist2DPanel.add(new JLabel("Y Max:"), gbc);
        gbc.gridx=5;
        hist2DPanel.add(yMax2DField, gbc);
        
        // DPS 2D
        gbc.gridy=0; gbc.gridx=0;
        gbc.anchor = GridBagConstraints.WEST;
        dps2DPanel.add(new JLabel("N Events"), gbc);
        gbc.gridx=1;
        gbc.anchor = GridBagConstraints.EAST;
        dps2DPanel.add(nEventsDps2DBox, gbc);
        
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy=1; gbc.gridx=2;
        dps2DPanel.add(new JLabel("X Min:"), gbc);
        gbc.gridx=3;
        dps2DPanel.add(xMinDps2DField, gbc);
        gbc.gridx=4;
        dps2DPanel.add(new JLabel("X Max:"), gbc);
        gbc.gridx=5;
        dps2DPanel.add(xMaxDps2DField, gbc);
        
        gbc.gridy=7; gbc.gridx=2;
        dps2DPanel.add(new JLabel("Y Min:"), gbc);
        gbc.gridx=3;
        dps2DPanel.add(yMinDps2DField, gbc);
        gbc.gridx=4;
        dps2DPanel.add(new JLabel("Y Max:"), gbc);
        gbc.gridx=5;
        dps2DPanel.add(yMaxDps2DField, gbc);
        
        advancedPanel.add(hist1DPanel);
        advancedPanel.add(hist2DPanel);
        advancedPanel.add(dps2DPanel);
        
        // Panel with Advanced button
        JPanel panel5 = new JPanel();
        panel5.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel5.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel5.add(advancedButton);
        
        // Panel with preview size
        previewPanel = new JPanel();
	previewPanel.setLayout(new java.awt.GridBagLayout());
        previewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        gbc.gridy=0; gbc.gridx=0;
        previewPanel.add(previewFitRegionBox);
        
        gbc.gridy=1; gbc.gridx=0;
        previewPanel.add(new JLabel("Preview Width: "), gbc);
        gbc.gridx=1;
        previewPanel.add(previewWidthField, gbc);
        gbc.gridx=2;
        previewPanel.add(new JLabel(" pixels"), gbc);
        
        gbc.gridy=2; gbc.gridx=0;
        previewPanel.add(new JLabel("Preview Height: "), gbc);
        gbc.gridx=1;
        previewPanel.add(previewHeightField, gbc);
        gbc.gridx=2;
        previewPanel.add(new JLabel(" pixels"), gbc);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(previewPanel);
        add(panel5);
    }
}
