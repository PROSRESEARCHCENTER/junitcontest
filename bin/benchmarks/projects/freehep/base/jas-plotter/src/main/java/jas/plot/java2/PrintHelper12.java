package jas.plot.java2;

import jas.plot.PrintHelper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

public class PrintHelper12 extends PrintHelper implements Printable 
{
	public void printTarget(Component target) throws Exception
	{
		if (debugPrinting) System.out.println("PrintHelper12 printing "+target.getClass().getName()); 
		this.target = target;
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);

        //PageFormat pf = printJob.pageDialog(printJob.defaultPage());
		boolean pj = printJob.printDialog();
		
		if (pj)
		{
			RepaintManager pm = RepaintManager.currentManager(target);
			boolean save = pm.isDoubleBufferingEnabled();
			try
			{
				pm.setDoubleBufferingEnabled(false);
				setPrintingThread(Thread.currentThread());
				printJob.print();
			}
			catch (Exception x)
			{
				if (debugPrinting) System.out.println("Exception during printing "+x); 	
				throw x;
			}
			finally
			{
				pm.setDoubleBufferingEnabled(save);
				setPrintingThread(null);
			}
		}
	}
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException
	{
		if (pi >= 1) return Printable.NO_SUCH_PAGE;
		if (debugPrinting) System.out.println("PrintHelper12 printing page"+pi); 

        Graphics2D g2 = (Graphics2D) g;
		g2.translate(pf.getImageableX(),pf.getImageableY());
		Dimension size = target.getSize();
		
		double pageWidth = pf.getImageableWidth();
		double pageHeight = pf.getImageableHeight();
		
		double portraiteRatio = Math.min(pageWidth/size.width,pageHeight/size.height);
		double landscapeRatio = Math.min(pageWidth/size.height,pageHeight/size.width);
		if (debugPrinting) System.out.println("portraiteRatio="+portraiteRatio+" landscapeRatio="+landscapeRatio); 
		
		if (!scaleUp)
		{
			portraiteRatio = Math.min(portraiteRatio,1);
			landscapeRatio = Math.min(landscapeRatio,1);
		}
		if (debugPrinting) System.out.println("portraiteRatio="+portraiteRatio+" landscapeRatio="+landscapeRatio); 
		boolean useLandscape = (orientation == ORIENTATION_BEST_FIT) ?
			landscapeRatio > portraiteRatio : orientation == ORIENTATION_LANDSCAPE;
		
		if (debugPrinting) System.out.println("portraiteRatio="+portraiteRatio+" landscapeRatio="+landscapeRatio
											  +" useLandscape="+useLandscape); 
		if (useLandscape)
		{
			g2.rotate(Math.PI/2);
			g2.translate(0,-pageWidth);
			g2.scale(landscapeRatio,landscapeRatio);
		}
		else
		{
			g2.scale(portraiteRatio,portraiteRatio);      	
		}
	    target.paint(g2);
        return Printable.PAGE_EXISTS;
    }
	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
	}
	public void setScaleUp(boolean scaleUp)
	{
		this.scaleUp = scaleUp;
	}

	private int orientation = ORIENTATION_BEST_FIT;
	private boolean scaleUp = false;
	
	private Component target;
}
