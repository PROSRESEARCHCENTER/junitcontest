package hep.aida.ext;

import hep.aida.IDataPointSet;
import hep.aida.IFitData;
import hep.aida.IFitResult;
import hep.aida.IFunction;
import hep.aida.dev.IDevFitter;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface IExtFitter extends IDevFitter {
    
    public IFitResult fit(IFitData d, IFunction originalFunction, String range, Object correlationObject);

    public IFitResult fit(IFitData d, String model, double[] initialParameters, String range, Object correlationObject);

    public IFitResult fit(IFitData d, String model, String range, Object correlationObject);

    public IFitResult fit(IDataPointSet dataPointSet, IFunction f, String range, Object correlationObject);

    public IFitResult fit(IDataPointSet dataPointSet, String model, double[] initialParameters, String range, Object correlationObject);
 
    public IFitResult fit(IDataPointSet dataPointSet, String model, String range, Object correlationObject);

}
