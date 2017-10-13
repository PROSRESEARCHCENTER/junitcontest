package jas.hist.test;

//******************************************************************************
// TestJASHistFrame.java:	
//
//******************************************************************************
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

//==============================================================================
// STANDALONE APPLICATION SUPPORT
// 	This frame class acts as a top-level window in which the applet appears
// when it's run as a standalone application.
//==============================================================================
class TestJASHistFrame extends JFrame
{
	// TestJASHistFrame constructor
	//--------------------------------------------------------------------------
	public TestJASHistFrame(String str)
	{
		// TODO: Add additional construction code here
		super (str);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});
	}
}
