package jas.hist.event;
import java.util.EventObject;
final public class FitEvent extends EventObject
{
	final public static int FIT_STARTED = 0;
	final public static int FIT_ENDED = 1;
	final public static int FIT_ERROR = 2;
	final public static int FIT_PROGRESS_CHANGED = 3;

	public FitEvent(final Object source, final int id)
	{
		super(source);
		m_id = id;
	}
	public int getID()
	{
		return m_id;
	}
	private int m_id;
}
