package hep.aida.ref.histogram;

/**
 * Implementation of IBaseHistogram.
 *
 * @author The AIDA Team at SLAC.
 *
 */
import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IBaseHistogram;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractBaseHistogram extends ManagedObject implements IBaseHistogram {
    
    private int dimension;
    private IAnnotation annotation;
    
    // Allowed keys that can be set in the annotation from options
    static String[] styleKeys = {"xaxislabel", "xaxisscale", "xaxistype", "yaxislabel", "yaxisscale", "yaxistype"};
    
    /**
     * Utility method to map the bin number from the external representation (from -2 to nBins-1 where -2 is the overflow and -1 is the underflow)
     * to the internal one (from 0 to nBins+1 where 0 is the underflow and nBins+1 if the overflow bin)
     * @param index The bin number in the external representation.
     * @param axis  The axis to which the bin belongs to.
     * @return The bin number in the internal representation.
     *
     */
    protected int mapBinNumber(int index, IAxis axis) {
        int bins = axis.bins()+2;
        if (index >= bins) throw new IllegalArgumentException("bin="+index);
        if (index >= 0) return index+1;
        if (index == IAxis.UNDERFLOW_BIN) return 0;
        if (index == IAxis.OVERFLOW_BIN) return bins-1;
        throw new IllegalArgumentException("bin="+index);
    }

    
    /**
     * Creates a new instance of BaseHistogram.
     * @param name The name of the BaseHistogram. See ManagedObject for details.
     * @param title The title of the BaseHistogram.
     * @param dimension The dimension of the BaseHistogram.
     *
     */
    public AbstractBaseHistogram(String name, String title, int dimension) {
        this(name, title, dimension, null);
    }
    public AbstractBaseHistogram(String name, String title, int dimension, String options) {
        super(name);
        this.dimension = dimension;
        annotation = new Annotation();
        annotation.addItem(Annotation.titleKey,title,true);
        setOptions(options);
    }
    
    private void setOptions(String options) {
        if (options == null || options.trim().equals("")) return;
        Map optionMap = AidaUtils.parseOptions( options );
        if (optionMap.isEmpty()) return;
        Iterator it = optionMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if ( key.toLowerCase().startsWith("annotation.") ||
                 key.toLowerCase().startsWith("annotation:") ) {
                String annVal = (String) optionMap.get(key);
                String annKey = key.substring(11);
                if ( annVal != null && !annVal.trim().equals("") &&
                     annKey != null && !annKey.trim().equals("") ) 
                    annotation.addItem(annKey, annVal, true);
            }
        }
    }
    
    
    /**
     * Get the histogram title.
     * @return the Histogram title.
     *
     */
    public String title() {
        String title =  annotation.value(Annotation.titleKey);
        if ( title == null )
            title = "";
        return title;
    }
    
    /**
     * Set the histogram title.
     * @param title The title.
     *
     */
    public void setTitle(String title) {
        if ( title == null )
            title = "";
        annotation.setValue(Annotation.titleKey,title);
        if (isValid) fireStateChanged();
    }
    
    /**
     * Get the IAnnotation associated with the histogram.
     * @return The IAnnotation.
     *
     */
    public IAnnotation annotation() {
        return annotation;
    }
    
    public void setAnnotation( IAnnotation annotation ) {
        this.annotation = annotation;
    }
    
    /**
     * Get the dimension of the histogram.
     *
     */
    public int dimension() {
        return dimension;
    }
    
    /**
     * Reset the histogram; as if just created.
     *
     */
    public void reset() {
//        annotation.reset();
    }
    
    /**
     * Number of in-range entries in the histogram.
     * @return The number of in-range entries.
     *
     */
    abstract public int entries();
    
    /**
     * Number of entries whose coordinate or weight is NaN. Such entries
     * are counted as allEntries but don't contribute to the statistics.
     * @return The number of entries whose value or weight is NaN.
     *
     */
    abstract public int nanEntries();
    
}
