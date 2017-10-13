/*
 * HistogramFactory.java
 *
 * Created on February 14, 2001, 1:09 PM
 */

package hep.aida.ref.histogram;
import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IBaseHistogram;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.histogram.binner.AbstractBinner1D;
import hep.aida.ref.tree.Tree;

/**
 * @author The AIDA team @ SLAC.
 * @version $Id: HistogramFactory.java 13789 2010-11-24 22:24:39Z turri $
 */
public class HistogramFactory implements IHistogramFactory {
    
    private Tree tree;
    private final static int defaultMaxEntries=100000;
    private HistMath histMath;
    
    private String nameInPath( String path ) {
        return AidaUtils.parseName(path);
    }
    
    private String parentPath( String path ) {
        return AidaUtils.parseDirName(path);
    }
    
    /**
     * Create a new HistogramFactory.
     * This constructor is used by AnalysisFactory, the Master Factory.
     * @param tree the ITree where the histogram is added.
     */
    public HistogramFactory(ITree tree) {
        this((Tree)tree);
    }
    
    /**
     * Create a new HistogramFactory.
     * This constructor is used by AnalysisFactory, the Master Factory.
     * @param tree the Tree where the histogram is added.
     */
    public HistogramFactory(Tree tree) {
        this.tree = tree;
        histMath = new HistMath();
    }
    
    /**
     * Destroy an IBaseHistogram ogject.
     *
     */
    public void destroy(IBaseHistogram hist) throws IllegalArgumentException {
	if (tree != null) {
	    String path = tree.findPath((IManagedObject) hist);
	    tree.rm(path);
	}
    }
    
