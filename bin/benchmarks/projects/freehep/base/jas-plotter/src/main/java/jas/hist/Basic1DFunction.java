package jas.hist;
import java.io.Serializable;
import java.util.Observable;

public abstract class Basic1DFunction
	extends Observable
	implements FunctionData, Serializable, Statistics, HasStatistics
{
	protected void destroy()
	{
	}
	public void setBatch(boolean b)
	{
		this.batch = b;
		if (!batch) notifyObservers();
	}
	public void setChanged()
	{
		super.setChanged();
		if (!batch) notifyObservers();
	}
	public void setChanged(Object o)
	{
		super.setChanged();
		if (!batch) notifyObservers(o);
	}
	public String[] getStatisticNames()
	{
		return getParameterNames();
	}
	public double getStatistic(String name)
	{
		String[] names = getParameterNames();
		for (int i=0; i<names.length; i++) if (name.equals(names[i])) return getParameterValues()[i];
		return 0;
	}
	public Statistics getStatistics()
	{
		return this;
	}
	private boolean batch = false;
}
