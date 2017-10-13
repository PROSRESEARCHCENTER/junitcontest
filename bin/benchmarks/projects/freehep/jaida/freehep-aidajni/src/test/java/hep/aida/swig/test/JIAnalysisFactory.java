package hep.aida.swig.test;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSetFactory;
import hep.aida.IFitFactory;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotterFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITupleFactory;

public class JIAnalysisFactory extends IAnalysisFactory {

	public JIAnalysisFactory() {
		System.err.println("JIAnalysisFactory created");
	}
	
	public IDataPointSetFactory createDataPointSetFactory(ITree arg0)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public IFitFactory createFitFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFunctionFactory createFunctionFactory(ITree arg0)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public IHistogramFactory createHistogramFactory(ITree arg0)
			throws IllegalArgumentException {
		System.err.println("JIHistogramFactory created");
		return new JIHistogramFactory();
	}

	public IPlotterFactory createPlotterFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITreeFactory createTreeFactory() {
		System.err.println("JITreeFactory created");
		return new JITreeFactory();
	}

	public ITupleFactory createTupleFactory(ITree arg0)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
}
