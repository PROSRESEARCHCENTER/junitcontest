package hep.aida.ref.plotter.style.editor;
/*
 * StyleTreeNode.java
 *
 * Created on June 13, 2005, 6:14 PM
 */

import hep.aida.IBaseStyle;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.BaseStyle;
import hep.aida.ref.plotter.PlotterStyle;
import hep.aida.ref.plotter.Style;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.l2fprod.common.propertysheet.PropertySheetPanel;

/**
 *
 * @author  The FreeHEP team @ SLAC
 */

public class StyleTreeNode implements TreeNode {
    
    private IBaseStyle style;
    private TreePath path;
    private PropertySheetPanel stylePanel;
    //private JScrollPane scrollPanel;
    
    private Vector children;
    private int nChildren;
    private boolean recursive;
    private boolean hasBeenFilled = false;
    private StyleTreeNode parent;
    private TreePath parentPath;
    
    
    // Mainly for constructing root node
    public StyleTreeNode(IPlotterStyle style) {
        this(style, true);
    }
    public StyleTreeNode(IPlotterStyle style, boolean recursive) {
        this(style, null, null, recursive);
    }
    
    // For constructing child nodes
    public StyleTreeNode(IBaseStyle style, StyleTreeNode parent, TreePath parentPath) {
        this(style, parent, parentPath, true);
    }
    public StyleTreeNode(IBaseStyle style, StyleTreeNode parent, TreePath parentPath, boolean recursive) {
        this.style  = style;
        this.parent = parent;
        this.parentPath = parentPath;
        String nodeName = null;
        try {
            if (style instanceof IPlotterStyle && ((BaseStyle) style).isParameterSet(Style.PLOTTER_STYLE_NAME, false))
                nodeName = ((IPlotterStyle) style).parameterValue(Style.PLOTTER_STYLE_NAME);
            
        } catch (Exception e) { e.printStackTrace();}
        if (nodeName == null || nodeName.trim().equals("")) nodeName = style.name();
        if (parentPath == null) path = new TreePath(nodeName);
        else path = parentPath.pathByAddingChild(nodeName);
        
        createChildren();
        
        createPanel();
    }
    
    
    // Service methods
    
    public String toString() { 
        String nodeName = null;
        if (style != null) nodeName = style.name();
        if (path != null) nodeName = path.getLastPathComponent().toString();
        return nodeName;
    }
    
    public IBaseStyle getStyle() {
        return style;
    }
    
    public JComponent getStylePanel() {
        return stylePanel;
    }
    //public JComponent getScrollPanel() {
    //    return scrollPanel;
    //}
    
    private void createChildren() {
        IBaseStyle[] ch = style.children();
        if (ch == null || ch.length == 0) {
            nChildren = 0;
            children = new Vector();
            hasBeenFilled = true;
            return;
        }
        nChildren = ch.length;
        
        if (style instanceof PlotterStyle) {
            List list = ((PlotterStyle) style).parentList();
            if (list != null) nChildren += list.size();
        }
        
        if (recursive) fillChildren();
    }
    
    private void fillChildren() {
        if (children == null) children = new Vector();
        IBaseStyle[] ch = style.children();
        
        if (ch == null) return;
        for (int i=0; i<ch.length; i++) {
            StyleTreeNode childNode = new StyleTreeNode(ch[i], this, path, recursive);
            children.add(childNode);
        }
        
        // Fill parents last - according to the priority of usage
        if (style instanceof PlotterStyle) {
            List list = ((PlotterStyle) style).parentList();
            if (list != null) {
                for (int i=0; i<list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof IPlotterStyle) {
                        StyleTreeNode childNode = new StyleTreeNode((IPlotterStyle) obj, this, path, recursive);
                        children.add(childNode);
                    }
                }
            }
        }
        
        hasBeenFilled = true;
    }
    
    private void createPanel() {
        StylePropertyTable pt = new StylePropertyTable(new StylePropertyTableModel(style));
        pt.setEditorFactory(new StylePropertyEditorRegistry());
        stylePanel = new PropertySheetPanel(pt) {
            public void propertyChange(PropertyChangeEvent evt) {
                //System.out.println("PropertySheetPanel.propertyChange :: Name="+evt.getPropertyName()+", newValue="+evt.getNewValue()+", oldValue="+evt.getOldValue());
                super.propertyChange(evt);
            }
        };
        stylePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), path.toString()));
        
        //scrollPanel = new JScrollPane(stylePanel);
        
        // everytime a property change, update the button with it
        /*
         PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Property prop = (Property)evt.getSource();
                prop.writeToObject(style);
            }
        };
        stylePanel.addPropertySheetChangeListener(listener);
        */
    }
    
    // TreeNode methods
    
    public java.util.Enumeration children() {
        if (!hasBeenFilled) fillChildren();
        return children.elements();
    }
    
    public boolean getAllowsChildren() {
        return (nChildren > 0);
    }
    
    public TreeNode getChildAt(int childIndex) {
        if (!hasBeenFilled) fillChildren();
        return (TreeNode) children.get(childIndex);
    }
    
    public int getChildCount() {
        return nChildren;
    }
    
    public int getIndex(TreeNode node) {
        if (!hasBeenFilled) fillChildren();
        return children.indexOf(node);
    }
    
    public TreeNode getParent() {
        return parent;
    }
    
    public boolean isLeaf() {
        return (nChildren == 0);
    }
}

