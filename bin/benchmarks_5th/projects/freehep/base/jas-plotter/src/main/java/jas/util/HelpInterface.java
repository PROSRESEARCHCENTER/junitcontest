package jas.util;
import java.awt.Dialog;
import java.awt.Window;
 
/**
 * HelpInterface is the interface which users of the help system deal with.
 * 
 * @author      Peter Armstrong
 *
 */

public interface HelpInterface 
{
	/**
	 * Shows the specified JavaHelp topic with the TOC visible 
	 *  @param helpTopicTarget the JavaHelp XML target name which maps to the .html page in the map file
	 */
	void showHelpTopic(String helpTopicTarget, Window owner);

	/**
	 * Shows the specified JavaHelp topic according to the display parameters provided.
	 *  @param helpTopicTarget the JavaHelp XML target name which maps to the .html page in the map file
	 *  @param navigatorView the string specifying which of the three views to have visible
	 */
	void showHelpTopic(String helpTopicTarget, String navigatorView, Window owner);

	void modalDialogOpening(Dialog dlg);
	void modalDialogClosing(Dialog dlg);
}
