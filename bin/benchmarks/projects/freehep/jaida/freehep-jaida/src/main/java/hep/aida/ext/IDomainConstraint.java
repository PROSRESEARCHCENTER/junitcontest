package hep.aida.ext;

/*
 *
 *  User level interface to the domain of constraints.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */


public interface IDomainConstraint {
    
    /**
     * Add a new constraint.
     * @param constraint The constraint to be added.
     * @return <code>true</code> if the constraint was added succesfully.
     *         <code>false</code> otherwise.
     */
    boolean addConstraint(IConstraint constraint);
    
    /**
     * Remove a constraint.
     * @param constraint The constraint to be removed.
     * @return <code>true</code> if the constraint was removed succesfully.
     *         <code>false</code> otherwise.
     */
    boolean removeConstraint(IConstraint constraint);
    
    /**
     * The constraints currently in the domain.
     * @return The IConstraint[] array of constraints.
     *
     */ 
    IConstraint[] constraints();
    
    /**
     * The number of constraints currently in the domain.
     * @return The number of constraints.
     *
     */
    int nConstraints();
    
    /**
     * Remove all the constraints.
     *
     */
    void reset();
    
}
