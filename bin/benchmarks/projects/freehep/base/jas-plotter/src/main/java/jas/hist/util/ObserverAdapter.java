package jas.hist.util;
import java.util.Observable;
import java.util.Observer;

/**
 * An ObserverAdapter can be used as a base class for class that wants to act
 * as both and Observer and Observable, and relay things it observers to its
 * Observers.
 */

public class ObserverAdapter extends Observable implements Observer
{
	// Ideally we would like to only add ourselves as an Observer if at least one person
	// is observing us. This makes things more efficient, and helps to avoid useless references
	// which hinder garbage collection
	
	private Observable obs;
	private boolean observing = false;
	
	public ObserverAdapter(Observable obs)
	{
		this.obs = obs;
	}
	public ObserverAdapter()
	{
	}
	public synchronized void setObservable(Observable newObs)
	{
		if (observing && obs != null) obs.deleteObserver(this);
		obs = newObs;
		if (observing && obs != null) obs.addObserver(this);
	}
	public synchronized void clearObservable()
	{
		setObservable(null);
	}
	
	/**
	 *  When the object which we are observing is updated, relay the response to our observers
	 */
	public void update(Observable o, Object arg)
	{
		setChanged();
		notifyObservers(arg);
	}
	private void observe(boolean set)
	{
		if (set != observing)
		{
			if (obs != null)
			{
				if (set) obs.addObserver(this);
				else     obs.deleteObserver(this);
			}
			observing = set;
		}
	}
	public synchronized void addObserver(Observer o)
	{
		super.addObserver(o);
		observe(true);
	}

	public synchronized void deleteObserver(Observer o)
	{
		super.deleteObserver(o);
		if (countObservers() == 0) observe(false);
	}

	public synchronized void deleteObservers()
	{
		super.deleteObservers();
		observe(false);
	}
}
