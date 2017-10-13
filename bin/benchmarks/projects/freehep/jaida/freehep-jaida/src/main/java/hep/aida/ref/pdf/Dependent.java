package hep.aida.ref.pdf;

import hep.aida.IFitData;
import hep.aida.ref.fitter.fitdata.FitData;

/**
 * A Dependent. Dependents take their values from a data set.
 *
 * @author The FreeHEP team @ SLAC.
 */
public class Dependent extends Variable implements RangeSetListener {
    
    private double value;
    private RangeSet range;
    private FitData data = null;
    
    public Dependent(String name, double lowerEdge, double upperEdge) {
        super(name, Variable.DEPENDENT);
        range = new RangeSet(lowerEdge, upperEdge);
    }
    
    public double value() {
        return value;
    }

    protected void setVariableValue(double value) {
        if ( range.isInRange(value) )
            this.value = value;
        else
            throw new IllegalArgumentException("Value "+value+" is out of the current range.");
    }
        
    public RangeSet range() {
        return range;
    }
    
    public void rangeSetChanged() {
        notifyVariableChanged(Variable.RANGE_CHANGED);
    }
    
    public void connectToData(Object dataObject) {
        if ( dataObject instanceof FitData )
            this.data = (FitData) dataObject;
        else {
            data = new FitData();
            data.create1DConnection(dataObject);
        }
    }
    
    public boolean isConnected() {
        return data != null;
    }
    
    public IFitData data() {
        return data;
    }
}
