/*
 * AbstractObj.java
 *
 * Created on July 15, 2002, 11:24 AM
 */

package org.freehep.xml.io.test;
import java.util.*;

/**
 *
 * @author  turri
 */
public abstract class AbstractObj {
    
    private int internalStatus = 0;
    protected Vector vect = new Vector();
    
    
    /** Creates a new instance of AbstractObj */
    public AbstractObj() {
        this(0);
    }
    
    public AbstractObj(int status) {
        setStatus( status );
    }
    
    public void setStatus( int status ) {
        this.internalStatus = status;
    }
    
    public int getStatus() {
        return internalStatus;
    }
    
    public void addObject( Object obj ) {
        vect.add(obj);
    }
    
    public boolean equalsObj( Object obj ) {
        Vector equalVect = new Vector();
        equalVect.add(this);
        if ( obj == this ) return true;
        return equalsObj( obj, equalVect, "" );
    }
    
    public boolean equalsObj( Object obj, Vector equalVect, String indent ) {
        if ( obj.getClass() != this.getClass() ) return false;
        if ( ((AbstractObj)obj).getStatus() != getStatus() ) return false;
        for ( int i = 0; i < vect.size(); i++ ) {
            if ( ! equalVect.contains( this.vect.get(i) ) ) {
                equalVect.add( this.vect.get(i) );
                if ( ! ((AbstractObj)((AbstractObj)obj).vect.get(i)).equalsObj( this.vect.get(i), equalVect, indent+"  " ) ) return false;
            }
        }
        return true;
    }
    

    public void print() {
        Vector printVect = new Vector();
        printVect.add(this);
        System.out.println(getClass()+" "+internalStatus);
        print( printVect, "  " );
    }
    
    public void print( Vector printVect, String indent ) {
        for ( int i = 0; i < vect.size(); i++ ) {
            AbstractObj tmpObj = (AbstractObj)vect.get(i);
            System.out.println(indent+tmpObj.getClass()+" "+tmpObj.getStatus());
            if ( ! printVect.contains( tmpObj ) ) {
                printVect.add( tmpObj );
                tmpObj.print( printVect, indent+"  " );
            }
        }
    }    
}
