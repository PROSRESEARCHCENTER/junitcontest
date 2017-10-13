package org.freehep.swing.wizard;

/**
 * A WizardPage that implements this interface will have one or more subsequent
 * pages.  When a page that implements this interface is showing in a WizardDialog,
 * the "Next" button will be enabled.  A sensible wizard page would implement this
 * interface, or the Finishable interface, or both.  If you implement neither then
 * neither the "Next" nor "Finish" button will enable on the wizard, and you page
 * will be a "dead end".
 *  @author Jonas Gifford
 *  @see WizardPage
 *  @see WizardDialog
 *  @see Finishable
 */
public interface HasNextPages
{
   /**
    * This method is called when the user clicks on the "Next" button.  You must
    * return a page that was in the array returned by <code>getNextWizardPages</code>.
    *  @return a WizardPage that was included in the array returned by <code>getNextWizardPages</code>
    */
   public abstract WizardPage getNext();

   /**
    * Returns an array of all possible next pages.  There could, of course, be just
    * one element in this array.  This method will be called before the wizard
    * shows on the screen.  The wizard needs to know all of the possible pages so that
    * it can size itself to fit them all.
    *  @return an array of all possible subsequent pages
    */
   public abstract WizardPage[] getNextWizardPages();
}
