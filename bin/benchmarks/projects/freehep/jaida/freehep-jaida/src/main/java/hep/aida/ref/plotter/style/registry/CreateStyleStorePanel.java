package hep.aida.ref.plotter.style.registry;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.freehep.application.RecentItemTextField;
import org.freehep.application.studio.Studio;

public class CreateStyleStorePanel extends JPanel {
    private Component parent;
    private String title;
    private JPanel thisPanel;
    private JPanel dbPanel;
    private JTextField storeName = new JTextField(15);
    private RecentItemTextField inputFile;
    private JFileChooser inputChooser = new JFileChooser();
    private JButton browse;
    private RecentItemTextField inputDB;
    private JTextField inputUser = new JTextField(20);
    private JPasswordField inputPass = new JPasswordField(20);
    private JTextField inputTable  = new JTextField(20);
    private JTextField inputColumn = new JTextField(20);
    private JRadioButton createNew;
    private JRadioButton createFromFile;
    private JRadioButton createFromDB;
    
    public CreateStyleStorePanel() {
        this(null, "Create Style Store");
    }
    public CreateStyleStorePanel(Component parent) {
        this(parent, "Create Style Store");
    }
    public CreateStyleStorePanel(Component parent, String title) {
        this(parent, title, new String[] { XMLStyleStore.TYPE } );
    }
    public CreateStyleStorePanel(String title, String[] types) {
        this(null, title, types);
    }
    public CreateStyleStorePanel(Component parent, String title, String[] types) {
        super();
        this.parent = parent;
        this.title = title;
        thisPanel = this;
        initComponents();
    }
    
