package org.freehep.application.studio;

import java.util.*;

/**
 * Exception attached to plugin descriptors to indicate installation or initialization errors.
 *
 * @author onoprien
 */
public class PluginException extends RuntimeException {
    
    private ArrayList<String> missingLibraries;
    private ArrayList<String> missingPlugins;
    
    public PluginException() {
    }

    public PluginException(String string) {
        super(string);
    }

    public PluginException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public PluginException(Throwable thrwbl) {
        super(thrwbl);
    }

    public PluginException(PluginInfo plugin) {
        missingPlugins = new ArrayList<String>(1);
        missingPlugins.add(plugin.getName());
    }

    public PluginException(LibInfo library) {
        missingLibraries = new ArrayList<String>(1);
        missingLibraries.add(library.getId());
    }
    
    static public boolean reportMissingPlugin(PluginInfo plugin, String missing) {
        Throwable t = plugin.getErrorStatus();
        if (t instanceof PluginException) {
            PluginException pe = (PluginException) t;
            if (pe.missingPlugins == null) {
                pe.missingPlugins = new ArrayList<String>(1);
            } else {
                pe.missingPlugins.ensureCapacity(pe.missingPlugins.size()+1);
            }
            pe.missingPlugins.add(missing);
        } else {
            PluginException pe = t == null ? new PluginException() : new PluginException(t);
            pe.missingPlugins = new ArrayList<String>(1);
            pe.missingPlugins.add(missing);
            plugin.setErrorStatus(pe);
        }
        return t == null;
    }
    
    static public boolean reportNoMissingPlugins(PluginInfo plugin) {
        Throwable t = plugin.getErrorStatus();
        if (t instanceof PluginException) {
            PluginException pe = (PluginException) t;
            if (pe.missingPlugins == null) {
                return false;
            } else {
                pe.missingPlugins = null;
                if (pe.hasNothingToReport()) {
                    plugin.setErrorStatus(t = pe.getCause());
                }
                return t == null;
            }
        } else {
            return false;
        }
    }
    
    static public boolean reportMissingLibrary(PluginInfo plugin, String missing) {
        Throwable t = plugin.getErrorStatus();
        if (t instanceof PluginException) {
            PluginException pe = (PluginException) t;
            if (pe.missingLibraries == null) {
                pe.missingLibraries = new ArrayList<String>(1);
            } else {
                pe.missingLibraries.ensureCapacity(pe.missingLibraries.size()+1);
            }
            pe.missingLibraries.add(missing);
        } else {
            PluginException pe = t == null ? new PluginException() : new PluginException(t);
            pe.missingLibraries = new ArrayList<String>(1);
            pe.missingLibraries.add(missing);
            plugin.setErrorStatus(pe);
        }
        return t == null;
    }
    
    static public boolean reportNoMissingLibraries(PluginInfo plugin) {
        Throwable t = plugin.getErrorStatus();
        if (t instanceof PluginException) {
            PluginException pe = (PluginException) t;
            if (pe.missingLibraries == null) {
                return false;
            } else {
                pe.missingLibraries = null;
                if (pe.hasNothingToReport()) {
                    plugin.setErrorStatus(t = pe.getCause());
                }
                return t == null;
            }
        } else {
            return false;
        }
    }
    
    public List<String> getMissingLibraries() {
        return missingLibraries == null ? Collections.<String>emptyList() : Collections.unmodifiableList(missingLibraries);
    }
    
    public List<String> getMissingPlugins() {
        return missingPlugins == null ? Collections.<String>emptyList() : Collections.unmodifiableList(missingPlugins);
    }
    
    protected boolean hasNothingToReport() {
        return missingLibraries == null && missingPlugins == null;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("\n");
        if (missingPlugins != null && !missingPlugins.isEmpty()) {
            sb.append("\nMissing, incompatible, or broken required plugins:\n");
            for (String name : missingPlugins) {
                sb.append(name).append("\n");
            }
        }
        if (missingLibraries != null && !missingLibraries.isEmpty()) {
            sb.append("\nMissing libraries:\n");
            for (String name : missingLibraries) {
                sb.append(name).append("\n");
            }
        }
        return sb.toString();
    }
    
}
