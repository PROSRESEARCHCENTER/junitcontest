package org.freehep.swing.wizard;

/**
 * A WizardPage that implements this interface can link the user to
 * a help topic.  When a HasHelpPage page is showing, the "Help" button
 * will enable.
 *  @see WizardPage
 *  @author Jonas Gifford
 *  @author Peter Armstrong
 */
public interface HasHelpPage
{
   /**
    * Returns the help topic to open if the user clicks on the "Help" button.  This
    * string must be one of the XML targets.  (This is a cheesy adaptation of
    * what Jonas built for the Oracle Help system.)
    */
   public abstract String getHelpTopic();
}
