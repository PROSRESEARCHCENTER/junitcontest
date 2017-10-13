/*
 * testXMLIO.java
 * JUnit based test
 *
 * Created on July 15, 2002, 11:16 AM
 */

package org.freehep.xml.io.test;

import junit.framework.*;
import java.util.*;
import org.freehep.xml.io.*;
import org.jdom.*;



/**
 *
 * @author turri
 */
public class testXMLIO extends TestCase {
    
    protected XMLIOManager     xmlioManager     = new XMLIOManager("testXMLIO.xml");
    
    private Vector saveVect = new Vector();
    private Vector restoreVect = new Vector();
    private Vector createdVect = new Vector();
    
    private Random r;
    
    private int nAvailableObjs = 6;
    private int maxNumberObjs = 10;
    private int maxNDaughters = 7;
    
    public testXMLIO(java.lang.String testName) {
        super(testName);
        xmlioManager.setClassId( ObjA.class, "arbor");
        xmlioManager.getXMLIORegistry().register( new ObjAFactory() );
        xmlioManager.getXMLIORegistry().register( new ObjBCDEFactory() );
        xmlioManager.getXMLIORegistry().register( new ObjDProxy() );
        xmlioManager.getXMLIORegistry().register( (XMLIOProxy) new ObjEFProxyFFactory() );
        r = new Random();
        xmlioManager.setClassId( ObjD.class, "dusk");
    }
    
    public static void main(java.lang.String[] args) {
        for ( int i = 0; i < 30; i++ )
            junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(testXMLIO.class);
        return suite;
    }
    
    public void testSaveRestore() {
        
        int startObjs = r.nextInt(maxNumberObjs) + 1;
        
        for ( int i = 0; i < startObjs; i++ ) {
            Object obj = createObject(r.nextInt(nAvailableObjs), r.nextInt(), 0);
            saveVect.add( obj );
        }

        Element saveEl = new Element("testXMLIO");
        xmlioManager.saveToXML( saveVect.toArray(), saveEl );

        
        Object[] objs = xmlioManager.restoreFromXML();
        for ( int i = 0; i < objs.length; i++ )
            restoreVect.add(objs[i]);

        
        assertEquals(saveVect.size(), startObjs);
        assertEquals(saveVect.size(), restoreVect.size());
        
        for ( int i = 0; i < startObjs; i++ ) {
            assertTrue( ((AbstractObj)saveVect.get(i)).equalsObj( restoreVect.get(i) ) );
        }
    }
        
    private Object createObject( int index, int status, int treeLevel ) {
        Object obj;
        
        int nCreatedObjs = createdVect.size();
        
        if ( r.nextDouble() < .2 && nCreatedObjs > 0 )
            obj = createdVect.get( r.nextInt( nCreatedObjs ) );
        else {            
            switch ( index ) {
                case 0 :
                    obj = new ObjA(status);
                    break;
                case 1 :
                    obj = new ObjB(status);
                    break;
                case 2 :
                    obj = new ObjC(status);
                    break;
                case 3 :
                    obj = new ObjD(status);
                    break;
                case 4 :
                    obj = new ObjE(status);
                    break;
                case 5 :
                    obj = new ObjF(status);
                    break;
                default :
                    throw new RuntimeException("Wrong index for object creation");
            }
            createdVect.add( obj );

            int daus = r.nextInt(maxNDaughters-treeLevel);
            for ( int j = 0; j<daus; j++ )
                ((AbstractObj)obj).addObject(createObject(r.nextInt(nAvailableObjs), r.nextInt(), treeLevel+1));
        }
        return obj;
    }
        
}
