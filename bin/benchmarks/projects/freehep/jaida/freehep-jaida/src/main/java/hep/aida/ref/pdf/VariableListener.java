package hep.aida.ref.pdf;

/**
 * A VariableListener.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface VariableListener {
    
    /**
     * Method invoked when the Variable's Units are about to change.
     * @param var   The Variable whose Units are changing.
     * @param units The new Units for the Variable.
     * @return      <code>true</code> if the change is allowed.
     *
     */
    public boolean variableChangingUnits(Variable var, Units units);
    
    /**
     * Method invoked when the Variable's Units have changed.
     * @param var The Variable whose Units have changed.
     *
     */
    public void variableChangedUnits(Variable var);

    /**
     * Method invoked when the Variable's value is about to change.
     * @param var   The Variable whose value is changing.
     * @param value The new value for the Variable.
     * @return      <code>true</code> if the change is allowed.
     *
     */
    public boolean variableChangingValue(Variable var, double value);
    
    /**
     * Method invoked when the Variable's value have changed.
     * @param var The Variable whose value have changed.
     *
     */
    public void variableChangedValue(Variable var);

    /**
     * Method invoked when the Variable's name is about to change.
     * @param var  The Variable whose name is changing.
     * @param name The new name for the Variable.
     * @return     <code>true</code> if the change is allowed.
     *
     */
    public boolean variableChangingName(Variable var, String name);
    
    /**
     * Method invoked when the Variable's name have changed.
     * @param var The Variable whose name has changed.
     *
     */
    public void variableChangedName(Variable var);

    /**
     * Method invoked when the Variable's range have changed.
     * @param var The Variable whose range has changed.
     *
     */
    public void variableChangedRange(Variable var);
}
