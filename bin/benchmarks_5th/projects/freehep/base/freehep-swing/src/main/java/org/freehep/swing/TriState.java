// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing;



/**
 * @author Mark Donszelmann
 * @version $Id: TriState.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TriState {

    public int getTriState(); 
    
    public void setTriState(int state);
    public void setTriState(boolean state);    
}
  
