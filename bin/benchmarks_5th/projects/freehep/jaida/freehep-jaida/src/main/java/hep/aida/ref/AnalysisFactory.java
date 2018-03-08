package hep.aida.ref;

import hep.aida.IPlotterFactory;
import hep.aida.ref.plotter.PlotterFactory;

/**
 *
 * @author tonyj
 * @version $Id: AnalysisFactory.java 15963 2014-04-02 21:16:43Z jeremym $
 */
public class AnalysisFactory extends BatchAnalysisFactory
{
    public IPlotterFactory createPlotterFactory() {
        return new PlotterFactory();
    }
    
    // Add this method to fix FREEHEP-12 where a DummyPlotterFactory was returned by 
    // the super class method.  --JM
    public IPlotterFactory createPlotterFactory(String name) {
        return new PlotterFactory();
    }
}
