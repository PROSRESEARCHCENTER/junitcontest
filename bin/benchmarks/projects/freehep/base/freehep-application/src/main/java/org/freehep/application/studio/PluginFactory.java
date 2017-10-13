package org.freehep.application.studio;

/**
 * Factory that constructs plugin instances.
 *
 * @author onoprien
 */
public interface PluginFactory {
    
    Plugin getInstance(Studio studio, PluginInfo plugin, ClassLoader loader) throws Throwable;
    
}
