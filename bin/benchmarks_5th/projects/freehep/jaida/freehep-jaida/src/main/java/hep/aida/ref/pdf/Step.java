package hep.aida.ref.pdf;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class Step extends Function {
    
    private static int INCLUDE_BOUNDS = 0;
    private static int EXCLUDE_BOUNDS = 1;
    private static int INCLUDE_UPPER_BOUND = 2;
    private static int INCLUDE_LOWER_BOUND = 2;
    
    private Dependent x;
    private Parameter a;
    private Parameter b;
    
    private double aVal;
    private double bVal;
    private double xVal;
    
    private int type;
    
    public Step(String name) {
        this(name, INCLUDE_BOUNDS);
    }
    
    public Step(String name, int type) {
        this(name, type, null);
    }

    public Step(String name, Dependent x) {
        this(name, INCLUDE_BOUNDS, x, null, null);
    }
    
    public Step(String name, int type, Dependent x) {
        this(name, type, x, null, null);
    }
    
    public Step(String name, Dependent x, Parameter a, Parameter b) {
        this(name, INCLUDE_BOUNDS, x, a, b);
    }

    public Step(String name, int type, Dependent x, Parameter a, Parameter b) {
        super(name);
        this.x = x;
        this.a = a;
        this.b = b;
        this.type = type;
        initializeVariables();
    }
    
    private void initializeVariables() {
        if ( x == null )
            x = new Dependent("x", -10, 10);
        if ( a == null )
            a = new Parameter("a");
        if ( b == null )
            b = new Parameter("b");
        VariableList list = new VariableList();
        list.add(x);
        list.add(a);
        list.add(b);
        addVariables(list);
    }
    
    public void variableChanged(Variable var) {
        if ( var == a )
            aVal = a.value();
        else if ( var == b ) {
            bVal = b.value();
        }
        else if ( var == x )
            xVal = x.value();
        if ( bVal < aVal )
            System.out.println("The value of parameter "+b.name()+" is less than paramter's "+a.name()+" "+bVal+" "+aVal);
    }

    public double functionValue() {
        double r = 0;
        if ( xVal < bVal && xVal > aVal )
            r = 1;
        else if ( xVal == aVal && ( type == INCLUDE_BOUNDS || type == INCLUDE_LOWER_BOUND ) )
            r = 1;
        else if ( xVal == bVal && ( type == INCLUDE_BOUNDS || type == INCLUDE_UPPER_BOUND ) )
            r = 1;
        return r;
    }
    
    public boolean hasAnalyticalVariableGradient(Variable var) {
        return true;
    }

    public double evaluateAnalyticalVariableGradient(Variable var) {
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
            throw new IllegalArgumentException("Normalization over multiple ranges is not supported for Function Step.");
        double le = xMin[0] < aVal ? aVal : xMin[0];
        double ue = xMax[0] < bVal ? xMax[0] : bVal;
        return ue - le;
        
        
    }
}
