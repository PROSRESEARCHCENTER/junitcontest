// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.rmic;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Compiles class files using the rmic compiler.
 * 
 * @goal generate
 * @description Compiles class files using the rmic compiler.
 * @phase process-classes
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: RmicMojo.java 8938 2006-09-11 18:38:45Z duns $
 */
public class RmicMojo extends AbstractMojo {

    /**
     * Keep the generated .java files in the same directory as the generated
     * .class files. Same as -keep option for rmic.
     *
     * @parameter expression="${rmic.keep}" default-value="false"
     */
    private boolean keep;       
       
    /**
     * Suppress warning messages, same as -nowarn option for rmic.
     *
     * @parameter expression="${rmic.noWarn}" default-value="false"
     */
    private boolean noWarn;              
    
    /**
     * Generates debug information, same as -g option for rmic.
     *
     * @parameter expression="${rmic.debug}" default-value="false"
     */
    private boolean debug;              
    
    /**
     * Generates verbose output, same as -verbose option for rmic.
     *
     * @parameter expression="${rmic.verbose}" default-value="false"
     */
    private boolean verbose;              
    
    /**
     * Specified the version of stubs to be generated. Possible values are 1.1, 1.2 or compat (default).
     * Equivalent to -v1.1, -v1.2 and -vcompat for rmic.
     * 
     * @parameter expression="${rmic.version}" default-value="compat"
     */
    private String version;              
    
    /**
     * Causes the rmic compiler to generate OMG IDL for the classes. Same as -idl option for rmic.
     *
     * @parameter expression="${rmic.idl}" default-value="false"
     */
    private boolean idl;              
    
    /**
     * Causes the rmic compiler to generate IIOP stubs rather than JRMP stubs. Same as -iiop option for rmic.
     *
     * @parameter expression="${rmic.iiop}" default-value="false"
     */
    private boolean iiop;              
    
    /**
     * The target directory into which to generate the output, same as -d option
     * for rmic.
     *
     * @parameter expression="${project.build.directory}/classes"
     */
    private String targetDirectory;
    
    /**
     * The package-qualified-class-name(s), for example com.somecompany.SomeClass.
     *
     * @parameter expression=""
     * @required
     */
    private List classes;

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

        // FIXME runs always. we could check for class_skel.class ...
        getLog().info( "Running rmic compiler on "+classes.size()+" file(s)...");
        runCommand(generateCommandLine());
    }


    private String[] generateCommandLine() throws MojoExecutionException {
        
        List cmdLine = new ArrayList();
        
        cmdLine.add("rmic");

        cmdLine.add("-classpath");
        try {
            cmdLine.add(StringUtils.join(project.getCompileClasspathElements().iterator(), File.pathSeparator));
        } catch (Exception e) {
            throw new MojoExecutionException("RMIC, cannot get classpath", e);
        }
        
        if (keep) {
            cmdLine.add("-keep");
        }
        
        if (noWarn) {
            cmdLine.add("-noWarn");
        }        
        
        if (debug) {
            cmdLine.add("-g");
        }        
        
        if (verbose) {
            cmdLine.add("-verbose");
        }        
        
        if (version.equals("1.1")) {
            cmdLine.add("-v1.1");
        } else if (version.equals("1.2")) {
        	cmdLine.add("-v1.2");
        } else if (version.equals("compat")) {
        	cmdLine.add("-vcompat"); 
        } else {
        	throw new MojoExecutionException("RMIC Illegal value for 'version' "+version);
        }

        if (idl) {
            cmdLine.add("-idl");
        }        

        if (iiop) {
            cmdLine.add("-iiop");
        }        

        cmdLine.add("-d");
        cmdLine.add(targetDirectory);        
        
        if (getLog().isDebugEnabled()) {
            cmdLine.add("-verbose");
        }
    
        if (classes != null) {
            for (Iterator i = classes.iterator(); i.hasNext(); ) {
                cmdLine.add((String)i.next());
            }
        }
                
        getLog().info(cmdLine.toString());
        
        return (String[])cmdLine.toArray(new String[cmdLine.size()]);
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