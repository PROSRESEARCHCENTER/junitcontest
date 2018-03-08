package hep.aida.ref.plotter.style.editor;
/*
 * StyleEditorPanel.java
 *
 * Created on June 14, 2005, 9:32 AM
 */

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IHistogram1D;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.BaseStyle;
import hep.aida.ref.plotter.Style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

/**
 *
 * @author  serbo
 */
public class StyleEditorPanel extends JSplitPane implements TreeSelectionListener, PropertyChangeListener {
    private String title;
    private IPlotterStyle style;
    private JComboBox previewTypeBox;
    private Class previewType = IHistogram1D.class;
    private JTree styleTree;
    private DefaultTreeModel styleModel;
    private StylePreviewCreator stylePreviewCreator;
    private JSplitPane rightPanel;
    private boolean previewFitRegion = true;
    private JCheckBox showPreviewCheckBox;
    private JComponent previewPanel;
    private int previewDividerLocation = 250;
    
    private PropertySheetPanel currentStylePanel;
    
    static boolean isAnyParameterSet(BaseStyle style) {
        return isAnyParameterSet(style, true);
    }
    static boolean isAnyParameterSet(BaseStyle style, boolean recursive) {
        boolean isSet = false;
        if (style == null) return false;
        
        // Loop over parameters first
        String[] parNames = style.availableParameters();
        if (parNames != null) {
            for (int i=0; i<parNames.length; i++) {
                String name = parNames[i];
                if (name.equals(Style.PLOTTER_STYLE_NAME)) continue;
                isSet = style.isParameterSet(name, recursive);
                if (isSet) break;
            }
        }
        
        // Now loop over the substyles (if needed)
        if (!isSet && recursive) {
            IBaseStyle[] children = style.children();
            if (children == null) return false;
            for (int i=0; i<children.length; i++) {
                if (children[i] instanceof BaseStyle && isAnyParameterSet((BaseStyle) children[i], recursive)) {
                    isSet = true;
                    break;
                }
            }
        }
        return isSet;
    }
    
