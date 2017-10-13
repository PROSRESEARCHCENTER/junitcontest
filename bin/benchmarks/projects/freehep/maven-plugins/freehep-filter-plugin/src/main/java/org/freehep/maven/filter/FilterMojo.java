// Copyright FreeHEP 2005.
package org.freehep.maven.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.PropertyUtils;
import org.apache.maven.plugin.resources.ReflectionProperties;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * Copy application sources while filtering them.
 * 
 * @author Mark Donszelmann
 * @version $Id: FilterMojo.java 8584 2006-08-10 23:06:37Z duns $
 * @goal process
 * @phase process-sources
 */
public class FilterMojo extends AbstractMojo {
    /**
     * The target directory into which to filter the output
     *
     * @parameter expression="${project.build.directory}/filtered-sources/java"
     * @required
     */
    private File targetDirectory;

    /**
     * The source directory.
     * 
     * @parameter expression="${basedir}/src/main/java"
     * @required
     */
    private File sourceDirectory;

    /**
     * A list of inclusion filters for sources. Defaults to **\/*.java
     * 
     * @parameter
     */
    private Set includes = new HashSet();

    /**
     * A list of exclusion filters for sources.
     * 
     * @parameter
     */
    private Set excludes = new HashSet();

    /**
     * @parameter expression="${project.build.filters}"
     */
    private List filters;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private Properties filterProperties;

    public void execute() throws MojoExecutionException {
        System.err.println("Filter");
        if (!targetDirectory.exists()) targetDirectory.mkdirs();

        project.addCompileSourceRoot(targetDirectory.getPath());

        if (includes.isEmpty()) {
            includes.add("**/*.java.template");
        }
        
        initializeFiltering();

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir(sourceDirectory);
        scanner.setIncludes((String[])includes.toArray(new String[0]));
        if (!excludes.isEmpty()) scanner.setExcludes((String[])excludes.toArray(new String[0]));

        scanner.addDefaultExcludes();
        scanner.scan();

        List includedFiles = Arrays.asList(scanner.getIncludedFiles());
        for (Iterator j = includedFiles.iterator(); j.hasNext();) {
            String name = (String) j.next();

            File destination = new File(targetDirectory, name);
            File source = new File(sourceDirectory, name);

            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }

            try {
                copyFile(source, destination, true);
            } catch (IOException e) {
                throw new MojoExecutionException("Error copying sources", e);
            }
        }
    }

    // copied from maven-resource-plugin
    private void initializeFiltering() throws MojoExecutionException {
        // System properties
        filterProperties = new Properties(System.getProperties());

        // Project properties
        filterProperties.putAll(project.getProperties());

        for (Iterator i = filters.iterator(); i.hasNext();) {
            String filtersfile = (String) i.next();

            try {
                Properties properties = PropertyUtils.loadPropertyFile(
                        new File(filtersfile), true, true);

                filterProperties.putAll(properties);
            } catch (IOException e) {
                throw new MojoExecutionException(
                        "Error loading property file '" + filtersfile + "'", e);
            }
        }
    }

    // copied from maven-resource-plugin
    private void copyFile(File from, File to, boolean filtering)
            throws IOException {
        if (!filtering) {
            if (to.lastModified() < from.lastModified()) {
                FileUtils.copyFile(from, to);
            }
        } else {
            // buffer so it isn't reading a byte at a time!
            Reader fileReader = null;
            Writer fileWriter = null;
            try {
                fileReader = new BufferedReader(new FileReader(from));
                fileWriter = new FileWriter(to);

                // support ${token}
                Reader reader = new InterpolationFilterReader(fileReader,
                        filterProperties, "${", "}");

                // support @token@
                reader = new InterpolationFilterReader(reader,
                        filterProperties, "@", "@");

                reader = new InterpolationFilterReader(reader,
                        new ReflectionProperties(project), "${", "}");

                IOUtil.copy(reader, fileWriter);
            } finally {
                IOUtil.close(fileReader);
                IOUtil.close(fileWriter);
            }
        }
    }
}
