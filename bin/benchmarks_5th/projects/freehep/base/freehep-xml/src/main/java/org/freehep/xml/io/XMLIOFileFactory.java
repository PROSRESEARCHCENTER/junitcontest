/*
 * XMLIOFileFactory.java
 *
 * Created on October 18, 2001, 3:39 PM
 */

package org.freehep.xml.io;
/**
 * @author turri
 * @version $Id: XMLIOFileFactory.java 8584 2006-08-10 23:06:37Z duns $
 */
public class XMLIOFileFactory implements XMLIOFactory
{

    private Class[] classes;
    
    /** 
     * Creates a new instance of XMLIOFileFactory
     */
    public XMLIOFileFactory() {
        classes = new Class[1];
        classes[0] = XMLIOFile.class;
    }

    public Object createObject(Class objClass) {
        if ( objClass == XMLIOFile.class ) return new XMLIOFile();
        else throw new IllegalArgumentException("XMLIOFileFactory cannot create object of class "+objClass);
    }
    
    public Class[] XMLIOFactoryClasses() {
	return classes;
    }
}
