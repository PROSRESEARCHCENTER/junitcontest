package jas.plot;
import java.awt.Component;

public abstract class PrintHelper
{
	/**
	 * Print the specified component
	 */
	public abstract void printTarget(Component target) throws Exception;
	/**
	 * Create an instance of a PrintHelper
	 */
	public static PrintHelper instance()
	{
		PrintHelper result;
		try
		{
			Class c = Class.forName("jas.plot.java2.PrintHelper12");
			result = (PrintHelper) c.newInstance();
		}
		catch (Throwable x)
		{
			result = new jas.plot.java1.PrintHelper11();
		}
		if (debugPrinting) System.out.println("PrintHelper created a "+result.getClass().getName());
		return result;
	}
	public static final int ORIENTATION_BEST_FIT = 0;
	public static final int ORIENTATION_PORTRAITE = 1;
	public static final int ORIENTATION_LANDSCAPE = 2;
	
	/**
	 * Set the print orientation
	 * @param orientation One of ORIENTATION_BEST_FIT,ORIENTATION_PORTRAITE,ORIENTATION_LANDSCAPE
	 */
	public abstract void setOrientation(int orientation);
	/**
	 * Controls whether the PrintHelper will scale an image up to make it fill the page 
	 * (PrintHelper will always try to shrink to fit)
	 */
	public abstract void setScaleUp(boolean scaleUp);
	
	/**
	 * Method to allow component being painted to find out if it is being printed.
	 * This is a crude workaround for limitations which make it impossible for a SwingComponent
	 * to tell whether its paint method is being called to print it or to display it on the screen.
	 * Obviously this static method only works if PrintHelper is being used to do the printing.
	 */
	public static boolean isPrinting()
	{
		return Thread.currentThread() == printingThread;
	}
	public void setPrintingThread(Thread thread)
	{
		if (debugPrinting) System.out.println("Printing thread="+thread);
		printingThread = thread;
	}
        public Thread printingThread() {
            return printingThread;
        }
        
	private static Thread printingThread;
	protected static final boolean debugPrinting;
	static
	{
		boolean result;
		try
		{
			result = System.getProperty("debugPrinting") != null;
		}
		catch (SecurityException x) // in case we are in an applet!
		{
			result = false;
		}
		debugPrinting = result;
	}
}
