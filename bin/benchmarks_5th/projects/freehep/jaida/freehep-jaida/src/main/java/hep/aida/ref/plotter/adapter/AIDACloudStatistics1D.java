package hep.aida.ref.plotter.adapter;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.ICloud1D;
import jas.hist.ExtendedStatistics;

import java.util.ArrayList;

/**
 *
 * @author manj
 * @version $Id: AIDACloudStatistics1D.java 10734 2007-05-16 19:31:00Z serbo $
 */
class AIDACloudStatistics1D implements ExtendedStatistics {
    public AIDACloudStatistics1D(ICloud1D histo) {
        this.histo=histo;
    }
    public String[] getStatisticNames() {
        ArrayList stat = new ArrayList();
        for ( int i = 0; i < statNames.length; i++ )
            stat.add(statNames[i]);
        if ( getExtendedStatistic(outOfRangeStat) != null && ((Integer) getExtendedStatistic(outOfRangeStat)).intValue() != 0 )
            stat.add(outOfRangeStat);
        if ( getExtendedStatistic(nanExtStat) != null && ((Integer) getExtendedStatistic(nanExtStat)).intValue() != 0 )
            stat.add(nanExtStat);
        if ( ((Integer) getExtendedStatistic("Entries")).intValue() != ((Double) getExtendedStatistic(sumOfWeightsStat)).doubleValue() )
            stat.add(sumOfWeightsStat);
        
        addAnnotationStatistics(stat);
        
        String[] statArray = new String[stat.size()];
        for ( int i = 0; i < stat.size(); i++ )
            statArray[i] = (String)stat.get(i);
        return statArray;
    }
    
    public double getStatistic(String name) {
        if (name.equals("Mean")) return histo.mean();
        if (name.equals("Rms"))  return histo.rms();
        return 0;
    }
    
    public Object getExtendedStatistic(String name) {
        if (name.equals("Entries"))    return new Integer(histo.entries());
        if ( histo.isConverted() ) {
            if (name.equals(outOfRangeStat))   return new Integer(histo.histogram().extraEntries());
            if (name.equals(overflowStat))     return new Integer(histo.histogram().binEntries(IAxis.OVERFLOW_BIN));
            if (name.equals(underflowStat))    return new Integer(histo.histogram().binEntries(IAxis.UNDERFLOW_BIN));
        } else {
            if (name.equals(outOfRangeStat))   return new Integer(0);
            if (name.equals(overflowStat))     return new Integer(0);
            if (name.equals(underflowStat))    return new Integer(0);
        }
        if (name.equals(nanExtStat))
            return new Integer( histo.nanEntries() );
        if (name.equals(sumOfWeightsStat)) return new Double(histo.sumOfWeights());
        
        IAnnotation an = histo.annotation();
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
    
    private void addAnnotationStatistics(ArrayList stat) {
        IAnnotation an = histo.annotation();
        if (an == null) return;
        boolean isOutOfRangeSet = false;
        int n = an.size();
        for (int i=0; i<n; i++) {
            try {
                String key = an.key(i);
                String statVal = an.value(i);
                if (key.toLowerCase().startsWith("stat.") || key.toLowerCase().startsWith("stat:")) {
                    String statKey = key.substring(5);
                    if (statVal.equalsIgnoreCase("false")) {
                        stat.remove(statKey);
                        continue;
                    }
                    if (  overflowStat.equals(statKey) && ((Integer) getExtendedStatistic(statKey)).intValue() == 0 ) continue;
                    if ( underflowStat.equals(statKey) && ((Integer) getExtendedStatistic(statKey)).intValue() == 0 ) continue;
                    if (outOfRangeStat.equals(statKey)) {
                        isOutOfRangeSet = true;
                        continue;
                    }
                    stat.add(statKey);
                }
            } catch (IllegalArgumentException e) {}
        }
        if (!isOutOfRangeSet && (stat.contains(overflowStat) || stat.contains(underflowStat)))
            stat.remove(outOfRangeStat);
    }
    
    private ICloud1D histo;
    private final String[] statNames = {"Entries","Mean","Rms"};
    private final String nanExtStat = "NaN";
    private final String sumOfWeightsStat = "SumOfWeights";
    private final String outOfRangeStat = "OutOfRange";
    private final String overflowStat  = "Overflow";
    private final String underflowStat = "Underflow";
}