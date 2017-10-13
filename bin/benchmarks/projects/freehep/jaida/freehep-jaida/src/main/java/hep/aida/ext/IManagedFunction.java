/*
 * IManagedFunction.java
 *
 * Created on October 8, 2002, 4:56 PM
 */

package hep.aida.ext;

import hep.aida.IFunction;
import hep.aida.IManagedObject;

/**
 *
 * @author  serbo
 */
public interface IManagedFunction extends IFunction, IManagedObject {

    /*
     * Set name for IManagedObject
     */
    public void setName(String name);
}
