package hep.aida.ref.histogram;

import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class HistUtils {
    
    private static double defaultLowerEdge = -1;
    private static double defaultUpperEdge = 1;
    
    /**
     * Create an IHistogram1D and fill it from an ICloud1D.
     *
     */
    public static IHistogram1D toShowableHistogram(ICloud1D cloud, int nBins, double lowerEdge, double upperEdge) {
        String name = "";
        if ( cloud instanceof IManagedObject )
            name = ( (IManagedObject) cloud ).name();
        if ( Double.isNaN(lowerEdge) )
            lowerEdge = defaultLowerEdge;
        if ( Double.isNaN(upperEdge) )
            upperEdge = defaultUpperEdge;
        
        if ( upperEdge == lowerEdge )
            upperEdge = lowerEdge + 1;
        
        IHistogram1D hist = new Histogram1D(name,cloud.title(),new FixedAxis(nBins,lowerEdge,upperEdge));
        
        for(int i=0; i<cloud.entries(); i++) hist.fill( cloud.value(i), cloud.weight(i) );
        return hist;
    }
    
    /**
     * Create an IHistogram2D and fill it from an ICloud2D.
     *
     */
    public static IHistogram2D toShowableHistogram(ICloud2D cloud, int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY) {
        String name = "";
        if ( cloud instanceof IManagedObject )
            name = ( (IManagedObject) cloud ).name();
        if ( Double.isNaN(lowerEdgeX) )
            lowerEdgeX = defaultLowerEdge;
        if ( Double.isNaN(upperEdgeX) )
            upperEdgeX = defaultUpperEdge;
        if ( Double.isNaN(lowerEdgeY) )
            lowerEdgeY = defaultLowerEdge;
        if ( Double.isNaN(upperEdgeY) )
            upperEdgeY = defaultUpperEdge;

        if ( upperEdgeX == lowerEdgeX )
            upperEdgeX = lowerEdgeX + 1;
        if ( upperEdgeY == lowerEdgeY )
            upperEdgeY = lowerEdgeY + 1;

        IHistogram2D hist = new Histogram2D(name,cloud.title(),new FixedAxis(nBinsX,lowerEdgeX,upperEdgeX),new FixedAxis(nBinsY,lowerEdgeY,upperEdgeY));
        for(int i=0; i<cloud.entries(); i++) hist.fill( cloud.valueX(i), cloud.valueY(i), cloud.weight(i) );
        return hist;
    }
    
    /**
     * Create an IHistogram3D and fill it from an ICloud3D.
     *
     */
    public static IHistogram3D toShowableHistogram(ICloud3D cloud, int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY,
    int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
        String name = "";
        if ( cloud instanceof IManagedObject )
            name = ( (IManagedObject) cloud ).name();
        if ( Double.isNaN(lowerEdgeX) )
            lowerEdgeX = defaultLowerEdge;
        if ( Double.isNaN(upperEdgeX) )
            upperEdgeX = defaultUpperEdge;
        if ( Double.isNaN(lowerEdgeY) )
            lowerEdgeY = defaultLowerEdge;
        if ( Double.isNaN(upperEdgeY) )
            upperEdgeY = defaultUpperEdge;
        if ( Double.isNaN(lowerEdgeZ) )
            lowerEdgeZ = defaultLowerEdge;
        if ( Double.isNaN(upperEdgeZ) )
            upperEdgeZ = defaultUpperEdge;

        if ( upperEdgeX == lowerEdgeX )
            upperEdgeX = lowerEdgeX + 1;
        if ( upperEdgeY == lowerEdgeY )
            upperEdgeY = lowerEdgeY + 1;
        if ( upperEdgeZ == lowerEdgeZ )
            upperEdgeZ = lowerEdgeZ + 1;

        IHistogram3D hist = new Histogram3D(name, cloud.title(),
        new FixedAxis(nBinsX,lowerEdgeX,upperEdgeX),
        new FixedAxis(nBinsY,lowerEdgeY,upperEdgeY),
        new FixedAxis(nBinsZ,lowerEdgeZ,upperEdgeZ));
        for(int i=0; i<cloud.entries(); i++) hist.fill( cloud.valueX(i), cloud.valueY(i), cloud.valueZ(i), cloud.weight(i) );
        return hist;
    }
    
    public static double histogramNormalization(IHistogram1D h1) {
        return h1.sumBinHeights()*(h1.axis().upperEdge()-h1.axis().lowerEdge())/h1.axis().bins();        
    }
    
    public static boolean isValidDouble(double d) {
        if ( Double.isNaN(d) )
            return false;
        if ( Double.isInfinite(d) )
            return false;
        return true;
    }
    
}

