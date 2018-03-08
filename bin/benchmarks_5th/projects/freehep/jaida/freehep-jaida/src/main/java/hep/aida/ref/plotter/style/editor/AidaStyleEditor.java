package hep.aida.ref.plotter.style.editor;
/*
 * AidaStyleEditor.java
 *
 * Created on June 13, 2005, 3:08 PM
 */

import hep.aida.IAnalysisFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ref.xml.AidaStyleXMLReader;
import hep.aida.ref.xml.AidaStyleXMLWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;

/**
 *
 * @author  The FreeHEP team @ SLAC
 */
public class AidaStyleEditor extends JFrame {
    JFrame frame;
    private JTabbedPane tabbedPanel;
    private Class previewType;
    private ConfigurePreviewPanel configurePanel;
    
    private JFileChooser inputChooser = new JFileChooser();
    private JTextField outputFile = new JTextField(30);
    private JCheckBox writeSetParameters = new JCheckBox("Write Only Set Parameters");
    private JFileChooser outputChooser = new JFileChooser();
    private JPanel outputPanel;
    private JButton outputBrowse;

    private int newStyleCounter = 0;
    private Hashtable files = new Hashtable();
    
    private Image homeImage;
    private JPanel homePanel;
    
    private Logger styleLogger;
    private Level logLevel;
    
