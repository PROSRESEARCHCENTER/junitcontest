package hep.aida.util.comparison;

import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.ext.IComparisonAlgorithm;
import hep.aida.ext.IComparisonData;
import hep.aida.ext.IComparisonResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public abstract class StatisticalComparison {
    
    private static Hashtable hash = new Hashtable();
    private static ArrayList algorithmList = new ArrayList();
    
    public static boolean canCompare(IHistogram1D h1, IHistogram1D h2, String algorithmName) {
        IComparisonData d1 = ComparisonDataConverter.comparisonData(h1, null);
        IComparisonData d2 = ComparisonDataConverter.comparisonData(h2, null);
        return canCompare(d1, d2, algorithmName);
    }

    public static IComparisonResult compare(IHistogram1D h1, IHistogram1D h2) {
        return compare(h1,h2,null);
    }
    
    public static IComparisonResult compare(IHistogram1D h1, IHistogram1D h2, String algorithmName) {
        return compare(h1, h2, algorithmName, null);
    }
    
    public static IComparisonResult compare(IHistogram1D h1, IHistogram1D h2, String algorithmName, String testOptions) {
        IComparisonData d1 = ComparisonDataConverter.comparisonData(h1, testOptions);
        IComparisonData d2 = ComparisonDataConverter.comparisonData(h2, testOptions);
        return compare(d1, d2, algorithmName, testOptions);
    }
    
    public static boolean canCompare(ICloud1D c1, ICloud1D c2, String algorithmName) {
        IComparisonData d1 = ComparisonDataConverter.comparisonData(c1, null);
        IComparisonData d2 = ComparisonDataConverter.comparisonData(c2, null);
        return canCompare(d1, d2, algorithmName);
    }

    public static IComparisonResult compare(ICloud1D c1, ICloud1D c2) {
        return compare(c1,c2,null);
    }
    
    public static IComparisonResult compare(ICloud1D c1, ICloud1D c2, String algorithmName) {
        return compare(c1, c2, algorithmName, null);
    }
    
    public static IComparisonResult compare(ICloud1D c1, ICloud1D c2, String algorithmName, String testOptions) {
        IComparisonData d1 = ComparisonDataConverter.comparisonData(c1, testOptions);
        IComparisonData d2 = ComparisonDataConverter.comparisonData(c2, testOptions);
        return compare(d1, d2, algorithmName, testOptions);
    }
    
    public static IComparisonResult compare(IComparisonData d1, IComparisonData d2, String algorithmName, String testOptions) {
        if ( algorithmName == null )
            algorithmName = "AndersonDarling";
        IComparisonAlgorithm comparisonAlgorithm = comparisonAlgorithm(algorithmName);
        return comparisonAlgorithm.compare(d1, d2, testOptions);
    }
    
    public static boolean canCompare(IComparisonData d1, IComparisonData d2, String algorithmName) {
        if ( algorithmName == null )
            algorithmName = "AndersonDarling";
        IComparisonAlgorithm comparisonAlgorithm = comparisonAlgorithm(algorithmName);
        return comparisonAlgorithm.canCompare(d1, d2);
    }

    public static int numberOfAvailableComparisonAlgorithm() {
        loadAlgorithms();
        return algorithmList.size();
    }
    
    public static IComparisonAlgorithm comparisonAlgorithm(int index) {
        if ( index < 0 )
            throw new IllegalArgumentException("Invalid index "+index+". Must non-negative.");
        if ( algorithmList.size() <= index )
            loadAlgorithms();
        if ( algorithmList.size() <= index )
            throw new IllegalArgumentException("Illegal index "+index+". There are only "+algorithmList.size()+" registered algorithms.");
        return (IComparisonAlgorithm) algorithmList.get(index);
        
    }
    
    public static IComparisonAlgorithm comparisonAlgorithm(String algorithmName) {
        String name = algorithmName.toLowerCase();
        Object obj = hash.get(name);
        if ( obj == null ) {
            loadAlgorithms();
            obj = hash.get(name);
        }
        if (obj == null) throw new IllegalArgumentException("Cannot create the IComparisonAlgorithm: "+algorithmName);
        return (IComparisonAlgorithm)obj;
    }
    
    private static void registerComparisonAlgorithm(String name, IComparisonAlgorithm algorithm) {
        if (hash.containsKey(name))
            throw new IllegalArgumentException("Algorithm with name "+name+" already exists. Please provide a different name.");
        hash.put(name, algorithm);
        if ( ! algorithmList.contains(algorithm) )
            algorithmList.add(algorithm);
    }
    
    private static boolean isAlgorithmRegistered(IComparisonAlgorithm algorithm) {
        return hash.containsValue(algorithm);
    }
        
    private static void loadAlgorithms() {
        Lookup.Template template = new Lookup.Template(IComparisonAlgorithm.class);
        Lookup.Result result = FreeHEPLookup.instance().lookup(template);
        Collection c = result.allInstances();
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            IComparisonAlgorithm tmpComparisonAlgorithm = (IComparisonAlgorithm)i.next();
            if ( ! isAlgorithmRegistered(tmpComparisonAlgorithm) ) {
                String[] names = tmpComparisonAlgorithm.algorithmNames();
                for ( int j = 0; j < names.length; j++ )
                    registerComparisonAlgorithm(names[j].toLowerCase(), tmpComparisonAlgorithm);
            }
        }
    }   
    
}
