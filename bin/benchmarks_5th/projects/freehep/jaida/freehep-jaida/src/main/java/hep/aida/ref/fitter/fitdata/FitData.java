/*
 * FitData.java
 *
 * Created on August 16, 2002, 10:44 AM
 */

package hep.aida.ref.fitter.fitdata;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IEvaluator;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.IRangeSet;
import hep.aida.ITuple;
import hep.aida.dev.IDevFitData;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.dev.IDevFitter;
import hep.aida.ref.function.RangeSet;
import hep.aida.ref.tuple.Tuple;
import hep.aida.ref.tuple.TupleFactory;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class FitData implements IDevFitData {
    
    private ITuple tup;
    private int dimension;
    private int fitType;
    private IRangeSet[] rangeSet;
    private String dataDescription = "";
    
    /** Creates a new instance of FitData */
    public FitData() {
        reset();
    }
    
    
    public void create1DConnection(Object data) {
        if ( data instanceof IHistogram1D )
            create1DConnection((IHistogram1D)data);
        else if ( data instanceof ICloud1D )
            create1DConnection((ICloud1D)data);
        else if ( data instanceof IProfile1D )
            create1DConnection((IProfile1D)data);
        else if ( data instanceof IDataPointSet )
            create1DConnection((IDataPointSet)data);
        else
            throw new IllegalArgumentException("Cannot create 1D connection with object of type "+data.getClass());
    }
    
    /**
     * 1D connections.
     */
    public void create1DConnection(IHistogram1D hist) throws IllegalArgumentException {
        dataDescription = "IHistogram1D "+((IManagedObject)hist).name();
        prepareConnections(1,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[1];
        int bins = hist.axis().bins();
        for ( int i = 0; i < bins; i++ ) {
            double e = hist.binError(i);
            double v = hist.binHeight(i);
            vals[0] = hist.binMean(i);
            if ( hist.binEntries(i) != 0 )
                fillConnections(v,e,e,vals);
        }
        rangeSet[0].includeAll();        
//        rangeSet[0].excludeAll();        
//        rangeSet[0].include( hist.axis().lowerEdge(), hist.axis().upperEdge() );        
    }
    public void create1DConnection(IProfile1D profile) throws IllegalArgumentException {
        dataDescription = "IProfile1D "+((IManagedObject)profile).name();
        prepareConnections(1,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[1];
        int bins = profile.axis().bins();
        for ( int i = 0; i < bins; i++ ) {
            double e = profile.binError(i);
            double v = profile.binHeight(i);
            vals[0] = profile.binMean(i);
            if ( profile.binEntries(i) != 0 )
                fillConnections(v,e,e,vals);
        }
        rangeSet[0].includeAll();        
//        rangeSet[0].excludeAll();        
//        rangeSet[0].include( profile.axis().lowerEdge(), profile.axis().upperEdge() );        
    }
    public void create1DConnection(ICloud1D cloud) throws IllegalArgumentException {
        dataDescription = "ICloud1D "+((IManagedObject)cloud).name();
        if ( cloud.isConverted() ) throw new IllegalArgumentException("This ICloud is converted. You should explicitely fit the IHistogram inside the ICloud, if that is what you want to do");
        else {
            prepareConnections(1,IDevFitter.UNBINNED_FIT);
            
            double[] vals = new double[1];
            int entries = cloud.entries();
            for ( int i = 0; i < entries; i++ ) {
                vals[0] = cloud.value(i);
                fillConnections(0.,0.,0.,vals);
            }
        }
        rangeSet[0].includeAll();        
//        rangeSet[0].excludeAll();        
//        rangeSet[0].include(cloud.lowerEdge(), cloud.upperEdge() );        
    }
    public void create1DConnection(IDataPointSet dataPointSet, int xIndex, int valIndex) throws IllegalArgumentException {
        int[] indeces = {xIndex};
        createConnection(dataPointSet, indeces, valIndex);
    }
    
    
    public void create1DConnection(double[] x, double[] y, double[] corrMatrix) throws IllegalArgumentException {
        throw new UnsupportedOperationException("");    
    }
    
    /**
     * 2D connections.
     */
    public void create2DConnection(IHistogram2D hist) throws IllegalArgumentException {
        create2DConnection(hist,0,1);
    }
    public void create2DConnection(IHistogram2D hist, int xIndex, int yIndex) throws IllegalArgumentException {
        if ( xIndex*yIndex != 0 || xIndex+yIndex != 1 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex);
        
        dataDescription = "IHistogram2D "+((IManagedObject)hist).name();
        prepareConnections(2,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[2];
        int xBins = hist.xAxis().bins();
        int yBins = hist.yAxis().bins();
        for ( int i = 0; i < xBins; i++ ) {
            for ( int j = 0; j < yBins; j++ ) {
                double e = hist.binError(i,j);
                double v = hist.binHeight(i,j);
                vals[xIndex] = hist.binMeanX(i,j);
                vals[yIndex] = hist.binMeanY(i,j);
                if ( hist.binEntries(i,j) != 0 )
                    fillConnections(v,e,e,vals);
            }
        }
	rangeSet[xIndex].includeAll();
	rangeSet[yIndex].includeAll();
////	rangeSet[xIndex].excludeAll();
//        rangeSet[xIndex].include( hist.xAxis().lowerEdge(), hist.xAxis().upperEdge() );        
//	rangeSet[yIndex].excludeAll();
//        rangeSet[yIndex].include( hist.yAxis().lowerEdge(), hist.yAxis().upperEdge() );        
    }
    public void create2DConnection(IProfile2D profile) throws IllegalArgumentException {
        create2DConnection(profile,0,1);
    }
    public void create2DConnection(IProfile2D profile, int xIndex, int yIndex) throws IllegalArgumentException {
        if ( xIndex*yIndex != 0 || xIndex+yIndex != 1 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex);
        
        dataDescription = "IProfile2D "+((IManagedObject)profile).name();
        prepareConnections(2,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[2];
        int xBins = profile.xAxis().bins();
        int yBins = profile.yAxis().bins();
        for ( int i = 0; i < xBins; i++ ) {
            for ( int j = 0; j < yBins; j++ ) {
                double e = profile.binError(i,j);
                double v = profile.binHeight(i,j);
                vals[xIndex] = profile.binMeanX(i,j);
                vals[yIndex] = profile.binMeanY(i,j);
                if ( profile.binEntries(i,j) != 0 )
                    fillConnections(v,e,e,vals);
            }
        }
	rangeSet[xIndex].includeAll();
	rangeSet[yIndex].includeAll();
//	rangeSet[xIndex].excludeAll();
//        rangeSet[xIndex].include( profile.xAxis().lowerEdge(), profile.xAxis().upperEdge() );        
//	rangeSet[yIndex].excludeAll();
//        rangeSet[yIndex].include( profile.yAxis().lowerEdge(), profile.yAxis().upperEdge() );        
    }
    public void create2DConnection(ICloud2D cloud) throws IllegalArgumentException {
        create2DConnection(cloud,0,1);
    }
    public void create2DConnection(ICloud2D cloud, int xIndex, int yIndex) throws IllegalArgumentException {
        if ( cloud.isConverted() ) throw new IllegalArgumentException("This ICloud is converted. You should explicitely fit the IHistogram inside the ICloud, if that is what you want to do");
        else {
            if ( xIndex*yIndex != 0 || xIndex+yIndex != 1 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex);

            dataDescription = "ICloud2D "+((IManagedObject)cloud).name();
            prepareConnections(2,IDevFitter.UNBINNED_FIT);
            
            double[] vals = new double[2];
            int entries = cloud.entries();
            for ( int i = 0; i < entries; i++ ) {
                vals[xIndex] = cloud.valueX(i);
                vals[yIndex] = cloud.valueY(i);
                fillConnections(0.,0.,0.,vals);
            }
        }
	rangeSet[xIndex].includeAll();
	rangeSet[yIndex].includeAll();
//	rangeSet[xIndex].excludeAll();
//        rangeSet[xIndex].include( cloud.lowerEdgeX(), cloud.upperEdgeX() );        
//	rangeSet[yIndex].excludeAll();
//        rangeSet[yIndex].include( cloud.lowerEdgeY(), cloud.upperEdgeY() );        
    }
    public void create2DConnection(IDataPointSet dataPointSet, int xIndex, int yIndex, int valIndex) throws IllegalArgumentException {
        int[] indeces = {xIndex,yIndex};
        createConnection(dataPointSet, indeces, valIndex);
    }
    
    
    /**
     * 3D connections.
     */
    public void create3DConnection(IHistogram3D hist) throws IllegalArgumentException {
        create3DConnection(hist,0,1,2);
    }
    public void create3DConnection(IHistogram3D hist, int xIndex, int yIndex, int zIndex) throws IllegalArgumentException {
        if ( xIndex*yIndex*zIndex != 0 || xIndex+yIndex+zIndex != 3 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex+" "+zIndex);
        if ( xIndex > 2 || yIndex > 2 || zIndex>2 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex+" "+zIndex);
        
        dataDescription = "IHistogram3D "+((IManagedObject)hist).name();
        prepareConnections(3,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[3];
        int xBins = hist.xAxis().bins();
        int yBins = hist.yAxis().bins();
        int zBins = hist.zAxis().bins();
        for ( int i = 0; i < xBins; i++ ) {
            for ( int j = 0; j < yBins; j++ ) {
                for ( int k = 0; k < zBins; k++ ) {
                    double e = hist.binError(i,j,k);
                    double v = hist.binHeight(i,j,k);
                    vals[xIndex] = hist.binMeanX(i,j,k);
                    vals[yIndex] = hist.binMeanY(i,j,k);
                    vals[zIndex] = hist.binMeanZ(i,j,k);
                    if ( hist.binEntries(i,j,k) != 0 )
                        fillConnections(v,e,e,vals);
                }
            }
        }
	rangeSet[xIndex].includeAll();
	rangeSet[yIndex].includeAll();
	rangeSet[zIndex].includeAll();
//	rangeSet[xIndex].excludeAll();
//        rangeSet[xIndex].include( hist.xAxis().lowerEdge(), hist.xAxis().upperEdge() );        
//	rangeSet[yIndex].excludeAll();
//        rangeSet[yIndex].include( hist.yAxis().lowerEdge(), hist.yAxis().upperEdge() );        
//	rangeSet[zIndex].excludeAll();
//        rangeSet[zIndex].include( hist.zAxis().lowerEdge(), hist.zAxis().upperEdge() );        
    }
    public void create3DConnection(ICloud3D cloud) throws IllegalArgumentException {
        create3DConnection(cloud,0,1,2);
    }
    public void create3DConnection(ICloud3D cloud, int xIndex, int yIndex, int zIndex) throws IllegalArgumentException {
        if ( cloud.isConverted() ) throw new IllegalArgumentException("This ICloud is converted. You should explicitely fit the IHistogram inside the ICloud, if that is what you want to do");
        else {
            if ( xIndex*yIndex*zIndex != 0 || xIndex+yIndex+zIndex != 3 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex+" "+zIndex);
            if ( xIndex > 2 || yIndex > 2 || zIndex>2 ) throw new IllegalArgumentException("Illegal values for the axis "+xIndex+" "+yIndex+" "+zIndex);
            dataDescription = "ICloud3D "+((IManagedObject)cloud).name();
            prepareConnections(3,IDevFitter.UNBINNED_FIT);
            
            double[] vals = new double[3];
            int entries = cloud.entries();
            for ( int i = 0; i < entries; i++ ) {
                vals[xIndex] = cloud.valueX(i);
                vals[yIndex] = cloud.valueY(i);
                vals[zIndex] = cloud.valueZ(i);
                fillConnections(0.,0.,0.,vals);
            }
        }
	rangeSet[xIndex].includeAll();
	rangeSet[yIndex].includeAll();
	rangeSet[zIndex].includeAll();
        /*
	rangeSet[xIndex].excludeAll();
        rangeSet[xIndex].include( cloud.lowerEdgeX(), cloud.upperEdgeX() );        
	rangeSet[yIndex].excludeAll();
        rangeSet[yIndex].include( cloud.lowerEdgeY(), cloud.upperEdgeY() );        
	rangeSet[zIndex].excludeAll();
        rangeSet[zIndex].include( cloud.lowerEdgeZ(), cloud.upperEdgeZ() );        
         */
    }
    
    public void create3DConnection(IDataPointSet dataPointSet, int xIndex, int yIndex, int zIndex, int valIndex) throws IllegalArgumentException {
        int[] indeces = {xIndex,yIndex,zIndex};
        createConnection(dataPointSet, indeces, valIndex);
    }
    
    /**
     * Generic connections.
     */
    public void createConnection(IDataPointSet dataPointSet, int[] indeces, int valIndex) throws IllegalArgumentException {
        int dimension = indeces.length;
        for ( int i = 0; i<dimension; i++ ) {
            if ( indeces[i] > dataPointSet.dimension()-1 ) throw new IllegalArgumentException("Variable index "+indeces[i]+" cannot be greater than the dataPointSet dimension "+dataPointSet.dimension());
            if ( indeces[i] == valIndex ) throw new IllegalArgumentException("Variable index cannot be the same as the value index "+valIndex);
            for ( int j = i+1; j<dimension; j++ )
                if ( indeces[i] == indeces[j] ) throw new IllegalArgumentException("Two indeces are identical! Impossible configuration");
        }
        dataDescription = "IDataPointSet "+((IManagedObject)dataPointSet).name();
        prepareConnections(dimension,IDevFitter.BINNED_FIT);
        
        double[] vals = new double[dimension];
        for ( int i = 0; i < dataPointSet.size(); i++ ) {
            double v = dataPointSet.point(i).coordinate(valIndex).value();
            double ep = dataPointSet.point(i).coordinate(valIndex).errorPlus();
            double em = dataPointSet.point(i).coordinate(valIndex).errorMinus();
                for ( int j = 0; j < dimension; j++ )
                    vals[j] = dataPointSet.point(i).coordinate(indeces[j]).value();
                if ( ep != 0 && em != 0 )
                    fillConnections(v,ep,em,vals);
        }
        for ( int i = 0; i < dimension; i++ ) {
	    rangeSet[i].includeAll();        
////	    rangeSet[i].excludeAll();        
//	    rangeSet[i].include(dataPointSet.lowerExtent(indeces[i]), dataPointSet.upperExtent(indeces[i]));
	}
    }
    public void createConnection(ITuple tuple, IEvaluator[] evals) {
        int dimension = evals.length;
        dataDescription = "ITuple "+((IManagedObject)tuple).name();
        prepareConnections(dimension,IDevFitter.UNBINNED_FIT);
        
        for ( int i = 0; i<dimension; i++ )
            evals[i].initialize(tuple);

        double[] upperEdges = new double[dimension];
        double[] lowerEdges = new double[dimension];
        for ( int i = 0; i<dimension; i++ ) {
            lowerEdges[i] = Double.NaN;
            upperEdges[i] = Double.NaN;
        }
        
        double[] vals = new double[dimension];
        
        tuple.start();
        while ( tuple.next() ) {
            for ( int i = 0; i<dimension; i++ ) {
                vals[i] = evals[i].evaluateDouble();
                if ( Double.isNaN(upperEdges[i]) || vals[i] > upperEdges[i] ) upperEdges[i] = vals[i];
                if ( Double.isNaN(lowerEdges[i]) || vals[i] < lowerEdges[i] ) lowerEdges[i] = vals[i];
            }
            fillConnections(0,0,0,vals);
        }
        for ( int i = 0; i<dimension; i++ ) {
	    rangeSet[i].includeAll();    
//	    rangeSet[i].excludeAll();    
//	    rangeSet[i].include(lowerEdges[i], upperEdges[i] );
	}
    }
    public void createConnection(ITuple tuple, String[] colNames) {
        int dimension = colNames.length;
        dataDescription = "ITuple "+((IManagedObject)tuple).name();
        prepareConnections(dimension,IDevFitter.UNBINNED_FIT);

        int[] indeces = new int[dimension];
        for ( int i = 0; i<dimension; i++ )
            indeces[i] = tuple.findColumn(colNames[i]);
        
        double[] vals = new double[dimension];
        tuple.start();
        while ( tuple.next() ) {
            for ( int i = 0; i<dimension; i++ )
                vals[i] = tuple.getDouble(indeces[i]);
            fillConnections(0,0,0,vals);
        }
        for ( int i = 0; i<dimension; i++ ) {
	    rangeSet[i].includeAll();        
//	    rangeSet[i].excludeAll();        
//	    rangeSet[i].include(tuple.columnMin(indeces[i]), tuple.columnMax(indeces[i]) );
	}
    }
    public IRangeSet range(int index) throws IllegalArgumentException {
        if ( rangeSet == null ) throw new RuntimeException("RangeSet have not been initialized!!");
        if ( index < 0 ) throw new IllegalArgumentException("The index cannot be negative!!");
        if ( index > (rangeSet.length-1) ) throw new IllegalArgumentException("Wrong index "+index+". It exceeds the number of RangeSets "+rangeSet.length+".");
        return rangeSet[index];
    }
    public IDevFitDataIterator dataIterator() {
        TupleFactory tupFactory = new TupleFactory(null);
        return new FitDataIterator(tupFactory.createFiltered("",tup,new RangeSetFilter(rangeSet)));
    }
    public int dimension() {
        return dimension;
    }
    public String dataDescription() {
        return dataDescription;
    }
    public void reset() {
        tup = null;
        dimension = -1;
        fitType = -1;
        rangeSet = null;
    }
    public int fitType() {
        return fitType;
    }
    private void prepareConnections( int dimension, int fitType ) {
        reset();
        this.dimension = dimension;
        this.fitType = fitType;
        String tupString = "double value, double error, double minusError";
        for ( int i = 0; i<dimension; i++ )
            tupString += ", double x"+i;
        tup = new Tuple("","", tupString,"");
        rangeSet = new RangeSet[dimension];
        for ( int i = 0; i<dimension; i++ ) 
            rangeSet[i] = new RangeSet();
    }
    private void fillConnections( double value, double error, double minusError, double[] vals ) {
        tup.fill(0,value);
        tup.fill(1,error);
        tup.fill(2,minusError);
        for ( int i = 0; i<vals.length; i++ )
            tup.fill(3+i,vals[i]);
        tup.addRow();
    }
}
