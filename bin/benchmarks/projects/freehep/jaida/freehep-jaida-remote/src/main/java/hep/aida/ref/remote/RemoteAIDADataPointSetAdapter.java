package hep.aida.ref.remote;

import hep.aida.IAnnotation;
import hep.aida.ref.event.ObserverAdapter;
import jas.hist.DataSource;
import jas.hist.ExtendedStatistics;
import jas.hist.HasStatistics;
import jas.hist.HistogramUpdate;
import jas.hist.XYDataSource;

import java.util.Vector;

/**
 *
 * @author  The AIDA team @ SLAC
 *
 */
public class RemoteAIDADataPointSetAdapter extends ObserverAdapter implements XYDataSource, HasStatistics {
    
    private int axisType = DataSource.DOUBLE;
    
    public RemoteAIDADataPointSetAdapter(RemoteDataPointSet dps) {
        super(dps);
        this.dpSet = dps;
        st = new RemoteDataPointSetStatistics(dpSet);
        this.dimension = dpSet.dimension();
        try {
            String xType = dps.annotation().value("xAxisType");
            if (xType != null && xType.equalsIgnoreCase("date")) axisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
        
        update = hr;
     }

    public void setAxisType( int type ) {
        this.axisType = type;
    }
    
    public int getAxisType() {
        return axisType;
    }
        
    public double getMinusError(int i) {
        return dpSet.errorMinus(i);
    }
    public double getPlusError(int i) {
        return dpSet.errorPlus(i);
    }
    public double getX(int i) {
        double x = 0;
        if (dimension == 1) 
            x = (double) i;
        else {
            x = dpSet.getX(i);
        }
        return x;
    }
    public double getY(int i) {
        return dpSet.getY(i);
    }
    public int getNPoints() {
        setValid();
        return dpSet.size();
    }
    public jas.hist.Statistics getStatistics() {
        return st;
    }
    public String getTitle() {
        return dpSet.title();
    }
    
    private RemoteDataPointSet dpSet;
    private RemoteDataPointSetStatistics st;
    private int dimension;
    private double[] x;
    private double[] y;
    private double[] ep;
    private double[] em;
    private final static jas.hist.HistogramUpdate hr = new jas.hist.HistogramUpdate(HistogramUpdate.TITLE_UPDATE+HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,false);
    static
    {
       hr.setAxis(hr.HORIZONTAL_AXIS);
       hr.setAxis(hr.VERTICAL_AXIS);
    }
    
    class RemoteDataPointSetStatistics implements ExtendedStatistics {

        RemoteDataPointSetStatistics( RemoteDataPointSet dps ) {
            this.dps = dps;
        }
        
        public double getStatistic(String str) {
            if ( str.equals(statName) ) return dps.size();
            return 0;
        }
        public String[] getStatisticNames() {
            IAnnotation an = dps.annotation();
            if (an == null) return new String[] { statName };
            else {
                int n = an.size();
                Vector keys = new Vector();
                keys.add(statName);
                for (int i=0; i<n; i++) {
                    try {
                        String key = an.key(i);
                        if (key.toLowerCase().startsWith("stat.")) {
                            keys.add(key.substring(5));
                        }    
                    } catch (IllegalArgumentException e) {}
                }
                String[] tmp = new String[keys.size()];
                keys.toArray(tmp);
                return tmp;
            }
        }
        
        public Object getExtendedStatistic(String name)  {
            if ( name.equals(statName) ) return null;
            else {
                IAnnotation an = dps.annotation();
                if (an == null) return null;
                String v = null;
                try {
                    v = an.value("stat."+name);
                } catch (IllegalArgumentException e) {}
                return v;
            }
        }

        private RemoteDataPointSet dps;
        private final String statName = "Entries";
    }
}
