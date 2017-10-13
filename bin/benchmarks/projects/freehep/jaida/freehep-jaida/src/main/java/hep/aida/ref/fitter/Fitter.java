package hep.aida.ref.fitter;

import hep.aida.IAnalysisFactory;
import hep.aida.IAnnotation;
import hep.aida.IBaseHistogram;
import hep.aida.ICloud;
import hep.aida.IDataPointSet;
import hep.aida.IFitData;
import hep.aida.IFitParameterSettings;
import hep.aida.IFitResult;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.IModelFunction;
import hep.aida.IProfile;
import hep.aida.IRangeSet;
import hep.aida.ITree;
import hep.aida.ITupleFactory;
import hep.aida.dev.IDevFitData;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.dev.IDevFitResult;
import hep.aida.ext.IExtFitter;
import hep.aida.ext.IFitMethod;
import hep.aida.ext.IOptimizer;
import hep.aida.ext.IOptimizerConfiguration;
import hep.aida.ext.IOptimizerFactory;
import hep.aida.ext.IVariableSettings;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.fitter.fitdata.FitDataCreator;
import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.FunctionCatalog;
import hep.aida.ref.function.RangeSet;
import hep.aida.ref.histogram.DataPointSet;
import hep.aida.ref.pdf.Dependent;
import hep.aida.ref.pdf.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 * @author The AIDA team @ SLAC.
 *
 */

public class Fitter implements IExtFitter {
    
    // The IOptimizer.
    private String engineType;
    private IOptimizer optimizer = null;
    
    // The IFitMethod.
    private String fitMethodType;
    private IFitMethod fitMethod = null;
    
    // The IFunction to be minimized.
    private IFunction fitFunction;
    
    // IFitParameterSettings
    private Hashtable fitParHash = new Hashtable();
    
    private boolean useGradient = true;
    
    private ArrayList constraintList = new ArrayList();
    private Hashtable simpleConstraintHash = new Hashtable();
    private boolean createClone = true;
    
    /**
     * Create a new Fitter specifying the underlying optimizing engine.
     * @param fitMethodType The type of fitter.
     * @param engineType The type of optimizer to use.
     * @throws IllegalArgumentException if the engineType does not exist.
     *
     */
    public Fitter(String fitMethodType, String engineType, String options) throws IllegalArgumentException {
        setFitMethod(fitMethodType);
        setEngine(engineType);
        
        Map opt = hep.aida.ref.AidaUtils.parseOptions(options);
        String val = (String) opt.get("noClone");
        if ( val != null && val.trim().equalsIgnoreCase("true") ) createClone = false;
    }
    
