package jas.hist;

/**
 * An interface that represents a set of Statistics values which
 * are not limited to simple doubles.
 */
public interface ExtendedStatistics extends Statistics
{
	/**
	 * Returns an object corresponding to the named statistic.
	 * In general the statistic will be displayed by calling 
	 * its toString method, however if a Format object has been 
	 * defined for this class (how?) then it will be used instead.
	 * By default the following formatters are defined
	 * <dl>
	 * <dt>java.lang.Double</dt><dd>jas.util.ScientificFormat</dd>
	 * <dt>jas.util.DoubleWithError</dt><dd>jas.util.ScientificFormat</dd>
	 * </dl>
	 * 
	 * If the method returns null, then the getStatistic() method from
	 * the subclass will be called instead. This simplifies the use of
	 * simple floating point statistics (no need to create a Double 
	 * object for each one). 
	 * @param name The name of the statistic to return
	 * @returns The statistic, or null indicating getStatistic(name) should be used.
	 * @see jas.util.ScientificFormat
	 * @see jas.util.DoubleWithError
	 */
	Object getExtendedStatistic(String name);
}
