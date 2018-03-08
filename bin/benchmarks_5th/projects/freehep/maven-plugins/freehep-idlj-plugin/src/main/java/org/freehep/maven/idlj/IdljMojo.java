// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.idlj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal generate
 * @description Compiles IDL files using the idlj compiler.
 * @phase generate-sources
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: IdljMojo.java 15852 2014-02-07 20:22:31Z onoprien $
 */
public class IdljMojo extends AbstractMojo {

    /**
     * Equivalent to "#define symbol" in IDL.
     *
     * @parameter
     */
    private List defines;
    
    /**
     * Emit all types, including those found in #include files.
     *
     * @parameter expression="${idlj.emitAll}" default-value=false"
     */
    private boolean emitAll;
       
    /**
     * Defines what bindings to emit: client, server, serverTIE, all or allTIE.
     * Assumes client if none is specified.
     *
     * @parameter
     */
    private List bindings;

    /**
     * Add include paths. By default the current directory is scanned.
     *
     * @parameter
     */
    private List includePaths;

    /**
     * If a file to be generated already exists, do not overwrite it.
     *
     * @parameter expression="${idlj.keep}" default-value="false"
     */
    private boolean keep;       
       
    /**
     * Suppress warning messages.
     *
     * @parameter expression="${idlj.noWarn}" default-value="false"
     */
    private boolean noWarn;       
       
    /**
     * Generates skeletons compatible with pre-1.4 JDK ORBs.
     *
     * @parameter expression="${idlj.oldImplBase}" default-value="false"
     */
    private boolean oldImplBase;       

    /**
     * Wherever type is encountered at the file scope, prefix the generated Java package name
     * with prefix for all files generated for that type. The type is the simple name of either
     * a top-level module, or an IDL type defined outside the scope of any module.
     *
     * @parameter
     */
    private List pkgPrefixes;
    
    /**
     * Whenever the module type is encountered in an identifier, replace it in the
     * identifier with package for all files in the generated Java package. Note that
     * pkfPrefixes changes are made first. Type is the simple name of either a top-level
     * module, or an IDL type defined outside of any module, and must match the full
     * apckage name exactly.
     *
     * @parameter
     */
    private List pkgTranslates;
    
    /**
     * Use xxx%yyy as the pattern for naming the skeletons.
     *
     * @parameter
     */
    private List skeletonNames;

    /**
     * The target directory into which to generate the output.
     *
     * @parameter expression="${project.build.directory}/generated-sources"
     * @required
     */
    private String targetDirectory;
    
    /**
     * Use xxx%yyy as the pattern for naming the ties.
     *
     * @parameter
     */
    private List tieNames;

    /**
     * The source directory.
     *
     * @parameter expression="${basedir}/src/main/idl"
     * @required
     */
    private String sourceDirectory;

    /**
     * The idl file.
     *
     * @parameter
     * @required
     */
    private String source;

    /**
     * The granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation
     *
     * @parameter expression="${idlj.staleMillis}" default-value="0"
     * @required
     */
    private int staleMillis;

    /**
     * The directory to store the processed .idl files. Defaults to targetDirectory.
     * 
     * @parameter
     */
    private String timestampDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    public void execute() throws MojoExecutionException {        
        if (!FileUtils.fileExists(targetDirectory)) {
            FileUtils.mkdir( targetDirectory );
        }

        if (project != null) {
            project.addCompileSourceRoot(targetDirectory);
        }

        if (!sourceDirectory.endsWith("/")) {
            sourceDirectory = sourceDirectory+"/";
        }

        if (timestampDirectory == null) {
            timestampDirectory = targetDirectory;
        } else {
            if (!FileUtils.fileExists(timestampDirectory)) {
                FileUtils.mkdir( timestampDirectory );
            }
        }
        
        File sourceFile = new File(sourceDirectory);
        File targetFile = new File(timestampDirectory);
        SourceInclusionScanner scanner = new StaleSourceScanner(staleMillis, Collections.singleton(source), Collections.EMPTY_SET);
        SuffixMapping mapping = new SuffixMapping( ".idl", ".flag" );
        scanner.addSourceMapping(mapping);
        try {
            Set files = scanner.getIncludedSources(sourceFile, targetFile);
        
            if (!files.isEmpty()) {
                getLog().info( "Running idlj compiler on "+source);
                runCommand(generateCommandLine());
				File flagFile = new File(timestampDirectory, FileUtils.basename(source, ".idl") + ".flag");
				FileUtils.fileDelete(flagFile.getPath());
				FileUtils.fileWrite(flagFile.getPath(), "");
            }
        } catch (InclusionScanException e) {
            throw new MojoExecutionException( "IDLJ: Source scanning failed", e );
        } catch (IOException e) {
            throw new MojoExecutionException( "IDLJ: Creation of timestamp flag file failed", e );
        }   
    }