    private void initComponents() {
        inputFile = new RecentItemTextField("hep.aida.ref.plotter.style.registry.CreateStyleStorePanel.InputFile", 15, false);
        inputFile.setMinWidth(30);
        if (inputFile.getItemCount() <= 0) {
            inputFile.setText("ftp://ftp.slac.stanford.edu/software/jas/JAS3/XMLStyleStore.xml");
            inputFile.saveState();
        }
        
        inputDB = new RecentItemTextField("hep.aida.ref.plotter.style.registry.CreateStyleStorePanel.InputDB", 15, false);
        inputDB.setMinWidth(30);
        if (inputDB.getItemCount() <= 0) {
            inputDB.setText("jdbc:oracle:thin:@glast-oracle01.slac.stanford.edu:1521:GLASTP");
            inputDB.saveState();
        }
        
        
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
        
        createNew = new JRadioButton("Create New StyleStore.  Name:");
        createNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() instanceof JRadioButton) {
                    boolean selected = ((JRadioButton) ev.getSource()).isSelected();
                    storeName.setEditable(selected);
                    if (selected) {
                        inputDB.setEnabled(!selected);
                        inputFile.setEnabled(!selected);
                        browse.setEnabled(!selected);
                    }
                }
            }
        });
        
        createFromFile = new JRadioButton("Read StyleStore from File ");
        createFromFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() instanceof JRadioButton) {
                    boolean selected = ((JRadioButton) ev.getSource()).isSelected();
                    inputFile.setEnabled(selected);
                    browse.setEnabled(selected);
                    if (selected) {
                        storeName.setEditable(!selected);
                        inputDB.setEnabled(!selected);
                    }
                }
            }
        });
        
        createFromDB = new JRadioButton("Read StyleStore from DB ");
        createFromDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getSource() instanceof JRadioButton) {
                    boolean selected = ((JRadioButton) ev.getSource()).isSelected();
                    inputDB.setEnabled(selected);
                    if (selected) {
                        inputFile.setEnabled(!selected);
                        browse.setEnabled(!selected);
                        storeName.setEditable(!selected);
                    }
                }
            }
        });
      
        ButtonGroup rbg = new ButtonGroup();
        rbg.add(createNew);
        rbg.add(createFromFile);
        rbg.add(createFromDB);
        createNew.setSelected(true);
        inputFile.setEnabled(false);
        inputDB.setEnabled(false);
        browse.setEnabled(false);
        storeName.setEditable(true);
        
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        this.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        this.add(createNew, gbc);
        gbc.gridx=1;
        this.add(storeName, gbc);
        gbc.gridy=1; gbc.gridx=0;
        this.add(createFromFile, gbc);
        gbc.gridx=1;
        this.add(inputFile, gbc);
        gbc.gridx=2;
        this.add(browse, gbc);
        gbc.gridy=2; gbc.gridx=0;
        this.add(createFromDB, gbc);
        gbc.gridx=1;
        this.add(inputDB, gbc);
        
        // Create DB panel with username and password
        inputUser.setText("GLASTGEN");
        //inputPass.setText("");
        inputTable.setText("STYLES");
        inputColumn.setText("STYLE");
        
        dbPanel = new JPanel();
        dbPanel.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        dbPanel.add(new JLabel("User Name: "), gbc);
        gbc.gridx=1;
        dbPanel.add(inputUser, gbc);
        gbc.gridy=1; gbc.gridx=0;
        dbPanel.add(new JLabel("Password: "), gbc);
        gbc.gridx=1;
        dbPanel.add(inputPass, gbc);
        gbc.gridy=2; gbc.gridx=0;
        dbPanel.add(new JLabel("Table: "), gbc);
        gbc.gridx=1;
        dbPanel.add(inputTable, gbc);
        gbc.gridy=3; gbc.gridx=0;
        dbPanel.add(new JLabel("Column: "), gbc);
        gbc.gridx=1;
        dbPanel.add(inputColumn, gbc);
    }
    
    
    IStyleStore createStoreAction() {
        String sn = storeName.getText();
        if (sn == null || sn.trim().equals(""))
            throw new IllegalArgumentException("Store Name can not be Empty, please correct");
        IStyleStore store = new XMLStyleStore(storeName.getText(), XMLStyleStore.TYPE, false);
        return store;
    }
    
    IStyleStore readStoreAction() throws IOException, org.jdom.JDOMException {
        IStyleStore store = null;
        File file = null;
        String fileName = inputFile.getText();
        store = StyleStoreXMLReader.restoreFromFile(fileName);
        return store;
    }
    
    IStyleStore dbStoreAction() throws Exception {
        IStyleStore store = null;
        String dbTitle = "DB Connection";
        String fileName = inputDB.getText();
        int reply = JOptionPane.showOptionDialog(this, dbPanel, dbTitle, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
        if (reply == JOptionPane.YES_OPTION) {
            String user  = inputUser.getText();
            String pass  = inputPass.getText();
            String table = inputTable.getText();
            String col   = inputColumn.getText();
            store = StyleStoreXMLReader.restoreFromDB(fileName, user, pass, table, col);
        }
        return store;
    }
        
    public IStyleStore createStore() throws IOException, org.jdom.JDOMException {
        IStyleStore store = null;
        Component comp = parent;
        if (comp == null) comp = (Component) SwingUtilities.getAncestorOfClass(Frame.class, this);
        boolean wrongInput = true;
        while (wrongInput) {
            int reply = JOptionPane.showOptionDialog(comp, this, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
            try {
                if (reply == JOptionPane.YES_OPTION) {
                    String fileName = inputFile.getText();
                    if (createNew.isSelected()) store = createStoreAction();
                    else if (createFromFile.isSelected()) {
                        store = readStoreAction();
                        inputFile.saveState();
                    } else if (createFromDB.isSelected()) {
                        store = dbStoreAction();
                        inputDB.saveState();
                    }
                    
                }
                wrongInput = false;
            } catch (Exception e) {
                String message = "Error: "+e.getMessage();
                if (parent != null && parent instanceof Studio) ((Studio) parent).error(this, message, e);
                e.printStackTrace();               
            }
        }
        return store;
    }
    
}
