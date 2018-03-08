package org.freehep.util.commanddispatcher;


/**
 * A CommandTarget which does not have a selected/deselected state associated with it
 * @see BooleanCommandTarget
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public interface SimpleCommandTarget extends CommandTarget
{
   /**
    * The invoke method is called to actually perform the command.
    */
   void invoke();
}
