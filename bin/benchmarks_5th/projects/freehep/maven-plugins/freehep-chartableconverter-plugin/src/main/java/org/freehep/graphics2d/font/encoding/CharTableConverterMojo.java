// Copyright FreeHEP, 2005.
package org.freehep.graphics2d.font.encoding;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal generate
 * @description Generates lookup tables from 16 bit Unicode text files.
 * @phase generate-sources
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: CharTableConverterMojo.java 8584 2006-08-10 23:06:37Z duns $
 */
public class CharTableConverterMojo extends AbstractMojo {

    /**
     * The target directory into which to generate the output
     *
     * @parameter expression="${project.build.directory}/generated-sources/encoding"
     * @required
     */
    private String targetDirectory;

    /**
     * The name of the package to be used in the generated lookup classes.
     *
     * @parameter expression=""
     * @required
     */
    private String packageName;

    /**
     * The configuration file directory.
     *
     * @parameter expression="${basedir}/src/main/encoding"
     * @required
     */
    private String sourceDirectory;

     /**
     * The 16 bit Unicode text file.
     *
     * @parameter expression=""
     * @required
     */
    private String source;

     /**
     * The 16 bit Unicode text file.
     *
     * @parameter expression=""
     */
    private String encodingType = null;

   /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;
    
    public void execute() throws MojoExecutionException {        
        if (!FileUtils.fileExists(targetDirectory)) {
            FileUtils.mkdir( targetDirectory );
        }

        try {
            org.freehep.graphics2d.font.encoding.CharTableConverter.main(generateArgumentList());
        } catch (Exception e) {
            throw new MojoExecutionException( "CHarTableConverter execution failed", e );
        }      

        if (project != null) {
           project.addCompileSourceRoot(targetDirectory);
        }
    }


    private String[] generateArgumentList() throws MojoExecutionException {
        
        List argList = new ArrayList();
        
        argList.add(targetDirectory);        
        argList.add(packageName);
    
        // FIXME, check for source out-of-date
        if (!sourceDirectory.endsWith("/")) {
            sourceDirectory = sourceDirectory+"/";
        }

        argList.add(sourceDirectory+source);
        
        if (encodingType != null) argList.add(encodingType);

        getLog().debug("CharTableConverter " + argList.toString());
        
        return (String[])argList.toArray(new String[argList.size()]);
    }
}