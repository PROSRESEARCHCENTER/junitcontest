package hep.aida.ref.tuple;
import gnu.jel.CompiledExpression;
import hep.aida.ITuple;
import hep.aida.ref.jel.JELLibraryFactory;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
class JELExpression {

    private String expression;
    private Object[] context;
    private CompiledExpression compExpression;
    
    /** 
     * Default contructor.
     * @param expression The expression to evaluate.
     *
     */   
    protected JELExpression(String expression) {
        this.expression = expression;
    }

    protected String expression() {
        return expression;
    }

    protected double evaluateDouble() {
        try {
            return compExpression.evaluate_double(context);
	} catch (Throwable t) {
	    throw new RuntimeException("Runtime JEL Evaluation Problems!", t);
	}
    }
            
    protected boolean evaluateBoolean() {
        try {
            return compExpression.evaluate_boolean(context);
	} catch (Throwable t) {
	    throw new RuntimeException("Runtime JEL Evaluation Problems!", t);
	}
    }
    
    protected void compile(Class type, ITuple tuple) {
	JELTupleProvider tupleProvider = new JELTupleProvider(tuple);
	context = new Object[]{tupleProvider};        
	compExpression = JELLibraryFactory.compile(tupleProvider, tupleProvider.getClass(), expression, type);
    }
}
