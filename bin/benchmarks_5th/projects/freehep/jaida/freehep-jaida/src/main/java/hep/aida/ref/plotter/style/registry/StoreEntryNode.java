package hep.aida.ref.plotter.style.registry;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;


public class StoreEntryNode extends DefaultMutableTreeNode {
    private JComponent page;
    
    public StoreEntryNode(StyleStoreEntry entry) {
        super(entry, false);
    }
    
    public void setPage(JComponent page) { this.page = page; }
    
    public JComponent getPage() { return page; }
    
    public String getName() {
        Object obj = super.getUserObject();
        if (obj instanceof StyleStoreEntry) return ((StyleStoreEntry) obj).getName();
        else if (obj != null) return obj.toString();
        else return null;
    }
    
    public StyleStoreEntry getStoreEntry() {
        StyleStoreEntry entry = null;
        Object obj = super.getUserObject();
        if (obj instanceof StyleStoreEntry) entry = (StyleStoreEntry) obj;
        return entry;
    }
    
}
