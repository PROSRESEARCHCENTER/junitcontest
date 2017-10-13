package hep.aida.ref.tuple;
import hep.aida.ITuple;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class Filter implements hep.aida.IFilter {

    private int rowToProcess;
    private int startingRow;
    private boolean useRows;
    private JELExpression jelExpression;
    private ITuple tuple;

    /** 
     * Default contructor.
     * @param expression The expression to evaluate on the tuple.
     *
     */   
    public Filter( String expression ) {
        this.jelExpression = new JELExpression(expression);
        this.rowToProcess = -1;
	this.startingRow = -1;
	useRows = false;
    }

    public Filter( String expression, int rowToProcess) {
        this.jelExpression = new JELExpression(expression);
	if (rowToProcess<0)
	    throw new IllegalArgumentException("Row parameter can not be negative: rowToProcess="+rowToProcess);
	this.rowToProcess = rowToProcess;
	this.startingRow = 0;
	useRows = false;
    }

    public Filter( String expression, int rowToProcess, int startingRow ) {
        this.jelExpression = new JELExpression(expression);
	if (rowToProcess<0 || startingRow<0)
	    throw new IllegalArgumentException("Row parameters can not be negative: rowToProcess="+
					       rowToProcess+", startingRow="+startingRow);
	this.rowToProcess = rowToProcess;
	this.startingRow = startingRow;
	useRows = false;
    }
    
    public String expression() {
        return jelExpression.expression();
    }

    public void initialize( ITuple tuple ) {
        this.tuple = tuple;

	if (tuple instanceof hep.aida.ref.tuple.Tuple) useRows = true;
	else useRows = false;

	if (rowToProcess<0 && startingRow<0) useRows = false;

	jelExpression.compile(Boolean.TYPE,tuple);
    }

    public boolean accept() {
	if (useRows && startingRow>((Tuple) tuple).getRow()) return false;
	if (useRows && (startingRow+rowToProcess)<=((Tuple) tuple).getRow()) return false;
        return jelExpression.evaluateBoolean();
    }
}
