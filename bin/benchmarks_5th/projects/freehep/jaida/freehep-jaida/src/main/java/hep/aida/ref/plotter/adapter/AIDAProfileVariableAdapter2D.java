package hep.aida.ref.plotter.adapter;

import hep.aida.IProfile2D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable2DVariableHistogramData;
import jas.hist.Statistics;

/**
 * @author serbo
 * @version $Id: AIDAProfileVariableAdapter2D.java 10740 2007-05-21 18:05:50Z serbo $
 */
public class AIDAProfileVariableAdapter2D extends AIDAProfileAdapter2D implements Rebinnable2DVariableHistogramData 
{
    private double[] xEdges;
    private double[] yEdges;
    
    AIDAProfileVariableAdapter2D(IProfile2D histo)
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
        if (profile == null) return;
        
        int nXBins = profile.xAxis().bins();
        xEdges = new double[nXBins+1];
        xEdges[0] = profile.xAxis().binLowerEdge(0);
        for (int i=0; i<nXBins; i++) {
            xEdges[i+1] = profile.xAxis().binUpperEdge(i);
        }
        
        int nYBins = profile.yAxis().bins();
        yEdges = new double[nYBins+1];
        yEdges[0] = profile.yAxis().binLowerEdge(0);
        for (int j=0; j<nYBins; j++) {
            yEdges[j+1] = profile.yAxis().binUpperEdge(j);
        }
    }
}
