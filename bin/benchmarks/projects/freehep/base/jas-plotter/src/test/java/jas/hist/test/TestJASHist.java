package jas.hist.test;

//******************************************************************************
// TestJASHist.java:	Applet
//
//******************************************************************************
import jas.hist.JASHist;
import jas.plot.PrintHelper;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//==============================================================================
// Main Class for applet TestJASHist
//
//==============================================================================
public class TestJASHist extends JApplet implements ChangeListener, ActionListener
{
	// STANDALONE APPLICATION SUPPORT:
	//		m_fStandAlone will be set to true if applet is run standalone
	//--------------------------------------------------------------------------
	boolean m_fStandAlone = false;
	
	// STANDALONE APPLICATION SUPPORT
	// 	The main() method acts as the applet's entry point when it is run
	// as a standalone application. It is ignored if the applet is run from
	// within an HTML page.
	//--------------------------------------------------------------------------
	public static void main(String args[])
	{
		// Create Toplevel Window to contain applet TestJASHist
		//----------------------------------------------------------------------
		frame = new TestJASHistFrame("TestJASHist");
      
      // Attempt to register functions/fitters
      // Will fail of Fitting.jar is not in the CLASSPATH
      try
      {
          Class c = TestJASHist.class.forName("jasext.hist.Register");
          java.lang.reflect.Method m = c.getMethod("init",noArgc);
          Object reg = c.newInstance();
          m.invoke(reg,noArgs);
      }
      catch (Throwable t)
      {
          System.err.println("Unable to register functions/fitters");
      }
      // Attempt to register exporters
      // Will fail of freehep-*.jar is not in the CLASSPATH
      try
      {
          Class c = TestJASHist.class.forName("jas.export.Register");
          java.lang.reflect.Method m = c.getMethod("init",noArgc);
          Object reg = c.newInstance();
          m.invoke(reg,noArgs);
      }
      catch (Throwable t)
      {
          System.err.println("Unable to register exporters");
      }

		// Must show Frame before we size it so insets() will return valid values
		//----------------------------------------------------------------------
		//frame.show();
        //frame.hide();
		//frame.resize(frame.insets().left + frame.insets().right  + 600,
		//			 frame.insets().top  + frame.insets().bottom + 500);

		// The following code starts the applet running within the frame window.
		// It also calls GetParameters() to retrieve parameter values from the
		// command line, and sets m_fStandAlone to true to prevent init() from
		// trying to get them from the HTML page.
		//----------------------------------------------------------------------
		TestJASHist applet_TestJASHist = new TestJASHist();
		
		frame.setContentPane(applet_TestJASHist);
		applet_TestJASHist.m_fStandAlone = true;
		//applet_TestJASHist.init();
		applet_TestJASHist.start();
		frame.pack();
        frame.show();
	}

	// TestJASHist Class Constructor
	//--------------------------------------------------------------------------
	public TestJASHist()
	{
		init();
	}

	// APPLET INFO SUPPORT:
	//		The getAppletInfo() method returns a string describing the applet's
	// author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Name: TestJASHist\n" +
		       "Author: Tony Johnson\n" +
		       "Application for testing features of JASHist";
	}


	// The init() method is called by the AWT when an applet is first loaded or
	// reloaded.  Override this method to perform whatever initialization your
	// applet needs, such as initializing data structures, loading images or
	// fonts, creating frame windows, setting the layout manager, or adding UI
	// components.
    //--------------------------------------------------------------------------
	public void init()
	{
        // If you use a ResourceWizard-generated "control creator" class to
        // arrange controls in your applet, you may want to call its
        // CreateControls() method from within this method. Remove the following
        // call to resize() before adding the call to CreateControls();
        // CreateControls() does its own resizing.
        //----------------------------------------------------------------------
		resize(400, 300);
		m_hist = new JASHist();
		RightPanel right = new RightPanel();
		BottomPanel bottom = new BottomPanel(m_hist);
	
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("East",right);
		getContentPane().add("South",bottom);
		getContentPane().add("Center",m_hist);
		
		right.addBinsChangeListener(this);
		right.addEventsChangeListener(bottom);
		right.addActionListener(this);
	}
	public void stateChanged(ChangeEvent ev) 
	{
		m_hist.getXAxis().setBins(((JSlider) ev.getSource()).getValue());
	}

	// Place additional applet clean up code here.  destroy() is called when
	// when you applet is terminating and being unloaded.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Place applet cleanup code here
	}

	// TestJASHist Paint Handler
	//--------------------------------------------------------------------------
	//public void paint(Graphics g)
	//{
	//}

	//		The start() method is called when the page containing the applet
	// first appears on the screen. The AppletWizard's initial implementation
	// of this method starts execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void start()
	{
		// TODO: Place additional applet start code here
	}
	
	//		The stop() method is called when the page containing the applet is
	// no longer on the screen. The AppletWizard's initial implementation of
	// this method stops execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void stop()
	{
	}

	// TODO: Place additional applet code here

	public void actionPerformed(ActionEvent evt)
	{
		String arg = evt.getActionCommand();

		if (arg.equals("About..."))
		{
			JOptionPane.showMessageDialog(this,getAppletInfo() ,
				"About...", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (arg.equals("Properties..."))
		{
			m_hist.showProperties();
		}
		else if (arg.equals("Print..."))
		{
			try
			{
				PrintHelper help = PrintHelper.instance();
				help.printTarget(m_hist);
			}
			catch (Exception x) { System.err.println("Error printing: "+x); }
		}
		else if (arg.equals("Save As..."))
		{
			try
			{
				m_hist.saveAs();
				
			}
			catch (Exception x) { System.err.println("Error saving: "+x); }
		}
	}
	private JASHist m_hist;
	private static TestJASHistFrame frame;
   public final static Class[] noArgc = {};
   public final static Object[] noArgs = {};
}
