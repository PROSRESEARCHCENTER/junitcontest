/*
 * ObjBCFactory.java
 *
 * Created on July 15, 2002, 11:50 AM
 */

package org.freehep.xml.io.test;
import org.freehep.xml.io.XMLIOFactory;

/**
 *
 * @author  turri
 */
public class ObjBCDEFactory implements XMLIOFactory {
    
    private Class[] classes;
    
    /** Creates a new instance of ObjBCFactory */
    public ObjBCDEFactory() {
        classes = new Class[4];
        classes[0] = ObjB.class;
        classes[1] = ObjC.class;
        classes[2] = ObjD.class;
        classes[3] = ObjE.class;
    }
    
    /**
     * Create XMLIO objects
     * @return the XMLIO object in the standard configuration
     *
     */
    public Object createObject(Class objClass) {
        if ( objClass == ObjB.class ) return new ObjB();
        else if ( objClass == ObjC.class ) return new ObjC();
        else if ( objClass == ObjD.class ) return new ObjD();
        else if ( objClass == ObjE.class ) return new ObjE();
        else throw new IllegalArgumentException("ObjBCDEFactory cannot create object of class "+objClass);
    }
    
    /** Returns the identifier of the objects that the factory is
     * able to restore. This identifier is left by the object
     * in the jdom node during the save procedure and should be
     * used to identify the objectFactory during the restore phase.
     * @return    The classes
     *
     */
    public Class[] XMLIOFactoryClasses() {
	return classes;
    }    
}
