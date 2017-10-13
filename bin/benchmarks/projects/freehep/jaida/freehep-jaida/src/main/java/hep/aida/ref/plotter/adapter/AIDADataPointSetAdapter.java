package hep.aida.ref.plotter.adapter;

import hep.aida.IAnnotation;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IMeasurement;
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
public class AIDADataPointSetAdapter extends ObserverAdapter implements XYDataSource, HasStatistics {
    
    private int axisType = DataSource.DOUBLE;
    
    /**
     * Create a DataSource from a DataPointSet
     */
    public static DataSource create(IDataPointSet dps) {
        if (dps instanceof DataSource) return (DataSource) dps;
        
        AIDADataPointSetAdapter result;
        if ( dps.dimension() < 3 && dps.dimension() > 0 )
            result = new AIDADataPointSetAdapter((IDataPointSet)dps);
        else throw new IllegalArgumentException("Cannot diplay DataPointSets with dimension grater than 2.");
        return result;
    }
    
    protected AIDADataPointSetAdapter(IDataPointSet dps) {
        super(dps);
        this.dpSet = dps;
        st = new AIDADataPointSetStatistics(dpSet);
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
        return dpSet.point(i).coordinate(dimension-1).errorMinus();
    }
    public double getPlusError(int i) {
        return dpSet.point(i).coordinate(dimension-1).errorPlus();
    }
    public double getX(int i) {
        double x = 0;
        if (dimension == 1) 
            x = (double) i;
        else {
            IDataPoint dp = dpSet.point(i);
            IMeasurement m = dp.coordinate(0);
            x = m.value();
        }
        return x;
    }
    public double getY(int i) {
        return dpSet.point(i).coordinate(dimension-1).value();
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
    
    private IDataPointSet dpSet;
    private AIDADataPointSetStatistics st;
    private int dimension;
    private final static jas.hist.HistogramUpdate hr = new jas.hist.HistogramUpdate(HistogramUpdate.TITLE_UPDATE+HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,false);
    static
    {
       hr.setAxis(hr.HORIZONTAL_AXIS);
       hr.setAxis(hr.VERTICAL_AXIS);
    }
    
    class AIDADataPointSetStatistics implements ExtendedStatistics {

        AIDADataPointSetStatistics( IDataPointSet dps ) {
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
                        if (key.toLowerCase().startsWith("stat.") || key.toLowerCase().startsWith("stat:")) {
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
                if (an.hasKey("stat."+name))
                    v = an.value("stat."+name);
                else if (an.hasKey("stat:"+name))
                    v = an.value("stat:"+name);
                } catch (IllegalArgumentException e) {}
                return v;
            }
        }

        private IDataPointSet dps;
        private final String statName = "Entries";
    }
}
