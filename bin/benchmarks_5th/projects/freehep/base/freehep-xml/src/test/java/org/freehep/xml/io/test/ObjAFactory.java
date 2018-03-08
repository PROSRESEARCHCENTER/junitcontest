/*
 * ObjAFactory.java
 *
 * Created on July 15, 2002, 11:50 AM
 */

package org.freehep.xml.io.test;
import org.freehep.xml.io.XMLIOFactory;

/**
 *
 * @author  turri
 */
public class ObjAFactory implements XMLIOFactory {


    private Class[] classes;

    /** Creates a new instance of ObjAFactory */
    public ObjAFactory() {
        classes = new Class[1];
        classes[0] = ObjA.class;
    }
    
    /**
     * Create XMLIO objects
     * @return the XMLIO object in the standard configuration
     *
     */
    public Object createObject(Class objClass) {
        if ( objClass == ObjA.class ) return new ObjA();
        else throw new IllegalArgumentException("ObjAFactory cannot create object of class "+objClass);
    }
    
    /** Returns the identifier of the objects that the factory is
     * able to restore. This identifier is left by the object
     * in the jdom node during the save procedure and should be
     * used to identify the objectFactory during the restore phase.
     * @return    The identifiers
     *
     */
    public Class[] XMLIOFactoryClasses() {
	return classes;
    }    
}
