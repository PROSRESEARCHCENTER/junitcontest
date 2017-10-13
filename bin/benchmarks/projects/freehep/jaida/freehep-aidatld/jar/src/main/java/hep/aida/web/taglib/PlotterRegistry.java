package hep.aida.web.taglib;

import hep.aida.IPlotter;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author The AIDA team @ SLAC.
 *
 */
public class PlotterRegistry {

    private Hashtable plotterHash = new Hashtable();
    private int maxCapacity = 10;
    private int plotterIndex = 0;
    private String name = "plot";
    
    public static String REGISTRY_SESSION_NAME = "plotterRegistrySessionName";    
    
    public PlotterRegistry(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public String addPlotter(IPlotter plotter) {
        
        String plotterName = name + "-" + String.valueOf(plotterIndex);
        String oldPlotterName = name + "-" + String.valueOf(plotterIndex-maxCapacity);
        
        plotterHash.put(plotterName, plotter);
        clearPlotter(oldPlotterName);
        plotterHash.remove(oldPlotterName); 
        
        plotterIndex++;        
        return plotterName;
    }
    
    public IPlotter plotter(String plotterName) {        
        Object obj =  plotterHash.get(plotterName);
        if ( obj != null )
            return (IPlotter) obj;
        return null;
    }
    
    public void clearPlotter(String plotterName) {
        IPlotter p = plotter(plotterName);
        if ( p != null )
            p.clearRegions();
    }

    public String printPlotRegistry() {
        String output = "This is the plotter registry: "+this+"\n";
        output +="It contains the following plots: \n";
        Enumeration e = plotterHash.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            output += " "+key+" --> "+plotterHash.get(key);
        }
        return output;
    }
    
    public void clear() {
        Enumeration keys = plotterHash.keys();
        while(keys.hasMoreElements()) {
            String plotterName = (String) keys.nextElement();
            clearPlotter(plotterName);
        }
        
        plotterHash.clear();
    }
    

    
}