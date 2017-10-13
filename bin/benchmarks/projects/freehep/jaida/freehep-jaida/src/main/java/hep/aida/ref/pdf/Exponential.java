package hep.aida.ref.pdf;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Exponential extends Function{
    
    public static int NORMAL = 0;
    public static int DECAY = 1;
    
    private Dependent x;
    private Parameter alpha;
    
    private double alphaVal;
    private double xVal;
    private double argFact;
    private int type;

    public Exponential(String name) {
        this(name, null, null, NORMAL);
    }

    public Exponential(String name, int type) {
        this(name, null, null, type);
    }
    
    public Exponential(String name, Dependent x) {
        this(name, x, null, NORMAL);
    }

    public Exponential(String name, Dependent x, int type) {
        this(name, x, null, type);
    }

    public Exponential(String name, Dependent x, Parameter alpha) {
        this(name, x, alpha, NORMAL);
    }

    public Exponential(String name, Dependent x, Parameter alpha, int type) {
        super(name);
        this.x = x;
        this.alpha = alpha;
        this.type = type;
        initializeVariables();
    }
    
    
    private void initializeVariables() {
        if ( x == null )
            x = new Dependent("x",0,1);
        if ( alpha == null ) {
            if ( type == NORMAL )
                alpha = new Parameter("alpha");
            else
                alpha = new Parameter("tau");
        }
        VariableList list = new VariableList();
        list.add(x);
        list.add(alpha);
        addVariables(list);
    }
    
    public void variableChanged(Variable var) {
        if ( var == alpha ) {
            alphaVal = alpha.value();
            if ( type == NORMAL )
                argFact = alphaVal;
            else
                argFact = -1./alphaVal;
        } else if ( var == x )
            xVal = x.value();
        
    }
    
    public double functionValue() {
        return Math.exp( argFact*xVal );
    }

    public boolean hasAnalyticalVariableGradient(Variable var) {
        return true;
    }

    public double evaluateAnalyticalVariableGradient(Variable var) {
        if ( var == x )
            return functionValue()*argFact;
        else if ( var == alpha ) {
            double r = functionValue()*xVal;
            if ( type == NORMAL )
                return r;
            else
                return argFact*argFact*r;
        }
        return 0;
    }

    public boolean hasAnalyticalNormalization(Dependent dep) {
        if ( dep == x )
            return true;
        return false;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        double[] xMax = x.range().upperBounds();
        double[] xMin = x.range().lowerBounds();
        if ( xMax.length != 1 || xMin.length != 1 )
            throw new IllegalArgumentException("Normalization over multiple ranges is not supported for Function Gaussian.");
        double a = xMin[0];
        if ( a < 0 ) {
            System.out.println("Lower bound cannot be less than zero");
            a = 0;
        }
        double b = xMax[0];
        return Math.exp(b*argFact)/argFact - Math.exp(a*argFact)/argFact;
    }

}
