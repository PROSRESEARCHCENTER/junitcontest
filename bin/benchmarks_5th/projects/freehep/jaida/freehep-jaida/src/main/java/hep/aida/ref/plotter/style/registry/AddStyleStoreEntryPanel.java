package hep.aida.ref.plotter.style.registry;

import hep.aida.IAnalysisFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ref.xml.AidaStyleXMLReader;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.freehep.application.RecentItemTextField;
import org.freehep.application.studio.Studio;

public class AddStyleStoreEntryPanel extends JPanel {
    private Component parent;
    private String title;
    private JPanel thisPanel;
    private JTextField styleName = new JTextField(15);
    private RecentItemTextField inputFile = new RecentItemTextField("hep.aida.ref.plotter.style.registry.AddStyleStoreEntryPanel.InputFile", 15, false);
    private JFileChooser inputChooser = new JFileChooser();
    private JButton browse;
    private JRadioButton createNew;
    private JRadioButton createFromFile;
    private JComboBox previewType;
    
    public AddStyleStoreEntryPanel() {
        this(null, "Add Style Store Entry");
    }
    public AddStyleStoreEntryPanel(Component parent) {
        this(parent, "Add Style Store Entry");
    }
    public AddStyleStoreEntryPanel(String title) {
        this(null, title);
    }
    public AddStyleStoreEntryPanel(Component parent, String title) {
        super();
        this.parent = parent;
        this.title = title;
        thisPanel = this;
        initComponents();
    }
    
    private void initComponents() {
        Class[] types = new Class[] { hep.aida.IHistogram1D.class, hep.aida.IHistogram2D.class, hep.aida.IDataPointSet.class };
        previewType = new JComboBox(types);
        previewType.setSelectedIndex(0);
        
        browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String fileName = inputFile.getText();
                int returnVal = inputChooser.showOpenDialog(thisPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = inputChooser.getSelectedFile();
                    fileName = file.getAbsolutePath();
                    inputFile.setText(fileName);
                } else {  }
            }
        });
        
        createNew = new JRadioButton("Create New Style ");
        createNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() instanceof JRadioButton) {
                    boolean selected = ((JRadioButton) ev.getSource()).isSelected();
                    inputFile.setEnabled(!selected);
                    browse.setEnabled(!selected);
                }
            }
        });
        
        createFromFile = new JRadioButton("Read Style from File ");
        createFromFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() instanceof JRadioButton) {
                    boolean selected = ((JRadioButton) ev.getSource()).isSelected();
                    inputFile.setEnabled(selected);
                    browse.setEnabled(selected);
                }
            }
        });
        
        ButtonGroup rbg = new ButtonGroup();
        rbg.add(createNew);
        rbg.add(createFromFile);
        createNew.setSelected(true);
        inputFile.setEnabled(false);
        browse.setEnabled(false);
        
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        this.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        this.add(new JLabel("Style Name: "), gbc);
        gbc.gridx=1;
        this.add(styleName, gbc);
        gbc.gridy=1; gbc.gridx=0;
        this.add(createNew, gbc);
        gbc.gridy=2; gbc.gridx=0;
        this.add(createFromFile, gbc);
        gbc.gridx=1;
        this.add(inputFile, gbc);
        gbc.gridx=2;
        this.add(browse, gbc);
        gbc.gridy=3; gbc.gridx=0;
        this.add(new JLabel("Style Preview: "), gbc);
        gbc.gridx=1;
        this.add(previewType, gbc);
        
    }
    
    
    IPlotterStyle createStyleAction() {
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();
        return style;
    }
    
    IPlotterStyle readStyleAction() throws IOException, org.jdom.JDOMException {
        IPlotterStyle style = null;
        File file = null;
        String fileName = inputFile.getText();
        style = AidaStyleXMLReader.restoreFromFile(fileName);
        return style;
    }
    
    public StyleStoreEntry createStoreEntry() throws IOException, org.jdom.JDOMException {
        StyleStoreEntry entry = null;
        Component comp = parent;
        if (comp == null) comp = (Component) SwingUtilities.getAncestorOfClass(Frame.class, this);
        boolean wrongInput = true;
        while (wrongInput) {
            int reply = JOptionPane.showOptionDialog(comp, this, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
            try {
                if (reply == JOptionPane.YES_OPTION) {
                    String sn = styleName.getText();
                    if (sn == null || sn.trim().equals(""))
                        throw new IllegalArgumentException("Style Name can not be Empty, please correct");
                    
                    String fileName = inputFile.getText();
                    IPlotterStyle style = null;
                    if (createNew.isSelected()) style = createStyleAction();
                    else readStyleAction();
                    
                    entry = new StyleStoreEntry(styleName.getText(), style, new JELRule());
                    Class preview = (Class) previewType.getSelectedItem();
                    if (preview != null) entry.setPreviewType(preview);
                    inputFile.saveState();
                }
                wrongInput = false;
            } catch (Exception e) {
                String message = "Error: "+e.getMessage();
                if (parent != null && parent instanceof Studio) ((Studio) parent).error(this, message, e);
                e.printStackTrace();
            }
        }
        return entry;
    }
    
}
