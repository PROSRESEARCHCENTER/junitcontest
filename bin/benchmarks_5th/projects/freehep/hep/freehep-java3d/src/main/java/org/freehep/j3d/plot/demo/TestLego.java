package org.freehep.j3d.plot.demo;

import org.freehep.j3d.plot.LegoPlot;
import java.applet.Applet;
import java.awt.BorderLayout;
import com.sun.j3d.utils.applet.MainFrame;
import java.io.IOException;

/**
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: TestLego.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TestLego extends Applet
{
	TestLego() throws IOException
	{
		setLayout(new BorderLayout());
		LegoPlot lego = new LegoPlot();
                lego.setLogZscaling(true);
		lego.setData(new TestBinned2DData());
		add(lego,BorderLayout.CENTER);
	}
	public static void main(String[] argv) throws IOException
	{
		new MainFrame(new TestLego(),300,300);
	}
}
