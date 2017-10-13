/*
 * DefaultXMLIORegistry.java
 */

package org.freehep.xml.io;
import java.util.Hashtable;
/**
 * Default XMLIORegistry.
 * @see XMLIORegistry
 * @author  turri
 * @version 1.0
 */

public class DefaultXMLIORegistry implements XMLIORegistry {
    private Hashtable factoryHash  = new Hashtable();
    private Hashtable proxyHash    = new Hashtable();
    private Hashtable classIds     = new Hashtable();
    private Hashtable idClasses    = new Hashtable();
    
    /**
     * Register an XMLIOFactory or an XMLIOProxy.
     * param xmlioFactory the XMLIOFactory being registered
     * param factoryId    the identification String.
     *
     */
    public void register( Object obj ) {
        if ( !(obj instanceof XMLIOFactory) && !(obj instanceof XMLIOProxy) ) throw new IllegalArgumentException("Cannot register object "+obj+"."+
        "Only XMLIOFactories and XMLIOProxies can be registered!");
        if ( obj instanceof XMLIOFactory ) {
            XMLIOFactory xmlioFactory = (XMLIOFactory)obj;
            Class[] classes = xmlioFactory.XMLIOFactoryClasses();
            for ( int i = 0; i < classes.length; i++ ) {
                if ( factoryHash.containsKey( classes[i] ) ) throw new IllegalArgumentException("Identifier "+classes[i]+" has already been registered!!");
                factoryHash.put( classes[i], xmlioFactory );
                addClassId( classes[i] );
            }
        }
        if ( obj instanceof XMLIOProxy ) {
            XMLIOProxy xmlioProxy = (XMLIOProxy)obj;
            Class[] classes = xmlioProxy.XMLIOProxyClasses();
            for ( int i = 0; i < classes.length; i++ ) {
                if ( proxyHash.containsKey( classes[i] ) ) throw new IllegalArgumentException("Class "+classes[i]+" has already been registered!!");
                proxyHash.put( classes[i], xmlioProxy );
            }
        }
    }
    
    private void addClassId( Class clazz ) {
        String objClassString = clazz.toString();
        String classId = objClassString.substring( objClassString.lastIndexOf(".")+1 );
        classId = classId.replace('$','-');
        if ( classIds.containsValue(classId) ) throw new RuntimeException("Already registered ClassId in the registry!!! "+classId);
        classIds.put(clazz,classId);
        idClasses.put(classId,clazz);
    }
    
    /**
     * Get the XMLIOFactory corresponding to the identification
     * string <code>factoryId<\code>
     * param factoryId the identification String
     * @return the XMLIOFactory corresponding to <code>factoryId<\code>
     *
     */
    public XMLIOFactory getXMLIOFactory(Class objClass) {
        return (XMLIOFactory) factoryHash.get( objClass );
    }
    
    
    public XMLIOProxy getXMLIOProxy(Class objClass) {
        return (XMLIOProxy) proxyHash.get( objClass );
    }
    
    public String getClassId( Class clName ) {
        if ( ! classIds.containsKey(clName ) )
            addClassId(clName);
        return (String) classIds.get( clName );
    }
    
    public Class getIdClass(String objName) {
        return (Class) idClasses.get(objName);
    }
    
}
