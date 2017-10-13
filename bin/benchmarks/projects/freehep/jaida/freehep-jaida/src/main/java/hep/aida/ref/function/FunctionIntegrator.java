/*
 * FunctionIntegrator.java
 *
 * Created on October 15, 2002, 9:47 AM
 */

package hep.aida.ref.function;

import hep.aida.IModelFunction;
/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class FunctionIntegrator {
    
    private static final int AllStages  = 0;
    private static final int ReuseGrid  = 1;
    private static final int RefineGrid = 2;
    
    private static final int Importance = 0;
    private static final int ImportanceOnly = 1;
    private static final int Stratified = 2;
    
    private static double wtdIntSum;
    private static double sumWgts;
    private static double chiSum;
    private static int itNum;
    private static int itStart;
    private static double samples;
    private static int callsPerBox;
    private static double jac;
    private static double alpha = 1.5;
    
    /** Creates a new instance of FunctionIntegrator */
    public FunctionIntegrator() {
    }
    
    public static double integralVegasMC( IModelFunction f ) {
        Grid grid = new Grid(f);
        if ( ! grid.isValid() ) throw new RuntimeException("Problem initializing the grid for function "+f);
        
        int nRefineIter = 5;
        int nRefinePerDim = 1000;
        int nIntegratePerDim = 5000;
        
        vegas(f, grid, AllStages, nRefinePerDim*grid.dimension(), nRefineIter, Importance);
        
        return vegas(f, grid, AllStages, nIntegratePerDim*grid.dimension(), 1, Importance);
    }
    
    private static double vegas( IModelFunction f, Grid grid, int stage, int calls, int iterations, int mode) {
     
        if ( stage == AllStages ) grid.initialize(f);
        
        if ( stage <= ReuseGrid ) {
            wtdIntSum = 0;
            sumWgts = 0;
            chiSum = 0;
            itNum = 1;
            samples = 0;
        }
        

        int dim = grid.dimension();

        if ( stage <= RefineGrid ) {
            int bins = grid.maxBins();
            int boxes = 1;
            
            if ( mode != ImportanceOnly ) {
                boxes = (int) Math.floor( Math.pow( calls/2., 1./dim ) );
                mode = Importance;
                
                if ( 2*boxes >= grid.maxBins() ) {
                    mode = Stratified;
                    int boxPerBin = ( boxes > grid.maxBins() ) ? boxes/grid.maxBins() : 1;
                    bins = boxes/boxPerBin;
                    
                    if ( bins > grid.maxBins() ) bins = grid.maxBins();
                    boxes = boxPerBin*bins;
                }
            }
            
            double totBoxes = Math.pow( (double)boxes, (double)dim );
            callsPerBox = (int)( calls/totBoxes );
            if ( callsPerBox < 2 ) callsPerBox = 2;
            calls = (int) ( callsPerBox*totBoxes );
            
            jac = grid.volume()*Math.pow((double)bins, (double)dim)/calls;
                        
            grid.setBoxes(boxes);
            if( bins != grid.nBins() ) grid.resize(bins);
        }
        
        int[][] box;
        int[][] bin;
        double[] x = new double[dim];
        
        double cumInt = 0; 
        double cumSig = 0;
        
        itStart = itNum;
        for ( int it = 0; it < iterations; it++ ) {
            double intgrl = 0;
            double intgrlSq = 0;
            double sig = 0;
            double jacbin = jac;
            
            itNum = itStart + it;
            
            grid.resetValues();
            
            box = grid.firstBox();
            bin = new int[dim][box[0].length];
            do {
                double m = 0;
                double q = 0;
                
                for ( int k = 0; k < callsPerBox; k++ ) {
                    double[] binVol = new double[1];
                    grid.generatePoint(box, x,  bin, binVol);
                    
                    double fVal = jacbin*binVol[0]*f.value(x);
                                        
                    double d = fVal - m;
                    m += d / ( k + 1. );
                    q += d * d * ( k / ( k + 1. ) );
                    
                    if ( mode != Stratified ) grid.accumulate(bin, fVal*fVal);
                }
                
                intgrl += m * callsPerBox;
                double fSqSum = q*callsPerBox;
                sig += fSqSum;
                
                if ( mode == Stratified ) grid.accumulate(bin, fSqSum);
            } while( grid.nextBox( box ) );

            
            double wgt;
            sig = sig / ( callsPerBox - 1.);
            if ( sig > 0 ) wgt = 1./sig;
            else if ( sumWgts > 0 ) wgt = sumWgts/samples;
            else wgt = 0;
            
            intgrlSq = intgrl*intgrl;
            
            if ( wgt > 0 ) {
                samples++;
                sumWgts += wgt;
                wtdIntSum += intgrl*wgt;
                chiSum += intgrlSq*wgt;
                
                cumInt = wtdIntSum/sumWgts;
                cumSig = Math.sqrt( 1./sumWgts);
                
                if ( samples > 1 ) {
                }
            } else {
                cumInt += (intgrl-cumInt)/(it+1.);
                cumSig = 0;
            }
            grid.refine(alpha);        
        }
        // error = cumSig;
        
        return cumInt;
    }
    
    
    public static double integralMC( IModelFunction f ) {
        int dim = f.dimension();
        RangeSet[] ranges = new RangeSet[dim];
        double vol = 1.;
        
        for ( int i = 0; i < dim; i++ ) {
            ranges[i] = (RangeSet)f.normalizationRange(i);
            vol *= ranges[i].length();
        }
        
        double integral = 0;
        double[] x = new double[dim];
    
        int nIter = 1000000;
        for ( int i = 0; i<nIter; i++ ) {
            for ( int j = 0; j < dim; j++ ) x[j] = ranges[j].generatePoint();
            integral += f.value(x);
        }
        return vol*integral/nIter;
    }
    
    public static double integralTrapezoid( IModelFunction f ) {
        if ( f.dimension() != 1 ) throw new IllegalArgumentException("Cannot integrate multi-dimensional functions!");
        
        int steps = 100;
        double integral = 0;
        double[] x = new double[1];
        
        RangeSet range = (RangeSet)f.normalizationRange(0);
        double[] lowerBounds = range.lowerBounds();
        double[] upperBounds = range.upperBounds();
        
	for (int i=0; i<range.size(); i++) {
            double rub = upperBounds[i];
            double rlb = lowerBounds[i];

            double delta = (rub-rlb)/steps;
            double norm = delta/2;
            double tmpIntegral = 0;
            x[0] = rub;
            tmpIntegral += f.value(x);
            x[0] = rlb;
            tmpIntegral += f.value(x);
            
            
            for ( int j = 1; j < steps; j++ ) {
                x[0] = rlb+j*delta;
                tmpIntegral += 2*f.value(x);
            }
            integral += norm*tmpIntegral;
        }
        return integral;
    }
        
    public static double integralSimpson( IModelFunction f ) {
        if ( f.dimension() != 1 ) throw new IllegalArgumentException("Cannot integrate multi-dimensional functions!");
        
        int steps = 20; //This has to be even!!!
        double integral = 0;
        double[] x = new double[1];
        
        RangeSet range = (RangeSet)f.normalizationRange(0);
        double[] lowerBounds = range.lowerBounds();
        double[] upperBounds = range.upperBounds();
        
	for (int i=0; i<range.size(); i++) {
            double rub = upperBounds[i];
            double rlb = lowerBounds[i];

            double delta = (rub-rlb)/steps;
            double norm = delta/3;
            double tmpIntegral = 0;
            x[0] = rub;
            tmpIntegral += f.value(x);
            x[0] = rlb;
            tmpIntegral += f.value(x);
            
            
            for ( int j = 0; j < steps/2; j++ ) {
                x[0] = rlb+(2*j+1)*delta;
                tmpIntegral += 4*f.value(x);
            }
            for ( int j = 1; j < steps/2; j++ ) {
                x[0] = rlb+(2*j)*delta;
                tmpIntegral += 2*f.value(x);
            }

            integral += norm*tmpIntegral;
        }
        
        return integral;
    }
        
    
    // FIXME: move to test or examples
    /*
    public static void main(String[] args) {
        FunctionFactory ff = new FunctionFactory(null);
        

        IModelFunction func  = (IModelFunction)ff.createFunctionFromScript("threeDdistr",3,"N*(exp( -(x[0]-mu0)*(x[0]-mu0)/(2*s0*s0) ))*(m*x[1]+2)*(exp(-x[2]/tau)/tau)","N,mu0,s0,m,tau","",null);
        IModelFunction func1 = (IModelFunction)ff.createFunctionFromScript("func1",1,"N*(exp( -(x[0]-mu0)*(x[0]-mu0)/(2*s0*s0) ))","N,mu0,s0","",null);
        IModelFunction func2 = (IModelFunction)ff.createFunctionFromScript("func2",1,"N*(m*x[0]+2)","N,m","",null);
        IModelFunction func3 = (IModelFunction)ff.createFunctionFromScript("func3",1,"N*(exp(-x[0]/tau)/tau)","N,tau","",null);

        double[] initialPars = { 1, 5, 1 ,1,0.35};
        func.setParameters( initialPars );
        func.normalizationRange(0).excludeAll();
        func.normalizationRange(0).include(0,2);
        func.normalizationRange(1).excludeAll();
        func.normalizationRange(1).include(1,3);
        func.normalizationRange(2).excludeAll();
        func.normalizationRange(2).include(2,4);
        
        double[] initialPars1 = { 1, 5, 1 };
        func1.setParameters( initialPars1 );
        func1.normalizationRange(0).excludeAll();
        func1.normalizationRange(0).include(0,2);
        
        double[] initialPars2 = { 1, 1};
        func2.setParameters( initialPars2 );
        func2.normalizationRange(0).excludeAll();
        func2.normalizationRange(0).include(1,3);
        
        double[] initialPars3 = { 1, 0.35};
        func3.setParameters( initialPars3 );
        func3.normalizationRange(0).excludeAll();
        func3.normalizationRange(0).include(2,4);

        
        IModelFunction line = (IModelFunction)ff.createFunctionFromScript("func3",1,"N*x[0]*x[0]","N","",null);
        double[] iP = {1};
        line.setParameters(iP);
        line.normalizationRange(0).excludeAll();
        line.normalizationRange(0).include(0,1);
        
        System.out.println(FunctionIntegrator.integralTrapezoid(line)+" "+FunctionIntegrator.integralVegasMC(line));
        
        
//        IModelFunction f = (IModelFunction) ff.createFunctionFromScript("", 1, "4+x[0]", "","",null);
//        f.normalizationRange(0).excludeAll();
//        f.normalizationRange(0).include(-2,0);
//        f.normalizationRange(0).include(3,5);

//        f.normalizationRange(1).excludeAll();
//        f.normalizationRange(1).include(-2,2);
        
//        System.out.println("Integral : "+FunctionIntegrator.integralMC(f)+" "+FunctionIntegrator.integralTrapezoid(f)+" "+FunctionIntegrator.integralSimpson(f));
        double funcInt  = FunctionIntegrator.integralMC(func);
        double func1Int = FunctionIntegrator.integralTrapezoid(func1);
        double func2Int = FunctionIntegrator.integralTrapezoid(func2);
        double func3Int = FunctionIntegrator.integralTrapezoid(func3);
        
      
        System.out.println(func1Int+" "+FunctionIntegrator.integralVegasMC(func1));
        System.out.println(func2Int+" "+FunctionIntegrator.integralVegasMC(func2));
        System.out.println(func3Int+" "+FunctionIntegrator.integralVegasMC(func3));
        
        
        System.out.println("Integral : "+funcInt+" "+func1Int*func2Int*func3Int);
        System.out.println("Vegas    : "+FunctionIntegrator.integralVegasMC(func));
    }
    */
    
}
