/*
 * StatisticsNormalizer.java
 *
 * Created on January 23, 2001, 6:13 PM
 */

package jas.hist.normalization;
import jas.hist.DataSource;
import jas.hist.ExtendedStatistics;
import jas.hist.HasStatistics;
import jas.hist.Statistics;
/**
 * A normalizer that calculates a normalization factor based on a specific statistics entry
 * @author  tonyj
 * @version $Id: StatisticsNormalizer.java 13351 2007-09-21 18:46:46Z serbo $
 */
public class StatisticsNormalizer extends DataSourceNormalizer
{
    /** Creates new StatisticsNormalizer 
     * @param source The data source
     * @param statsName The name of the statistic
     */
    public StatisticsNormalizer(DataSource source, String statsName) 
    {
        super(source);
        this.statsName = statsName;
        init();
    }
    protected double calculateNormalization()
    {
        if (source instanceof HasStatistics)
        {
            Statistics stats = ((HasStatistics) source).getStatistics();
            double stat = stats.getStatistic(statsName);
            if (stat == 0 && stats instanceof ExtendedStatistics) {
                Object obj = ((ExtendedStatistics) stats).getExtendedStatistic(statsName);
                try {
                    stat = Double.parseDouble(obj.toString());
                } catch (NumberFormatException e) { stat = 0; }
            }
            return stat > 0 ? stat : 1;
        }
        else return 1;
    }
    private String statsName;
}

