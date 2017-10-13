/*
 * XMLIOManager.java
 *
 * Created on October 16, 2001, 8:03 AM
 */
package org.freehep.xml.io;
import java.util.*;
import org.jdom.*;

/**
 * The XMLIOManager is in charge of saving and restoring any object
 * configuration through XML. It internally assigns objects and unique
 * identification string used for cross-reference among xml nodes.
 *
 * @author  turri
 * @version 1.0
 *
 */

public class XMLIOManager {
    
    private Hashtable objIdHash = new Hashtable();
    private Hashtable idHash    = new Hashtable();
    private Hashtable nodeHash  = new Hashtable();
    private Hashtable classHash = new Hashtable();
    private Hashtable refTable  = new Hashtable();

    private XMLIORegistry xmlioRegistry;
    private XMLIOStreamManager xmlioStreamManager;
    
    private boolean useId = true;
    
    /**
     * The basic constructor.
     * @param xmlioRegistry    The XMLIORegistry from which the XMLIOManager
     *                         gets the XMLIOFactories and the XMLIOProxies to
     *                         save and restore objects.
     * @param xmlioStreamManager The xmlioStreamManager that deals with the input/output file.
     *
     */
    
/* MD: never used
    private XMLIOManager() {
        this( new DefaultXMLIORegistry(), new XMLIOFileManager("defaultXMLIOFile.xml"), true);
    }
*/
    public XMLIOManager(String fileName) {
        this( new DefaultXMLIORegistry(), new XMLIOFileManager(fileName), true);
    }

/* MD: never used
    private XMLIOManager( boolean useId ) {
        this( new DefaultXMLIORegistry(), new XMLIOFileManager("defaultXMLIOFile.xml"), useId);
    }
*/
/* MD: never used    
    private XMLIOManager( XMLIORegistry xmlioRegistry, XMLIOStreamManager xmlioFileManager ) {
        this( xmlioRegistry, xmlioFileManager, true);
    }
*/    
    private XMLIOManager( XMLIORegistry xmlioRegistry, XMLIOStreamManager xmlioFileManager, boolean useId ) {
        setXMLIORegistry( xmlioRegistry );
        setXMLIOStreamManager( xmlioFileManager );
        this.useId = useId;
    }
    
    protected void setXMLIORegistry( XMLIORegistry xmlioRegistry ) {
        this.xmlioRegistry = xmlioRegistry;
    }
    
    /**
     * Get the XMLIORegistry.
     * @return the XMLIORegistry
     *
     */
    public XMLIORegistry getXMLIORegistry() {
        return xmlioRegistry;
    }
    
    public void setXMLIOStreamManager( XMLIOStreamManager xmlioStreamManager ) {
        this.xmlioStreamManager = xmlioStreamManager;
    }
    
    public XMLIOStreamManager getXMLIOStreamManager() {
        return xmlioStreamManager;
    }
    
    /**
     * Restore an object from an xml node.
     * @param nodeEl The node containing the object's information.
     *               This method should be used by the user to restore
     *               nested objects within the restore method of an object
     *               implementing the XMLIO interface.
     * @return       The restored object. The object is first created by
     *               invoking the create method on the corresponding XMLIOFactory.
     *               Once created the restore method is invoked either on the object
     *               itself (if it implements the XMLIO interface) or on the appropriate
     *               XMLIOProxy.
     *
     */
    public Object restore( Object obj, Element nodeEl ) {
        String objIdRef = (String) nodeEl.getAttributeValue( "objIdRef" );
        if ( objIdRef != null ) 
            if ( useId )
                if ( isObjIdRegistered( objIdRef ) )
                    return getObj( objIdRef );
                else
                    return restore(obj, (Element) refTable.get(objIdRef) );
        
        String objId = (String) nodeEl.getAttributeValue( "objId" );
        if ( useId && isObjIdRegistered( objId ) ) return getObj( objId );
        
        Class objClass;
        if ( classHash.containsKey( nodeEl.getName() ) )
            objClass = (Class) classHash.get( nodeEl.getName() );
        else
            objClass = xmlioRegistry.getIdClass(nodeEl.getName());
        
        if ( obj == null )
            obj = getXMLIOFactory( objClass ).createObject( objClass );
        if ( useId ) registerObj( obj, objId );
        if ( obj instanceof XMLIO  ) ((XMLIO)obj).restore( this, nodeEl );
        else getXMLIOProxy( objClass ).restore( obj, this, nodeEl);
        return obj;
    }
    
    public Object restore( Element nodeEl ) {
        return restore(null, nodeEl);
    }
    
    /**
     * Save an object to an XML node.
     * @param obj The object to be saved.
     * @return    The jdom Element containing the object's unique
     *            identification string (in the attribute "objId") and
     *            whose name is the object's identifier. The user will have
     *            to append to this node all the attributes and other
     *            jdom elemets required to save the object's configuration.
     *
     */
    public Element saveAs( Object obj, Class clazz ) {
        String nodeName;
        if ( nodeHash.containsKey( clazz ) ) nodeName = (String) nodeHash.get(clazz);
        else nodeName = (String) xmlioRegistry.getClassId( clazz );
        Element el = new Element( nodeName );
        
        if ( isObjRegistered( obj ) ) el.setAttribute( "objIdRef", getObjId( obj ) );
        else {
            if ( useId ) el.setAttribute( "objId", registerObj( obj ) );
            if ( obj instanceof XMLIO ) ((XMLIO)obj).save( this, el );
            else getXMLIOProxy( clazz ).save( obj, this, el );
        }
        return el;
    }

    public Element save( Object obj ) {
        return saveAs( obj, obj.getClass() );
    }
    
