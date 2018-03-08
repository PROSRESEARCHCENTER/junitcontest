// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.jarjar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

/**
 * Unpacks jar dependencies and creates one jar file out of it.
 * @description Unpacks jar dependencies and creates one jar file out of it.
 * @goal jarjar
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: JarJarMojo.java 8947 2006-09-12 18:16:26Z duns $
 */
public class JarJarMojo extends AbstractMojo {

    /**
     * Add also all transitive dependencies
     * 
     * @parameter expression="${jarjar.transitive}" default-value="false"
     */
    protected boolean addTransitiveDependencies;
    
    /**
     * Sets the scope
     * 
     * @parameter expression="${jarjar.scope}" default-value="runtime"
     */
    protected String scope;
    
    /**
     * A list of inclusion filters in the format groupId:artifactId. 
     * Defaults to all included.
     * 
     * @parameter
     */
    protected Set includes = null;

    /**
     * A list of exclusion filters in the format groupId:artifactId. 
     * Defaults to none excluded.
     * 
     * @parameter
     */
    protected Set excludes = new HashSet();

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;    

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * To look up Archiver/UnArchiver implementations
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
     * @required
     */
    protected ArchiverManager archiverManager;

    /**
     * @parameter expression="${project.build.outputDirectory}"
     * @readonly
     */
    protected File outputDirectory;

    private String metainfServices = "META-INF/services";

    public void execute() throws MojoExecutionException, MojoFailureException {
        execute(false);
    }
    
    protected void execute(boolean showInfo) throws MojoExecutionException, MojoFailureException {
        if (!showInfo && !outputDirectory.exists()) outputDirectory.mkdirs();

        if (showInfo) {
            getLog().info("Resulting jar contains the following dependencies for scope='"+scope+"'");
            getLog().info("      A = added; N = not included; X = excluded; O = out of scope;");
        }
        
        if (addTransitiveDependencies) {
            unpack(showInfo, project.getArtifacts());
        } else {
            unpack(showInfo, project.getDependencyArtifacts());
        }
    }

    private void unpack(boolean showInfo, Collection artifacts) throws MojoExecutionException, MojoFailureException {
        Map/*<String, Set<String>>*/ services = new HashMap();
        
        mergeServices(services, outputDirectory);
        for (Iterator i=artifacts.iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();

            String id = dependency.getGroupId()+":"+dependency.getArtifactId();
            if ((includes != null) && !includes.contains(id)) {
                if (showInfo) getLog().info("N   "+id);
                continue;
            }
            
            if (excludes.contains(id)) {
                if (showInfo) getLog().info("X   "+id);
                continue;
            }
            
            /*
             * Scope handling
             * 
             * scope        depScopes
             * COMPILE      COMPILE, SYSTEM, PROVIDED
             * TEST         all
             * RUNTIME      COMPILE, RUNTIME
             * SYSTEM       SYSTEM
             * PROVIDED     PROVIDED
             */
            String depScope = dependency.getScope();
            if (scope.equals(Artifact.SCOPE_COMPILE)) {
                if (!depScope.equals(Artifact.SCOPE_COMPILE)
                        && !depScope.equals(Artifact.SCOPE_SYSTEM)
                        && !depScope.equals(Artifact.SCOPE_PROVIDED)) {
                    if (showInfo)
                        getLog().info("O   " + id + " scope=" + depScope);
                    continue;
                }
            } else if (scope.equals(Artifact.SCOPE_TEST)) {
                // always added
            } else if (scope.equals(Artifact.SCOPE_RUNTIME)) {
                if (!depScope.equals(Artifact.SCOPE_COMPILE)
                        && !depScope.equals(Artifact.SCOPE_RUNTIME)) {
                    if (showInfo)
                        getLog().info("O   " + id + " scope=" + depScope);
                    continue;
                }
            } else if (scope.equals(Artifact.SCOPE_SYSTEM)) {
                if (!depScope.equals(Artifact.SCOPE_SYSTEM)) {
                    if (showInfo)
                        getLog().info("O   " + id + " scope=" + depScope);
                    continue;
                }
            } else if (scope.equals(Artifact.SCOPE_PROVIDED)) {
                if (!depScope.equals(Artifact.SCOPE_PROVIDED)) {
                    if (showInfo)
                        getLog().info("O   " + id + " scope=" + depScope);
                    continue;
                }
            } else {
                throw new MojoFailureException("Invalid requested scope '"
                        + scope + "'");
            }
            
            if (showInfo) {
                getLog().info("A   "+id+" "+dependency.getVersion());
                continue;
            }

            // FIXME reported to maven developer list, isSnapshot changes behaviour of getBaseVersion, called in pathOf.
            if (dependency.isSnapshot());
            File file = new File(localRepository.getBasedir(), localRepository.pathOf(dependency));
            unpack(file, outputDirectory);
            mergeServices(services, outputDirectory);
        }        
        writeServices(services, outputDirectory);
    }
    
    private void unpack(File file, File location) throws MojoExecutionException {
        try {
            UnArchiver unArchiver;
            unArchiver = archiverManager.getUnArchiver("jar");
            unArchiver.setSourceFile(file);
            unArchiver.setDestDirectory(location);
            unArchiver.extract();
        } catch (IOException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } catch (NoSuchArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } 
    }
    
    private void mergeServices(Map/*<String, Set<String>>*/ services, File location) throws MojoExecutionException {
        File servicesDirectory = new File(location, metainfServices);
        if (servicesDirectory.exists() && servicesDirectory.isDirectory()) {
            File[] files = servicesDirectory.listFiles();
            for (int i=0; i<files.length; i++) {
                String fileName = files[i].getName();
                Set/*<String>*/ classes = (Set)services.get(fileName);
                if (classes == null) {
                    classes = new HashSet();
                    services.put(fileName, classes);
                    System.err.println("Adding "+fileName);
                }
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(files[i]));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        classes.add(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    throw new MojoExecutionException("Problem reading services file '"+fileName+"'", e);
                }
            }
        }
    }
    
    private void writeServices(Map/*<String, Set<String>>*/ services, File location) throws MojoExecutionException {
        File servicesDirectory = new File(location, metainfServices);
        if (!servicesDirectory.exists()) servicesDirectory.mkdirs();
        
        for (Iterator i=services.keySet().iterator(); i.hasNext(); ) {
            String fileName = (String)i.next();
            File file = new File(servicesDirectory, fileName);
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(file));
                Set/*<String>*/ classes = (Set)services.get(fileName);
                for (Iterator j=classes.iterator(); j.hasNext(); ) {
                    String className = (String)j.next();
                    writer.println(className);
                }
                writer.close();
            } catch (IOException e) {
                throw new MojoExecutionException("Problem writing services file '"+fileName+"'", e);
            }
        }   
    }
}
