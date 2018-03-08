package jas.hist;

/**
 * An interface that represents a set of Statistics values all of whose
 * values can be expressed as doubles.
 * @see jas.hist.ExtendedStatistics
 */
public interface Statistics
{
	/**
	 * @return A list of statistic names
	 */
	String[] getStatisticNames();
	/**
	 * @param The statistic name, as returned by getStatisticNames()
	 * @return The value for the statistic
	 */
	double getStatistic(String name);
}
