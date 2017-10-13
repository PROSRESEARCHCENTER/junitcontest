/*
 * test.java
 *
 * Created on July 28, 2004, 2:14 PM
 */

package hep.aida.util.comparison;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.aida.ext.IComparisonAlgorithm;
import hep.aida.ext.IComparisonResult;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author  turri
 */
public class ComparisonTest {
    
    /** Creates a new instance of test */
    public ComparisonTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create("testComparison.aida","xml",false,true);
        IHistogramFactory hf = af.createHistogramFactory(tree);
        
        ITupleFactory tf = af.createTupleFactory(tree);
        String tupleName = "";
        
        int nCompAlg = StatisticalComparison.numberOfAvailableComparisonAlgorithm();
        for ( int i = 0; i < nCompAlg; i++ ) {
            IComparisonAlgorithm compAlg = StatisticalComparison.comparisonAlgorithm(i);
            String compAlgName = compAlg.algorithmNames()[0];
            
            tupleName += "ITuple "+compAlgName+" = { double binnedProbability = 1999., double binnedLessEventsProbability = 1999., double binnedWithWeightsProbability = 1999., double unbinnedProbability = 1999., double unbinnedLessEventsProbability = 1999., double unbinnedWithWeigthsProbability = 1999.}";
            if ( i != nCompAlg-1 )
                tupleName += "; ";
        }
        
        
        
        ITuple tuple = tf.create("tuple","Comparison Algorithm Test tuple",tupleName,"");
        
        int nLoop = 1000;
        int nEntries = 1000;
        int nBins = 50;
        
        IHistogram1D h1 = hf.createHistogram1D("h1","h1", nBins, -5, 5);
        IHistogram1D h2 = hf.createHistogram1D("h2","h1", nBins, -5, 5);
        IHistogram1D wh = hf.createHistogram1D("wh","wh", nBins, -5, 5);
        IHistogram1D lh = hf.createHistogram1D("lh","lh", nBins, -5, 5);
        
        ICloud1D c1 = hf.createCloud1D("c1");
        ICloud1D c2 = hf.createCloud1D("c2");
        ICloud1D wc = hf.createCloud1D("wc");
        ICloud1D lc = hf.createCloud1D("lc");
        
        
        Random r = new Random(23452);
        Random r1 = new Random(252);
        Random rw = new Random(39);
        
        
        IComparisonResult result;
        
        for ( int j = 0; j < nLoop; j++ ) {
            
            h2.reset();
            wh.reset();
            lh.reset();
            c2.reset();
            wc.reset();
            lc.reset();

            h1.reset();
            c1.reset();
            for ( int i = 0; i < nEntries; i++ ) {
                double x = r1.nextGaussian();
                h1.fill(x);
                c1.fill(x);
            }
            
            for ( int i = 0; i < nEntries; i++ ) {
                double x2 = r.nextGaussian();
                double w = rw.nextDouble();
                double w2 = 0.8;
                
                h2.fill(x2);
                c2.fill(x2);
                
                wh.fill(x2, w2);
                wc.fill(x2, w2);
                
                if ( w < 0.8 ) {
                    lh.fill(x2);
                    lc.fill(x2);
                }
            }
            
            for ( int i = 0; i < nCompAlg; i++ ) {
                ITuple tup = tuple.getTuple(i);
                IComparisonAlgorithm compAlg = StatisticalComparison.comparisonAlgorithm(i);
                String algName = compAlg.algorithmNames()[0];
                if ( StatisticalComparison.canCompare(h1, h2,algName) ) {
                    result = StatisticalComparison.compare(h1,h2,algName);
                    tup.fill(0,result.quality());
                    result = StatisticalComparison.compare(h1,lh,algName);
                    tup.fill(1,result.quality());
                    result = StatisticalComparison.compare(h1,wh,algName);
                    tup.fill(2,result.quality());
                }
                if ( StatisticalComparison.canCompare(c1, c2,algName) ) {
                    result = StatisticalComparison.compare(c1,c2,algName);
                    tup.fill(3,result.quality());
                    result = StatisticalComparison.compare(c1,lc,algName);
                    tup.fill(4,result.quality());
                    result = StatisticalComparison.compare(c1,wc,algName);
                    tup.fill(5,result.quality());
                }
                tup.addRow();
            }
            
            tuple.addRow();
        }
        
        tree.commit();
    }
    
}
