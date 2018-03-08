package org.freehep.maven.jas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @description Inserts a list of required jars into plugins.xml descriptor.
 * @goal submit-descriptor
 * @phase deploy
 * @requiresProject
 * @requiresDependencyResolution
 * @author onoprien
 * @version $Id: SubmitDescriptorMojo.java 16391 2016-03-24 22:00:56Z onoprien $
 */
public class SubmitDescriptorMojo extends AbstractMojo {

    /**
     * Skip execution if true.
     * 
     * @parameter (defaultValue="false")
     * @readonly
     */
    protected boolean skip;

    /**
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    protected File outputDirectory;

    /**
     * URL for submitting plugin descriptors.
     * 
     * @parameter (defaultValue="http://jas.freehep.org/jas3-plugins/upload.jsp")
     * @readonly
     */
    protected String urlDescriptorDataBase;

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;    
    
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    if (skip || !project.getPackaging().equals("jar")) return;
    if (urlDescriptorDataBase == null) urlDescriptorDataBase = "http://jas.freehep.org/jas3-plugins/upload.jsp";

    File descriptorFile = new File(outputDirectory, "classes/PLUGIN-inf/plugins.xml");
    String boundary = Long.toHexString(System.currentTimeMillis());

    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(urlDescriptorDataBase).openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      PrintWriter writer = null;
      try {
        writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.print("--" + boundary +"\r\n");
        writer.print("Content-Disposition: form-data; name=\"xml\"; filename=\"plugins.xml\"\r\n");
        writer.print("Content-Type: text/xml; charset=UTF-8\r\n");
        writer.print("\r\n");
        BufferedReader reader = null;
        try {
          reader = new BufferedReader(new InputStreamReader(new FileInputStream(descriptorFile), "UTF-8"));
          String line;
          while ((line = reader.readLine()) != null) {
            writer.print(line +"\r\n");
          }
        } finally {
          if (reader != null) {
            try {
              reader.close();
            } catch (IOException x) {
            }
          }
        }

        writer.print("--" + boundary + "--\r\n");
      } finally {
        if (writer != null) {
          writer.close();
        }
      }      

      int responseCode = connection.getResponseCode();
      String responseMessage = connection.getResponseMessage();
      
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      boolean ok = false;
      String line;
      while ((line = in.readLine()) != null) {
        if (line.contains("File uploaded successfully")) {
          ok = true;
          break;
        }
      }
      in.close();
      
      if (responseCode == 200 && ok) {
        getLog().info("Plugin descriptor for "+ project.getName() +":"+ project.getVersion() +" submitted successfully: "+ responseMessage);
      } else {
        getLog().error("Failed to submit plugin descriptor, response code "+ responseCode +" : "+ responseMessage);
        throw new MojoFailureException(responseMessage);
      }
    } catch (FileNotFoundException x) {
    } catch (IOException x) {
      getLog().error("Failed to submit plugin descriptor: "+ x);
      throw new MojoFailureException("Failed to submit plugin descriptor: "+ x);
    }

  }
     
    
}
