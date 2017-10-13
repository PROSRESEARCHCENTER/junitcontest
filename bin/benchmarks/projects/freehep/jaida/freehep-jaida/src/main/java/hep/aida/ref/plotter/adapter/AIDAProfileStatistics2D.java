/*
 * AidaProfileStatistics2D.java
 *
 * Created on March 19, 2001, 1:32 PM
 */

package hep.aida.ref.plotter.adapter;

import hep.aida.IAnnotation;
import hep.aida.IProfile2D;
import jas.hist.ExtendedStatistics;

import java.util.ArrayList;

/**
 *
 * @author  manj
 * @version
 */
class AIDAProfileStatistics2D implements ExtendedStatistics 
{
    
    /** Creates new AidaProfileStatistics2D */
    AIDAProfileStatistics2D(IProfile2D profile)
    {
        this.profile=profile;
    }
   public String[] getStatisticNames()
   {
       ArrayList stat = new ArrayList();
       for ( int i = 0; i < statNames.length; i++ )
           stat.add(statNames[i]);
       if ( ((Integer) getExtendedStatistic(outOfRangeStat)).intValue() != 0 )
           stat.add(outOfRangeStat);
       if ( ((Integer) getExtendedStatistic(nanExtStat)).intValue() != 0 )
           stat.add(nanExtStat);
       if ( ((Integer) getExtendedStatistic("Entries")).intValue() != ((Double) getExtendedStatistic(sumOfWeightsStat)).doubleValue() )
           stat.add(sumOfWeightsStat);
       
        IAnnotation an = profile.annotation();
        if (an != null) {
            int n = an.size();
            for (int i=0; i<n; i++) {
                try {
                    String key = an.key(i);
                    if (key.toLowerCase().startsWith("stat.") || key.toLowerCase().startsWith("stat:")) {
                        stat.add(key.substring(5));
                    }                 
                } catch (IllegalArgumentException e) {}
            }
        }
       
       String[] statArray = new String[stat.size()];
       for ( int i = 0; i < stat.size(); i++ )
           statArray[i] = (String)stat.get(i);
      return statArray;
   }
    public double getStatistic(String name)
    {
        if (name.equals("XMean"))      return profile.meanX();
        if (name.equals("XRms"))       return profile.rmsX();
        if (name.equals("YMean"))      return profile.meanY();
        if (name.equals("YRms"))       return profile.rmsY();
        return 0;
    }
    public Object getExtendedStatistic(String name)
    {
        if (name.equals("Entries"))    return new Integer(profile.entries());
        if (name.equals(outOfRangeStat)) return new Integer(profile.extraEntries());
        if (name.equals(nanExtStat))   return new Integer(profile.nanEntries());
        if (name.equals(sumOfWeightsStat)) return new Double(profile.sumBinHeights());

        IAnnotation an = profile.annotation();
        if (an == null) return null;
        String v = null;
        try {
            if (an.hasKey("stat."+name))
                v = an.value("stat."+name);
            else if (an.hasKey("stat:"+name))
                v = an.value("stat:"+name);
        } catch (IllegalArgumentException e) {}
        return v;
    }
    private IProfile2D profile;
    private String[] statNames = {"Entries","XMean","XRms","YMean","YRms"};
    private final String nanExtStat = "NaN";
    private final String sumOfWeightsStat = "SumOfWeights";
    private final String outOfRangeStat = "OutOfRange";
}
