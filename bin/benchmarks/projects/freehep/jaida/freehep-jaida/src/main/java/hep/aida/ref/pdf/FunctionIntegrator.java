package hep.aida.ref.pdf;

/**
 *
 * @author  The FreeHEP team @ SLAC.
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
    

    public static double integralVegasMC( Function f, Dependent x ) {
        return integralVegasMC(f, new Dependent[] {x} );
    }
    
    public static double integralVegasMC( Function f, VariableList v ) {
        if ( v.type() != VariableList.DEPENDENT )
            throw new IllegalArgumentException("Only a list of Dependents can be provided when integrating.");
        Dependent[] vars = new Dependent[v.size()];
        for ( int i = 0; i < vars.length; i++ )
            vars[i] = (Dependent) v.get(i);
        return integralVegasMC(f,vars);
    }
    
    public static double integralVegasMC( Function f, Dependent[] vars ) {
        Grid grid = new Grid(vars);
        if ( ! grid.isValid() ) throw new RuntimeException("Problem initializing the grid for function "+f);
        
        int nRefineIter = 5;
        int nRefinePerDim = 1000;
        int nIntegratePerDim = 5000;
        
        vegas(f, grid, AllStages, nRefinePerDim*grid.dimension(), nRefineIter, Importance);
        
        return vegas(f, grid, AllStages, nIntegratePerDim*grid.dimension(), 1, Importance);
    }
    

    
    
    private static double vegas( Function ff, Grid grid, int stage, int calls, int iterations, int mode) {
        if ( stage == AllStages ) grid.initialize();
        
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
                    grid.generatePoint(box, bin, binVol);
                    
                    double fVal = jacbin*binVol[0]*ff.functionValue();
                                        
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
    
    
    public static double integralMC( Function f, Dependent x ) {
        return integralMC(f, new Dependent[] {x} );
    }
    
    public static double integralMC( Function f, VariableList v ) {
        if ( v.type() != VariableList.DEPENDENT )
            throw new IllegalArgumentException("Only a list of Dependents can be provided when integrating.");
        Dependent[] vars = new Dependent[v.size()];
        for ( int i = 0; i < vars.length; i++ )
            vars[i] = (Dependent) v.get(i);
        return integralMC(f,v);
    }
    
    public static double integralMC( Function f, Dependent[] deps ) {
        int dim = deps.length;
        RangeSet[] ranges = new RangeSet[dim];
        double vol = 1.;
        
        for ( int i = 0; i < dim; i++ ) {
            ranges[i] = deps[i].range();
            vol *= ranges[i].length();
        }
        
        double integral = 0;
    
        int nIter = 1000000;
        for ( int i = 0; i<nIter; i++ ) {
            for ( int j = 0; j < dim; j++ ) deps[j].setValue( ranges[j].generatePoint() );
            integral += f.functionValue();
        }
        return vol*integral/nIter;
    }

    public static double integralTrapezoid( Function f, Dependent x ) {
        return integralTrapezoid(f, new Dependent[] {x} );
    }
    
    public static double integralTrapezoid( Function f, VariableList v ) {
        if ( v.type() != VariableList.DEPENDENT )
            throw new IllegalArgumentException("Only a list of Dependents can be provided when integrating.");
        Dependent[] vars = new Dependent[v.size()];
        for ( int i = 0; i < vars.length; i++ )
            vars[i] = (Dependent) v.get(i);
        return integralTrapezoid(f,vars);
    }
    
    public static double integralTrapezoid( Function f, Dependent[] deps ) {
        if ( deps.length != 1 ) throw new IllegalArgumentException("Cannot integrate multi-dimensional functions!");
        
        Dependent x = deps[0];
        
        int steps = 1000;
        double integral = 0;
        
        RangeSet range = x.range();
        double[] lowerBounds = range.lowerBounds();
        double[] upperBounds = range.upperBounds();
        
	for (int i=0; i<range.size(); i++) {
            double rub = upperBounds[i];
            double rlb = lowerBounds[i];

            double delta = (rub-rlb)/steps;
            double norm = delta/2;
            double tmpIntegral = 0;
            x.setValue(rub);
            tmpIntegral += f.functionValue();
            x.setValue(rlb);
            tmpIntegral += f.functionValue();
            
            
            for ( int j = 1; j < steps; j++ ) {
                x.setValue(rlb+j*delta);
                tmpIntegral += 2*f.functionValue();
            }
            integral += norm*tmpIntegral;
        }
        
        return integral;
    }
        

    public static double integralSimpson( Function f, Dependent x ) {
        return integralSimpson(f, new Dependent[] {x} );
    }
    
    public static double integralSimpson( Function f, VariableList v ) {
        if ( v.type() != VariableList.DEPENDENT )
            throw new IllegalArgumentException("Only a list of Dependents can be provided when integrating.");
        Dependent[] vars = new Dependent[v.size()];
        for ( int i = 0; i < vars.length; i++ )
            vars[i] = (Dependent) v.get(i);
        return integralSimpson(f,v);
    }
    
    public static double integralSimpson( Function f, Dependent[] deps ) {
        if ( deps.length != 1 ) throw new IllegalArgumentException("Cannot integrate multi-dimensional functions!");
        
        int steps = 120; //This has to be even!!!
        double integral = 0;
        
        Dependent x = deps[0];
        
        RangeSet range = x.range();
        double[] lowerBounds = range.lowerBounds();
        double[] upperBounds = range.upperBounds();
        
	for (int i=0; i<range.size(); i++) {
            double rub = upperBounds[i];
            double rlb = lowerBounds[i];

            double delta = (rub-rlb)/steps;
            double norm = delta/3;
            double tmpIntegral = 0;
            x.setValue(rub);
            tmpIntegral += f.functionValue();
            x.setValue(rlb);
            tmpIntegral += f.functionValue();
            
            
            for ( int j = 0; j < steps/2; j++ ) {
                x.setValue(rlb+(2*j+1)*delta);
                tmpIntegral += 4*f.functionValue();
            }
            for ( int j = 1; j < steps/2; j++ ) {
                x.setValue(rlb+(2*j)*delta);
                tmpIntegral += 2*f.functionValue();
            }

            integral += norm*tmpIntegral;
        }
        
        return integral;
    }
        
}
