package org.freehep.application;
import javax.swing.BoundedRangeModel;

/**
 * A interface to be implemented by things that can be stopped.
 * @author  tonyj
 * @version $Id: Stoppable.java 14082 2012-12-12 16:16:53Z tonyj $
 * @see StatusBar
 */
public interface Stoppable 
{
    BoundedRangeModel getModel();
    void stop();
}