    private String[] generateCommandLine() throws MojoExecutionException {
        
        List cmdLine = new ArrayList();
        
        cmdLine.add("idlj");

        if (defines != null) {
            for (Iterator i = defines.iterator(); i.hasNext(); ) {
                cmdLine.add("-d");
                cmdLine.add((String)i.next());
            }
        }
        
        if (emitAll) {
            cmdLine.add("-emitAll");
        }
        
        if (bindings != null) {
            for (Iterator i = bindings.iterator(); i.hasNext(); ) {
                cmdLine.add("-f"+(String)i.next());
            }
        }
        
        if (includePaths != null) {
            for (Iterator i = includePaths.iterator(); i.hasNext(); ) {
                cmdLine.add("-i");
                cmdLine.add((String)i.next());
            }
        }
        
        if (keep) {
            cmdLine.add("-keep");
        }
        
        if (noWarn) {
            cmdLine.add("-noWarn");
        }
        
        if (oldImplBase) {
            cmdLine.add("-oldImplBase");
        }
        
        if (pkgPrefixes != null) {
            for (Iterator i = pkgPrefixes.iterator(); i.hasNext(); ) {
                addParamPair(cmdLine, "-pkgPrefix", i.next());
            }
        }
        
        if (pkgTranslates != null) {
            for (Iterator i = pkgTranslates.iterator(); i.hasNext(); ) {
                addParamPair(cmdLine, "-pkgTranslate", i.next());
            }
        }
        
        if (skeletonNames != null) {
            for (Iterator i = skeletonNames.iterator(); i.hasNext(); ) {
                cmdLine.add("-skeletonName");
                cmdLine.add((String)i.next());
            }
        }
        
        cmdLine.add("-td");
        cmdLine.add(targetDirectory);        

        if (tieNames != null) {
            for (Iterator i = tieNames.iterator(); i.hasNext(); ) {
                cmdLine.add("-tieName");
                cmdLine.add((String)i.next());
            }
        }
        
        if (getLog().isDebugEnabled()) {
            cmdLine.add("-verbose");
        }
    
        cmdLine.add(sourceDirectory+source);
        
        getLog().debug(cmdLine.toString());
        
        return (String[])cmdLine.toArray(new String[cmdLine.size()]);
    }
    
    private void addParamPair(List cmdLine, String option, Object obj) throws MojoExecutionException {
        cmdLine.add(option);
        String[] param = ((String)obj).split(" ", 2);
        if (param.length != 2) throw new MojoExecutionException( "IDLJ: "+option+" takes 2 parameters");
        cmdLine.add(param[0]);
        cmdLine.add(param[1]);     
    }
    
    private int runCommand(String[] cmdLine) throws MojoExecutionException {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmdLine);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), true);
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), false);
            
            errorGobbler.start();
            outputGobbler.start();
            return process.waitFor();
        } catch (Throwable e) {
            throw new MojoExecutionException("Could not launch " + cmdLine[0], e);
        }
    }
    
    class StreamGobbler extends Thread {
        InputStream is;
        boolean error;
        
        StreamGobbler(InputStream is, boolean error) {
            this.is = is;
            this.error = error;
        }
        
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (error) {
                        getLog().error(line);
                    } else {
                        getLog().debug(line);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}