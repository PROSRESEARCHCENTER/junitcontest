/*
 * AIDAFunctionAdapter.java
 *
 * Created on February 8, 2002, 12:01 AM
 */

package hep.aida.ref.plotter.adapter;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.function.FunctionChangedEvent;
import hep.aida.ref.function.FunctionCoreListener;
import hep.aida.ref.function.FunctionDispatcher;
import hep.aida.ref.function.FunctionListener;
import jas.hist.DataSource;
import jas.hist.ExtendedStatistics;
import jas.hist.Fittable1DFunction;
import jas.hist.Fitter;
import jas.hist.FunctionValueUndefined;
import jas.hist.Handle;
import jas.hist.HasHandles;
import jas.hist.InvalidFunctionParameter;

/**
 *
 * @author Tony Johnson
 * @version $Id: AIDAFunctionAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AIDAFunctionAdapter extends Fittable1DFunction implements ExtendedStatistics, HasHandles, FunctionListener, FunctionCoreListener {

    private IFunction function;
    private double[] d = new double[1];
    private boolean notify = true;
    private String title = "function";
    
    
    static DataSource create(IFunction function) {
        if (function.dimension() == 1) return new AIDAFunctionAdapter(function);
        else throw new IllegalArgumentException("Only 1-D functions supported");
    }
    public AIDAFunctionAdapter(IFunction function) {
        this.function = function;
        if ( function instanceof FunctionDispatcher ) {
            ( (FunctionDispatcher) function ).addFunctionListener(this);
            notify = false;
        }
        if ( function instanceof BaseModelFunction )
            ( (BaseModelFunction) function ).core().addCoreListener(this);

        if ( function instanceof IManagedObject )
            title = ((IManagedObject)function).name();
    }
    public void setParameter(int index, double value) throws jas.hist.InvalidFunctionParameter {
        function.setParameter(function.parameterNames()[index], value);
        if ( notify )
            functionChanged( new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    public String[] getParameterNames() {
        return function.parameterNames();
    }
    public double[] getParameterValues() {
        return function.parameters();
    }
    public double valueAt(double x) throws FunctionValueUndefined {
        d[0] = x;
        double result = function.value(d);
        if (Double.isNaN(result)) throw new FunctionValueUndefined();
        return result;
    }
    public double valueAt(double x, double[] values) throws FunctionValueUndefined {
        for ( int i = 0; i < values.length; i++ ) {
            try {
                setParameter(i, values[i]);
            } catch ( jas.hist.InvalidFunctionParameter ifp ) {
                throw new FunctionValueUndefined();
            }
        }
        return valueAt(x);
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if ( function instanceof ManagedObject )
            ((ManagedObject)function).setName(title);
        this.title = title;
    }

    public void setFit(Fitter fitter, double[] values) throws InvalidFunctionParameter {
        for ( int i = 0; i < values.length; i++ ) {
            try {
                setParameter(i, values[i]);
            } catch ( jas.hist.InvalidFunctionParameter ifp ) {
                throw new InvalidFunctionParameter(getParameterNames()[i]);
            }
        }
        
        setFit(fitter);
        setChanged();
    }
    
    public IFunction function() {
        return function;
    }
    
    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh) {
        Handle[] handles;
        if ( ! ( function instanceof BaseModelFunction ) )
            handles = new Handle[0];
        else {
            BaseModelFunction modelFunction = (BaseModelFunction) function;
            handles = modelFunction.core().getHandles(xLow, xHigh, yLow, yHigh);
        }
        if (handles == null)
            handles = new Handle[0];
        return handles;
    }    
    
    public void functionChanged(FunctionChangedEvent event) {
        int eventId = event.eventId();
        if ( eventId == FunctionChangedEvent.PARAMETER_VALUE_CHANGED || eventId == FunctionChangedEvent.RANGE_CHANGED )
            clearFit();
        setChanged();
    }
    
    public void coreChanged() {
        clearFit();
        setChanged();
    }
    
}