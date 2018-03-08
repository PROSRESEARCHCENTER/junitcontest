package jas.hist;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

public abstract class Fitter extends Observable implements Observer, Runnable, Serializable
{
	public abstract double getChiSquared();
	public abstract double[] getParameterSigmas();
	protected abstract void fit(Fittable1DFunction fa, double[] x, double[] y, double[] sigmaY) throws FitFailed;

	public void fit() throws FitFailed
	{
		if (state != READYTOFIT) throw new FitFailed("Not ready to fit");
		internalFit();
		observeData();
	}
	public synchronized void start()
	{
		if (state == READYTOFIT)
		{
			thread = new Thread(this,"Fit thread");
			thread.start();
		}
	}
	public synchronized void stop()
	{
		if (thread != null) thread.stop();
	}
	public void run()
	{
		try
		{
			internalFit();
			observeData();
		}
		catch (FitFailed x) {}
		finally
		{
			synchronized (this) { thread = null; }
		}
	}
	public synchronized void update(Observable obs, Object arg)
	{
		if (obs == m_data) 
		{
			if (state == FIT) setState(READYTOFIT);
			start();
		}
	}
	public int getState()
	{
		return state;
	}
	private synchronized void observeData()
	{
		if (!observing && m_data instanceof Observable) 
			((Observable) m_data).addObserver(this);
	}
	private synchronized void setState(int state)
	{
		this.state = state;
		setChanged();
		notifyObservers(new FitUpdate(state));
	}
	private synchronized void setState(int state, FitFailed x)
	{
		this.state = state;
		setChanged();
		notifyObservers(new FitUpdate(state,x));
	}
	protected void setPercentComplete(int percent)
	{
		setChanged();
		notifyObservers(new FitUpdate(state,percent));
	}
	private void internalFit() throws FitFailed
	{
		setState(FITTING);
		try
		{
			int n = m_data.getNPoints();
			double[] x = new double[n];
			double[] y = new double[n];
			double[] yerr = new double[n];	

         for (int i=0; i<n; i++)
         {
            x[i] = m_data.getX(i);
            y[i] = m_data.getY(i);
            yerr[i] = m_data.getPlusError(i);
            if (yerr[i] != m_data.getMinusError(i)) 
				   throw new FitFailed("Cannot fit data with asymmetric error bars");
         }
			
			fit(new FitAdapter1D(m_func), x, y, yerr);
			/*
			 * The FitAdapter1D is an extension
			 * of Fittable1DFunction so it looks
			 * to the subclass of Fitter like it
			 * was passed a Fittable1DFunction.
			 * In fact, it has been passed an
			 * adapter class that passes to the
			 * fitter only selected parameters
			 * (those selected by the user) and
			 * provides methods that look
			 * just like those in
			 * Fittable1DFunction.  Those methods
			 * in fact perform a parameter list
			 * conversion each time.
			 */

			setState(FIT);
		}
		catch (FitFailed x)
		{
			setState(FAILED,x);
			throw x;
		}
	}
	public synchronized void setFunction(Fittable1DFunction func)
	{
		if (m_func != null && m_func.getFit() == this) m_func.clearFit();
		m_func = func;
		if (m_func != null & m_data != null) setState(READYTOFIT);
	}
	public Fittable1DFunction getFunction()
	{
		return m_func;
	}
	public synchronized void setData(XYDataSource data)
	{
		if (observing) 
		{
			((Observable) m_data).deleteObserver(this);
			observing = false;
		}
		m_data = data;
		if (m_func != null & m_data != null) setState(READYTOFIT);
	}
	public XYDataSource getData()
	{
		return m_data;
	}
	protected synchronized void dispose()
	{
		if (thread != null) thread.stop();
		// Tell any observers we are outahere
		setState(OUTAHERE);
		deleteObservers();
		setData(null);
		setFunction(null);
	}

	public final static int FITTING = 0;
	public final static int FIT = 1;
	public final static int FAILED = 2;
	public final static int READYTOFIT = 3;
	public final static int NOTREADYTOFIT = 4;
	public final static int OUTAHERE = 5;

    static final long serialVersionUID = -7769799329320822801L;
	
	private XYDataSource m_data;
	private Fittable1DFunction m_func;
	private int state = NOTREADYTOFIT;
	private Thread thread;
	private boolean observing = false;
}
