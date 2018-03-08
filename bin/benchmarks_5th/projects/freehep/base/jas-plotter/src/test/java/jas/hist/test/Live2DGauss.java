package jas.hist.test;

import jas.hist.HistogramUpdate;

public class Live2DGauss extends Gauss2D
{
	public Live2DGauss(String name, int entries)
	{
		super(name,entries);

		Thread t = new Thread("Live2DGauss")
		{
			public void run()
			{ 
				loop(); 
			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
	public void setUpdateFrequency(long delay)
	{
		m_delay = delay;
	}
	private void loop()
	{
		Thread t = Thread.currentThread();
		HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,false);
		try
		{
			for (;;)
			{
				t.sleep(m_delay);
				m_entries++;
				if (m_entries > MAXSIZE) m_entries = 0;
				synchronized (this) { notifyObservers(hu); }
			}
		}
		catch (InterruptedException e)
		{
			return;
		}
	}
	private long m_delay = 10;
}
