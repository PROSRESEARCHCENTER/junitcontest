// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Mark Donszelmann
 * @version $Id: Generator.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface Generator
{
    Properties getProperties();
    public String directory(IClass clazz);
    public String filename(IClass clazz);
    /**
     * @return true to stop generating more files
     */
	public boolean print(File file, IClass clazz) throws IOException;
}

