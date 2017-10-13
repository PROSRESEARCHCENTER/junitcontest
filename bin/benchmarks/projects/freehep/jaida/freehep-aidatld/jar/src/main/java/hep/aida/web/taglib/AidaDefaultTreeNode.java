package hep.aida.web.taglib;

import org.freehep.webutil.tree.DefaultTreeNode;
import org.freehep.webutil.tree.Icon;
import org.freehep.webutil.tree.Tree;
import org.freehep.webutil.util.properties.PropertiesLoader;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class AidaDefaultTreeNode extends DefaultTreeNode {
    protected Icon icon = null;
    protected String type;
    
    public AidaDefaultTreeNode(String name) {
        this(name, null);
    }
    
    public AidaDefaultTreeNode(String name, String type) {
        this(name, type, null);
    }
    
    public AidaDefaultTreeNode(String name, String nodeType, DefaultTreeNode parent) {
        super(name, parent);
        if (nodeType == null) icon = null;
        else {
            int w = 24;
            int h = 22;
            if (nodeType.toLowerCase().indexOf("icloud") >= 0) {
                nodeType = "icloud1d";
                w = 24; h = 18;
            } else if (nodeType.toLowerCase().indexOf("idatapointset") >= 0) {
                nodeType = "idatapointset";
                w = 24; h = 18;
            } else if (nodeType.toLowerCase().indexOf("ifunction") >= 0) {
                nodeType = "ifunction1d";
                w = 24; h = 18;
            } else if (nodeType.toLowerCase().indexOf("ihistogram") >= 0) {
                nodeType = "ihistogram1d";
            } else if (nodeType.toLowerCase().indexOf("iprofile") >= 0) {
                nodeType = "iprofile1d";
                w = 24; h = 18;
            } else if (nodeType.toLowerCase().indexOf("ituplecolumn") >= 0) {
                nodeType = "ituplecolumn";
                w = 24; h = 18;
            } else if (nodeType.toLowerCase().indexOf("ituple") >= 0) {
                nodeType = "ituple";
            } else nodeType = "doc";
            
            icon = new AidaNodeIcon(nodeType, "o", w, h);
        }
        this.type =  nodeType;
        
    }
    
    public Icon getIcon() {
        return icon;
    }
    
    class AidaNodeIcon implements Icon {
        private String alt;
        private String name;
        private int height;
        private int width;
        
        AidaNodeIcon(String name, String alt, int width, int height) {
            this.name = name;
            this.alt = alt;
            this.width = width;
            this.height = height;
        }
        public String getAlt() {
            return alt;
        }
        
        public String getSourceURL() {
            String tmpName = name;
            String iconUrl = PropertiesLoader.property("freehep.tree.images."+tmpName);
            if ( iconUrl != null )
                return iconUrl;
            return "icon.jsp?name="+tmpName;
        }
        public int getHeight() {
            return height;
        }
        
        public int getWidth() {
            return width;
        }
    }
    
}
