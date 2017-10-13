package jas.hist.test;

import jas.hist.HistogramUpdate;

public class LiveGauss extends Gauss
{
	public LiveGauss(String name, int entries,double max,double offX,double offY)
	{
		super(name,entries,max,offX,offY);

		Thread t = new Thread("LiveGauss")
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
