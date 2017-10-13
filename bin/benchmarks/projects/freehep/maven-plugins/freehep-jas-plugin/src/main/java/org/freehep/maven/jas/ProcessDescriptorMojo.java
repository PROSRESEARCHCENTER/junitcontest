package org.freehep.maven.jas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @description Inserts a list of required jars into plugins.xml descriptor.
 * @goal process-descriptor
 * @phase prepare-package
 * @requiresProject
 * @requiresDependencyResolution
 * @author onoprien
 * @version $Id: ProcessDescriptorMojo.java 16391 2016-03-24 22:00:56Z onoprien $
 */
public class ProcessDescriptorMojo extends AbstractMojo {

    /**
     * Skip execution if true.
     * 
     * @parameter (defaultValue="false")
     * @readonly
     */
    protected boolean skip;

    /**
     * List of dependencies to include in the descriptor. If not
     * specified (default) all non-provided dependencies will be listed.
     * If empty list is specified no dependencies will be copied.
     * Entries are to be specified using format "groupId:artifactId".
     *
     * @parameter
     */
    protected List includes = null;

    /**
     * List of dependencies to exclude from the descriptor.
     * If not specified (default), nothing is excluded.
     * If a dependency is listed in both includes and excludes, it is not included.
     * Entries are to be specified using format "groupId:artifactId".
     *
     * @parameter
     */
    protected List<String> excludes = null;
    /**
     * Map of dependencies to download URLs.
     * Entries are to be specified using format "<groupId:artifactId>URL</groupId:artifactId>".
     * If URL is not specified for a particular dependency, it is generated using <tt>urlBase</tt>
     * in the form <tt>urlBase/gr/oup/artifact/version/artifact-version.jar</tt>
     *
     * @parameter
     */
    protected Properties urls;

    /**
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    protected File outputDirectory;

    /**
     * Repository url base.
     *
     * @parameter (defaultValue="http://srs.slac.stanford.edu/nexus/content/groups/jas-plugin-public/")
     * @readonly
     */
    protected String urlBase;

    /**
     * List of redirect URLS
     *
     * @parameter
     * @readonly
     */
    protected Properties redirectUrls;
    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

      if (skip || !project.getPackaging().equals("jar") ) return;

      if (urls == null) urls = new Properties();
      if (redirectUrls == null) redirectUrls = new Properties();
      if (urlBase == null) urlBase = "http://srs.slac.stanford.edu/nexus/content/groups/jas-plugin-public/";
        
        File file = new File(outputDirectory, "classes/PLUGIN-inf/plugins.xml");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            String line;
            while ((line = reader.readLine()) != null) {
                int i = line.indexOf("</resources>");
                if (i == -1) {
                    writer.println(line);
                } else {
            listFiles(writer, i+4);
                    writer.println(line);
                }
            }
            reader.close();
            writer.flush();
            PrintWriter fileWriter = new PrintWriter(file);
            reader = new BufferedReader(new StringReader(stringWriter.toString()));
            while ((line = reader.readLine()) != null) {
                fileWriter.println(line);
            }
            reader.close();
            fileWriter.close();
        } catch (FileNotFoundException x) {
        throw new MojoFailureException("Failed to open input file: "+ x);
        } catch (IOException x) {
            throw new MojoFailureException("Error while reading: " + x);
        }
    }

    private void listFiles(PrintWriter writer, int indent) {
        String prefix = new String(new char[indent]).replace('\0', ' ');
        write(prefix, project.getArtifact(), writer);
        for (Object o : project.getArtifacts()) {
            Artifact dep = (Artifact) o;
            String scope = dep.getScope();
            if ((scope.equals(Artifact.SCOPE_COMPILE) || scope.equals(Artifact.SCOPE_RUNTIME)) && dep.getType().equals("jar")) {
                String id = dep.getArtifactId();
                String group = dep.getGroupId();
          String key = group +":"+ id;
          if ((includes == null || includes.contains(key)) && (excludes == null || !excludes.contains(key))) {
            write(prefix, dep, writer);
                }
            }
        }
    }
    
    private void write(String prefix, Artifact art, PrintWriter writer) {
        String id = art.getArtifactId();
        String group = art.getGroupId();
        String version = art.getVersion();
        String baseVersion = art.getBaseVersion();
        StringBuilder sb = (new StringBuilder()).append(prefix).append("<file href=\"");            
        String repoHref = (String) urls.get(group + ":" + id);
        if (repoHref == null) {
            repoHref = (String) urls.get(group + ":*");
        }
        if (repoHref == null) {
            repoHref = urlBase;
        }
        String repoRedirect = (String) redirectUrls.get(repoHref);
        if ( !art.isSnapshot() || repoRedirect == null || ! baseVersion.equals(version) ) {
            if (repoHref.endsWith("/")) {
                sb.append(repoHref);
                for (String dir : art.getGroupId().split("\\.")) {
                    sb.append(dir).append("/");
                }
                sb.append(id).append("/").append(baseVersion).append("/").append(id).append("-").append(version).append(".jar");
            } else {
                sb.append(repoHref);
            }
        } else {
            String href = repoRedirect;
            sb.append(href);
            sb.append("&amp;g=").append(art.getGroupId())
                    .append("&amp;a=").append(art.getArtifactId())
                    .append("&amp;v=").append(art.getBaseVersion())
                    .append("&amp;e=").append(art.getType());
            
        }
        sb.append("\" location=\"").append(id).append("-").append(version).append(".jar\"/>");
        writer.println(sb.toString());

    }
    
}