    /**
     * Create a Cloud1D, and unbinned 1-dimensional Histogram.
     * @param path The path of the Cloud1D.
     * @param title The title of the Cloud1D.
     * @param nMax The maximum number of entries after which the Cloud1D will convert to an Histogram1D.
     *             If nMax = -1 then the Cloud1D will not convert automatically to an Histogram1D.
     * @param options The options for creating a Cloud1D.
     *
     */
    public ICloud1D createCloud1D(String path, String title, int nMax, String options) {
        Cloud1D result= new Cloud1D(nameInPath(path),title,nMax,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
    
    public ICloud1D createCloud1D(String path, String title, int nMax) {
        return createCloud1D(path, title, nMax, "");
    }
    
    public ICloud1D createCloud1D(String path, String title) {
        return createCloud1D(path, title, defaultMaxEntries);
    }
    
    public ICloud1D createCloud1D(String pathAndTitle) {
        return createCloud1D(pathAndTitle, nameInPath(pathAndTitle));
    }
    
    public ICloud1D createCopy( String path, ICloud1D cloud) {
        Cloud1D newCloud = copy(nameInPath(path), cloud);
        if (tree != null) tree.addFromFactory(parentPath(path),newCloud);
        return newCloud;
    }
    
    private Cloud1D copy( String path, ICloud1D cloud) {
        boolean hAida = !(cloud instanceof Cloud1D);
        String options = null;
        if (!hAida) options = ((Cloud1D) cloud).getOptions();
        Cloud1D newCloud = new Cloud1D(nameInPath(path), cloud.title(), cloud.maxEntries(), options );
        copy(newCloud.annotation(), cloud.annotation());
        
        if ( cloud.isConverted() ) 
            newCloud.setHistogram( copy( nameInPath(path), cloud.histogram() ) );
        else 
            for ( int i = 0; i < cloud.entries(); i++ )
                newCloud.fill( cloud.value(i), cloud.weight(i) );
        newCloud.setLowerEdge( cloud.lowerEdge() );
        newCloud.setUpperEdge( cloud.upperEdge() );
        if (cloud instanceof ManagedObject) newCloud.setFillable( ((ManagedObject) cloud).isFillable() );
        copy(newCloud.annotation(),cloud.annotation());
        return newCloud;
    }
    

    
    /**
     * Create a Cloud2D, and unbinned 2-dimensional Histogram.
     * @param path The path of the Cloud2D.
     * @param title The title of the Cloud2D.
     * @param nMax The maximum number of entries after which the Cloud2D will convert to an Histogram2D.
     *             If nMax = -1 then the Cloud2D will not convert automatically to an Histogram2D.
     * @param options The options for creating a Cloud2D.
     *
     */
    public ICloud2D createCloud2D(String path, String title, int nMax, String options) {
        Cloud2D result= new Cloud2D(nameInPath(path),title,nMax,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
    
    public ICloud2D createCloud2D(String path, String title, int nMax) {
        return createCloud2D(path, title, nMax, "");
    }
    
    public ICloud2D createCloud2D(String path, String title) {
        return createCloud2D(path, title, defaultMaxEntries);
    }
    
    public ICloud2D createCloud2D(String pathAndTitle) {
        return createCloud2D(pathAndTitle, nameInPath(pathAndTitle));
    }
    
    public ICloud2D createCopy( String path, ICloud2D cloud) {
        Cloud2D newCloud = copy(nameInPath(path), cloud);
        if (tree != null) tree.addFromFactory(parentPath(path),newCloud);
        return newCloud;
    }
    
    private Cloud2D copy( String path, ICloud2D cloud) {
        boolean hAida = !(cloud instanceof Cloud2D);
        String options = null;
        if (!hAida) options = ((Cloud2D) cloud).getOptions();
        Cloud2D newCloud = new Cloud2D(nameInPath(path), cloud.title(), cloud.maxEntries(), options );
        copy(newCloud.annotation(), cloud.annotation());

        if ( cloud.isConverted() ) 
            newCloud.setHistogram( copy( nameInPath(path), cloud.histogram() ) );
        else 
            for ( int i = 0; i < cloud.entries(); i++ )
                newCloud.fill( cloud.valueX(i), cloud.valueY(i), cloud.weight(i) );
        newCloud.setLowerEdgeX( cloud.lowerEdgeX() );
        newCloud.setUpperEdgeX( cloud.upperEdgeX() );
        newCloud.setLowerEdgeY( cloud.lowerEdgeY() );
        newCloud.setUpperEdgeY( cloud.upperEdgeY() );
        if (cloud instanceof ManagedObject) newCloud.setFillable( ((ManagedObject) cloud).isFillable() );
        copy(newCloud.annotation(),cloud.annotation());
        return newCloud;
    }


    /**
     * Create a Cloud3D, and unbinned 3-dimensional Histogram.
     * @param path The path of the Cloud3D.
     * @param title The title of the Cloud3D.
     * @param nMax The maximum number of entries after which the Cloud3D will convert to an Histogram3D.
     *             If nMax = -1 then the Cloud3D will not convert automatically to an Histogram3D.
     * @param options The options for creating a Cloud3D.
     *
     */
    public ICloud3D createCloud3D(String path, String title, int nMax, String options) {
        Cloud3D result= new Cloud3D(nameInPath(path),title,nMax,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
    
    public ICloud3D createCloud3D(String path, String title, int nMax) {
        return createCloud3D(path, title, nMax, "");
    }
    
    public ICloud3D createCloud3D(String path, String title) {
        return createCloud3D(path, title, defaultMaxEntries);
    }
    
    public ICloud3D createCloud3D(String pathAndTitle) {
        return createCloud3D(pathAndTitle, nameInPath(pathAndTitle));
    }
        
    public ICloud3D createCopy( String path, ICloud3D cloud) {
        Cloud3D newCloud = copy(nameInPath(path), cloud);
        if (tree != null) tree.addFromFactory(parentPath(path),newCloud);
        return newCloud;
    }
        
    private Cloud3D copy( String path, ICloud3D cloud) {
        boolean hAida = !(cloud instanceof Cloud3D);
        String options = null;
        if (!hAida) options = ((Cloud3D) cloud).getOptions();
        Cloud3D newCloud = new Cloud3D(nameInPath(path), cloud.title(), cloud.maxEntries(), options );
        copy(newCloud.annotation(), cloud.annotation());

        if ( cloud.isConverted() ) 
            newCloud.setHistogram( copy( nameInPath(path), cloud.histogram() ) );
        else 
            for ( int i = 0; i < cloud.entries(); i++ )
                newCloud.fill( cloud.valueX(i), cloud.valueY(i), cloud.valueZ(i), cloud.weight(i) );
        newCloud.setLowerEdgeX( cloud.lowerEdgeX() );
        newCloud.setUpperEdgeX( cloud.upperEdgeX() );
        newCloud.setLowerEdgeY( cloud.lowerEdgeY() );
        newCloud.setUpperEdgeY( cloud.upperEdgeY() );
        newCloud.setLowerEdgeZ( cloud.lowerEdgeZ() );
        newCloud.setUpperEdgeZ( cloud.upperEdgeZ() );
        if (cloud instanceof ManagedObject) newCloud.setFillable( ((ManagedObject) cloud).isFillable() );
        copy(newCloud.annotation(),cloud.annotation());
        return newCloud;
    }



    /**
     * Create a IHistogram1D.
     *
     */
    private IAxis copy( IAxis axis ) {
        if ( axis.isFixedBinning())
            return new FixedAxis( axis.bins(), axis.lowerEdge(),  axis.upperEdge() );
        else {
            double[] edges = new double[ axis.bins() + 1 ];
            edges[0] = axis.binLowerEdge(0);
            for ( int i = 0; i < axis.bins(); i ++ )
                edges[i+1] = axis.binUpperEdge(i);
            return new VariableAxis( edges );
        }
    }
    
    public static void copy(IAnnotation newAn, IAnnotation an) {
        int size = an.size();
        for (int i=0; i<size; i++) {
            String key = an.key(i);
            if (key.equals(Annotation.titleKey))    continue;
            if (key.equals(Annotation.aidaPathKey)) continue;
            if (key.equals(Annotation.fullPathKey)) continue;
            String val = an.value(key);
            boolean sticky = an.isSticky(key);
            if (newAn.hasKey(key)) {
                newAn.setValue(key, val);
                newAn.setSticky(key, sticky);
            } else {
                newAn.addItem(key, val, sticky);
            }
        }
    }
    
    
    public IHistogram1D createHistogram1D(String path, String title, int nBins, double lowerEdge, double upperEdge, String options) {
        IAxis axis = new FixedAxis(nBins,lowerEdge,upperEdge);
        Histogram1D result = new Histogram1D(nameInPath(path),title,axis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
        
    public IHistogram1D createHistogram1D(String path, String title, int nBins, double lowerEdge, double upperEdge) {
        return createHistogram1D(path, title, nBins, lowerEdge, upperEdge, "");
    }
    
    public IHistogram1D createHistogram1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge) {
        return createHistogram1D(pathAndTitle, nameInPath(pathAndTitle), nBins, lowerEdge, upperEdge);
    }
    
    public IHistogram1D createHistogram1D(String path, String title, double[] binEdges, String options) {
        IAxis axis = new VariableAxis(binEdges);
        Histogram1D result = new Histogram1D(nameInPath(path),title,axis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
    
    public IHistogram1D createHistogram1D(String path, String title, double[] binEdges) {
        return createHistogram1D(path,title,binEdges,"");
    }
    
    public IHistogram1D createCopy(String path, IHistogram1D hist) {
        Histogram1D newHist = copy(nameInPath(path),hist);
        if (tree != null) tree.addFromFactory( parentPath(path),newHist);
        return newHist;
    }
        
    private Histogram1D copy(String name, IHistogram1D hist) {
        IAxis axis = hist.axis();
        
        boolean hAida = !(hist instanceof Histogram1D);
        String options = null;
        if (!hAida) options = ((Histogram1D) hist).options();

        Histogram1D newHist = new Histogram1D(name, hist.title(), copy( axis ), options );
        copy(newHist.annotation(), hist.annotation());

        if (!hAida) {
            newHist.binner().initBinner(((Histogram1D) hist).binner());
            newHist.initHistogram1D(((Histogram1D) hist).binner());
            newHist.setNEntries(hist.allEntries());
            newHist.setValidEntries(hist.entries());
            if (((Histogram1D) hist).isMeanAndRmsSet()) newHist.setMeanAndRms( hist.mean(), hist.rms() );
        } else {
            int bins = axis.bins()+2;
            
            double[] heights = new double[bins];
            double[] errors  = new double[bins];
            double[] means   = new double[bins];
            double[] rmss    = new double[bins];
            int[]    entries = new int   [bins];
            
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                int bin = newHist.mapBinNumber(i,axis);
                heights[bin] = hist.binHeight(i);
                errors [bin] = hist.binError(i);
                entries[bin] = hist.binEntries(i);
                means  [bin] = hist.binMean(i);
                rmss [bin] = (hist.axis().binUpperEdge(i)-hist.axis().binLowerEdge(i))/Math.sqrt(12);
            }
            
            newHist.setContents(heights,errors,entries,means,rmss);
            newHist.setNEntries(hist.allEntries());
            newHist.setValidEntries(hist.entries());
            newHist.setMeanAndRms( hist.mean(), hist.rms() );
        }
        if (hist instanceof ManagedObject) newHist.setFillable( ((ManagedObject) hist).isFillable() );
        copy(newHist.annotation(),hist.annotation());
        return newHist;
    }
    
 
    /**
     * Create a IHistogram2D.
     *
     */
    public IHistogram2D createHistogram2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, String options) {
        IAxis xAxis = new FixedAxis(nBinsX, lowerEdgeX, upperEdgeX);
        IAxis yAxis = new FixedAxis(nBinsY, lowerEdgeY, upperEdgeY);
        Histogram2D result = new Histogram2D(nameInPath(path),title,xAxis,yAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
       
    public IHistogram2D createHistogram2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
        return createHistogram2D(path, title, nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY,"");
    }
    
    public IHistogram2D createHistogram2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
        return createHistogram2D(pathAndTitle, nameInPath(pathAndTitle), nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY);
    }
        
    public IHistogram2D createHistogram2D(String path, String title, double[] binEdgesX, double[] binEdgesY, String options) {
        IAxis xAxis = new VariableAxis(binEdgesX);
        IAxis yAxis = new VariableAxis(binEdgesY);
        Histogram2D result = new Histogram2D(nameInPath(path),title,xAxis,yAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }

    public IHistogram2D createHistogram2D(String path, String title, double[] binEdgesX, double[] binEdgesY) {
        return createHistogram2D(path,title,binEdgesX,binEdgesY,"");
    }
    
    public IHistogram2D createCopy(String path, IHistogram2D hist) {
        Histogram2D newHist = copy(nameInPath(path),hist);
        if (tree != null) tree.addFromFactory( parentPath(path),newHist);
        return newHist;
    }

    private Histogram2D copy(String name, IHistogram2D hist) {
        IAxis xAxis = hist.xAxis();
        IAxis yAxis = hist.yAxis();
        
        boolean hAida = !(hist instanceof Histogram2D);
        String options = null;
        if (!hAida) options = ((Histogram2D) hist).options();
        Histogram2D newHist = new Histogram2D(name, hist.title(), copy( xAxis ), copy( yAxis ), options );
        copy(newHist.annotation(), hist.annotation());

        int xBins = xAxis.bins()+2;
        int yBins = yAxis.bins()+2;
        
        double[][] heights = new double[xBins][yBins];
        double[][] errors  = new double[xBins][yBins];
        double[][] meanXs  = new double[xBins][yBins];
        double[][] rmsXs   = new double[xBins][yBins];
        double[][] meanYs  = new double[xBins][yBins];
        double[][] rmsYs   = new double[xBins][yBins];
        int[][]    entries = new int   [xBins][yBins];
        
        for(int i=IAxis.UNDERFLOW_BIN; i<xBins-2;i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<yBins-2;j++) {
                int xbin = newHist.mapBinNumber(i,xAxis);
                int ybin = newHist.mapBinNumber(j,yAxis);
                heights[xbin][ybin] = hist.binHeight(i,j);
                errors [xbin][ybin] = hist.binError(i,j);
                entries[xbin][ybin] = hist.binEntries(i,j);
                meanXs [xbin][ybin] = hist.binMeanX(i,j);
                meanYs [xbin][ybin] = hist.binMeanY(i,j);
                if (hAida) {
                    rmsXs  [xbin][ybin] = (hist.xAxis().binUpperEdge(i)-hist.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsYs  [xbin][ybin] = (hist.yAxis().binUpperEdge(j)-hist.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsXs  [xbin][ybin] = ((Histogram2D) hist).binRmsX(i,j);
                    rmsYs  [xbin][ybin] = ((Histogram2D) hist).binRmsY(i,j);
                }
            }
        }
        newHist.setContents(heights,errors,entries,meanXs,rmsXs,meanYs,rmsYs);
        newHist.setMeanX( hist.meanX() );
        newHist.setRmsX( hist.rmsX() );
        newHist.setMeanY( hist.meanY() );
        newHist.setRmsY( hist.rmsY() );
        newHist.setNEntries( hist.allEntries() );
        newHist.setValidEntries(hist.entries());
        if (hist instanceof ManagedObject) newHist.setFillable( ((ManagedObject) hist).isFillable() );
        copy(newHist.annotation(),hist.annotation());
        return newHist;
    }


    
    
    /**
     * Create a IHistogram3D.
     *
     */
    public IHistogram3D createHistogram3D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ, String options) {
        IAxis xAxis = new FixedAxis(nBinsX, lowerEdgeX, upperEdgeX);
        IAxis yAxis = new FixedAxis(nBinsY, lowerEdgeY, upperEdgeY);
        IAxis zAxis = new FixedAxis(nBinsZ, lowerEdgeZ, upperEdgeZ);
        Histogram3D result = new Histogram3D(nameInPath(path),title,xAxis,yAxis,zAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
    
    public IHistogram3D createHistogram3D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
        return createHistogram3D(path, title, nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY, nBinsZ, lowerEdgeZ, upperEdgeZ, "");
    }
    
    public IHistogram3D createHistogram3D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
        return createHistogram3D(pathAndTitle, nameInPath(pathAndTitle), nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY, nBinsZ, lowerEdgeZ, upperEdgeZ, "");
    }
        
    public IHistogram3D createHistogram3D(String path, String title, double[] binEdgesX, double[] binEdgesY, double[] binEdgesZ, String options) {
        IAxis xAxis = new VariableAxis(binEdgesX);
        IAxis yAxis = new VariableAxis(binEdgesY);
        IAxis zAxis = new VariableAxis(binEdgesZ);
        Histogram3D result = new Histogram3D(nameInPath(path),title,xAxis,yAxis,zAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }
        
    public IHistogram3D createHistogram3D(String path, String title, double[] binEdgesX, double[] binEdgesY, double[] binEdgesZ) {
        return createHistogram3D(path,title,binEdgesX,binEdgesY,binEdgesZ,"");
    }
    
    public IHistogram3D createCopy(String path, IHistogram3D hist) {
        Histogram3D newHist = copy(nameInPath(path),hist);
        if (tree != null) tree.addFromFactory( parentPath(path),newHist);
        return newHist;
    }

    private Histogram3D copy(String name, IHistogram3D hist) {
        IAxis xAxis = hist.xAxis();
        IAxis yAxis = hist.yAxis();
        IAxis zAxis = hist.zAxis();
        
        boolean hAida = !(hist instanceof Histogram3D);
        String options = null;
        if (!hAida) options = ((Histogram3D) hist).options();
        Histogram3D newHist = new Histogram3D(name, hist.title(), copy( xAxis ), copy( yAxis ), copy( zAxis ), options );
        copy(newHist.annotation(), hist.annotation());

        int xBins = xAxis.bins()+2;
        int yBins = yAxis.bins()+2;
        int zBins = zAxis.bins()+2;
        
        double[][][] heights = new double[xBins][yBins][zBins];
        double[][][] errors  = new double[xBins][yBins][zBins];
        double[][][] meanXs  = new double[xBins][yBins][zBins];
        double[][][] rmsXs   = new double[xBins][yBins][zBins];
        double[][][] meanYs  = new double[xBins][yBins][zBins];
        double[][][] rmsYs   = new double[xBins][yBins][zBins];
        double[][][] meanZs  = new double[xBins][yBins][zBins];
        double[][][] rmsZs   = new double[xBins][yBins][zBins];
        int[][][]    entries = new int   [xBins][yBins][zBins];
        
        for(int i=IAxis.UNDERFLOW_BIN; i<xBins-2;i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<yBins-2;j++) {
                for(int k=IAxis.UNDERFLOW_BIN; k<zBins-2;k++) {
                    int xbin = newHist.mapBinNumber(i,xAxis);
                    int ybin = newHist.mapBinNumber(j,yAxis);
                    int zbin = newHist.mapBinNumber(k,zAxis);
                    heights[xbin][ybin][zbin] = hist.binHeight(i,j,k);
                    errors [xbin][ybin][zbin] = hist.binError(i,j,k);
                    entries[xbin][ybin][zbin] = hist.binEntries(i,j,k);
                    meanXs [xbin][ybin][zbin] = hist.binMeanX(i,j,k);
                    meanYs [xbin][ybin][zbin] = hist.binMeanY(i,j,k);
                    meanZs [xbin][ybin][zbin] = hist.binMeanZ(i,j,k);
                    
                    if (hAida) {
                        rmsXs  [xbin][ybin][zbin] = (hist.xAxis().binUpperEdge(i)-hist.xAxis().binLowerEdge(i))/Math.sqrt(12);
                        rmsYs  [xbin][ybin][zbin] = (hist.yAxis().binUpperEdge(j)-hist.yAxis().binLowerEdge(j))/Math.sqrt(12);
                        rmsZs  [xbin][ybin][zbin] = (hist.zAxis().binUpperEdge(k)-hist.zAxis().binLowerEdge(k))/Math.sqrt(12);
                    } else {
                        rmsXs  [xbin][ybin][zbin] = ((Histogram3D) hist).binRmsX(i,j,k);
                        rmsYs  [xbin][ybin][zbin] = ((Histogram3D) hist).binRmsY(i,j,k);
                        rmsZs  [xbin][ybin][zbin] = ((Histogram3D) hist).binRmsZ(i,j,k);
                    }
                }
            }
        }
        newHist.setContents(heights,errors,entries,meanXs,rmsXs,meanYs,rmsYs,meanZs,rmsZs);
        newHist.setMeanX( hist.meanX() );
        newHist.setRmsX( hist.rmsX() );
        newHist.setMeanY( hist.meanY() );
        newHist.setRmsY( hist.rmsY() );
        newHist.setMeanZ( hist.meanZ() );
        newHist.setRmsZ( hist.rmsZ() );
        newHist.setNEntries( hist.allEntries() );
        newHist.setValidEntries(hist.entries());
        if (hist instanceof ManagedObject) newHist.setFillable( ((ManagedObject) hist).isFillable() );
        copy(newHist.annotation(),hist.annotation());
        return newHist;
    }

    /**
     * Create a IProfile1D.
     *
     */
    public IProfile1D createProfile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, String options) {
        IAxis axis = new FixedAxis(nBins,lowerEdge,upperEdge);
        Profile1D result = new Profile1D(nameInPath(path),title,axis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }

    public IProfile1D createProfile1D(String path, String title, int nBins, double lowerEdge, double upperEdge) {
        return createProfile1D(path, title, nBins, lowerEdge, upperEdge, "");
    }

    public IProfile1D createProfile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue, String options) {
        // lowerValue and upperValue currently ignored
        return createProfile1D(path, title, nBins, lowerEdge, upperEdge, "");
    }

    public IProfile1D createProfile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue) {
        // lowerValue and upperValue currently ignored
        return createProfile1D(path, title, nBins, lowerEdge, upperEdge, "");
    }

    public IProfile1D createProfile1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge) {
        return createProfile1D(pathAndTitle, nameInPath(pathAndTitle), nBins, lowerEdge, upperEdge, "");
    }

    public IProfile1D createProfile1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue) {
        // lowerValue and upperValue currently ignored
        return createProfile1D(pathAndTitle, nameInPath(pathAndTitle), nBins, lowerEdge, upperEdge, "");
    }

    public IProfile1D createProfile1D(String path, String title, double[] binEdges, String options) {
        IAxis axis = new VariableAxis(binEdges);
        Profile1D result = new Profile1D(nameInPath(path),title,axis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }

    public IProfile1D createProfile1D(String path, String title, double[] binEdges) {
        return createProfile1D(path,title,binEdges,"");
    }
    
    public IProfile1D createProfile1D(String path, String title, double[] binEdges, double lowerValue, double upperValue, String options) {
        // lowerValue and upperValue currently ignored
        return createProfile1D(path,title,binEdges,"");
    }

    public IProfile1D createProfile1D(String path, String title, double[] binEdges, double lowerValue, double upperValue) {
        return createProfile1D(path,title,binEdges,lowerValue,upperValue,"");
    }
    
    public IProfile1D createCopy(String path, IProfile1D profile) {
        Profile1D newProfile = copy(nameInPath(path), profile);
        if (tree != null) tree.addFromFactory( parentPath(path),newProfile);
        return newProfile;
    }
    
    private Profile1D copy(String path, IProfile1D profile) {
        if ( !(profile instanceof Profile1D) ) return copyAida(path, profile);
        Profile1D oldProfile = (Profile1D) profile;
        Profile1D newProfile = new Profile1D( nameInPath(path), oldProfile.title(), copy( oldProfile.axis() ), oldProfile.options() );
        copy(newProfile.annotation(), profile.annotation());
        newProfile.setHistogram( copy(nameInPath(path), oldProfile.histogram() ) );
        if (profile instanceof ManagedObject) newProfile.setFillable( ((ManagedObject) profile).isFillable() );
        copy(newProfile.annotation(),profile.annotation());
        return newProfile;
    }
    
    private Profile1D copyAida(String path, IProfile1D profile) {
        IAxis axis = profile.axis();
        Profile1D newProfile = new Profile1D( nameInPath(path), profile.title(), copy( axis ), null );
        copy(newProfile.annotation(), profile.annotation());
        
        int bins = axis.bins()+2;
        
        double[] heights = new double[bins];
        double[] errors  = new double[bins];
        double[] means   = new double[bins];
        double[] rmss    = new double[bins];
        int[]    entries = new int   [bins];
        
        for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
            int bin = newProfile.mapBinNumber(i, axis);
            heights[bin] = profile.binHeight(i);
            errors [bin] = profile.binError(i);
            entries[bin] = profile.binEntries(i);
            means  [bin] = profile.binMean(i);
            rmss   [bin] = profile.binRms(i);
        }
        newProfile.setContents(heights,errors,entries,rmss,means);
        newProfile.setMean( profile.mean() );
        newProfile.setRms( profile.rms() );
        newProfile.setNEntries( profile.allEntries() );
        
        if (profile instanceof ManagedObject) newProfile.setFillable( ((ManagedObject) profile).isFillable() );
        copy(newProfile.annotation(),profile.annotation());
        return newProfile;
    }
    

    /**
     * Create a IProfile2D.
     *
     */
    public IProfile2D createProfile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, String options) {
        IAxis xAxis = new FixedAxis(nBinsX, lowerEdgeX, upperEdgeX);
        IAxis yAxis = new FixedAxis(nBinsY, lowerEdgeY, upperEdgeY);
        Profile2D result = new Profile2D(nameInPath(path),title,xAxis,yAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }

    public IProfile2D createProfile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
        return createProfile2D(path,title,nBinsX,lowerEdgeX,upperEdgeX,nBinsY,lowerEdgeY,upperEdgeY,"");
    }

    public IProfile2D createProfile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue, String options) {
        // lowerValue and upperValue currently ignored
        return createProfile2D(path,title,nBinsX,lowerEdgeX,upperEdgeX,nBinsY,lowerEdgeY,upperEdgeY,"");
    }

    public IProfile2D createProfile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue) {
        // lowerValue and upperValue currently ignored
        return createProfile2D(path,title,nBinsX,lowerEdgeX,upperEdgeX,nBinsY,lowerEdgeY,upperEdgeY,"");
    }

    public IProfile2D createProfile2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
        return createProfile2D(pathAndTitle,nameInPath(pathAndTitle),nBinsX,lowerEdgeX,upperEdgeX,nBinsY,lowerEdgeY,upperEdgeY,"");
    }

