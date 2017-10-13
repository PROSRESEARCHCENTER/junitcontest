package hep.aida.ref.plotter.adapter;

import hep.aida.IProfile1D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DVariableHistogramData;
import jas.hist.Statistics;

/**
 * @author serbo
 * @version $Id: AIDAProfileVariableAdapter1D.java 10740 2007-05-21 18:05:50Z serbo $
 */
public class AIDAProfileVariableAdapter1D extends AIDAProfileAdapter1D implements Rebinnable1DVariableHistogramData 
{
    private double[] edges;
    
    AIDAProfileVariableAdapter1D(IProfile1D histo)
   {
      super(histo);
   } 
    
    public double[] getBinEdges() {
        if (edges == null) fillEdges();
        return edges;
    }
    
    private void fillEdges() {
        if (profile == null) return;
        int nBins = profile.axis().bins();
        edges = new double[nBins+1];
        edges[0] = profile.axis().binLowerEdge(0);
        for (int i=0; i<nBins; i++) {
            edges[i+1] = profile.axis().binUpperEdge(i);
        }
    }
}
