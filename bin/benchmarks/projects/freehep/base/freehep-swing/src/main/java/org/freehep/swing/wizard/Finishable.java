package org.freehep.swing.wizard;

/**
 * A WizardPage that implements this interface can be the final page
 * on a WizardDialog.  When a Finishable page is displayed on the wizard, the
 * "Finish" button is enabled.  A sensible wizard page would implement either
 * this interface, or HasNextPages, or both.  If your page implemented neither
 * then neither the "Next" nor "Finish" button would enable, so your page
 * would be a "dead end."
 *  @author Jonas Gifford
 *  @see WizardPage
 *  @see WizardDialog
 *  @see HasNextPages
 */
public interface Finishable
{
   /**
    * Invoked by the WizardDialog when the used clicks on
    * the "Finish" button.
    *  @see WizardDialog
    */
   public abstract void onFinish();
}
