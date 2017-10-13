package org.freehep.maven.jas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * @description Copies artifact and dependencies to jas extensions directory.
 * @goal install
 * @phase install
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: InstallMojo.java 16391 2016-03-24 22:00:56Z onoprien $
 */
public class InstallMojo extends AbstractMojo {

    /**
     * Skip execution if true.
     * 
     * @parameter (defaultValue="false")
     * @readonly
     */
    protected boolean skip;

    /**
     * The name the artifact is translated to for JAS. 
     * Defaults to ${artifactId}
     * 
     * @parameter
     */
    protected String jarName;

    /**
     * List of dependencies to include in the copy. If not specified (default) all dependencies
     * will be copied. If empty list is specified no dependencies will be copied. Entries are to be
     * specified using format "groupId:artifactId".
     * 
     * @parameter
     */
    protected List includes = null;

    /**
     * List of dependencies to exclude in the copy with same format as the includes. If it is not
     * specified then all dependencies will be copied with the includes still applied. If both
     * includes and excludes are present, the excludes are applied after the includes.
     * 
     * @parameter
     */
    protected List excludes = null;

    // NOTE: properties did not work (mvn 2.0)
    /**
     * Translations for all dependencies.
     * 
     * @parameter
     */
    protected Map dependencyNames;

    /**
     * The directory in which to install the extensions. Defaults to "${user.home}/.JAS3"
     * 
     * @parameter expression="${jas3.user.dir}"
     */
    protected File jasUserDirectory;

    /**
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    protected File outputDirectory;

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
     * If true (default), files in the output directory will be overwritten even if they contain
     * newer version of the libraries being copied.
     * 
     * @parameter
     */
    protected boolean overwriteNewer = true;

    private File jasExtensionsDirectory;
    private Pattern versionFilePattern = Pattern.compile("([^:\\s]+):([^:\\s]+)\\s*=\\s*(\\S+)");

    public void execute() throws MojoExecutionException, MojoFailureException {

        // for (Object exclude : excludes) {
        // System.out.println("  Using exclude: " + exclude.toString());
        // }

        if (skip || project.getPackaging().equals("pom")) return;

        int copies = 0;

        if (jasUserDirectory == null) {
            jasUserDirectory = new File(System.getProperty("user.home"), ".JAS3");
        }

        jasExtensionsDirectory = new File(jasUserDirectory, "extensions");
        if (!jasExtensionsDirectory.exists()) {
            if (!jasExtensionsDirectory.mkdirs()) {
                getLog().info("Could not create the extensions directory");
                return;
            }
        }

        if (dependencyNames == null) {
            dependencyNames = new HashMap();
        }

        // copy artifact itself
        File artifact = new File(outputDirectory, project.getArtifactId() + "-" + project.getVersion() + ".jar");
        copyJar(artifact, jarName, project.getGroupId(), project.getArtifactId(), project.getVersion());
        copies++;

        // copy dependencies
        Set artifacts = project.getArtifacts();
        for (Iterator i = artifacts.iterator(); i.hasNext();) {
            Artifact dependency = (Artifact) i.next();
            String scope = dependency.getScope();
            if (scope.equals(Artifact.SCOPE_COMPILE) || scope.equals(Artifact.SCOPE_RUNTIME)) {
                if (dependency.getType().equals("jar")) {
                    // FIXME reported to maven developer list, isSnapshot
                    // changes behaviour of getBaseVersion, called in pathOf.
                    if (dependency.isSnapshot())
                        ;
                    File file = new File(localRepository.getBasedir(), localRepository.pathOf(dependency));
                    String id = dependency.getArtifactId();
                    String group = dependency.getGroupId();

                    // translate names (based on artifactId only)
                    String translation = (String) dependencyNames.get(id);

                    // copy only if in list, if list exists
                    if ((includes == null) || includes.contains(group + ":" + id)) {
                        if ((excludes == null) || !excludes.contains(group + ":" + id)) {
                            getLog().info("  Copying " + group + ":" + id);
                            copyJar(file, translation, group, id, dependency.getVersion());
                            copies++;
                        } /*
                           * else { getLog().info("  Excluded " + group + ":" + id); }
                           */
                    }
                }
            }
        }

        getLog().info("Copied " + copies + " jar file" + (copies == 1 ? "" : "s") + " to " + jasExtensionsDirectory);
    }

    private String getVersionFromFile(File file, String groupId, String artifactId) {
        String out = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine().trim();
            Matcher matcher = versionFilePattern.matcher(line);
            if (matcher.matches() && matcher.group(1).equals(groupId) && matcher.group(2).equals(artifactId)) {
                out = matcher.group(3);
            }
            reader.close();
        } catch (FileNotFoundException x) {
        } catch (IOException x) {
        } catch (NullPointerException x) {
        }
        return out;
    }

    private void copyJar(File source, String destination, String groupId, String artifactId, String version) throws MojoExecutionException {

        String jarFileName = destination == null ? (artifactId + "-" + version) : destination;
        String versionFileName = destination == null ? artifactId : destination;

        String oldVersionFileName = versionFileName;
        String oldVersion = null;

        if (oldVersion == null) {
            Pattern p = Pattern.compile("^" + artifactId + "-([.0-9]+(?:-SNAPSHOT)?)\\.jar$");
            File[] files = jasExtensionsDirectory.listFiles();
            for (File file : files) {
                String name = file.getName();
                Matcher matcher = p.matcher(name);
                if (matcher.matches()) {
                    oldVersionFileName = name.substring(0, name.length() - 4);
                    oldVersion = matcher.group(1);

                    break;
                }
            }
        }

        if (oldVersion == null) {
            (new File(jasExtensionsDirectory, artifactId + ".jar")).delete();
        } else {
            if (overwriteNewer || (new DefaultArtifactVersion(version)).compareTo(new DefaultArtifactVersion(oldVersion)) >= 0) {
                if (oldVersionFileName != null)
                    (new File(jasExtensionsDirectory, oldVersionFileName + ".version")).delete();
                if (!(new File(jasExtensionsDirectory, artifactId + "-" + version + ".jar")).delete()) {
                    if (oldVersionFileName == null || !(new File(jasExtensionsDirectory, oldVersionFileName + ".jar")).delete()) {
                        (new File(jasExtensionsDirectory, artifactId + ".jar")).delete();
                    }
                }
            } else {
                getLog().info("  -- Newer version of " + artifactId + " found - skipping");
                return;
            }
        }

        File destFile = new File(jasExtensionsDirectory, jarFileName + ".jar");
        try {
            FileUtils.copyFile(source, destFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Jas Mojo: cannot copy jar file: " + source + " to destination " + destFile, e);
        }
    }
}
