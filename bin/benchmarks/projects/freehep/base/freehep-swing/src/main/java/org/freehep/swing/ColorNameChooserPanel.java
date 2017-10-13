package org.freehep.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ColorNameChooserPanel  extends AbstractColorChooserPanel implements ListSelectionListener {
    private JList list;
    
    public ColorNameChooserPanel() {
    }
    
    // AbstractColorChooserPanel methods
    
    protected void buildChooser() {
        String[] names = ColorConverter.getNames();
        
        Vector labels = new Vector(names.length);
        for (int i=0; i<names.length; i++) {
            try {
                Color c = ColorConverter.get(names[i]);
                IconLabel cl = new IconLabel(names[i], c);
                labels.add(cl);
            } catch (ColorConverter.ColorConversionException e) {
                e.printStackTrace();
            }
        }
        
        list = new JList(labels);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(-1);
        list.setCellRenderer(new ColorListCellRenderer());
        list.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane);
    }
    
    public void updateChooser() {
        //System.out.println("ColorNameChooserPanel.updateChooser :: Color: " + getColorSelectionModel().getSelectedColor());
    }
    
    public String getDisplayName() {
        return "By Name";
    }
    
    public Icon getSmallDisplayIcon() {
        return null;
    }
    
    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    // ListSelectionListener methods
    public void valueChanged(ListSelectionEvent e) {
        Color color = null;
        int index = list.getSelectedIndex();
        if (index >= 0) {
            color = ((IconLabel) list.getSelectedValue()).getColor();
        }
        getColorSelectionModel().setSelectedColor(color);
    }
    
    
    // inner classes
    
    private class ColorListCellRenderer extends DefaultListCellRenderer {
        ColorListCellRenderer() {
            super();
        }
        
        public  Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof IconLabel) {
                Icon icon = ((IconLabel) value).getIcon();
                this.setIcon(icon);
            }
            return c;
        }
        
    }
    
    private class IconLabel {
        private Color color;
        private String name;
        private ColorIcon icon;
        
        IconLabel(String name, Color color) {
            this.name = name;
            this.color = color;
            icon = new ColorIcon(color);
        }
        
        public Color getColor() {
            return color;
        }
        public Icon getIcon() {
            return icon;
        }
        public String toString() {
            return name;
        }
    }
    
    private class ColorIcon implements Icon {
        private Color color;
        private int sizeX = 16;
        private int sizeY = 10;
        
        ColorIcon(Color color) {
            this.color = color;
        }
        public void setIconHeight(int size) {
            sizeY = size;
        }
        public void setIconWidth(int size) {
            sizeX = size;
        }
        
        public int getIconHeight() {
            return sizeY;
        }
        public int getIconWidth() {
            return sizeX;
        }
        
        public void paintIcon(Component p1, Graphics g, int x, int y) {
            Color save = g.getColor();
            g.setColor((color == null) ? getBackground() : color);
            g.fill3DRect(x, y, sizeX, sizeY, true);
            g.setColor(save);
        }
    }
    
}
