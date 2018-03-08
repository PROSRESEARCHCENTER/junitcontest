package hep.aida.ext;

/**
 *
 *  User level interface to the variable
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
import hep.aida.IEvaluator;
import hep.aida.ITuple;

public interface IVariable

{

    /**
     * Get label for this IVariable.
     * @return label.
     */
    public String label();

    /**
     * Get name for this IVariable.
     * @return name.
     */
    public String name();

    /**
     * Set value for the IVariable.
     * @param value Value of IVariable.
     * @return <code> true</code> if the value was set succesfully, <code>false</code> otherwise, for example if
     *         the IVariable is bounded and value is out of range.
     *
     */
    public boolean setValue(double value);

    /**
     * Get current value of the IVariable.
     * @return Current value of the IVariable.
     */
    public double value();

    /**
     * Get current error of the IVariable.
     * @return Current error of the IVariable.
     */
    public double error();

    /**
     * Set new range for the IVariable. All previous ranges are
     * discarded first.
     * @param lower Lower edge of the valid range.
     * @param upper Upper edge of the valid range.
     */
    public void setRange(double lower, double upper);

    /**
     * Add a new range to the existing range set for the IVariable.
     * @param lower Lower edge of the valid range.
     * @param upper Upper edge of the valid range.
     */
    public void addRange(double lower, double upper);

    /**
     * Check if current value of the IVariable is in the valid range set.
     * @return <code>true</code> if current value of the IVariable is in the valid range set.
     */
    public boolean isInRange();

    /**
     * Check if provided value is in the IVariable valid range set.
     * @param value Value to be checked
     * @return <code>true</code> if provided value is in the IVariable valid range set.
     */
    public boolean isInRange(double value);

 
    /**
     * Set IVariable to represent variable or parameter.
     * @param state <code>true</code> for variable, <code>false</code> for parameter
     */
     public void setDependent(boolean state); 

    /**
     * Check if the IVariable represents variable or parameter.
     * @return <code>true</code> if this is variable, <code>false</code> if this is parameter
     */
    public boolean isDependent();

    /**
     * Set initial step for fitting.
     * @param step Initial step for fitting.
     */
    public void setStep(double step);

    /**
     * Set how the IVariable can be used in fitting.
     * @param state <code>true</code> for fixed parameter, <code>false</code> for variable parameter.
     */
    public void setFixed(boolean state);

    /**
     * Get how the IVariable can be used in fitting.
     * @return <code>true</code> for fixed parameter, <code>false</code> for variable parameter.
     */
    public boolean isFixed();

    /**
     * Set how fitter should treat bounds for the IVariable. 
     * @param state <code>true</code> use bounds, <code>false</code> don't use bounds.
     */
    public void setUseBounds(boolean state); 

    /**
     * Get how fitter should treat bounds for the IVariable.
     * @return <code>true</code> use bounds, <code>false</code> don't use bounds.
     */
    public boolean useBounds();

 
    /*
     * IVariable can be connected to ITuple to derive its value
     * from the current ITuple row. Connect the IVariable to ITuple.
     * @param data ITuple to connect the IVariable to.
     */
    public void connect(ITuple data);

    /*
     * Connect the IVariable to evaluator.
     * @param data IEvaluator to connect the IVariable to.
     */
    public void connect(IEvaluator ev);

    /**
     * Check if the IVariable is connected.
     * @return <code>true</code> if IVariable is connected, <code>false</code> if IVariable is not connected.
     */
    public boolean isConnected();

    /*
     * Get ITuple that IVariable is connected to.
     * @return ITuple that IVariable is connected to.
     */
    public ITuple connection();

    /**
     * Set units. Units can be used to annotate plot axis
     * @param units String that describes units
     *         
     */
   public void setUnits(String units);

    /**
     * Get units. Units can be used to annotate plot axis
     * @return String that describes units
     */
   public String units();
}

