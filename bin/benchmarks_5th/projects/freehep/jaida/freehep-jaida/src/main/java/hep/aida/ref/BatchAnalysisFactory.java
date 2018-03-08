package hep.aida.ref;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSetFactory;
import hep.aida.IFitFactory;
import hep.aida.IFunctionFactory;
import hep.aida.IGenericFactory;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotterFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITupleFactory;
import hep.aida.ref.fitter.FitFactory;
import hep.aida.ref.function.FunctionFactory;
import hep.aida.ref.histogram.DataPointSetFactory;
import hep.aida.ref.histogram.HistogramFactory;
import hep.aida.ref.plotter.DummyPlotterFactory;
import hep.aida.ref.tree.TreeFactory;
import hep.aida.ref.tuple.TupleFactory;


/**
 * @author Mark Donszelmann
 * @version $Id: BatchAnalysisFactory.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BatchAnalysisFactory extends IAnalysisFactory {
    
    public BatchAnalysisFactory() {        
    }

    public ITreeFactory createTreeFactory(String options) {
        return new TreeFactory(this);
    }

    public IHistogramFactory createHistogramFactory(ITree tree, String options) {
        return new HistogramFactory(tree);
    }
   
    public ITupleFactory createTupleFactory(ITree tree, String options) {
        return new TupleFactory(tree);
    }

    public IFunctionFactory createFunctionFactory (ITree tree, String options) {
        return new FunctionFactory(tree); 
    }  

    public IPlotterFactory createPlotterFactory(String options) {
        return new DummyPlotterFactory();
    }  

    public IDataPointSetFactory createDataPointSetFactory(ITree iTree, String options) {
        return new DataPointSetFactory(iTree);
    }
    
    public IFitFactory createFitFactory(String options) {
        return new FitFactory(); 
    }  

    public IGenericFactory createGenericFactory(String factoryType, String options) {
        throw new UnsupportedOperationException("CreateGenericFactory is currently not supported");
    }

    public IGenericFactory createManagedObjectGenericFactory(String arg0, ITree arg1, String arg2) {
        throw new UnsupportedOperationException("CreateManagedObjectGenericFactory is currently not supported");
    }
}