    public void setEngine(String engineType) throws IllegalArgumentException {
        if (engineType == null || engineType.length() == 0) engineType = "jminuit";
        String enType = engineType.toLowerCase();
        
        IOptimizerFactory tmpOptimizerFactory = null;
        Lookup.Template template = new Lookup.Template(IOptimizerFactory.class);
        Lookup.Result result = FreeHEPLookup.instance().lookup(template);
        Collection c = result.allInstances();
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            IOptimizerFactory of = (IOptimizerFactory)i.next();
            String[] names = of.optimizerFactoryNames();
            if ( names == null || names.length == 0 )
                throw new IllegalArgumentException("IOptimizerFactory with illegal names!");
            for ( int j = 0; j < names.length; j++ ) {
                if ( enType.equals( names[j].toLowerCase() ) ) {
                    tmpOptimizerFactory = of;
                    break;
                }
            }
        }
        if (tmpOptimizerFactory == null) throw new IllegalArgumentException("Cannot create IOptimizer of type: "+engineType);
        this.engineType = engineType;
        this.optimizer = tmpOptimizerFactory.create(engineType);
    }
    public String engineName() {
        return engineType;
    }
    
    public void setFitMethod(String fitMethodType) throws IllegalArgumentException {
        if (fitMethodType == null || fitMethodType.length() == 0) fitMethodType = "chi2";
        // Check the lookup table to look for the fitMethod of the given type.
        String fitMet = fitMethodType.toLowerCase();
        
        IFitMethod tmpFitMethod = null;
        Lookup.Template template = new Lookup.Template(IFitMethod.class);
        Lookup.Result result = FreeHEPLookup.instance().lookup(template);
        Collection c = result.allInstances();
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            IFitMethod fm = (IFitMethod)i.next();
            String[] names = fm.fitMethodNames();
            if ( names == null || names.length == 0 )
                throw new IllegalArgumentException("IFitMethod with illegal names!");
            for ( int j = 0; j < names.length; j++ ) {
                if ( fitMet.equals( names[j].toLowerCase() ) ) {
                    tmpFitMethod = fm;
                    break;
                }
            }
        }
        if (tmpFitMethod == null) throw new IllegalArgumentException("Unknown IFitMethod type: "+fitMethodType);
        this.fitMethodType = fitMethodType;
        this.fitMethod = tmpFitMethod;
    }
    
    public IOptimizer optimizer() {
        return optimizer;
    }
    
    public String fitMethodName() {
        return fitMethodType;
    }
    
    public IFitParameterSettings fitParameterSettings(String name) {
        if ( fitParHash.containsKey(name) ) return (IFitParameterSettings) fitParHash.get(name);
        IFitParameterSettings fitPar = new FitParameterSettings(name);
        fitParHash.put(name,fitPar);
        return fitPar;
    }
    
    public String[] listParameterSettings() {
        int size = fitParHash.size();
        String[] parNames = new String[size];
        Enumeration e = fitParHash.keys();
        for (int i = 0; e.hasMoreElements(); i++)
            parNames[i] = (String) e.nextElement();
        return parNames;
    }
    
    public void resetParameterSettings() {
        fitParHash.clear();
    }
    
    public IFitResult fit( Function f ) {
        if ( f.numberOfDependents() != 1 )
            throw new IllegalArgumentException("Currently we only fit 1 dimensional functions.");
        Dependent x = f.getDependent(0);
        if ( ! x.isConnected() )
            throw new IllegalArgumentException("Dependent "+x.name()+" is not connected to a data set");
        
        IFitData data = x.data();
        return fit( data, f );
    }
    
    public IFitResult fit(IFitData d, IFunction originalFunction) {
        return fit(d, originalFunction, null);
    }
    
    public IFitResult fit(IFitData d, IFunction originalFunction, String range) {
        return fit(d, originalFunction, range, null);
    }
    
    private void addBounds(IRangeSet rs, int index, double[] lb, double[] ub) {
        if ( lb.length == 0 ) throw new RuntimeException("No ranges are set in the FitData. Please report the problem");
        for ( int j = 0; j < ub.length; j++ )
                rs.include(lb[j],ub[j]);
    }
    
    public IFitResult fit(IFitData d, IFunction originalFunction, String range, Object correlationObject) {
        
        String name = "";
        if ( originalFunction instanceof IManagedObject ) {
            name = ((IManagedObject) originalFunction).name();
        } else {
            name = originalFunction.title();
        }
        name += " "+engineName()+" fit";
        
        IFunction fClone = cloneFunction(name,originalFunction);
        
        boolean setRange = false;
        if ( originalFunction instanceof IFunction )
            setRange = true;
        //        IModelFunction f = (IModelFunction)originalFunction;
        IModelFunction f;
        
        if ( fClone instanceof IModelFunction ) {
            f = (IModelFunction)fClone;
        } else if (fClone instanceof IFunction) {
            f = new BaseModelFunction(name, name, fClone);
        } else {
            throw new RuntimeException("Fitter for now can only use IModelFunctions and IFunctions. Please report this problem");
        }
        
        if ( range != null ) {
            Map optionsMap = AidaUtils.parseOptions( range );
            range = (String) optionsMap.get("range");
            RangeSet usersRange = new RangeSet(range);
            IRangeSet rs = d.range(0);
            rs.excludeAll();
            addBounds(rs, 0, usersRange.lowerBounds(), usersRange.upperBounds());
        }

        if ( setRange ) {
            f.excludeNormalizationAll();
                //Set ranges on function.            
                IRangeSet d_rs = null;
                for ( int i = 0; i < d.dimension(); i++ ) {
                    d_rs = d.range(i);
                    IRangeSet f_rs = f.normalizationRange(i);
                    addBounds(f_rs, i, d_rs.lowerBounds(), d_rs.upperBounds());
                }
        }        
        
        //Clear the fit method and set the correlation Object
        fitMethod.clear();
        fitMethod.setCorrelationObject(correlationObject);
                
        loadFitDataAndFunction(d,f);
        
        long startFit = System.currentTimeMillis();
        optimizer.optimize();
        long endFit = System.currentTimeMillis();
        double fitSeconds = (double)(endFit-startFit)/1000.;
        
        // Set the minimum parameters on the function
        String[] parNames = f.parameterNames();
        for( int i = 0; i < parNames.length; i++ )
            f.setParameter(parNames[i],optimizer.result().parameters()[i]);
        
        double[][] covMatrix = optimizer.result().covarianceMatrix();
        
        int status = optimizer.result().optimizationStatus();
        int dataEntries = ((FitFunction) fitFunction).dataEntries();
        int freePars = ((FitFunction) fitFunction).nFreePars();
        int nDoF = dataEntries - freePars;
        double funcVal = fitFunction.value(f.parameters());
        
        IDevFitResult result = new FitResult(fitFunction.dimension(), fitSeconds);
        result.setConstraints( constraints() );
        result.setDataDescription( d.dataDescription() );
        result.setEngineName( engineName() );
        result.setFitMethodName( fitMethodName() );
        result.setFitStatus( status );
        result.setFittedFunction( f ); // FIX ME! Replace with clone.
        result.setIsValid( true ); ////??????
        result.setNdf( nDoF );
                
        if ( fitMethod.fitType() == IFitMethod.UNBINNED_FIT )
            result.setQuality( funcVal/nDoF/Math.sqrt(2.) );
        else
            result.setQuality( funcVal/nDoF );
        
        int countI = 0;
        for( int i = 0; i < parNames.length; i++ ) {
            result.setFitParameterSettings( parNames[i], fitParameterSettings(parNames[i]) );
            int countJ = 0;
            for( int j = 0; j < parNames.length; j++ ) {
                if ( ! optimizer.variableSettings(parNames[i]).isFixed() &&  ! optimizer.variableSettings(parNames[j]).isFixed() )
                    result.setCovMatrixElement( i, j , covMatrix[countI][countJ++] );
            }
            if ( ! optimizer.variableSettings(parNames[i]).isFixed() ) countI++;
        }
        
        return (IFitResult)result;
    }
    
    
    public IFitResult fit(IBaseHistogram h, IFunction f) {
        return fit( h, f, null);
    }

    public IFitResult fit(IBaseHistogram h, IFunction f, String range) {    
        if ( h instanceof IHistogram || h instanceof IProfile )
            if ( fitMethod.fitType() == IFitMethod.UNBINNED_FIT ) throw new IllegalArgumentException("Cannot perform unbinned fit on a IHistogram!!");
            else if ( h instanceof ICloud )
                if ( fitMethod.fitType() == IFitMethod.BINNED_FIT ) throw new IllegalArgumentException("Cannot perform binned fit on a ICloud!!");
        IFitData fitData = FitDataCreator.create(h);
        return fit( fitData, f, range );
    }    
    
    private void setDefaultInitialParameters(IFunction f, String model, Object data) {
        if ( model.equals("g") ) {
            if ( data instanceof IHistogram1D ) {
                IHistogram1D h1 = (IHistogram1D) data;
                double[] pars = new double[3];
                pars[0] = h1.maxBinHeight();
                pars[1] = h1.mean();
                pars[2] = h1.rms();
                f.setParameters(pars);
            }
        }        
    }
    
    public IFitResult fit(IBaseHistogram h, String model) {
        return fit(h, model, (String)null);
    }
    
    public IFitResult fit(IBaseHistogram h, String model, String range) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        setDefaultInitialParameters(func, model, h);
        return fit(h,func, range);
    }

    public IFitResult fit(IBaseHistogram h, String model, double[] initialParameters) {
        return fit(h,model, initialParameters, null);
    }
    
    public IFitResult fit(IBaseHistogram h, String model, double[] initialParameters, String range) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        if ( initialParameters.length != func.numberOfParameters() ) throw new IllegalArgumentException("Wrong number of parameters "+initialParameters.length+
        "! This function requires "+func.numberOfParameters());
        func.setParameters(initialParameters);
        return fit(h,func, range);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, IFunction f) {
        return fit(dataPointSet, f, (String)null);
    }
    public IFitResult fit(IDataPointSet dataPointSet, IFunction f, String range) {
        return fit(dataPointSet, f, range, null);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, IFunction f, double[] initialParameters) {
        return fit(dataPointSet, f, initialParameters, null, null);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, IFunction f, String range, Object correlationObject) {
        return fit(dataPointSet, f, null, range, correlationObject);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, IFunction f, double[] initialParameters, String range, Object correlationObject) {
        if ( fitMethod.fitType() == IFitMethod.UNBINNED_FIT ) throw new IllegalArgumentException("Cannot perform unbinned fit on a IDataPointSet!!");
        if ( dataPointSet.dimension() != f.dimension() + 1 ) throw new IllegalArgumentException("Wrong dimension match. DataPointSets can only be fitted if"+
        " their dimension is one unit bigger than the one of the function");
        
        if ( initialParameters != null ) {
            if ( initialParameters.length != f.numberOfParameters() ) throw new IllegalArgumentException("Wrong number of parameters "+initialParameters.length+
            "! This function requires "+f.numberOfParameters());
            f.setParameters(initialParameters);
        }
        
        IFitData fitData = FitDataCreator.create(dataPointSet);
        
        return fit( fitData, f, range, correlationObject );
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, String model) {
        return fit(dataPointSet, model, (String)null);
    }
    public IFitResult fit(IDataPointSet dataPointSet, String model, String range) {
        return fit(dataPointSet,model, range, null);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, String model, String range, Object correlationObject) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        setDefaultInitialParameters(func, model, dataPointSet);
        return fit(dataPointSet,func, range, correlationObject);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, String model, double[] initialParameters) {
        return fit(dataPointSet, model, initialParameters, null, null);        
    }
    public IFitResult fit(IDataPointSet dataPointSet, String model, double[] initialParameters, String range) {
        return fit(dataPointSet, model, initialParameters, range, null);
    }
    
    public IFitResult fit(IDataPointSet dataPointSet, String model, double[] initialParameters, String range, Object correlationObject) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        
        return fit(dataPointSet,func, initialParameters, range, correlationObject);
    }
    
    
    public IFitResult fit(IFitData d, String model, double[] initialParameters) {
        return fit(d,model,initialParameters,null);
    }
    
    public IFitResult fit(IFitData d, String model, double[] initialParameters, String range) {
        return fit(d,model,initialParameters, range, null);
    }

    public IFitResult fit(IFitData d, String model, double[] initialParameters, String range, Object correlationObject) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        
        if ( initialParameters != null ) {
            if ( initialParameters.length != func.numberOfParameters() ) throw new IllegalArgumentException("Wrong number of parameters "+initialParameters.length+
            "! This function requires "+func.numberOfParameters());
            func.setParameters(initialParameters);
        }
        return fit(d,func, range, correlationObject);
    }
    
    public IFitResult fit(IFitData d, String model) {
        return fit(d,model,(String)null);
    }
    
    public IFitResult fit(IFitData d, String model, String range) {
        return fit(d,model, range, null);
    }

    public IFitResult fit(IFitData d, String model, String range, Object correlationObject) {
        IFunction func = FunctionCatalog.getFunctionCatalog().create(model);
        return fit(d,func, range, correlationObject);
    }
    
    public void setConstraint(String expression) throws IllegalArgumentException {
        if ( ! optimizer.acceptsConstraints() )
            throw new UnsupportedOperationException("Optimizer "+engineName()+" does not accept constraints.");
        StringTokenizer st = new StringTokenizer(expression,"=");
        if ( st.countTokens() != 2 ) throw new IllegalArgumentException("Only constraints of the form \" parName = SomeExpression \" are supported");
        String parName = st.nextToken().trim();
        if ( ! Pattern.matches("\\w+", parName) ) throw new IllegalArgumentException("Incorrect parameter name "+parName);
        String constrExpression = st.nextToken().trim();
        
        if ( Pattern.matches("\\w+",constrExpression) ) {
            simpleConstraintHash.put(parName,constrExpression);
        } else {
            throw new IllegalArgumentException("This type of constraint is not supported yet "+parName+" = "+constrExpression);
        }
        constraintList.add(expression);
    }
    public String[] constraints() {
        int size = constraintList.size();
        String[] constraints = new String[size];
        for ( int i = 0; i<size; i++ )
            constraints[i] = (String) constraintList.get(i);
        return constraints;
    }
    public void resetConstraints() {
        simpleConstraintHash.clear();
        constraintList.clear();
    }
    public IDataPointSet createScan1D(IFitData d, IFunction originalFunction, String parName, int npts, double pmin, double pmax) {
        if ( pmin > pmax ) throw new IllegalArgumentException("Incorret parameter limits : "+pmin+" has to be less than "+pmax);
        if ( npts < 1 ) throw new IllegalArgumentException("The number of points has to be greater than zero : "+npts);
        
        IFunction fClone = cloneFunction(null, originalFunction);
        
        IModelFunction f;
        if ( fClone instanceof IModelFunction ) {
            f = (IModelFunction)fClone;
        } else {
            f = new BaseModelFunction("", "", fClone);
        }
        
        loadFitDataAndFunction(d,f);
        
        int index = f.indexOfParameter( parName );
        
        IFitParameterSettings fitParSet = fitParameterSettings(parName);
        if ( fitParSet.isFixed() ) throw new IllegalArgumentException("Parameter "+parName+" is fixed");
        
        IDataPointSet dps = new DataPointSet("",parName+" scan",2,npts);
        double step = (pmax - pmin)/(npts-1);
        
        double startValue = optimizer.variableSettings(parName).value();
        
        for ( int i = 0; i<npts; i++ ) {
            double val = pmin + i*step;
            if ( i == npts-1 ) val = pmax;
            
            optimizer.variableSettings(parName).setValue(val);
            
            String[] parNames = f.parameterNames();
            double[] fitParsVal = new double[ parNames.length ];
            for( int j = 0; j < parNames.length; j++ )
                fitParsVal[j] = optimizer.variableSettings(parNames[j]).value();
            
            dps.point(i).coordinate(0).setValue( val );
            dps.point(i).coordinate(0).setErrorPlus(0);
            dps.point(i).coordinate(0).setErrorMinus(0);
            dps.point(i).coordinate(1).setValue( fitFunction.value(fitParsVal) );
            dps.point(i).coordinate(1).setErrorPlus(0);
            dps.point(i).coordinate(1).setErrorMinus(0);
        }
        optimizer.variableSettings(parName).setValue(startValue);
        f.setParameter(parName,startValue);
        return dps;
    }
    
    public IDataPointSet createContour(IFitData d, IFitResult r, String par1, String par2, int npts, double nSigmas) {
        
        IFunction result = r.fittedFunction();
        IModelFunction f;
        if ( result instanceof IModelFunction ) {
            f = (IModelFunction)result;
        } else {
            f = new BaseModelFunction("", "", result);
        }
        loadFitDataAndFunction(d,f);

        double[][] contour;
        
        if ( optimizer.canCalculateContours() )
            contour = optimizer.calculateContour(par1,par2, npts, nSigmas);
        else {
            String eName = engineName();
            setEngine("minuit");
            contour = optimizer.calculateContour(par1, par2, npts, nSigmas);
            setEngine(eName);
        }
        
        int found = contour[0].length;
        IDataPointSet dps = new DataPointSet("",par1+" vs "+par2+" "+nSigmas+" sigma contour",2,found);
        for ( int i = 0; i<found; i++ ) {
            dps.point(i).coordinate(0).setValue( contour[0][i] );
            dps.point(i).coordinate(0).setErrorPlus(0);
            dps.point(i).coordinate(0).setErrorMinus(0);
            dps.point(i).coordinate(1).setValue( contour[1][i] );
            dps.point(i).coordinate(1).setErrorPlus(0);
            dps.point(i).coordinate(1).setErrorMinus(0);
        }
        return dps;
    }
    
    private IFunction fitFunction() {
        return fitFunction;
    }
    
    public boolean useFunctionGradient() {
        return useGradient;
    }
    public void setUseFunctionGradient(boolean useGradient) {
        this.useGradient = useGradient;
    }
    
    private void loadFitDataAndFunction(IFitData d, IModelFunction f) {
        if ( fitMethod.fitType() != ( (IDevFitData) d ).fitType() ) throw new IllegalArgumentException("This FitData is incompatible with the selected fit method");
        if ( d.dimension() != f.dimension() ) throw new IllegalArgumentException("Dimension mismatch!! Function's dimension "+f.dimension()+" FitData's dimension "+d.dimension());
        
        optimizer.reset();
        
        if ( fitMethod.fitType() == IFitMethod.BINNED_FIT ) {
            optimizer.configuration().setErrorDefinition(IOptimizerConfiguration.CHI2_FIT_ERROR);
            f.normalize(false);
        } else {
            optimizer.configuration().setErrorDefinition(IOptimizerConfiguration.LOGL_FIT_ERROR);
            f.normalize(true);
        }
        
        
        IDevFitDataIterator dataIter = ( (IDevFitData) d ).dataIterator();
        fitFunction = new FitFunction(dataIter, f);
        
        // Create the FitParameterSettings
        String[] parNames = f.parameterNames();        
        for( int i = 0; i < parNames.length; i++ ) {
            String parName = parNames[i];
            IFitParameterSettings fitPar = fitParameterSettings(parName);
            double parVal = f.parameter(parName);
            IVariableSettings varSet = optimizer.variableSettings(parName);
            varSet.setValue( parVal );
            varSet.setFixed( fitPar.isFixed() );
            
            String simpleConstrString = (String)simpleConstraintHash.get( parName );
            if ( simpleConstrString != null )
                if ( ( (FitFunction)fitFunction ).isValidSimpleConstraint( parName, simpleConstrString ) ) {
                    ( (FitFunction) fitFunction ).setSimpleConstraint( parName, simpleConstrString );
                    varSet.setFixed( true );
                }
            
            double stepSize = fitPar.stepSize();
            if ( Double.isNaN( stepSize ) ) {
                stepSize = 0.1*Math.abs(parVal);
                if ( stepSize < 1 ) stepSize = 1;
            }
            varSet.setStepSize( stepSize );
            if ( fitPar.isBound() )
                varSet.setBounds( fitPar.lowerBound(), fitPar.upperBound() );
        }
        
        optimizer.setFunction( fitFunction );
//        For the time being don't use function's derivatives. The seem to be incorrect.        
//        optimizer.configuration().setUseFunctionGradient(fitFunction.providesGradient() && useFunctionGradient());
        optimizer.configuration().setUseFunctionGradient(false);
        
        optimizer.configuration().setMaxIterations(500);
        //        optimizer.configuration().setPrintLevel(-2);
    }
        
    private IFunction cloneFunction(String name, IFunction originalFunction) {
        IFunction fClone = originalFunction;
        if (createClone) fClone = FunctionCatalog.getFunctionCatalog().clone(name, originalFunction);
        return fClone;
    }
    
    
    
    public static void main(String args[]) throws java.io.IOException {
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create();
        IHistogramFactory hf = af.createHistogramFactory( tree );
        ITupleFactory tf = af.createTupleFactory( tree );
        IFunctionFactory  ff = af.createFunctionFactory( tree );
        
/*
        hep.aida.IHistogram1D hist = hf.createHistogram1D("hist","hist",100,0,1);
        hep.aida.IHistogram1D gaussHist = hf.createHistogram1D("gaussHist","gaussHist",100,-5,5);
        hep.aida.IHistogram2D hist2d = hf.createHistogram2D("hist2d","hist2d",100,-5,5,10,-10,0);
        java.util.Random r = new java.util.Random();
 
 
        hep.aida.IHistogram1D parabola1 = hf.createHistogram1D("parabola1","parabola1",100,0,1);
        hep.aida.IHistogram1D parabola2 = hf.createHistogram1D("parabola2","parabola2",50,0,1);
 
 
        ITuple tup = tf.create("tup","tup","double x, double y");
 
        for ( int i = 0; i < 5000; i++ ) {
            double x = r.nextDouble();
            double y = r.nextGaussian()-5;
            double w = Math.exp(-.1*x);
            hist.fill(x, w);
            hist2d.fill(x,y);
            tup.fill(0,x);
            tup.fill(1,y);
            tup.addRow();
 
            parabola1.fill(x,x*x-x+1);
            parabola2.fill(x,x*x-x+1);
            gaussHist.fill(r.nextGaussian());
        }
 
 
        Fitter fitter = new Fitter("Chi2","uncmin");
        //        fitter.setConstraint(" mean1 = mean2");
        //        fitter.setConstraint("       mean1=Math.sqrt(mean2)");
        //        fitter.setConstraint(" mean1 =2*mean2");
 */
        IFunction f = ff.createFunctionByName("parabola","p2");
/*
        FitResult fitr1 = (FitResult)fitter.fit(parabola1,f);
        fitr1.printResult();
        FitResult fitr2 = (FitResult)fitter.fit(parabola2,f);
        fitr2.printResult();
 
        /*
        IFunction f = ff.createFunctionFromScript("f",1,"b*exp(c*x[0])","b,c","",null);
        f.setParameter("b",50);
        f.setParameter("c",.1);
        //        f.setParameter("d",.1);
 
 
        //          fitter.setConstraint( "d = c");
        //          fitter.fitParameterSettings("c").setFixed(true);
        //        fitter.fitParameterSettings("d").setFixed(true);
        //        fitter.setFitMethod("uml");
 
        //        FitResult fitResult = (FitResult)fitter.fit( hist, f );
        //        fitResult.printResult();
 
 
 
        fitter.setEngine("minuit");
        FitResult fitResult2 = (FitResult)fitter.fit( hist, f );
        fitResult2.printResult();
 
 
 
 
 
 
 
 
        //IFunction f = ff.createFunctionByName("gauss","g");
 
        /*
        IModelFunction exp = new ExponentialModel();
        FitResult expFitRes = (FitResult)fitter.fit(hist, exp);
        expFitRes.printResult();
 
        FitResult expFitRes2 = (FitResult)fitter.fit(hist, "e");
        expFitRes2.printResult();
 */
        //        f.normalize(false);
        //        f2.normalize(false);
        
        
/*
        f2.setParameter("mean",0);
        f2.setParameter("sigma",1);
        f2.setParameter("mean1",-5);
        f2.setParameter("sigma1",1);
        f2.setParameter("norm",5000);
 */
        /*
        fitter.fitParameterSettings("sigma").setFixed(true);
        //      fitter.fitParameterSettings("mean").setFixed(true);
        //        fitter.fitParameterSettings("sigma1").setFixed(true);
        //fitter.fitParameterSettings("mean1").setFixed(true);
        //                fitter.fitParameterSettings("norm").setFixed(true);
         
        fitter.fitParameterSettings("mean").setStepSize(0.0001);
        fitter.fitParameterSettings("sigma").setStepSize(0.01);
        fitter.fitParameterSettings("norm").setStepSize(0.1);
        fitter.fitParameterSettings("mean1").setStepSize(0.0001);
        fitter.fitParameterSettings("sigma1").setStepSize(0.01);
/*
         
        fitter.fit(hist,f);
         
        fitter.setFitMethod("chi2");
        fitter.fit(hist,f);
         
        fitter.setEngine("uncmin");
        fitter.fit(hist,f);
         
        fitter.setFitMethod("cleverChi2");
        fitter.fit(hist,f);
        fitter.setFitMethod("bml");
        //fitter.setFitMethod("chi2");
        fitter.fit(hist,f);
         */
        /*
         
        //        fitter.setEngine("uncmin");
        IEvaluator[] ev = null;
        FitData fd = new FitData();
         
         
        fitter.setFitMethod("chi2");
        fitter.setUseFunctionGradient(true);
        fitter.fit(hist,f);
        fitter.setUseFunctionGradient(false);
        fitter.fit(hist,f);
         
         
         
/*
        fitter.setFitMethod("uml");
        fd.connectPipes(f.variableNames(),tup,ev);
        f.normalize(true);
        fitter.fit(fd,f);
/*
        fitter.setFitMethod("chi2");
        fitter.fit(hist2d,f2);
         
        fitter.setFitMethod("uml");
        fd.connectPipes(f2.variableNames(),tup,ev);
        f2.normalize(true);
        fitter.fit(fd,f2);
         
         
         
         
         
/*
        f2.setParameter("mean",-5);
        f2.setParameter("mean1",0);
         
        String[] varNames = {f2.variableName(1),f2.variableName(0)};
        FitData fitData = new FitData();
        fitData.connectPipes(varNames,hist2d);
        fitter.fit(fitData,f2);
         
         
        hep.aida.IHistogram1D hist2 = hf.createHistogram1D("hist2","hist2",100,0,1);
         
        for ( int i = 0; i < 500; i++ )
            hist2.fill(r.nextDouble());
         
        IModelFunction fd = new FlatDistributionModel();
        fitter.fit(hist2,fd);
         */
        
    }
    
    private class FitFunction implements IFunction {
        
        private IDevFitDataIterator dataIterator;
        private IModelFunction func;
        private ArrayList varSimpleConstraint1;
        private ArrayList varSimpleConstraint2;
        
        FitFunction(IDevFitDataIterator dataIterator,IModelFunction func) {
            this.dataIterator = dataIterator;
            this.func = func;
            varSimpleConstraint1 = new ArrayList();
            varSimpleConstraint2 = new ArrayList();
        }
        
        public int dimension() { return func.numberOfParameters(); }
        
        public double value(double[] x) {
            if ( varSimpleConstraint1.size() != 0 ) applySimpleConstraint(x);
            func.setParameters( x );
            return fitMethod.evaluate(dataIterator, func);
        }
        
        public boolean providesGradient() { return func.providesParameterGradient(); }
        public String variableName(int i) { return func.parameterNames()[i]; }
        
        public String[] variableNames() { return func.parameterNames(); }
        public int numberOfParameters() { return 0; }
        
        public double[] gradient(double[] x) {
            if ( varSimpleConstraint1.size() != 0 ) applySimpleConstraint(x);
            func.setParameters( x );
            double[] result = fitMethod.evaluateGradient(dimension(), dataIterator, func);            
            return result;
        }
        
        public boolean isEqual(IFunction f) { throw new UnsupportedOperationException(); }
        public IAnnotation annotation() { throw new UnsupportedOperationException(); }
        public String codeletString() { throw new UnsupportedOperationException(); }
        public void setParameters(double[] params) { throw new UnsupportedOperationException(); }
        public double[] parameters() { throw new UnsupportedOperationException(); }
        public int indexOfParameter(String name) { throw new UnsupportedOperationException(); }
        public String[] parameterNames() { throw new UnsupportedOperationException(); }
        public void setParameter(String name, double x) { throw new UnsupportedOperationException(); }
        public double parameter(String name) { throw new UnsupportedOperationException(); }
        public void setTitle(String str) { throw new UnsupportedOperationException(); }
        public String title() { throw new UnsupportedOperationException(); }
        
        public String normalizationParameter() { throw new UnsupportedOperationException(); }
        
        protected int nFreePars() {
            int freePars = 0;
            String[] names = variableNames();
            for( int i = 0; i < names.length; i++ ) {
                IFitParameterSettings fitPar = Fitter.this.fitParameterSettings(names[i]);
                if ( ! fitPar.isFixed() ) freePars++;
            }
            return freePars;
        }
        protected int dataEntries() {
            return dataIterator.entries();
        }
        
        protected int indexOfVariable( String varName ) {
            return func.indexOfParameter( varName );
        }
        
        protected void setSimpleConstraint(String varName1, String varName2) {
            int ind1 = indexOfVariable( varName1 );
            int ind2 = indexOfVariable( varName2 );
            if ( ind1 > -1 && ind2 > -1 ) {
                varSimpleConstraint1.add( new Integer(ind1) );
                varSimpleConstraint2.add( new Integer(ind2) );
            }
        }
        
        protected boolean isValidSimpleConstraint(String varName1, String varName2 ) {
            int ind1 = indexOfVariable( varName1 );
            int ind2 = indexOfVariable( varName2 );
            if ( ind1 > -1 && ind2 > -1 ) return true;
            return false;
        }
        
        protected void applySimpleConstraint( double[] x ) {
            for ( int i = 0; i < varSimpleConstraint1.size(); i++ ) {
                int ind1 = ( (Integer) varSimpleConstraint1.get(i) ).intValue();
                int ind2 = ( (Integer) varSimpleConstraint2.get(i) ).intValue();
                x[ind1] = x[ind2];
            }
        }
        
    }
}

