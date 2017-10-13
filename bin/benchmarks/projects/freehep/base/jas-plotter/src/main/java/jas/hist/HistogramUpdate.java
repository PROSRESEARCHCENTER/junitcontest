package jas.hist;

 /**
  * This class encapsulates update messages sent from an observable histogram data source to the
  * histogram display
  */

final public class HistogramUpdate implements java.io.Serializable
{
    static final long serialVersionUID = 5574611010721023059L;
	
	private final static int FINAL_UPDATE = 1;
	public final static int DATA_UPDATE  = 2;
	public final static int RANGE_UPDATE = 4;
	public final static int TITLE_UPDATE = 8;
	public final static int RESET = 16;
	private final int AXIS_BASE = 32;
	/** The index to indicate changes to the horizontal axis for histogram updates. */
	final public int HORIZONTAL_AXIS = 0;
	/** The index to indicate changes to the vertical axis for histogram updates. */
	final public int VERTICAL_AXIS = 1;

	public HistogramUpdate(int id, boolean f)
	{
		if (f) id |= FINAL_UPDATE;
		m_id = id;
	}

	public void setAxis(final int axisIndex)
	{
		m_id |= AXIS_BASE << axisIndex;
	}
	public boolean axisIsSet(final int axisIndex)
	{
		return (m_id & AXIS_BASE << axisIndex) != 0;
	}
	public boolean isFinalUpdate()
	{
		return (m_id & FINAL_UPDATE) != 0;
	}
	public boolean isDataUpdate()
	{
		return (m_id & DATA_UPDATE) != 0;
	}
	public boolean isRangeUpdate()
	{
		return (m_id & RANGE_UPDATE) != 0;
	}
	public boolean isTitleUpdate()
	{
		return (m_id & TITLE_UPDATE) != 0;
	}
	public boolean isReset()
	{
		return (m_id & RESET) != 0;
	}
	public String toString()
	{
		return "HistogramUpdate! "+
			(isDataUpdate()  ? "DATA " :"")+
			(isRangeUpdate() ? "RANGE ":"")+
			(isTitleUpdate() ? "TITLE ":"")+
			(isReset()       ? "RESET ":"")+
			(isFinalUpdate() ? "FINAL ":"")+
			(axisIsSet(HORIZONTAL_AXIS) ? "HORIZONTAL_AXIS ":"")+
			(axisIsSet(VERTICAL_AXIS)   ? "VERTICAL_AXIS ":"");
	}
	protected int m_id;

}