    // Set Logger and Handler level and Formatter
    public static void setLoggerLevel(Logger logger, Level l) {
        final String lineSeparator = System.getProperty("line.separator", "\n");
        logger.setLevel(l);
        Handler[] handlers = Logger.getLogger( "" ).getHandlers();
        for ( int index = 0; index < handlers.length; index++ ) {
            handlers[index].setLevel(l);
            
            handlers[index].setFormatter( new SimpleFormatter() {
                public String format(LogRecord record) {
                    long millis = record.getMillis()%1000;
                    String tmp = super.format(record);
                    tmp = tmp.replaceFirst(lineSeparator, " ["+millis+"] :: \t");
                    return tmp;
                }
            } );
            
        }
        logger.fine("Set verbose level to: "+logger.getLevel());
    }
    
    
    /** Creates a new instance of AidaStyleEditor */
    public AidaStyleEditor() {
        this("AIDA Style Editor");
    }
    public AidaStyleEditor(String frameName) {
        super(frameName);
        styleLogger = Logger.getLogger("hep.aida.ref.plotter.style.editor");
        logLevel = Level.INFO;
        setLoggerLevel(styleLogger, logLevel);
        frame = this;
        init();
        
        pack();
        setSize(700, 600);
        addHomePage();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (d.width-getSize().width )/2, (d.height-getSize().height )/2 );
        setVisible(true);
    }
    
    private void init() {
        tabbedPanel = new JTabbedPane();
        configurePanel = new ConfigurePreviewPanel();
        
        createMenus();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", tabbedPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void createMenus() {
        // FILE Menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem item = new JMenuItem("New Style");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newStyleAction();
            }
        });
        fileMenu.add(item);
        
        item = new JMenuItem("Open Style...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                readStyleAction();
            }
        });
        fileMenu.add(item);
        
        item = new JMenuItem("Save Style As...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveStyleAsAction();
            }
        });
        fileMenu.add(item);
        
        item = new JMenuItem("Close Style");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeSelectedTabAction();
            }
        });
        fileMenu.add(item);
        
        
        fileMenu.addSeparator();
        item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitAction();
            }
        });
        fileMenu.add(item);
        
        
        // VIEW Menu
        JMenu viewMenu = new JMenu("View");
        JMenu previewMenu = new JMenu("Set Preview Type");
        viewMenu.add(previewMenu);
        
        // Set preview type here
        JRadioButtonMenuItem rbItem = null;
        ButtonGroup rbGroup = new ButtonGroup();
        Class[] possibleTypes = StylePreviewCreator.getPossiblePreviewTypes();
        ActionListener typeListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setPreviewTypeAction(evt.getActionCommand());
            }
        };
        
        for (int i=0; i<possibleTypes.length; i++) {
            rbItem = new JRadioButtonMenuItem(possibleTypes[i].getName());
            rbItem.addActionListener(typeListener);
            rbGroup.add(rbItem);
            previewMenu.add(rbItem);
        }
        
        // Initial selection
        ((JRadioButtonMenuItem) rbGroup.getElements().nextElement()).setSelected(true);
        previewType = possibleTypes[0];
        
        
        item = new JMenuItem("Configure Preview...");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                configurePreviewAction();
            }
        });
        viewMenu.add(item);
        
        viewMenu.addSeparator();
        JMenu verboseMenu = new VerboseMenu();
        viewMenu.add(verboseMenu);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
        
        
        outputBrowse = new JButton("Browse...");
        outputBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = outputChooser.showOpenDialog(frame);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = outputChooser.getSelectedFile();
                    String fileName = file.getAbsolutePath();
                    outputFile.setText(fileName);
                } else {
                    
                }
            }
        });
        
        outputPanel = new JPanel();
        writeSetParameters.setSelected(true);
        outputPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);
        
        gbc.gridy=0; gbc.gridx=0;
        outputPanel.add(new JLabel("Output File: "), gbc);
        gbc.gridx=1;
        outputPanel.add(outputFile, gbc);
        gbc.gridx=2;
        outputPanel.add(outputBrowse, gbc);
        
        gbc.gridy=1; gbc.gridx=1;
        outputPanel.add(writeSetParameters, gbc);
                
    }
    
    // Menu item actions
    void newStyleAction() {
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();
        newStyleCounter++;
        addTab("New Style "+newStyleCounter, style);
    }
    
    void saveStyleAsAction() {
        StyleEditorPanel panel = getSelectedEditorPanel();
        int index = tabbedPanel.getSelectedIndex();
        if (panel == null) {
            styleLogger.info("No Tab is currently selected.");
            return;
        }
        File file = (File) files.get(panel);
        IPlotterStyle style = panel.getStyle();
        
        if (file != null) {
            outputFile.setText(file.getAbsolutePath());
            outputChooser.setCurrentDirectory(file.getParentFile());
        }
        
        String title = "Select Output File";
        int reply = JOptionPane.showOptionDialog(frame, outputPanel, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
        if (reply == JOptionPane.YES_OPTION) {
            String fileName = outputFile.getText();
            boolean writeAll = !writeSetParameters.isSelected();
            
            try {
                styleLogger.fine("Writing: file="+fileName+", Style="+style+", writeAll="+writeAll);
                AidaStyleXMLWriter.writeToFile(fileName, style, writeAll);
                
                if (file == null || !fileName.equals(file.getAbsoluteFile())) {
                    File newFile = new File(fileName);
                    if (file != null) files.remove(panel);
                    files.put(panel, newFile);
                    
                    tabbedPanel.setTitleAt(index, newFile.getName());
                }
                
            } catch (Exception e) {
                styleLogger.info("Can not write style to file: "+fileName);
                styleLogger.log(Level.FINE, "", e);
            }
        } else {
            return;
        }
    }
    
    void readStyleAction() {
        File file = null;
        String fileName = null;
        int returnVal = inputChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = inputChooser.getSelectedFile();
            fileName = file.getAbsolutePath();
            
        } else { return; }
        
        try {
            IPlotterStyle style = AidaStyleXMLReader.restoreFromFile(fileName);
            StyleEditorPanel panel = addTab(file.getName(), style);
            files.put(panel, file);
        } catch (Exception e) {
            styleLogger.info("Can not edit style from file: "+fileName);
            styleLogger.log(Level.FINE, "", e);
        }
    }
    
    void setPreviewTypeAction(String type) {
        try {
            previewType = Class.forName(type);
        } catch (Exception e) { e.printStackTrace(); }
        
        StyleEditorPanel panel = getSelectedEditorPanel();
        if (panel == null) return;
        
        panel.setPreviewType(previewType);
    }
    
    StyleEditorPanel getSelectedEditorPanel() {
        StyleEditorPanel panel = null;
        Component comp = tabbedPanel.getSelectedComponent();
        if (comp instanceof StyleEditorPanel) panel = (StyleEditorPanel) comp;
        return panel;
    }
    
    void configurePreviewAction() {
        StyleEditorPanel panel = getSelectedEditorPanel();
        String title = "Configure Preview Dialog";
        boolean wrong = true;
        while (wrong) {
            int reply = JOptionPane.showOptionDialog(frame, configurePanel, title, JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION, null, new String[] {"Ok", "Cancel"},  "Cancel");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    configurePanel.validateInput();
                    configurePanel.readInput();
                    if (panel != null) panel.setupEditorPanel(configurePanel);
                    wrong = false;
                } catch (NumberFormatException e) {
                    String message = "Wrong input! Please correct: ";
                    message += "\n"+e.getMessage();
                    JOptionPane.showMessageDialog(frame, message, "Wrong Input", JOptionPane.DEFAULT_OPTION);                    
                }
            } else {
                configurePanel.resetPanel();
                wrong = false;
            }
        }
    }
    
    StyleEditorPanel addTab(String title, IPlotterStyle style) {
        StyleEditorPanel panel = null;
        try {
            panel = new StyleEditorPanel(style, configurePanel, previewType);
            tabbedPanel.addTab(title, null, panel, "To Save style or Close selected tab use File menu");
            tabbedPanel.setSelectedComponent(panel);
        } catch (Exception e) {
            styleLogger.info("**** Problem with initial setup of Preview Panel Size: "+e.getMessage());
            styleLogger.log(Level.FINE, "", e);
        }
        return panel;
    }
    
    void removeSelectedTabAction() {
        StyleEditorPanel panel = getSelectedEditorPanel();
        if (panel == null) {
            styleLogger.fine("No Tab is currently selected.");
            return;
        }
        tabbedPanel.remove(panel);
        files.remove(panel);
        panel.clear();
    }
    
    void exitAction() {
        System.exit(1);
    }
    
    
    private void addHomePage() {
        // Load image and create home panel if needed
        if (homeImage == null) {
            URL url = this.getClass().getResource("images/homePageImage.png");
            homeImage = Toolkit.getDefaultToolkit().getImage(url);
        }
        
        if (homePanel == null) {
            homePanel = new JPanel() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(homeImage, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            };
            
            JPanel p2 = new JPanel();
            p2.setMinimumSize(new Dimension(60, 200));
            p2.setBackground(Color.WHITE);
            GridLayout grid = new GridLayout(1, 2);
            grid.setHgap(10);
            p2.setLayout(grid);
            
            final JButton newStyleButton = new JButton("New Style");
            newStyleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    newStyleAction();
                }
            });
            
            final JButton openStyleButton = new JButton("Open Style");
            openStyleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    readStyleAction();
                }
            });
            
            p2.add(newStyleButton);
            p2.add(openStyleButton);
            
            homePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            homePanel.add(p2, BorderLayout.PAGE_END);
            homePanel.setBackground(Color.WHITE);
        }
        tabbedPanel.addTab("Home", homePanel);
        tabbedPanel.setSelectedComponent(homePanel);
    }
    
    
    // Menu to set Verbose Level
    public class VerboseMenu extends JMenu implements ActionListener {
        
        public VerboseMenu() {
            this("Set Verbose Level", Level.INFO);
        }
        
        public VerboseMenu(String name) {
            this(name, Level.INFO);
        }
        
        public VerboseMenu(String name, Level startLevel) {
            super(name);
            setLoggerLevel(styleLogger, startLevel);
            
            ButtonGroup group = new ButtonGroup();
            
            JRadioButtonMenuItem item = new JRadioButtonMenuItem("Not Verbose");
            item.setMnemonic('N');
            item.addActionListener(this);
            group.add(item);
            add(item);
            if (startLevel == Level.SEVERE) item.setSelected(true);
            
            item = new JRadioButtonMenuItem("Verbose");
            item.setMnemonic('V');
            item.addActionListener(this);
            group.add(item);
            add(item);
            if (startLevel == Level.INFO) item.setSelected(true);
            
            item = new JRadioButtonMenuItem("Very Verbose");
            item.setMnemonic('W');
            item.addActionListener(this);
            group.add(item);
            add(item);
            if (startLevel == Level.FINE) item.setSelected(true);
            
            
            item = new JRadioButtonMenuItem("Debug");
            item.setMnemonic('D');
            item.addActionListener(this);
            add(item);
            group.add(item);
            if (startLevel == Level.FINEST) item.setSelected(true);
            
        }
        
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            
            if (command.equals("Not Verbose")) {
                setLoggerLevel(styleLogger, Level.SEVERE);
            } else if (command.equals("Verbose")) {
                setLoggerLevel(styleLogger, Level.INFO);
            } else if (command.equals("Very Verbose")) {
                setLoggerLevel(styleLogger, Level.FINE);
            } else if (command.equals("Debug")) {
                setLoggerLevel(styleLogger, Level.FINEST);
            }
        }
    }    
    
    
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        LookAndFeelTweaks.tweak();
        LookAndFeelAddons addon = LookAndFeelAddons.getAddon();
        
        AidaStyleEditor editor = new AidaStyleEditor();
    }
    
}
