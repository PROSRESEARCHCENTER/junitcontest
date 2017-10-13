package org.freehep.application.studio;

/** 
 * Enumeration of application extension directories.
 */
public enum PluginDir {
    
    BUILTIN("built-in"), 
    SYSTEM("system"), 
    GROUP("group"), 
    USER("user");
    
    private String label;
    
    PluginDir(String label) {
        this.label = label;
    }
    
    /** Returns a label for this directory to be used by GUI and loggers. */
    public String getLabel() {
        return label;
    }
    
    /** Returns extensions directories in the plugin search order (builtin - user - group - system). */
    public static PluginDir[] searchOrder() {
        return new PluginDir[] {BUILTIN, USER, GROUP, SYSTEM};
    }
    
    /** Returns extensions directories in the inverse plugin search order. */
    public static PluginDir[] inverseSearchOrder() {
        return new PluginDir[] {SYSTEM, GROUP, USER, BUILTIN};
    }
    
    /** Returns extensions directories in the inverse plugin search order, excluding <tt>BUILTIN</tt>. */
    public static PluginDir[] sgu() {
        return new PluginDir[] {SYSTEM, GROUP, USER};
    }
    
    /** Returns extensions directories in the plugin search order, excluding <tt>BUILTIN</tt>. */
    public static PluginDir[] ugs() {
        return new PluginDir[] {USER, GROUP, SYSTEM};
    }
  
}
