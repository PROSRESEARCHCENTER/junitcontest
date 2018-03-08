package hep.aida.ref.pdf;

import hep.aida.ext.IFitMethod;
import hep.aida.ref.fitter.fitdata.FitData;
import hep.aida.ref.fitter.fitdata.FitDataIterator;

/**
 * A Pdf builtg from a given data set.
 * The Pdf is evaluated using the idea of adaptive kernel estimation presented
 * at http://www-wisconsin.cern.ch/~cranmer/keys.html
 *
 * Mirroring is to be performed when distributions don't naturally taper to
 * zero on one or both sides. With abrupt interruptions of the distribution there
 * is a leak of probability due to the way the kernel is built (gaussian). By
 * mirroring the distribution on the side of the abrupt edge the loss of probability
 * is minimized.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class NonParametricPdf extends Function {
    
    public static final int NO_MIRROR = 0;
    public static final int MIRROR_LEFT = 1;
    public static final int MIRROR_RIGHT = 2;
    public static final int MIRROR_BOTH = 3;
    
    private static final int nPoints = 1000;
    private static final double sqrt2pi = Math.sqrt(2*Math.PI);
    
    private Dependent x;
    private double xVal;
    
    private double rho = 1;
    
    private double[] lookupTable;
    private double[] dataPoints;
    private double[] weights;
    private int nEntries;
    private int nData;
    private double lowerBound;
    private double upperBound;
    private double binWidth;
    
    private boolean mirrorLeft;
    private boolean mirrorRight;
        
    private int type;
        
    public NonParametricPdf(String name, FitData data, Dependent x) {
        this(name, data, x, MIRROR_BOTH);
    }
    
    public NonParametricPdf(String name, FitData data, Dependent x, int mirrorCode) {
        super(name);
        this.x = x;
        
        switch(mirrorCode) {
            case NO_MIRROR:
                mirrorLeft = false;
                mirrorRight = false;
                break;
            case MIRROR_LEFT:
                mirrorLeft = true;
                mirrorRight = false;
                break;
            case MIRROR_RIGHT:
                mirrorLeft = false;
                mirrorRight = true;
                break;
            case MIRROR_BOTH:
                mirrorLeft = true;
                mirrorRight = true;
                break;
            default:
                throw new IllegalArgumentException("Unsupported mirror code "+mirrorCode);
                
        }
        
        lowerBound = x.range().lowerBounds()[0];
        upperBound = x.range().upperBounds()[0];
        binWidth = (upperBound - lowerBound)/(nPoints-1);
        
        VariableList list = new VariableList();
        list.add(x);
        addVariables(list);
        
        if ( data.dimension() != 1 )
            throw new IllegalArgumentException("Cannot create a multi-dimensional NonParametricPdf");
        
        type = data.fitType();
        
        if ( data.fitType() == IFitMethod.BINNED_FIT ) {
            throw new IllegalArgumentException("Cannot create NonParametricPdf for binned data");
        } else {
            initializeUnbinnedDataSet(data);
        }
    }
    
    public void variableChanged(Variable var) {
        if ( var == x )
            xVal = x.value();
    }
    
    public double functionValue() {
        if ( type == IFitMethod.UNBINNED_FIT ) {
            int i = (int)Math.floor((xVal-lowerBound)/binWidth);
            
            if ( i < 0 ) {
                System.out.println("Got point below lower bound "+xVal+". Peforming linear extrapolation.");
                i=0;
            }
            if ( i > nPoints-1 ) {
                System.out.println("Got point above upper bound "+xVal+". Peforming linear extrapolation.");
                i= nPoints - 1;
            }
            
            double dx = (xVal-(lowerBound+i*binWidth))/binWidth;
            double r = (lookupTable[i]+dx*(lookupTable[i+1]-lookupTable[i]));
            //        System.out.println("Value for "+xVal+" "+i+" "+dx+" "+r);
            return r;
        } else {
            throw new UnsupportedOperationException("Cannot evaluate NonParametricPdf for binned data");
        }
    }
    
    private void initializeUnbinnedDataSet(FitData d) {
        lookupTable = new double[nPoints+1];
        
        FitDataIterator iter = (FitDataIterator)d.dataIterator();
        nEntries = iter.entries();
        nData = nEntries;
        
        if (mirrorLeft) nEntries += iter.entries();
        if (mirrorRight) nEntries += iter.entries();
        
        dataPoints = new double[nEntries];
        weights = new double[nEntries];
        
        double sumX = 0;
        double sumXSquared = 0;
        int count = 0;
        double tmpx;
        while ( iter.next() ) {
            tmpx = iter.vars()[0];
            dataPoints[count] = tmpx;
            sumX += tmpx;
            sumXSquared += tmpx*tmpx;
            count++;

            if (mirrorLeft) {
                dataPoints[count]= 2*lowerBound - tmpx;
                count++;
            }

            if (mirrorRight) {
                dataPoints[count]= 2*upperBound - tmpx;
                count++;
            }        
        }
        
        double mean = sumX/nData;
        double sigma = Math.sqrt(sumXSquared/nData-mean*mean);
        double h = Math.pow(4./3.,0.2)*Math.pow((double)nData,-0.2)*rho;
        double hmin = h*sigma*Math.sqrt(2.)/10.;
        double norm=h*Math.sqrt(sigma)/(2.0*Math.sqrt(3.0));
        
        weights=new double[nEntries];
        for(int j = 0; j < nEntries; j++) {
            weights[j]=norm/Math.sqrt(weightFactor(dataPoints[j],h*sigma));
            if (weights[j]<hmin) weights[j]=hmin;
        }
        
        for (int i = 0; i < nPoints+1; i++)
            lookupTable[i] = evaluateFull( lowerBound + (double)i*binWidth );
    }
    
    private double weightFactor(double x, double sigma) {
        double c = 1./(2*sigma*sigma);
        double y=0;
        for (int i = 0; i < nEntries; i++) {
            double arg = x - dataPoints[i];
            y += Math.exp(-c*arg*arg);
        }
        
        return y/(sigma*sqrt2pi*nData);
    }
    
    private double evaluateFull( double x ) {
        double y=0;
        for( int i = 0; i < nEntries; i++ ) {
            double chi = (x - dataPoints[i])/weights[i];
            y += Math.exp(-0.5*chi*chi)/weights[i];
        }
        return y/(sqrt2pi*nData);
    }
      
    public boolean hasAnalyticalVariableGradient(Variable var) {
        return false;
    }
    
    /**
     * FIXME
     * Should the normalization be left to 1? or should we evaluate it numerically?
     * The problem is the leaking of probability at the edge.
     *
     */    
    public boolean hasAnalyticalNormalization(Dependent dep) {
        if ( dep == x )
            return true;
        return false;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        return 1;
    }        
}
