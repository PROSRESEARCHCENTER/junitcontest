package hep.aida.ref.plotter.adapter;

import hep.aida.IHistogram2D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable2DVariableHistogramData;
import jas.hist.Statistics;

/**
 * @author serbo
 * @version $Id: AIDAHistogramVariableAdapter2D.java 10738 2007-05-16 22:47:34Z serbo $
 */
public class AIDAHistogramVariableAdapter2D extends AIDAHistogramAdapter2D implements Rebinnable2DVariableHistogramData 
{
    private double[] xEdges;
    private double[] yEdges;
    
    AIDAHistogramVariableAdapter2D(IHistogram2D histo)
   {
      super(histo);
   } 
    
    public double[] getXBinEdges() {
        if (xEdges == null || yEdges == null) fillEdges();
        return xEdges;
    }
    
    public double[] getYBinEdges() {
        if (xEdges == null || yEdges == null) fillEdges();
        return yEdges;
    }
    
    private void fillEdges() {
        if (h2d == null) return;
        
        int nXBins = h2d.xAxis().bins();
        xEdges = new double[nXBins+1];
        xEdges[0] = h2d.xAxis().binLowerEdge(0);
        for (int i=0; i<nXBins; i++) {
            xEdges[i+1] = h2d.xAxis().binUpperEdge(i);
        }
        
        int nYBins = h2d.yAxis().bins();
        yEdges = new double[nYBins+1];
        yEdges[0] = h2d.yAxis().binLowerEdge(0);
        for (int j=0; j<nYBins; j++) {
            yEdges[j+1] = h2d.yAxis().binUpperEdge(j);
        }
    }
}