    public IProfile2D createProfile2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue) {
        // lowerValue and upperValue currently ignored
        return createProfile2D(pathAndTitle,nameInPath(pathAndTitle),nBinsX,lowerEdgeX,upperEdgeX,nBinsY,lowerEdgeY,upperEdgeY,"");
    }

    public IProfile2D createProfile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, String options) {
        IAxis xAxis = new VariableAxis(binEdgesX);
        IAxis yAxis = new VariableAxis(binEdgesY);
        Profile2D result = new Profile2D(nameInPath(path),title,xAxis,yAxis,options);
        if (tree != null) tree.addFromFactory(parentPath(path),result);
        return result;
    }

    public IProfile2D createProfile2D(String path, String title, double[] binEdgesX, double[] binEdgesY) {
        return createProfile2D(path,title,binEdgesX,binEdgesY,"");
    }
    
    public IProfile2D createProfile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, double lowerValue, double upperValue, String options) {
        // lowerValue and upperValue currently ignored
        return createProfile2D(path,title,binEdgesX,binEdgesY,"");
    }

    public IProfile2D createProfile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, double lowerValue, double upperValue) {
        return createProfile2D(path,title,binEdgesX,binEdgesY,lowerValue,upperValue,"");
    }
        
    public IProfile2D createCopy(String path, IProfile2D profile) {
        Profile2D newProfile = copy(path, profile);
        if (tree != null) tree.addFromFactory( parentPath(path),newProfile);
        return newProfile;
    }

    private Profile2D copy(String path, IProfile2D profile) {
        if ( !(profile instanceof Profile2D) ) return copyAida(path, profile);
        Profile2D oldProfile = (Profile2D) profile;
        Profile2D newProfile = new Profile2D( nameInPath(path), oldProfile.title(), copy( oldProfile.xAxis() ), copy( oldProfile.yAxis() ), oldProfile.options() );
        copy(newProfile.annotation(), profile.annotation());
        newProfile.setHistogram( copy(nameInPath(path), oldProfile.histogram() ) );
        if (profile instanceof ManagedObject) newProfile.setFillable( ((ManagedObject) profile).isFillable() );
        copy(newProfile.annotation(),profile.annotation());
        return newProfile;
    }

    private Profile2D copyAida(String path, IProfile2D profile) {
        IAxis xAxis = profile.xAxis();
        IAxis yAxis = profile.yAxis();
        Profile2D newProfile = new Profile2D( nameInPath(path), profile.title(), copy( xAxis ), copy( yAxis ), null );
        copy(newProfile.annotation(), profile.annotation());

        int xBins = xAxis.bins()+2;
        int yBins = yAxis.bins()+2;
        
        double[][] heights = new double[xBins][yBins];
        double[][] errors  = new double[xBins][yBins];
        double[][] meanXs  = new double[xBins][yBins];
        double[][] meanYs  = new double[xBins][yBins];
        double[][] rmss   = new double[xBins][yBins];
        int[][]    entries = new int   [xBins][yBins];
        
        for(int i=IAxis.UNDERFLOW_BIN; i<xBins-2;i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<yBins-2;j++) {
                int xbin = newProfile.mapBinNumber(i,xAxis);
                int ybin = newProfile.mapBinNumber(j,yAxis);
                heights[xbin][ybin] = profile.binHeight(i,j);
                errors [xbin][ybin] = profile.binError(i,j);
                entries[xbin][ybin] = profile.binEntries(i,j);
                meanXs [xbin][ybin] = profile.binMeanX(i,j);
                meanYs [xbin][ybin] = profile.binMeanY(i,j);
                rmss  [xbin][ybin] = profile.binRms(i,j);
            }
        }
        newProfile.setContents(heights,errors,entries,rmss,meanXs,meanYs);
        newProfile.setMeanX( profile.meanX() );
        newProfile.setRmsX( profile.rmsX() );
        newProfile.setMeanY( profile.meanY() );
        newProfile.setRmsY( profile.rmsY() );
        newProfile.setNEntries( profile.allEntries() );  
        
        if (profile instanceof ManagedObject) newProfile.setFillable( ((ManagedObject) profile).isFillable() );
        copy(newProfile.annotation(),profile.annotation());
        return newProfile;
    }

    /**
     * IHistogram operations
     *
     */
    
    /**
     * Adds two 1D Histogram
     *
     * @return a+b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram1D add(String path, IHistogram1D a, IHistogram1D b) throws IllegalArgumentException {
        IHistogram1D result = histMath.add(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Subtracts two 1D Histogram
     *
     * @return a-b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram1D subtract(String path, IHistogram1D a, IHistogram1D b) throws IllegalArgumentException {
        IHistogram1D result = histMath.sub(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Multiplies two 1D Histogram
     *
     * @return a*b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram1D multiply(String path, IHistogram1D a, IHistogram1D b) throws IllegalArgumentException {
        IHistogram1D result = histMath.mul(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Divides two 1D Histogram
     *
     * @return a/b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram1D divide(String path, IHistogram1D a, IHistogram1D b) throws IllegalArgumentException {
        IHistogram1D result = histMath.div(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Adds two 2D Histogram
     *
     * @return a+b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram2D add(String path, IHistogram2D a, IHistogram2D b) throws IllegalArgumentException {
        IHistogram2D result = histMath.add(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Subtracts two 2D Histogram
     *
     * @return a-b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram2D subtract(String path, IHistogram2D a, IHistogram2D b) throws IllegalArgumentException {
        IHistogram2D result = histMath.sub(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Multiplies two 2D Histogram
     *
     * @return a*b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram2D multiply(String path, IHistogram2D a, IHistogram2D b) throws IllegalArgumentException {
        IHistogram2D result = histMath.mul(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Divides two 2D Histogram
     *
     * @return a/b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram2D divide(String path, IHistogram2D a, IHistogram2D b) throws IllegalArgumentException {
        IHistogram2D result = histMath.div(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
        
    /**
     * Adds two 3D Histogram
     *
     * @return a+b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram3D add(String path, IHistogram3D a, IHistogram3D b) throws IllegalArgumentException {
        IHistogram3D result = histMath.add(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Subtracts two 3D Histogram
     *
     * @return a-b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram3D subtract(String path, IHistogram3D a, IHistogram3D b) throws IllegalArgumentException {
        IHistogram3D result = histMath.sub(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Multiplies two 3D Histogram
     *
     * @return a*b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram3D multiply(String path, IHistogram3D a, IHistogram3D b) throws IllegalArgumentException {
        IHistogram3D result = histMath.mul(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Divides two 3D Histogram
     *
     * @return a/b
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public IHistogram3D divide(String path, IHistogram3D a, IHistogram3D b) throws IllegalArgumentException {
        IHistogram3D result = histMath.div(nameInPath(path), a, b);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a projection from a 2D histogram parallel to the X axis.
     * Equivalent to <tt>sliceX(UNDERFLOW_BIN,OVERFLOW_BIN)</tt>.
     */
    public IHistogram1D projectionX(String path, IHistogram2D h) {
        IHistogram1D result = histMath.sliceX(h, nameInPath(path), IAxis.UNDERFLOW_BIN, IAxis.OVERFLOW_BIN);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a projection from a 2D histogram parallel to the Y axis.
     * Equivalent to <tt>sliceY(UNDERFLOW_BIN,OVERFLOW_BIN)</tt>.
     */
    public IHistogram1D projectionY(String path, IHistogram2D h) {
        IHistogram1D result = histMath.sliceY(h, nameInPath(path), IAxis.UNDERFLOW_BIN, IAxis.OVERFLOW_BIN);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Slice parallel to the Y axis from a 2D histogram at bin indexY and one bin wide.
     * Equivalent to <tt>sliceX(indexY,indexY)</tt>.
     */
    public IHistogram1D sliceX(String path, IHistogram2D h, int indexY) {
        IHistogram1D result = histMath.sliceX(h, nameInPath(path),indexY, indexY);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Slice parallel to the X axis from a 2D histogram at bin indexX and one bin wide.
     * Equivalent to <tt>sliceY(indexX,indexX)</tt>.
     */
    public IHistogram1D sliceY(String path, IHistogram2D h, int indexX) {
        IHistogram1D result = histMath.sliceY(h, nameInPath(path),indexX, indexX);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }

    /**
     * Create a slice parallel to the X axis from a 2D histogram,
     * between "indexY1" and "indexY2" (inclusive).
     * The returned IHistogram1D represents an instantaneous snapshot of the
     * histogram at the time the slice was created.
     */
    public IHistogram1D sliceX(String path, IHistogram2D h, int indexY1, int indexY2) {
        //Check the order of the indexes
        IHistogram1D result = histMath.sliceX(h, nameInPath(path),indexY1, indexY2);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a slice parallel to the Y axis from a 2D histogram,
     * between "indexX1" and "indexX2" (inclusive).
     * The returned IHistogram1D represents an instantaneous snapshot of the
     * histogram at the time the slice was created.
     */
    public IHistogram1D sliceY(String path, IHistogram2D h, int indexX1, int indexX2) {
        IHistogram1D result = histMath.sliceY(h, nameInPath(path),indexX1, indexX2);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a projection parallel to the X axis from a 3D histogram.
     * Equivalent to <tt>sliceXY(UNDERFLOW_BIN,OVERFLOW_BIN)</tt>.
     */
    public IHistogram2D projectionXY(String path, IHistogram3D h) {
        IHistogram2D result = histMath.sliceXY(h, nameInPath(path),IAxis.UNDERFLOW_BIN, IAxis.OVERFLOW_BIN);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a projection parallel to the Z axis from a 3D histogram.
     * Equivalent to <tt>sliceXZ(UNDERFLOW_BIN,OVERFLOW_BIN)</tt>.
     */
    public IHistogram2D projectionXZ(String path, IHistogram3D h) {
        IHistogram2D result = histMath.sliceXZ(h, nameInPath(path),IAxis.UNDERFLOW_BIN, IAxis.OVERFLOW_BIN);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }

    /**
     * Create a projection parallel to the Y axis from a 3D histogram.
     * Equivalent to <tt>sliceYZ(UNDERFLOW_BIN,OVERFLOW_BIN)</tt>.
     */
    public IHistogram2D projectionYZ(String path, IHistogram3D h) {
        IHistogram2D result = histMath.sliceYZ(h, nameInPath(path),IAxis.UNDERFLOW_BIN, IAxis.OVERFLOW_BIN);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a slice perpendicular to the Z axis from a 3D histogram,
     * between "indexZ1" and "indexZ2" (inclusive).
     * The returned IHistogram2D represents an instantaneous snapshot of the
     * histogram at the time the slice was created.
     * The X axis of the returned histogram corresponds to the X axis of this histogram.
     * The Y axis of the returned histogram corresponds to the Y axis of this histogram.
     */
    public IHistogram2D sliceXY(String path, IHistogram3D h, int indexZ1, int indexZ2) {
        IHistogram2D result = histMath.sliceXY(h, nameInPath(path),indexZ1, indexZ2);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
    /**
     * Create a slice perpendicular to the Y axis from a 3D histogram,
     * between "indexY1" and "indexY2" (inclusive).
     * The returned IHistogram2D represents an instantaneous snapshot of the
     * histogram at the time the slice was created.
     * The X axis of the returned histogram corresponds to the X axis of this histogram.
     * The Y axis of the returned histogram corresponds to the Z axis of this histogram.
     */
    public IHistogram2D sliceXZ(String path, IHistogram3D h, int indexY1, int indexY2) {
        IHistogram2D result = histMath.sliceXZ(h, nameInPath(path),indexY1, indexY2);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    /**
     * Create a slice perpendicular to the X axis from a 3D histogram,
     * between "indexX1" and "indexX2" (inclusive).
     * The returned IHistogram2D represents an instantaneous snapshot of the
     * histogram at the time the slice was created.
     * The X axis of the returned histogram corresponds to the Y axis of this histogram.
     * The Y axis of the returned histogram corresponds to the Z axis of this histogram.
     */
    public IHistogram2D sliceYZ(String path, IHistogram3D h, int indexX1, int indexX2) {
        IHistogram2D result = histMath.sliceYZ(h, nameInPath(path),indexX1, indexX2);
        if (tree != null && result instanceof IManagedObject) tree.addFromFactory( parentPath(path),(IManagedObject) result);
        return result;
    }
    
}
