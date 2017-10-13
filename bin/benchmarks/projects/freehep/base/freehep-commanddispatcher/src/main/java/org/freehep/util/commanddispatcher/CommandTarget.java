package org.freehep.util.commanddispatcher;


/**
 * A CommandTarget represents a single action that will be performed as a result of a command
 * being issued. CommandTargets can be enabled or disabled. A set of CommandTargets are
 * typically grouped together into a CommandProcessor.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public interface CommandTarget
{
   /**
    * Gets the CommandGroup associated with this CommandTarget.
    */
   CommandGroup getGroup();

   /**
    * Called to determine if CommandTarget is enabled or disabled.
    */
   void enable(CommandState state);
}
