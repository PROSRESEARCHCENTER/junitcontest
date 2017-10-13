// Copyright FreeHEP, 2005.
package org.freehep.maven.aid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal generate
 * @description Generates interfaces in several languages from AID (Abstract
 * Interface Definition) files.
 * @phase generate-sources
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: AidMojo.java 15793 2014-01-16 23:52:19Z turri $
 */
public class AidMojo extends AbstractMojo {

    /**
     * The generator to use to generate output files Possible choices are
     * JavaInterfaceGenerator, JavaClassGenerator, CPPHeaderGenerator and
     * PythonClassGenerator
     *
     * @parameter expression="${aid.generator}"
     * default-value="JavaInterfaceGenerator"
     * @required
     */
    private String generator;
    /**
     * The source file directory.
     *
     * @parameter expression="${basedir}/src/main/aid"
     * @required
     */
    private File configDirectory;
    /**
     * The configuration file directory.
     *
     * @parameter expression="${basedir}/src/main/aid"
     * @required
     */
    private File sourceDirectory;
    /**
     * The target directory into which to generate the output
     *
     * @parameter expression="${project.build.directory}/generated-sources/aid"
     * @required
     */
    private File targetDirectory;
    /**
     * A list of inclusion filters for AID. Defaults to **\/*.aid and
     * **\/*.properties
     *
     * @parameter
     */
    private Set includes = new HashSet();
    /**
     * A list of exclusion filters for AID.
     *
     * @parameter
     */
    private Set excludes = new HashSet();
    /**
     * The granularity in milliseconds of the last modification date for testing
     * whether a source needs recompilation
     *
     * @parameter expression="${aid.staleMillis}" default-value="0"
     * @required
     */
    private long staleMillis;
    /**
     * The directory to store the timestampfile for the processed aid files.
     * Defaults to targetDirectory.
     *
     * @parameter
     */
    private File timestampDirectory;
    /**
     * The timestampfile for the processed aid files. Defaults to generator.
     *
     * @parameter
     */
    private String timestampFile;
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException {

        System.out.println("Executing AID plugin with " + configDirectory.getPath() + " " + targetDirectory.getPath() + " " + sourceDirectory.getPath() + " " + staleMillis);

        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        if (project != null) {
            project.addCompileSourceRoot(targetDirectory.getPath());
        }

        if (timestampDirectory == null) {
            timestampDirectory = targetDirectory;
        } else {
            if (!timestampDirectory.exists()) {
                timestampDirectory.mkdirs();
            }
        }

        if (timestampFile == null) {
            timestampFile = generator;
        }

        if (includes.isEmpty()) {
            includes.add("**/*.aid");
            includes.add("**/*.properties");
        }

        try {


            File tsFile = new File(timestampDirectory, timestampFile);
            if (!tsFile.exists() || (tsFile.lastModified() + staleMillis) < System.currentTimeMillis()) {

                // we need all source files (due to aid).
                StaleSourceScanner scanner = new StaleSourceScanner(staleMillis, includes,
                        excludes);
                scanner.addSourceMapping(new SuffixMapping(".aid", ".dummy"));
                Set sources = scanner.getIncludedSources(sourceDirectory,
                        targetDirectory);

                getLog().info(
                        "Running aid compiler with " + generator + " on "
                        + sources.size() + " file(s)...");

                org.freehep.aid.cli.Aid.runMain(generateAidArgumentList(sources));
                FileUtils.fileWrite(timestampDirectory + "/" + timestampFile,
                        "");

            }
        } catch (InclusionScanException e) {
            throw new MojoExecutionException("AID: Source scanning failed", e);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "AID: Copy of timestamp file failed", e);
        } catch (Exception e) {
            throw new MojoExecutionException("AID: execution failed", e);
        }

    }

    private String[] generateAidArgumentList(Set sources)
            throws MojoExecutionException {

        List argList = new ArrayList();

        if (getLog().isDebugEnabled()) {
            argList.add("-verbose");
        }

        // FIXME, rename these options according to above
        argList.add("-directory");
        argList.add(targetDirectory.getPath());

        argList.add("-property");
        argList.add(configDirectory.getPath());

        argList.add(generator);

        for (Iterator i = sources.iterator(); i.hasNext();) {
            argList.add(((File) i.next()).getPath());
        }

        getLog().debug("aid " + argList.toString());

        return (String[]) argList.toArray(new String[argList.size()]);
    }
}
