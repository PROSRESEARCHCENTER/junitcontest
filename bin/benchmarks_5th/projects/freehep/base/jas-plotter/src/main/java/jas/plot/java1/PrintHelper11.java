package jas.plot.java1;

import jas.plot.PrintHelper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.PrintJob;

import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class PrintHelper11 extends PrintHelper
{
	public void printTarget(Component target) throws Exception
	{
		if (debugPrinting) System.out.println("PrintHelper11 printing "+target.getClass().getName()); 
		Frame f = (Frame) SwingUtilities.getAncestorOfClass(Frame.class,target);
		PrintJob pj = target.getToolkit().getPrintJob(f,"JAS Print Job",null);
		if (pj != null)
		{
			RepaintManager pm = RepaintManager.currentManager(target);
			boolean save = pm.isDoubleBufferingEnabled();
			try
			{
				pm.setDoubleBufferingEnabled(false);
				Graphics g = pj.getGraphics();
				
				// Lets try to center the image on the page (if possible)
				// We are rather limited in what we can do with JDK1.1 since there
				// is no easy way to scale the image. We can do much better with
				// JDK 1.2
				
				Dimension page = pj.getPageDimension();
				Dimension size = target.getSize();
				boolean trouble  = page.width<size.width || page.height<size.height;
				if (!trouble) g.translate((page.width-size.width)/2,(page.height-size.height)/2);
				setPrintingThread(Thread.currentThread());
				target.print(g);
				g.dispose();
				if (trouble) throw new PrintWarning("Warning - page was too big for printer");
			}
			catch (Exception t)
			{
				if (debugPrinting) System.out.println("Exception during printing "+t);
				throw t;
			}
			finally
			{
				pj.end();
				pm.setDoubleBufferingEnabled(save);
				setPrintingThread(null);
			}
		}
	}
	class PrintWarning extends Exception 
	{
		PrintWarning(String s)
		{
			super(s);
		}
	}
	// Unimplemented methods
	public void setOrientation(int orientation){}
	public void setScaleUp(boolean scaleUp){}
}
