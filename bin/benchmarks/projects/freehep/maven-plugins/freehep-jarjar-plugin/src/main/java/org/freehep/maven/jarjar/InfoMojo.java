// Copyright FreeHEP, 2005.
package org.freehep.maven.jarjar;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Shows what the resulting jar consists of.
 * @description Shows what the resulting jar consists of.
 * @goal info
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: InfoMojo.java 8947 2006-09-12 18:16:26Z duns $
 */
public class InfoMojo extends JarJarMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        execute(true);
    }   
}
