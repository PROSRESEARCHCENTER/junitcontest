package hep.aida.ref.test.pdf;

import hep.aida.IAnalysisFactory;
import hep.aida.IFitFactory;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import junit.framework.TestCase;

public abstract class AbstractPdfTestCase extends TestCase {
    
    private IFitFactory fitFactory;
    private IHistogramFactory histogramFactory;
    private IFunctionFactory functionFactory;
    
    AbstractPdfTestCase(java.lang.String name) {
        super( name );
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITreeFactory      treeFactory = analysisFactory.createTreeFactory();
        ITree             tree = treeFactory.create();
        histogramFactory = analysisFactory.createHistogramFactory(tree);
        functionFactory = analysisFactory.createFunctionFactory(tree);
        fitFactory = analysisFactory.createFitFactory();
    }
    
    protected IFitFactory fitFactory() {
        return fitFactory;
    }
    
    protected IHistogramFactory histogramFactory() {
        return histogramFactory;
    }
    
    protected IFunctionFactory functionFactory() {
        return functionFactory;
    }
    
}