    /** Creates a new instance of StyleEditorPanel */
    public StyleEditorPanel(IPlotterStyle style) {
        this("Plotter Style Editor", style, new ConfigurePreviewPanel(), StylePreviewCreator.getPossiblePreviewTypes()[0]);
    }
    public StyleEditorPanel(IPlotterStyle style, Class previewType) {
        this("Plotter Style Editor", style, new ConfigurePreviewPanel(), previewType);
    }
    public StyleEditorPanel(IPlotterStyle style, ConfigurePreviewPanel configurePanel) {
        this("Plotter Style Editor", style, configurePanel, StylePreviewCreator.getPossiblePreviewTypes()[0]);
    }
    public StyleEditorPanel(IPlotterStyle style, ConfigurePreviewPanel configurePanel, Class previewType) {
        this("Plotter Style Editor", style, configurePanel, previewType);
    }
    public StyleEditorPanel(String title, IPlotterStyle style, ConfigurePreviewPanel configurePanel, Class previewType) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.title = title;
        init(style, configurePanel, previewType);
    }
    
    public IPlotterStyle getStyle() { return style; }
    
    public void setPreviewVisible(boolean b) { 
        showPreviewCheckBox.setSelected(b);
    }
    
    public boolean isPreviewVisible() { return showPreviewCheckBox.isSelected(); }
    
    public void setCurrentPreviewPanelDimension(Dimension d) {
        if (d.width < 0 && d.height < 0) {
            previewFitRegion = true;
        } else {
            previewFitRegion = false;
        }
        stylePreviewCreator.setPreviewPanelDimension(d);
        updatePreview();
    }
    
    public Dimension getCurrentPreviewPanelDimension() {
        return previewPanel.getComponent(1).getSize();
    }
    
    public void setupEditorPanel(ConfigurePreviewPanel configurePanel) {
        stylePreviewCreator.setupPreviewCreator(configurePanel);
        Dimension d = null;
        if (configurePanel == null || configurePanel.previewFitRegion) {
            d = new Dimension(-1, -1);
            previewFitRegion = true;
        } else {
            d = new Dimension(configurePanel.previewWidth, configurePanel.previewHeight);
            previewFitRegion = false;
        }
        setCurrentPreviewPanelDimension(d);
    }
    
    public void clear() {
        if (styleTree != null) styleTree.removeTreeSelectionListener(this);
        
    }
    
    public void setPreviewType(Class previewType) {
        previewTypeBox.setSelectedItem(previewType.getName());
    }
    void executeSetPreviewType(Class previewType) {
        this.previewType = previewType;
        updatePreview();
    }
    
    public Class getPreviewType() { return previewType; }
    
    public void updatePreview() {
        /*
        try {
            int index = previewPanel.getComponentCount();
            if (index > 0) previewPanel.remove(0);
        } catch (ArrayIndexOutOfBoundsException e) { e.printStackTrace(); }
        */
        
        //if (previewFitRegion) {
            Dimension d = null;
            //newComponent.setMinimumSize(d);
        //}
        if (showPreviewCheckBox.isSelected()) {
            JComponent newComponent = stylePreviewCreator.getPreviewPanel(style, previewType);
            newComponent.setMaximumSize(d);
            newComponent.setPreferredSize(d);
            if (rightPanel.getBottomComponent() != null) previewDividerLocation = rightPanel.getDividerLocation();
            rightPanel.setBottomComponent(newComponent);
            rightPanel.setDividerLocation(previewDividerLocation);
            previewPanel = newComponent;
        }
    }
    
    private void init(IPlotterStyle ps, ConfigurePreviewPanel configurePanel, Class previewType) {
        this.style = ps;
        this.previewType = previewType;
        
        showPreviewCheckBox = new JCheckBox("Show Preview ");
        showPreviewCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                boolean selected = ((JCheckBox) ev.getSource()).isSelected();
                //System.out.println("Preview Selected: "+selected);
                setPreviewVisibleAction(selected);
            }
        });
        
        Class[] possibleTypes = StylePreviewCreator.getPossiblePreviewTypes();
        String[] possibleTypesString = new String[possibleTypes.length];
        for (int i=0; i<possibleTypes.length; i++) { possibleTypesString[i] = possibleTypes[i].getName(); }
        previewTypeBox = new JComboBox(possibleTypesString);
        previewTypeBox.setSelectedItem(previewType.getName());
        previewTypeBox.addItemListener(new  ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    try {
                        Class pt = Class.forName((String) evt.getItem());
                        executeSetPreviewType(pt);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        });
        
        Dimension d = null;

        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolsPanel.setBorder(new EtchedBorder());
        toolsPanel.add(showPreviewCheckBox);
        toolsPanel.add(previewTypeBox);
        
        createStyleTree(ps);
        
        rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        previewPanel = null;        
        rightPanel.setBottomComponent(previewPanel);
        rightPanel.setDividerLocation(previewDividerLocation);
                
        JPanel superRightPanel = new JPanel();
        superRightPanel.setLayout(new BoxLayout(superRightPanel, BoxLayout.Y_AXIS)); 
        superRightPanel.add(toolsPanel);
        
        rightPanel.setTopComponent(superRightPanel);
        
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        //this.setRightComponent(superRightPanel);
        this.setRightComponent(rightPanel);
        this.setDividerLocation(200);
        
        stylePreviewCreator = new StylePreviewCreator(configurePanel);
        previewFitRegion = configurePanel.previewFitRegion;
        
        styleTree.expandRow(0);
        styleTree.setSelectionRow(0);
        updatePreview();
        setPreviewVisible(false);
    }
    
    private void setPreviewVisibleAction(boolean showPreview) {
        if (showPreview) {
            if (rightPanel.getBottomComponent() != null) return;
            rightPanel.setBottomComponent(previewPanel);
            rightPanel.setDividerLocation(previewDividerLocation);
        } else {
            if (rightPanel.getBottomComponent() == null) return;
            previewDividerLocation = rightPanel.getDividerLocation();
            rightPanel.setBottomComponent(null);
        }
        updatePreview();
    }
    
    private void createStyleTree(IPlotterStyle plotterStyle) {
        StyleTreeNode rootNode = new StyleTreeNode(plotterStyle, false);
        TreePath rootPath = new TreePath(rootNode);
        styleModel = new DefaultTreeModel(rootNode);
        styleTree = new JTree(styleModel);
        styleTree.setCellRenderer(new StyleTreeCellRenderer());
        styleTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        styleTree.addTreeSelectionListener(this);
        this.setLeftComponent(new JScrollPane(styleTree));
    }
    
    
    // TreeSelectionListener methods
    
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getNewLeadSelectionPath() == null) return;
        Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
        if (obj instanceof StyleTreeNode) {
            StyleTreeNode node = (StyleTreeNode) obj;
            JComponent panel = node.getStylePanel();
            int rdl = rightPanel.getDividerLocation();
            
            if (currentStylePanel != null) currentStylePanel.removePropertySheetChangeListener(this);
            
            if (((JComponent) rightPanel.getTopComponent()).getComponentCount() > 1)
                ((JComponent) rightPanel.getTopComponent()).remove(1);
            ((JComponent) rightPanel.getTopComponent()).add(panel);
            rightPanel.setDividerLocation(rdl);
            if (panel instanceof PropertySheetPanel) {
                currentStylePanel = (PropertySheetPanel) panel;
                currentStylePanel.addPropertySheetChangeListener(this);
            }
        }
    }
    
    
    // PropertyChangeLisener methods
    
    public void propertyChange(PropertyChangeEvent evt) {
        Property prop = (Property)evt.getSource();
        updatePreview();
        
        // repaint Tree and the panel
        //Component tc = rightPanel.getTopComponent();
        //if (((JComponent) rightPanel.getTopComponent()).getComponentCount() > 1)
        //        tc = ((JComponent) rightPanel.getTopComponent()).getComponent(1);
        //if (tc != null) tc.repaint();
        styleTree.repaint();
        if (currentStylePanel != null) { 
            //RuntimeException re = new RuntimeException("**** Property Change *** ");
            //re.printStackTrace();
            currentStylePanel.repaint();
        }
    }
    
    // Style-specific Cell Renderer
    
    class StyleTreeCellRenderer extends DefaultTreeCellRenderer {
        
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
            Font font = c.getFont();
            if (font != null) {
                boolean normal = !font.isItalic();
                String name = font.getName();
                int size = font.getSize();
                int style = Font.PLAIN;
                boolean isSet = false;
                boolean isSetHere = false;
                if (value instanceof StyleTreeNode) {
                    IBaseStyle st = ((StyleTreeNode) value).getStyle();
                    if (st instanceof BaseStyle) {
                        BaseStyle bst = (BaseStyle) st;
                        isSet = isAnyParameterSet(bst, true);
                        isSetHere = isAnyParameterSet(bst, false);
                    }
                }
                
                if (isSetHere) {
                    style = Font.PLAIN;
                    if (isSelected) c.setForeground(Color.white);
                    else c.setForeground(Color.black);
                } else if (isSet) {
                    style = Font.PLAIN;
                    if (isSelected) c.setForeground(Color.green);
                    else c.setForeground(Color.blue);
               } else {
                    style = Font.ITALIC;
                    if (isSelected) c.setForeground(Color.yellow);
                    else c.setForeground(Color.red);
                }
                Font newFont = new Font(name, style, size);
                c.setFont(newFont);
            }
            return c;
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //LookAndFeelTweaks.tweak();
        
        JFrame frame = new JFrame("PropertySheet");
        frame.getContentPane().setLayout(new BorderLayout());
        
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();
        
        StyleEditorPanel sePanel = new StyleEditorPanel(style, new ConfigurePreviewPanel(),IHistogram1D.class);
        frame.getContentPane().add("Center", sePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 500);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( (d.width-frame.getSize().width )/2, (d.height-frame.getSize().height )/2 );
        frame.setVisible(true);
    }
    
}
