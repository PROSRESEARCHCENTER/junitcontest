package hep.aida.ref.plotter.adapter;

import hep.aida.IHistogram1D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DVariableHistogramData;
import jas.hist.Statistics;

/**
 * @author serbo
 * @version $Id: AIDAHistogramVariableAdapter1D.java 10738 2007-05-16 22:47:34Z serbo $
 */
public class AIDAHistogramVariableAdapter1D extends AIDAHistogramAdapter1D implements Rebinnable1DVariableHistogramData 
{
    private double[] edges;
    
    AIDAHistogramVariableAdapter1D(IHistogram1D histo)
   {
      super(histo);
   } 
    
    public double[] getBinEdges() {
        if (edges == null) fillEdges();
        return edges;
    }
    
    private void fillEdges() {
        if (h1d == null) return;
        int nBins = h1d.axis().bins();
        edges = new double[nBins+1];
        edges[0] = h1d.axis().binLowerEdge(0);
        for (int i=0; i<nBins; i++) {
            edges[i+1] = h1d.axis().binUpperEdge(i);
        }
    }
}
