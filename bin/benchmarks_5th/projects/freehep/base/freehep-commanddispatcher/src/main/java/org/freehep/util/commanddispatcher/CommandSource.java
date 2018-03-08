package org.freehep.util.commanddispatcher;


/**
 * A CommandSource represents a source of a command, such as a MenuItem or Toolbar Button.
 * A CommandSource may have a CommandTarget associated with it. The CommandTarget represents
 * the command that will be fired when the CommandSource is activated. Typically a
 * CommandTargetManager is responsible for setting the CommandTarget.
 *
 * The CommandSource will typically be dimmed out if either there is no CommandTarget associated
 * with it, or if the CommandTarget is not enabled.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public interface CommandSource
{
   /**
    * Returns the command associated with the CommandSource
    */
   String getCommand();

   /**
    * Sets a CommandTarget associated with the CommandSource
    */
   boolean setTarget(CommandTarget target);

   /**
    * Returns the current CommandTarget, or null of there isnt a current command target
    */
   CommandTarget getTarget();

   /**
    * Clears the CommandTarget associated with the CommandSource
    */
   void clearTarget();
}
