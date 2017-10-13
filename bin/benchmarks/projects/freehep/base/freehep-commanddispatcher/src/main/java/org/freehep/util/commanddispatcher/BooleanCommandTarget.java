package org.freehep.util.commanddispatcher;


/**
 * A boolean command target is a CommandTarget which corresponds to a command which
 * may have an on/off state associated with it.
 * @see SimpleCommandTarget
 * @see CommandTarget
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: BooleanCommandTarget.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface BooleanCommandTarget extends CommandTarget
{
   /**
    * Called when the on/off state changes (i.e. when the comamnd is invoked).
    */
   void invoke(boolean onOff);
}