    /**
     * The main method to save an array of objects to XML.
     * @param objs The array of objects to be saved.
     *
     */
    public void saveToXML( Object[] objs, Element nodeEl ) {
        resetObjId();
        for ( int i = 0; i < objs.length; i++ ) {
            nodeEl.addContent( save( objs[i] ) );
        }
        try {
            xmlioStreamManager.saveRootElement( nodeEl );
        } catch ( java.io.IOException ioe ) {
            System.out.println(" Problem with file "+ioe.getMessage());
        }
    }
    
    /**
     * The main method to restore from xml.
     * @return The array of the restored objects.
     *
     */
    public Object[] restoreFromXML() {
        resetObjId();
        Object[] objs = null;
        try {
            Element restoreElement = xmlioStreamManager.getRootElement();
            //Create the list of references here.
            List children = restoreElement.getChildren();
            for (Iterator it = children.iterator(); it.hasNext(); )
                loadRefTable( (Element) it.next() );
            
            objs = new Object[ children.size() ];
            int child = 0;
            for (Iterator it = children.iterator(); it.hasNext(); )
                objs[ child++ ] = restore( (Element) it.next() );
        } catch ( JDOMException je ) {
            System.out.println(" Problem with restoring "+je.getMessage());
        } catch ( java.io.IOException ioe ) {
            System.out.println(" Problem with file "+ioe.getMessage());
        }
        return objs;
    }
    
    /**
     * Assign to a Class a string identifier.
     * @param clName The Class.
     * @param id     The new identifier for the class.
     *
     */
    public void setClassId(Class clName, String id) {
        nodeHash.put(clName, id);
        classHash.put(id, clName);
    }
    
    protected void loadRefTable(Element el) {
        String objId = el.getAttributeValue("objId");
        if ( objId != null ) 
            refTable.put( objId, el );
        List children = el.getChildren();
        for (Iterator it = children.iterator(); it.hasNext(); )
                loadRefTable( (Element) it.next() );
    }
    
    /**
     * Down here are only private and protected methods.
     *
     */
    
    /**
     * Create the object's unique Id for cross-reference among xml nodes
     * @param obj the object.
     * @return    the object's unique Id.
     */
    private String makeObjId( Object obj ) {
        String clName  = obj.getClass().getName();
        String pkgName = obj.getClass().getPackage().getName();
        String idName  = clName.substring( pkgName.length()+1, clName.length() ).trim();
        while ( idName.indexOf('$') != -1 )
            idName  = idName.substring( idName.indexOf('$')+1, idName.length() ).trim();
        int i = 1;
        if ( idHash.containsKey( idName ) ) i = ( (Integer)( idHash.get( idName ) ) ).intValue() + 1;
        idHash.put( idName, new Integer( i ) );
        String objId = idName+i;
        return objId;
    }
    
    /**
     * Register object assigning an unique Id for cross-reference among xml nodes
     * @param obj the object to be registered.
     * @return    the object's unique Id.
     */
    private String registerObj( Object obj ) {
        if ( objIdHash.contains( obj ) ) return getObjId( obj );
        String objId = makeObjId( obj );
        objIdHash.put( objId, obj );
        return objId;
    }
    
    /**
     * Register object with a pre-assigned Id for cross-reference among xml nodes
     * @param obj   the object to be registered
     * @param objId the object's Id
     */
    private void registerObj( Object obj, String objId ) {
        if ( objIdHash.containsKey( objId ) ) return;
        objIdHash.put( objId, obj);
    }

    /**
     * Get the object's unique Id for cross-reference among xml nodes
     * @param obj the object whose unique id we are looking for
     * @return    the unique String Id of object <code>obj</code>
     *            if no string is found the object itself is returned
     */
    private String getObjId( Object obj ) {
        for ( Enumeration keys = objIdHash.keys(); keys.hasMoreElements(); ) {
            String key = (String) ( keys.nextElement() );
            if ( objIdHash.get( key ).equals( obj ) ) return key;
        }
        return (String)obj;
    }
    
    /**
     * Get the object corresponding to unique Id for cross-reference among xml nodes
     * @param objId the object's unique String Id
     * @return      the object corrisponding to unique Id <code>objId</code>
     *              if the object is registered or the string <code>objId</code>
     *              if the object is not registered
     *
     */
    private Object getObj( String objId ) {
        if ( objIdHash.containsKey( objId ) )
            return objIdHash.get( objId );
        return objId;
    }
    
    /**
     * Check if an object is registered
     * @param obj the object to be checked
     * @return <code>true</code> if the object is registered
     *         <code>false</code> otherwise
     *
     */
    private boolean isObjRegistered( Object obj ) {
        if ( objIdHash.contains( obj ) ) return true;
        return false;
    }
    
    /**
     * Check if an unique Id is registered
     * @param objId the unique Id to be checked
     * @return <code>true</code> if the unique Id is registered
     *         <code>false</code> otherwise
     *
     */
    private boolean isObjIdRegistered( String objId ) {
        if ( objIdHash.containsKey( objId ) ) return true;
        return false;
    }
    
    /**
     * Reset the stored ids
     */
    public void resetObjId() {
        objIdHash.clear();
        idHash.clear();
        refTable.clear();
    }
    
    /**
     * Get the XMLIOFactory to restore the xml node
     * param nodeEl the node to be restored
     * @return  the XMLIOFactory
     *
     */
    protected XMLIOFactory getXMLIOFactory( Class objClass ) {
        return xmlioRegistry.getXMLIOFactory( objClass );
    }
    
    protected XMLIOProxy getXMLIOProxy( Class objClass ) {
        return xmlioRegistry.getXMLIOProxy( objClass );
    }
    
    
}



