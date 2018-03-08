/*
 * Evaluator.java
 *
 * Created on February 4, 2002, 6:54 PM
 */

package hep.aida.ref.tuple;
import hep.aida.ITuple;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class Evaluator implements hep.aida.IEvaluator {

    protected ITuple tuple;
    private JELExpression jelExpression;
    
    /** 
     * Default contructor.
     * @param expression The expression to evaluate on the tuple.
     *
     */   
    public Evaluator( String expression ) {
        this.jelExpression = new JELExpression( expression );
    }

    public String expression() {
        return jelExpression.expression();
    }
    
    public void initialize( ITuple tuple ) {
	jelExpression.compile(Double.TYPE, tuple);
    }
    
    public double evaluateDouble() {
	return jelExpression.evaluateDouble();
    }    
}
