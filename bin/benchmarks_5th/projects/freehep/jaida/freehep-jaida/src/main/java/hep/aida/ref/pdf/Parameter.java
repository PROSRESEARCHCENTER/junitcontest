package hep.aida.ref.pdf;

/**
 * A Parameter. Parameter's values are changed either by the user or by
 * the Fitter during the fit procedure.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Parameter extends Variable {
    
    private double value;
    private Range range;
    private double stepSize = Double.NaN;
    
    private boolean isFixed = false;
    private boolean useBounds = false;
    
    public Parameter(String name) {
        this( name, Double.NaN );
    }
    
    public Parameter(String name, double value) {
        this( name, value, Double.NaN);
    }

    public Parameter(String name, double value, boolean isFixed) {
        this( name, value, Double.NaN, isFixed );
        
    }

    public Parameter(String name, double value, double lowerBound, double upperBound) {
        this( name, value, Double.NaN, lowerBound, upperBound);
    }
            
    public Parameter(String name, double value, double stepSize) {
        this( name, value, stepSize, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Parameter(String name, double value, double stepSize, boolean isFixed) {
        this( name, value, stepSize, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, isFixed);        
    }

    public Parameter(String name, double value, double stepSize, double lowerBound, double upperBound) {
        this( name, value, stepSize, lowerBound, upperBound, false);
    }
    
    public Parameter(String name, double value, double stepSize, double lowerBound, double upperBound, boolean isFixed) {
        super(name, Variable.PARAMETER);
        range = new Range(lowerBound, upperBound);
        setVariableValue(value);
        setStepSize(stepSize);
        setFixed(isFixed());
    }
    
    public double value() {
        return value;
    }

    protected void setVariableValue(double value) {
        if ( isFixed() )
            throw new RuntimeException("Cannot change parameter "+name()+" value; it is fixed.");
        if ( useBounds() )
            if ( ! range.isInRange(value) )
                throw new IllegalArgumentException("Value "+value+" is outside the range "+range.lowerBound()+" - "+range.upperBound()+" for parameter "+name());
        this.value = value;
    }
        
    public void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }
    
    public boolean isFixed() {
        return isFixed;
    }
    
    public void setUseBounds(boolean useBounds) {
        this.useBounds = useBounds;
    }
    
    public boolean useBounds() {
        return useBounds;
    }
    
    public double stepSize() {
        if (Double.isNaN(stepSize)) {
            if ( ( ! Double.isInfinite(range.upperBound()) ) && ( ! Double.isInfinite(range.lowerBound() ) ) )
                return ( range.upperBound() - range.lowerBound() )/2;
        }
        return stepSize;
    }
    
    public void setStepSize(double stepSize) {
        if ( stepSize < 0 )
            throw new IllegalArgumentException("Cannot set negative step size "+stepSize);
        if ( stepSize == 0 )
            throw new IllegalArgumentException("Setting step size to 0 is equivalent to fixing the parameter. Please use the setFixed(boolean) method instead.");
        this.stepSize = stepSize;
    }
    
    public double upperBound() {
        return range.upperBound();
    }
    
    public double lowerBound() {
        return range.lowerBound();
    }
    
    public void setUpperBound(double upperBound) {
        range.setUpperBound(upperBound);
    }

    public void setLowerBound(double lowerBound) {
        range.setLowerBound(lowerBound);
    }
    
    public void setBounds(double lowerBound, double upperBound) {
        range.setUpperBound(upperBound);
        range.setLowerBound(lowerBound);        
    }
}
