/*
 * CorbaConverter.java
 *
 * Created on June 12, 2003, 7:00 PM
 */

package hep.aida.ref.remote.corba.converters;

import hep.aida.ref.remote.RemoteConverter;

import org.omg.CORBA.ORB;

/**
 *
 * @author  serbo
 */
public abstract class CorbaConverter extends RemoteConverter {
    
    protected org.omg.CORBA.ORB orb;
    
    protected CorbaConverter() {
        super();
        protocol = "corba";

        // Create and initialize the ORB
        String[] orbArgs = {};
        orb = ORB.init(orbArgs, null);
    }
}
