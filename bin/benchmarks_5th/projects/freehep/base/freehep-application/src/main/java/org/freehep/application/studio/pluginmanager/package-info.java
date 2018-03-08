/**
 * Plugin that handles installing, updating, and removing other plugins.
 * <p>
 * When a Studio application starts, it scans its extension directories for jar files that
 * contain plugins.xml manifests. Manifests are parsed to produce {@link PluginInfo} plugin
 * descriptors. Each plugin is considered to be installed in the extension directory where the 
 * file containing the manifest was found. All library files required by the plugin must also be present in the 
 * same directory.
 * <p>
 * Plugins are uniquely identified by their names. If several versions of the same plugin are installed,
 * only one is loaded. Loading preference order depends on the installation directory:
 * built-in - user - group - system. Within the same directory, the latest version is loaded, and the plugin
 * manager will attempt to uninstall older versions. If a plugin that would otherwise be loaded requires
 * another plugin, but that other plugin is missing or its version is not compatible, the plugin will not be 
 * loaded. All dependencies are installed in the same extension directory as the plugin that requires them.
 * <p>
 * JAR files containing the plugin code and required libraries are saved in the extension directory where 
 * the plugin is installed. Files are uniquely identified by ID and version, and are named
 * <pre>ID-version.jar</pre>. The version is omitted if unknown. When a file is first downloaded and installed,
 * its ID and version are determined by parsing the "location" attribute specified in the plugin
 * descriptor. The location is expected to be in the 
 * <pre>id-major.minor[.incremental][-qualifier][-build].jar</pre> format ("_" can be used instead of "-",
 * and the minor version can be omitted if this does not introduce ambiguity in the location string interpretation
 * (not recommended)). If the location is not in the expected format, plugin manager will
 * try to extract maven artifact and version attributes from the jar file manifest, and use them as
 * ID and version of the library file. In this case, mapping from "location" to ID will be added
 * to the <tt>library.map</tt> file in the target extension directory. If maven manifest in not found, the entire
 * location string (except the extension) will be used as an ID, and the version will remain unknown.
 * <p>
 * A set of active library file IDs is determined by the requirements of active plugins. For each active ID,
 * exactly one library is activated. That library is chosen from all installed libraries with the same
 * ID using the following rules:
 * <ul>
 * <li>If there is a library in the built-in directory, it is chosen;
 * <li>If all active plugins referencing this ID are in the same directory, and there is a
 *     required library in that directory, it is chosen; if there is no such library, all
 *     referencing plugins are removed;
 * <li>If there are referencing plugins in several directories, the latest version of the required
 *     library installed in these directories is activated; if there is no such library, all
 *     referencing plugins are removed;
 * </ul>
 * At the application start, active plugins are loaded, and active libraries are added to the extensions
 * class loader classpath.
 * A plugin is started if its main class (subclass of {@link Plugin}) has been loaded by the 
 * extensions class loader, an instance was created, and its start() method was called.
 * In the current implementation, a plugin can be stopped (stop() method is called and a reference
 * to the instance is removed from the plugin descriptor) but cannot be unloaded. If a plugin is
 * removed or updated (through the plugin manager), it is stopped (if possible), its files are deleted
 * or marked for deletion, and the user is informed that restarting the application is required for the 
 * changes to take effect.
 */
package org.freehep.application.studio.pluginmanager;
