package jas.util;
/**
 * A JASWizardPage that implements this interface can be the final page
 * on a JASWizard.  When a Finishable page is displayed on the wizard, the
 * "Finish" button is enabled.  A sensible wizard page would implement either
 * this interface, or HasNextPages, or both.  If your page implemented neither
 * then neither the "Next" nor "Finish" button would enable, so your page
 * would be a "dead end."
 *  @author Jonas Gifford
 *  @see JASWizardPage
 *  @see JASWizard
 *  @see HasNextPages
 */
public interface Finishable
{
	/**
	 * Invoked by the JASWizard when the used clicks on
	 * the "Finish" button.
	 *  @see JASWizard
	 */
	public abstract void onFinish();
}
