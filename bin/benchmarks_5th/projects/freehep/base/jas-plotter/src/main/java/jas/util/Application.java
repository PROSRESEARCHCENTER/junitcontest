package jas.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 * This class is meant to provide access from all packages to
 * certain general-purpose items of the application, such as
 * the UserProperties objects that the application
 * uses, and the JFrame that the application is in.  It allows
 * classes from all packages to show help topics.  However,
 * getting the actual topics themselves is the responsibility
 * of a class which subclasses Application.
 *  @see UserProperties
 *	@see javax.swing.JFrame
 *  @author Jonas Gifford
 *  @author Peter Armstrong
 */
abstract public class Application extends javax.swing.JPanel
{	
	/**
	 * Creates an instance of the Application.  This constructor may
	 * only be called once in an application.
	 */
	public Application()
	{
		super(new BorderLayout());
		if (app != null) throw new RuntimeException("Cannot make two applications.");
		app = this;
		
		//Add the options that the Application needs.
		gopt.addOption("debug", 'd', false, "Takes a : separated list of debug modes to set");
		gopt.addOption("help",		'h', true, "Print this message");
		
		//Register the help and F1 keys to show the help contents.
		ActionListener helpListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				showHelpContents();
			}
		};	
		registerKeyboardAction(	helpListener,
								KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0),
								JComponent.WHEN_IN_FOCUSED_WINDOW
							  );
		registerKeyboardAction(	helpListener,
								KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
								JComponent.WHEN_IN_FOCUSED_WINDOW
							  );		
	}
	/**
	 * Called to parse the command line arguments (after setting appropriate
	 * options)
	 * @param argv The command line arguments
	 * @return The number of parameters found
	 * @see #getGetOptions
	 */
	protected int parseArgs(String[] argv)
	{
		//Now parse the command line arguments and act accordingly
		try 
		{
			gopt.parseArgs(argv);
		} 
		catch (GetOptions.BadArguments x) 
		{
			doHelp();
			System.out.println("The command line options you specified had the following problem:\n" + x.getMessage());
			System.exit(0);
		}
		if (gopt.hasOption("help")) 
		{
			doHelp();
			System.exit(0);
		}
		setDebugFlags();
		return gopt.numParams();
	}
	/**
	 * Called to show user the syntax of the command, as a result of a syntax error
	 * in the command line arguments, or in response to the --help option
	 * Override to provide specialized instructions for your application
	 */
	protected void doHelp()
	{
		gopt.dumpOptions();
	}
	
	private void initializeHelpSystem() throws Exception
	{
		// Implemented this way so the appplication still runs even if the jar file
		// with the help system is not in the Class Path
		if (debugHelp) System.err.println("Initializing help system...");
		try
		{
			hi = (HelpInterface)(Class.forName("jas.util.HelpInterfaceImpl")).newInstance();
			if (debugHelp) System.err.println("Help system initialized successfully");
		}
		catch (Exception x)
		{
			if (debugHelp) System.err.println("Failed: "+x.getMessage());
			throw x;
		}
	}	
	/**
	 * Add a Help location to the Vector of locations to look in for the helpset.
	 * At the time of writing, this was a one-element Vector, but we're being flexible :-)
	 */
	public void addHelpLocation(Class base,String s) 
	{
		addHelpLocation(base.getResource(s));
	}
	public void addHelpLocation(URL url) 
	{
		if (debugHelp) System.err.println("addHelpLocation: "+url);
		if (url != null) theHelpLocations.addElement(url);
	}
	public void addHelpLocation(String s)
	{
		try
		{
			if (debugHelp) System.err.println("addHelpLocation: "+s);
			theHelpLocations.addElement(new URL(s));
		}
		catch (MalformedURLException x)
		{
			System.err.println("Bad URL for addHelpLocation - ignored: "+s);
			x.printStackTrace();
		}
	}
	
	/**
	 * Get an Enumeration of all the locations to look in for the helpset.
	 */
	Enumeration getHelpLocations() 
	{
		return theHelpLocations.elements();
	}
	
	private void setDebugFlags() 
	{
		//Now we can ask gopt about the command-line parameters in order to set our flags.
		if (gopt.hasOption("debug")) 
		{
			StringTokenizer st = new StringTokenizer(gopt.getOption("debug"), ":");
			while (st.hasMoreTokens())
			{
				setDebugFlag(st.nextToken());
			}
		}
	}
	/**
	 * Called once for each debug flag set in the command options.
	 * By default sets a system property of the form debugFlag where flag
	 * is the flag specified (with its initial letter uppercased).
	 * Override for application specific behaviour.
	 */
	protected void setDebugFlag(String flag)
	{
		String name = "debug" +flag.substring(0,1).toUpperCase()+flag.substring(1);
		System.getProperties().put(name,"true");
		System.out.println("Set debug flag "+name);
		if (flag.equals("help")) debugHelp = true;
	}
	
	/**
	 * Returns the application.
	 */
	static final public Application getApplication()
	{
		return app;
	}

	/**
	 * Returns the UserProperties object.
	 */
	final public UserProperties getUserProperties()
	{
		return m_prop;
	}

	/**
	 * Returns the parent JFrame.
	 * @see javax.swing.JFrame
	 */
	final public JFrame getFrame()
	{
		if (m_parent == null)
		{
			Component parent = this;
			while (parent != null && !(parent instanceof JFrame)) parent = parent.getParent();
			m_parent = (JFrame) parent;
		}
		return m_parent;
	}
		
	/**
	 * Shows the table of contents for the help system.
	 */
	public final void showHelpContents()
	{
		showHelpTopic("top");
	}

	/**
	 * Shows the index for the help system.
	 */
	public final void showHelpIndex()
	{
		showHelpTopic("top", "Index");
	}

	/**
	 * Opens a search window for the help system.
	 */
	public final void showHelpSearch()
	{
		showHelpTopic("top", "Search");
	}

	/**
	 * Parents an error box to the application's frame and displays a Throwable
	 * object's stack trace.
	 *  @see ErrorBox
	 *  @param s an error message
	 *  @param t the Exception or Error that caused the error
	 */
	public final void error(final String s, final Throwable t)
	{
		if (getFrame() != null) new ErrorBox(getFrame(), s, t).doModal();
		else System.err.println("Error: "+s+" "+t);
	}	

	/**
	 * Parents an error box to the application's frame.
	 *  @see ErrorBox
	 *  @param s an error message
	 */
	public final void error(final String s)
	{	
		if (getFrame() != null) new ErrorBox(getFrame(), s).doModal();
		else System.err.println("Error: "+s);
	}

	/**
	 * Parents an error box to the application's frame, displays a Throwable
	 * object's stace trace, and contains a button that opens a given help page.
	 *  @see ErrorBox
	 *  @param s an error message
	 *  @param t the Exception or Error that caused the error
	 *  @param helpTopic the topic you want to link to
	 */
	public final void error(final String s, final Throwable t, final String helpTopic)
	{	
		if (getFrame() != null) new ErrorBox(getFrame(), s, t, helpTopic).doModal();
		else System.err.println("Error: "+s+" "+t);
	}

	/**
	 * Parents an error box to the application's frame, and contains a button
	 * that opens a given help page.
	 *  @see ErrorBox
	 *  @param s an error message
	 *  @param helpTopic the topic you want to link to
	 */
	public final void error(final String s, final String helpTopic)
	{	
		if (getFrame() != null) new ErrorBox(getFrame(), s, helpTopic).doModal();
		else System.err.println("Error: "+s);
	}
	public final void showHelpTopic(String helpTopicTarget)
	{
		showHelpTopic(helpTopicTarget,getFrame());
	}
	/**
	 * Shows the specified JavaHelp topic with the TOC visible
	 *  @param helpTopicTarget the JavaHelp XML target name which maps to the .html page in the map file
	 */
	public final void showHelpTopic(String helpTopicTarget, Window owner) 
	{
		try 
		{
			if (hi == null) initializeHelpSystem();
			hi.showHelpTopic(helpTopicTarget,owner);
		} 
		catch (Throwable eek) 
		{
			whine(eek);
		}
	}

	/**
	 * Shows the specified JavaHelp topic according to the display parameters provided.
	 *  @param helpTopicTarget the JavaHelp XML target name which maps to the .html page in the map file
	 *  @param navigatorView the string specifying which of the three views to have visible
	 */
	private final void showHelpTopic(String helpTopicTarget, String navigatorView)
	{
		try 
		{
			if (hi == null) initializeHelpSystem();
			hi.showHelpTopic(helpTopicTarget, navigatorView, getFrame());
		} 
		catch (Throwable eek) 
		{
			whine(eek);
		}
	}
	private final void whine(Throwable eek) 
	{
		app.error("Could not initialize help system!",eek);	
	}
	/**
	 * Get the GetOptions object that will be used to parse the command line arguments
	 */
	public GetOptions getGetOptions()
	{
		return gopt;
	}
	public CommandTargetManager getCommandManager()
	{
		return m_commandTargetManager;
	}
	/**
	 * Workaround for modal dialog/help interface interaction
	 */
	void modalDialogOpening(Dialog dlg)
	{
		if (hi != null) hi.modalDialogOpening(dlg);
	}
	void modalDialogClosing(Dialog dlg)
	{
		if (hi != null) hi.modalDialogClosing(dlg);
	}
	private CommandTargetManager m_commandTargetManager = new CommandTargetManager();
	private Vector theHelpLocations = new Vector();	
	private static Application app = null;
	private boolean debugHelp = false;
	private JFrame m_parent;
	private final UserProperties m_prop = new UserProperties();

	private GetOptions gopt = new GetOptions();
	private HelpInterface hi = null;
}
