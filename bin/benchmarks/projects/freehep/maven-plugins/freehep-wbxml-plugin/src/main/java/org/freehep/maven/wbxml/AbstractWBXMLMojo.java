package org.freehep.maven.wbxml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractWBXMLMojo extends AbstractMojo {

    /**
     * The source directory.
     *
     * @parameter expression="${basedir}/src/main/wbxml"
     * @required
     */
    protected String sourceDirectory;

    /**
     * The wbxml definition file.
     *
     * @parameter
     * @required
     */
    protected String source;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

	protected List/*<Tag>*/ tags;
	protected List/*<Attribute>*/ attributes;
	
	protected void readWBXML(String name) throws IOException {
		tags = new ArrayList();
		attributes = new ArrayList();
		
		BufferedReader reader = new BufferedReader(new FileReader(name+".wbxml"));
		
		String line;
		boolean readingTags = false;
		boolean readingAttributes = false;
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			// skip comments
			if (line.length() == 0) continue;
			if (line.startsWith("#")) continue;
			
			if (line.startsWith("[tags]")) {
				readingTags = true;
				readingAttributes = false;
			} else if (line.startsWith("[attributes]")) {
				readingTags = false;
				readingAttributes = true;
			} else {
				if (readingTags) {
					tags.add(new Tag(line.split("( |\t)+", 5)));
				} else if (readingAttributes) {
					attributes.add(new Attribute(line.split("( |\t)+", 5)));
				} else {
					throw new IOException("Needs to either have a line with [tags] or [attributes] before the first definition");
				}
			}
 		}
		reader.close();
	}
}
