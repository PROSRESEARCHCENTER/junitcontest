package jas.hist;

/**
 * Interface implemented by any JASHist datasource
 */
public interface DataSource
{
	public final static int DOUBLE = 1;
	public final static int STRING = 2;
	public final static int DATE      = 3;
        public final static int INTEGER = 4;
	public final static int DELTATIME = 5;
	/**
	 * Return the caption to be used in the legend for this data.
	 */
	String getTitle();
}

