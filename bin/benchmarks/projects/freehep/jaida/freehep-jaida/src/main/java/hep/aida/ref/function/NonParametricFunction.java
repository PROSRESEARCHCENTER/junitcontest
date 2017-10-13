package hep.aida.ref.function;

import hep.aida.IFitData;
import hep.aida.dev.IDevFitData;
import hep.aida.dev.IDevFitDataIterator;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class NonParametricFunction extends AbstractIFunction {
    
    private boolean mirrorLeft = false;
    private boolean mirrorRight = false;
    private double rho = 1;
    
    private int nPoints = 1000;
    private double[] lookupTable;
    
    private int nEvents;
    private double binWidth;
    
    private double[] dataPts;
    private double[] weights;
    
    double upperLimit = Double.NaN;
    double lowerLimit = Double.NaN;
    
    private static final double sqrt2pi = Math.sqrt(2*Math.PI);
    
    public NonParametricFunction(String title, IFitData data) {
        super(title, data.dimension(), 1);
        if ( data.dimension() != 1 )
            throw new IllegalArgumentException("Only one dimensional non-parametric functions are supported!");
        IDevFitDataIterator dataIter = ((IDevFitData) data).dataIterator();
        
        lookupTable = new double[ nPoints+1 ];
        
        nEvents = dataIter.entries();
        if (mirrorLeft) nEvents += dataIter.entries();
        if (mirrorRight) nEvents += dataIter.entries();
        
        //Calculate upper and lower limits
        dataIter.start();
        while( dataIter.next() ) {
            double x = dataIter.vars()[0];
            if ( Double.isNaN(upperLimit) || x > upperLimit )
                upperLimit = x;
            if ( Double.isNaN(lowerLimit) || x < lowerLimit )
                lowerLimit = x;
        }
        
        binWidth = (upperLimit - lowerLimit)/(nPoints-1);
        
        dataPts = new double[nEvents];
        weights = new double[nEvents];
        
        double entries = 0;
        double m = 0;
        double r = 0;
        
        int dataCount = 0;
        
        dataIter.start();
        while ( dataIter.next() ) {
            double x = dataIter.vars() [0];
            
            dataPts[dataCount] = x;
            
            entries++;
            m += x;
            r += x*x;
            
            dataCount++;
            
            if (mirrorLeft) {
                dataPts[dataCount]= 2*lowerLimit - x;
                dataCount++;
            }
            
            if (mirrorRight) {
                dataPts[dataCount]= 2*upperLimit - x;
                dataCount++;
            }
        }
        
        
        double mean = m/entries;
        double sigma = Math.sqrt(r/entries-mean*mean);
        double h = Math.pow(4./3.,0.2)*Math.pow(nEvents,-0.2)*rho;
        double hmin = h*sigma*Math.sqrt(2.)/10.;
        double norm = h*Math.sqrt(sigma)/(2.0*Math.sqrt(3.0));
        
        for(int j=0; j<nEvents; ++j) {
            weights[j]=norm/Math.sqrt( gauss(dataPts[j],h*sigma) );
            if (weights[j]<hmin) weights[j]=hmin;
        }
        
        for (int i=0; i < nPoints+1; ++i) {
            lookupTable[i]=evaluateFull( lowerLimit + i*binWidth );
        }
        
    }
    
    public String normalizationParameter() {
        return null;
    }
    
    
    private double gauss(double x, double sigma) {
        
        double c = 1./(2*sigma*sigma);
        double y = 0;
        
        for (int i = 0; i<nEvents; ++i) {
            double r = x - dataPts[i];
            y += Math.exp(-c*r*r);
        }
        
        return y/(sigma*sqrt2pi*nEvents);
    }
    
    public double value(double[] v) {
        double x = v[0];
        
        int i = (int)Math.floor( (x - lowerLimit)/binWidth );
        if (i<0)
//            throw new IllegalArgumentException("Cannot have point below lower limit.");
            i=0;
        if (i>nPoints-1)
//            throw new IllegalArgumentException("Cannot have point above upper limit.");
            i=nPoints-1;
        
        double dx = (x -(lowerLimit + i*binWidth) )/binWidth;
        
        double val = (lookupTable[i] + dx*(lookupTable[i+1]-lookupTable[i]) );
        return val;
    }
    
    
    private double evaluateFull( double x ) {
        
        double y=0;
        
        for (int i=0;i<nEvents;++i) {
            double chi = (x-dataPts[i])/weights[i];
            y += Math.exp(-0.5*chi*chi)/weights[i];
            
            /*
            // if mirroring the distribution across either edge of
            // the range ("Boundary Kernels"), pick up the additional
            // contributions
            //      if (_mirrorLeft) {
            //        chi=(x-(2*_lo-_dataPts[i]))/_weights[i];
            //        y+=exp(-0.5*chi*chi)/_weights[i];
            //      }
            if (_asymLeft) {
                chi=(x-(2*_lo-_dataPts[i]))/_weights[i];
                y-=exp(-0.5*chi*chi)/_weights[i];
            }
            //      if (_mirrorRight) {
            //        chi=(x-(2*_hi-_dataPts[i]))/_weights[i];
            //        y+=exp(-0.5*chi*chi)/_weights[i];
            //      }
            if (_asymRight) {
                chi=(x-(2*_hi-_dataPts[i]))/_weights[i];
                y-=exp(-0.5*chi*chi)/_weights[i];
            }
             */
        }
        return y/(sqrt2pi*nEvents);
    }
    
}
