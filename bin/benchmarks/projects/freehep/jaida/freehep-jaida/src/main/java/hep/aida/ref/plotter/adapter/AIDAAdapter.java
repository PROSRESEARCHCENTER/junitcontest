/*
 * AIDAAdapter.java
 *
 * Created on January 22, 2002, 4:35 PM
 */

package hep.aida.ref.plotter.adapter;
import hep.aida.ICloud;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram;
import hep.aida.IProfile;
import jas.hist.DataSource;

/**
 * An Adapter for displaying AIDA histograms using the JAS Plot widget
 * @author  tonyj
 */
public class AIDAAdapter {
    
    public static DataSource create(IProfile profile) {
        return AIDAProfileAdapter.create(profile);
    }
    public static DataSource create(IDataPointSet dpSet) {
        return AIDADataPointSetAdapter.create(dpSet);
    }
    public static DataSource create(IHistogram hist) {
        return AIDAHistogramAdapter.create(hist);
    }
    public static DataSource create(ICloud cloud) {
        return AIDACloudAdapter.create(cloud);
    }
    public static DataSource create(IFunction function) {
        return AIDAFunctionAdapter.create(function);
    }
}
