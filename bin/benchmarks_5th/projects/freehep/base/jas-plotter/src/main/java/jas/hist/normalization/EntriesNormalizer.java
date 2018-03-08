/*
 * EntriesNormalizer.java
 *
 * Created on January 24, 2001, 11:53 AM
 */

package jas.hist.normalization;
import jas.hist.DataSource;

/**
 * Calculates a normalization factor based on the number of entries in the data source.
 * @author  tonyj
 * @version $Id: EntriesNormalizer.java 11550 2007-06-05 21:44:14Z duns $
 */
public class EntriesNormalizer extends StatisticsNormalizer 
{
    /** Creates new EntriesNormalizer 
     * @param source The data source
     */
    public EntriesNormalizer(DataSource source) 
    {
        super(source,"Entries");
    }
}

