/*
 * @(#)HelpInterfaceImpl.java
 */

package jas.util;

import java.awt.Window;
import java.net.URL;
import java.util.Enumeration;

//import javax.help.DefaultHelpBroker;
//import javax.help.HelpSet;
//import javax.help.HelpSetException;
//import javax.help.Map.ID;
import javax.swing.SwingUtilities;

/**
 * HelpInterfaceImpl implements the HelpInterface.  It is the only class which contains
 * references to javax.help.*  Since it is only referred to as a String in a Class.forName
 * call, this allows the system to function if the help system is missing.
 * 
 * @author      Peter Armstrong
 *
 */
class HelpInterfaceImpl implements HelpInterface
{
	HelpInterfaceImpl() throws HelpException
	{ 
		createHelpSet();
	}
	public void showHelpTopic(String helpTopicTarget, Window owner) 
	{
		showHelpTopic(helpTopicTarget, "TOC", owner);
	}
	public void showHelpTopic(String helpTopicTarget, String navigatorView, Window owner) 
	{
//		try
//		{
//			ID id = ID.create(helpTopicTarget, mainHS);
//			if (id == null) { id = mainHS.getHomeID(); }
//
//			mainHB.setActivationWindow(owner);
//			mainHB.setCurrentView(navigatorView);
//			mainHB.setCurrentID(id);
//			mainHB.setDisplayed(true);
//	    }
//		catch (Exception eek)
//		{
//			Application.getApplication().error("Sorry, the help topic could not be found.");
//		}
	}
	public void modalDialogOpening(final java.awt.Dialog dlg)
	{
//		if (debugHelp) System.out.println("modelDialogOpening "+mainHB.isDisplayed());
		// It turns out that the modal dialog workaround must be done AFTER the dialog
		// has become modal. This code seems to do the trick, but how robust is it??
		// TODO: If we were smarter, we could keep track of who owned the help broker
		// before the dialog was opened, and put things back afterwards.
		// If java were smarter (and allowed multiple independent GUI apps within one
		// JVM), this would all be unnecessary.
//		if (mainHB.isDisplayed()) SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				mainHB.setActivationWindow(dlg);
//				mainHB.setDisplayed(true);
//			}
//		});
	}
	public void modalDialogClosing(java.awt.Dialog dlg)
	{
//		if (debugHelp) System.out.println("modelDialogClosing "+mainHB.isDisplayed());
//		mainHB.setActivationWindow(null);

	}
	/**
	 * Find the HelpSet and initialize the main HelpBroker (mainHB).
	 */
	private void createHelpSet() throws HelpException
	{
		Enumeration e = Application.getApplication().getHelpLocations();
		while (e.hasMoreElements()) 
		{
//			try
//			{
//				URL u = (URL) e.nextElement();
//				if (debugHelp) System.out.print("Looking for hs at: "+u+" ... ");
//				mainHS = new HelpSet(null, u);
//				if (debugHelp) System.out.println("Success");
//				break;
//			}
//			catch (HelpSetException e1)
//			{
//				if (debugHelp) System.out.println("Failed");
//			}
		}
//		if (mainHS == null) throw new HelpException("No HelpSet found");
//
//		try
//		{
//			mainHB = (DefaultHelpBroker) mainHS.createHelpBroker();
//		}
//		catch (Throwable x)
//		{
//			throw new HelpException("Could not create Help Broker",x);
//		}
	}
//    private HelpSet mainHS;
//	private DefaultHelpBroker mainHB;
	private final boolean debugHelp = System.getProperty("debugHelp") != null;
}
class HelpException extends NestedException
{
	HelpException(String s) { super(s); }
	HelpException(String s, Throwable t) { super(s,t); }
}
