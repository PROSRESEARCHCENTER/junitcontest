package hep.aida.ext;


/** 
 * IVariableSettings contains the settings for a give variable.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */

public interface IVariableSettings {


    /**
     * Get the name of the variable to which settings apply.
     * @return The name.
     *
     */
    public String name();

    /**
     * The step size for this variable.
     * @return the step size.
     *
     */
    public double stepSize();

    /**
     * Get the upper bound.
     * @return The upper bound.
     *
     */
    public double upperBound();

    /**
     * Get the lower bound.
     * @return The lower bound.
     *
     */    
    public double lowerBound();

    /** 
     * Check if a variable is bounded. A variable is bounded if
     * either the upper bound or the lower bound are not infinity.
     * @return <code>true</code> if the variable is bounded.
     *
     */
    public boolean isBound();

    /**
     * Check if a variable is fixed.
     * @return <code>true</code> if the variable is fixed.
     *
     */
    public boolean isFixed();

    /**
     * Set the step size for this variable.
     * @param step The step size.
     * @throws     IllegalArgumentException if the step is not positive.
     *
     */
    public void setStepSize(double step) throws IllegalArgumentException;

    /**
     * Set the bounds for the variable. 
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     * @throws           IllegalArgumentException if the lowerBound is not lower than the upperBound.
     *
     */
    public void setBounds(double lowerBound, double upperBound) throws IllegalArgumentException;
    
    /**
     * Reset the bounds.
     *
     */
    public void removeBounds();

    /**
     * Set the fixed/unfixed status of a variable.
     * @param isFixed The fixed status of a variable.
     *
     */
    public void setFixed(boolean isFixed);
    
    /**
     * Reset the IVariableSetting ot its defaults.
     *
     */
    public void reset();
    
    /**
     * Set the current value of this variable.
     * @param value The current value.
     *
     */
    public void setValue(double value);

    /**
     * The current value for this variable.
     * @return The current value.
     *
     */
    public double value();
    
} 

