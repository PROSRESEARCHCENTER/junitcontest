package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This abstract class as "In-Memory" copy of persistent Store
 * and implements transient style/rule manipulations.
 * Exact "commit" operations are left to concrete
 * implementations, like XMLStyleStore, DBStyleSTore, etc.
 */

public abstract class BaseStyleStore implements IStyleStore {
    protected String storeName;
    protected String storeType;
    protected boolean isReadOnly;
    protected Object lock = new Object();
    protected List entries;
    protected Properties props = new Properties();
    
    public BaseStyleStore(String storeName) {
        this.storeName = storeName;
        entries = new ArrayList(10);
    }
    
    
    // Service methods
    
    
    // Can use properties to store all needed extra information  about the Style Store
    public Properties getProperties() {
        return props;
    }
    
    public StyleStoreEntry getStoreEntry(String styleName) {
        StyleStoreEntry entry = null;
        for (int i=0; i<entries.size(); i++) {
            if (styleName.equals(((StyleStoreEntry) entries.get(i)).getName())) {
                entry = (StyleStoreEntry) entries.get(i);
                break;
            }
        }        
        return entry;
    }
    
    public void addStoreEntry(StyleStoreEntry entry) {
        if (!hasStyle(entry.getName())) {
            entry.getStyle().setParameter(Style.PLOTTER_STYLE_NAME, storeName+"."+entry.getName());
            entries.add(entry);
        }
        else
            throw new IllegalArgumentException("StyleStore "+storeName+" already has Style "+entry.getName());            
    }
    
    public void moveUp(String styleName) {
        moveUp(getStoreEntry(styleName));
    }
    
    public void moveUp(StyleStoreEntry entry) {
        if (!hasStyle(entry.getName()))
            throw new IllegalArgumentException("StyleStore "+storeName+" does not have Style "+entry.getName());
        
        int index = entries.indexOf(entry);
        if (index == 0) return;
        entries.remove(index);
        entries.add((index-1), entry);
    }
    
    public void moveDown(String styleName) {
        moveDown(getStoreEntry(styleName));
    }
    
    public void moveDown(StyleStoreEntry entry) {
        if (!hasStyle(entry.getName()))
            throw new IllegalArgumentException("StyleStore "+storeName+" does not have Style "+entry.getName());
        
        int index = entries.indexOf(entry);
        if (index == (entries.size()-1)) return;
        entries.remove(index);
        entries.add((index+1), entry);
    }
    
    
    // IStyleStore methds
    
    public String getStoreName() { return storeName; }
    public String getStoreType() { return storeType; }
    public boolean isReadOnly()  { return isReadOnly; }
    
    public void addStyle(String styleName, IPlotterStyle style) {
        addStyle(styleName, style, createRule());
    }
    
    public void addStyle(String styleName, IPlotterStyle style, IStyleRule rule) {
        if (hasStyle(styleName))
            throw new IllegalArgumentException("StyleStore "+storeName+" already has Style "+styleName);
        
        style.setParameter(Style.PLOTTER_STYLE_NAME, storeName+"."+styleName);
        StyleStoreEntry entry = new StyleStoreEntry(styleName, style, rule);
        entries.add(entry);
    }
    
    public boolean hasStyle(String styleName) {
        boolean ok = false;
        StyleStoreEntry entry = getStoreEntry(styleName);
        if (entry != null) ok = true;
        return ok;
    }
    
    public IPlotterStyle getStyle(String styleName) {
        IPlotterStyle style = null;
        StyleStoreEntry entry = getStoreEntry(styleName);
        if (entry != null) style = entry.getStyle();
        return style;
    }
    
    public IPlotterStyle removeStyle(String styleName) {
        IPlotterStyle style = null;
        StyleStoreEntry entry = getStoreEntry(styleName);
        if (entry != null) {
            style = entry.getStyle();
            entries.remove(entry);
        }
        return style;
    }
    
    public String[] getAllStyleNames() {
        String[] names = new String[entries.size()];
        for (int i=0; i<names.length; i++) {
            names[i] = ((StyleStoreEntry) entries.get(i)).getName();
        }
        return names;
    }
    
    /**
     * This StyleStore creates IStyleRule based on the JEL library
     */
    public IStyleRule createRule() { return new JELRule(); }
    
    public IStyleRule getRuleForStyle(String styleName) {
        IStyleRule rule = null;
        StyleStoreEntry entry = getStoreEntry(styleName);
        if (entry != null) rule = entry.getRule();
        return rule;
    }
    
    public void setRuleForStyle(String styleName, IStyleRule rule) {
        StyleStoreEntry entry = getStoreEntry(styleName);
        if (entry != null) entry.setRule(rule);
        
    }
    
    public void removeRuleForStyle(String styleName) {
        setRuleForStyle(styleName, createRule());
    }
    
    /**
     * Write all information from Store to the undelying persistent
     * facility: XML file, database, etc.
     */
    public abstract void commit();
    
    /**
     * Close all connections and free all resources.
     * Store is not usable after this method is executed.
     */
    public void close() {
        entries.clear();
    }
 
    public String toString() { return storeName; }
}
