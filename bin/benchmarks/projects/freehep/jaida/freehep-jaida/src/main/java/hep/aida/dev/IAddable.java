/*
 * IAddable.java
 *
 * Created on March 27, 2007, 1:58 PM
 *
 */

package hep.aida.dev;

import hep.aida.IManagedObject;

/**
 *
 * @author serbo
 */
public interface IAddable {
    
    void add(String path, IManagedObject object) throws IllegalArgumentException;

    void hasBeenFilled(String path) throws IllegalArgumentException;
    
    void mkdirs(String path) throws IllegalArgumentException; 
}
