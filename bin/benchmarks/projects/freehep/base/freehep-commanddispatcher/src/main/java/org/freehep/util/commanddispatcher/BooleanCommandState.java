package org.freehep.util.commanddispatcher;


/**
 * An interface representing the state of a CommandTarget
 * that has a selected/unselected state in addition to
 * enabled/disabled. Such command targets typically correspond
 * to JCheckBoxMenuItems or JRadioButtonMenuItems.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public interface BooleanCommandState extends CommandState
{
   public void setSelected(boolean check);
}
