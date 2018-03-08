package org.freehep.util.commanddispatcher;

import java.util.Observer;

/**
 * A CommandGroup represents a collection of CommandTargets. The CommandGroup is Observable,
 * and is normally Observed by the CommandTargetManager. When the CommandGroup calls
 * its notifies its observers, the CommandTargetManager prompts each CommandSource currently
 * attached to CommandTargets within the CommandGroup to update their enabled/disabled status.
 * @author tonyj
 * @version $Id: CommandGroup.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface CommandGroup
{
   CommandTarget acceptCommand(String command);
   void addObserver(Observer observer);
   void deleteObserver(Observer observer);
   void setManager(CommandTargetManager manager);
}
