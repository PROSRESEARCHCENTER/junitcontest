package org.freehep.util.commanddispatcher;


/**
 * An interface representing the state of a CommandTarget
 * (enabled or disabled, text and tooltip text)
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: CommandState.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface CommandState
{
   public void setEnabled(boolean state);

   public void setText(String text);

   public void setToolTipText(String text);
}
