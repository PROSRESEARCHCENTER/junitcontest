package hep.aida.ref.fitter;

import hep.aida.IFitFactory;
import hep.aida.IFitter;
import hep.aida.ext.IFitMethod;
import hep.aida.ext.IOptimizerFactory;
import hep.aida.ref.fitter.fitdata.FitData;

import java.util.Collection;
import java.util.Iterator;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 * @author The AIDA team @ SLAC.
 *
 */

public class FitFactory implements IFitFactory {
    
    public hep.aida.IFitData createFitData()  {
        return new FitData();
    }
        
    public IFitter createFitter() throws IllegalArgumentException {
        return createFitter(null);
    }
    public IFitter createFitter(String fitterType) throws IllegalArgumentException {
        return createFitter(fitterType,null);
    }
    public IFitter createFitter(String fitterType, String engineType) throws IllegalArgumentException {
        return createFitter(fitterType,engineType,null);
    }
    public IFitter createFitter(String fitterType, String engineType, String options) throws IllegalArgumentException {
        return new Fitter(fitterType,engineType,options);
    }

    /**
     * Get the list the fit methods provided by the used implementation (e.g. "chi2", "unbinnedMaximumLikelihood" etc).
     * @return An array containing the list of the available fit methods.
     *
     */
    public String[] availableFitMethods() {
        Lookup.Template template = new Lookup.Template(IFitMethod.class);
        Lookup.Result result = FreeHEPLookup.instance().lookup(template);
        Collection c = result.allInstances();
        String[] r = new String[ c.size() ];
        int count = 0;
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            IFitMethod fm = (IFitMethod)i.next();
            r[count++] = fm.fitMethodNames()[0];
        }
        return r;
    }
    
    /**
     * Get the list the fit engines provided by the used implementation (e.g. "minuit", etc).
     * @return An array containing the list of the available fit engines.
     *
     */
    public String[] availableFitEngines() {
        Lookup.Template template = new Lookup.Template(IOptimizerFactory.class);
        Lookup.Result result = FreeHEPLookup.instance().lookup(template);
        Collection c = result.allInstances();
        String[] r = new String[ c.size() ];
        int count = 0;
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            IOptimizerFactory of = (IOptimizerFactory)i.next();
            r[count++] = of.optimizerFactoryNames()[0];
        }
        return r;
    }
}

