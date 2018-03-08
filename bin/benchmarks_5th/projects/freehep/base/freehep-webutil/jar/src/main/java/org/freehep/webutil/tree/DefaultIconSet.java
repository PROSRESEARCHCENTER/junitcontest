package org.freehep.webutil.tree;

import java.io.InputStream;
import java.net.URL;
import org.freehep.webutil.util.properties.PropertiesLoader;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */

public class DefaultIconSet implements IconSet {
    
    private Icon doc;
    private Icon folderClosed;
    private Icon folderOpen;
    private Icon lastMinus;
    private Icon lastNode;
    private Icon lastPlus;
    private Icon node;
    private Icon plus;
    private Icon minus;
    private Icon verticalLine;
    private Icon blank;
    //private String path;
    
    /** Creates a new instance of DefaultIconSet */
    public DefaultIconSet() {
        //this.path = path;
        this.doc = new DefaultIcon("doc","o",24, 22);
        this.folderClosed = new DefaultIcon("folderclosed","o",24,22);
        this.folderOpen = new DefaultIcon("folderopen","o",24,22);
        this.lastMinus = new DefaultIcon("mlastnode","-",16,22);
        this.lastNode = new DefaultIcon("lastnode","o",16,22);
        this.lastPlus = new DefaultIcon("plastnode","+",16,22);
        this.minus = new DefaultIcon("mnode","-",16,22);
        this.node = new DefaultIcon("node","o",16,22);
        this.plus = new DefaultIcon("pnode","+",16,22);
        this.verticalLine = new DefaultIcon("vertline","|",16,22);
        this.blank = new DefaultIcon("blank"," ",16,22);
    }
    
    public Icon getDocument() {
        return doc;
    }
    
    public Icon getFolderClosed() {
        return folderClosed;
    }
    
    public Icon getFolderOpen() {
        return folderOpen;
    }
    
    public Icon getLastMinus() {
        return lastMinus;
    }
    
    public Icon getLastNode() {
        return lastNode;
    }
    
    public Icon getLastPlus() {
        return lastPlus;
    }
    
    public Icon getMinus() {
        return minus;
    }
    
    public Icon getNode() {
        return node;
    }
    
    public Icon getPlus() {
        return plus;
    }
    
    public Icon getVerticalLine() {
        return verticalLine;
    }
    
    public Icon getBlank() {
        return blank;
    }
    
    private class DefaultIcon implements Icon {
        private String alt;
        private String name;
        private int height;
        private int width;
        DefaultIcon(String name, String alt, int width, int height) {
            this.name = name;
            this.alt = alt;
            this.width = width;
            this.height = height;
        }
        public String getAlt() {
            return alt;
        }
        
        public String getSourceURL() {
            String iconUrl = PropertiesLoader.property("freehep.tree.images."+name);
            if ( iconUrl != null )
                return iconUrl;
            return "icon.jsp?name="+name;
        }
        
        public int getHeight() {
            return height;
        }
        
        public int getWidth() {
            return width;
        }
    }
}
