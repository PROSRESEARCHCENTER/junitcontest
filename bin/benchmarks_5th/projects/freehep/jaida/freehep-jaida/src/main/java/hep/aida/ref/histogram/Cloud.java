package hep.aida.ref.histogram;
import hep.aida.ICloud;
import hep.aida.IHistogram;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;

import java.util.Map;

public abstract class Cloud extends AbstractBaseHistogram implements ICloud, IsObservable {
    
    /**
     * Create a Cloud.
     * @param name The name of the Cloud as a ManagedObject.
     * @param title The title of the Cloud.
     * @param dimension The Cloud's dimension.
     * @param maxEntries The maximum number of entries after which the Cloud will convert to a Histogram.
     *                  If maxEntries is negative the Cloud will not convert automatically.
     * @param options A String containing options (e.g. "autoconvert=false")
     *
     */
    protected Cloud(String name, String title, int dimension, int maxEntries, String options) {
        super(name, title, dimension, options);
        initCloud(maxEntries,options);
    }
    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }
    /**
     * Reset the histogram; as if just created.
     *
     */
    public void reset() {
        super.reset();
        validEntries = 0;
        if (isValid) fireStateChanged();
    }

    /**
     * Get the sum of weights of of all the entries
     * @return The sum of the weights of all the entries.
     *
     */
    abstract public double sumOfWeights();

    /**
     * Convert the ICloud to an IHistogram using the default number of bins.
     *
     */
    abstract public void convertToHistogram();
    
    /**
     * Check if the ICloud has been converted to an IHistogram.
     * @return <code>true</code> if it has been converted.
     *
     */
    abstract public boolean isConverted();
    
    /**
     * Scale the weights by a given factor.
     * @param scaleFactor The scale factor.
     *
     */
    abstract public void scale(double scaleFactor);

    /**
     *
     * All the non-AIDA methods should go below this.
     *
     */
    
    /**
     * Check if the Cloud is set to convert to an Histogram automatically.
     * @return <code>true</code> if it will convert automatically, <code>false</code> otherwise.
     *
     */
    protected boolean autoConvert() {
        return autoConvert;
    }
    
    /**
     * Get the maximum number of entries after which the Cloud will convert
     * to an Histogram.
     * @return The maximum number of entries.
     *
     */
    public int maxEntries() {
        return maxEntries;
    }
    
    /**
     * Get the options with which the Cloud was created.
     * @return The String of options.
     *
     */
    public String getOptions() {
        return options;
    }
    
    public void initCloud(int maxEntries, String options) {
        this.maxEntries = maxEntries;
        if (options != null) this.options = options;
        
        Map optionMap = hep.aida.ref.AidaUtils.parseOptions( options );
        String autoconv = (String) optionMap.get("autoconvert");
        if ( autoconv == null ) autoConvert = true;
        else autoConvert = Boolean.valueOf(autoconv).booleanValue();
        if ( maxEntries < 0 ) autoConvert = false;
        
        if ( maxEntries <= 0 ) arraySize = Cloud.CLOUD_ARRAY_ENTRIES;
        else arraySize = maxEntries > Cloud.CLOUD_ARRAY_ENTRIES ? Cloud.CLOUD_ARRAY_ENTRIES : maxEntries;
        
        String marginStr = (String) optionMap.get("margin");
        if ( marginStr != null ) {
            try {
                double tmpMargin = Double.valueOf(marginStr).doubleValue();
                if ( tmpMargin < 0 ) throw new IllegalArgumentException("Illegal margin "+tmpMargin+"; it cannot be negative");
                margin = tmpMargin;
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Illegal value for margin option "+marginStr+". It has to be a positive double.");
            }
        }
    }
    
    protected double margin() {
        return margin;
    }
    
    protected abstract IHistogram hist();
    
    public int nanEntries() {
        if ( isConverted() )
            return hist().nanEntries();
        return entries() - validEntries;
    }
    
    protected int arraySize;
    protected int maxEntries;
    private boolean autoConvert;
    public static final int CLOUD_ARRAY_ENTRIES = 1000;
    private String options = "";
    private double margin = 0.05;
    protected int validEntries = 0;
}

