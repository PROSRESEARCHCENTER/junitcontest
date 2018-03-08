package org.freehep.application.studio;

/**
 * Default implementation of {@link PluginFactory}.
 * Once the source code compatibility is switched to 1.8, this class can be removed,
 * and the default implementation should be provided in the {@link PluginFactory} interface.
 *
 * @author onoprien
 */
class DefaultPluginFactory implements PluginFactory {

    @Override
    public Plugin getInstance(Studio studio, PluginInfo plugin, ClassLoader loader) throws Throwable {
        Class c = loader.loadClass(plugin.getMainClass());
        return (Plugin) c.newInstance();
    }

}
