package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterStyle;

/**
 * This interface can be implemented as "In-Memory" copy of persistent
 * facility, or as keeping live connections and committing any change 
 * immediately.
 */

public interface IStyleStore {
    
    // Key for AIDA type of object that the Style is going to be used with
    public static String STYLE_PREVIEW_TYPE = "STYLE_PREVIEW_TYPE";
    
    // Key for Style name
    public static String STYLE_STORE_NAME = "STYLE_STORE_NAME";
            
    String getStoreName();
    
    String getStoreType();
    
    boolean isReadOnly();
    
    
    // Manage Styles
    
    boolean hasStyle(String styleName);
    
    void addStyle(String styleName, IPlotterStyle style);
    
    void addStyle(String styleName, IPlotterStyle style, IStyleRule rule);
    
    IPlotterStyle getStyle(String styleName);
    
    void moveUp(String styleName);

    void moveDown(String styleName);

    
    /**
     * Remove Style and Rule associated with it from the Store
     */
    IPlotterStyle removeStyle(String styleName);
    
    String[] getAllStyleNames();
    
    
    // Create new Rule for this Store - Store acts as a Rule Factory
    
    IStyleRule createRule();
    
    
    // Manage Rules - only one rule per style is allowed
    
    IStyleRule getRuleForStyle(String styleName);
    
    void setRuleForStyle(String styleName, IStyleRule rule);
    
    void removeRuleForStyle(String styleName);
    
    /**
     * Write all information from Store to the undelying persistent
     * facility: XML file, database, etc.
     */
    void commit();
           
    /**
     * Close all connections and free all resources.
     * Store is not usable after this method is executed.
     */
    void close(); 
           
}
