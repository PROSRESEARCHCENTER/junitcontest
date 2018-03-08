package jas.hist;

import jas.plot.LegendEntry;
import jas.plot.Overlay;

import java.util.Observable;
import java.util.Observer;

/**
 * This class is returned whenever a DataSource is added to a JASHist
 * It supports making the data visible/invisible, and controlling the
 * style (color etc) in which the data is shown.
 * @author Tony Johnson
 */
public abstract class JASHistData
extends Observable
implements Observer
{
    JASHistData(DataManager parent)
    {
        this.parent = parent;
    }
        /**
         * Only used for object serialization, do not call
         */
    public JASHistData()
    {
    }
        /**
         * Controls the visibility of the associated DataSource
         * Only listens to changes in DataSources if visible
         * @param show true if the DataSource should be visible
         */
    public void show(boolean show)
    {
        if (show != isVisible)
        {
            isVisible = show;
            DataSource dataSource = getDataSource();
            if (dataSource instanceof Observable)
            {
                if (show) {
                    ((Observable) dataSource).addObserver(this);
                    restoreNormalizationObserver();
                } else {
                    ((Observable) dataSource).deleteObserver(this);
                    deleteNormalizationObserver();
                }
            }
            if (show) parent.requestShow(this);
            else parent.requestHide(this);
        }
    }
    /**
     * Controls whether statistics associated with this datasource should be shown
     */
    public void setShowStatistics(boolean show)
    {
       showStatistics = show;
    }
    public boolean getShowStatistics()
    {
       return showStatistics;
    }
    /**
     * Controls whether statistics associated with this datasource should be shown
     */
    public void setShowLegend(boolean show)
    {
       showLegend = show;
    }
        /**
         * Get the legend entry for this data set
         * @return The LegendEntry, or null if there isnt one
         */
    LegendEntry getLegendEntry()
    {
        if ( ! showLegend )
            return null;
        Overlay ol = getOverlay();
        return ol instanceof LegendEntry ? (LegendEntry) ol : null;
    }
    abstract Overlay createOverlay();
        /**
         * Get the overlay associated with this data set
         */
    Overlay getOverlay()
    {
        if (overlay == null)
        { 		
            CustomOverlay co = getStyle().getCustomOverlay();     
            if (co != null)     
            {     
                co.setDataSource(getDataSource());     
                overlay = co;     
            }     
            else     
            {     
             overlay = createOverlay();     
            }     
        }
        return overlay;
    }
        /**
         * Get the DataSource associated with this JASHistData
         * @return The associated DataSource
         */
    public abstract DataSource getDataSource();
        /**
         * Get a DataSource suitable for using as the source for
         * fit operations. In the case where the binning is set
         * by the plot itself, this will differ from the orginal
         * DataSource attached to the plot in that it reflects the
         * current binning of the plot (remember a binned fit needs
         * to be redone if the binning changes).
         * @see #getDataSource()
         */
    public DataSource getFittableDataSource()
    {
        return getDataSource();
    }
        /**
         * Get the title of the associated DataSource
         * @return The title
         */
    public abstract String getTitle();
    abstract void axisChanged();
        /**
         * Set a style for this data. The JASHistStyle object passed to this method must be
         * appropriate for the corresponding DataSource, e.g. if the DataSource is a
         * Rebinnable1DHistogramData then the JASHistStyle must be a JASHist1DStyle.
         * @param style The style to be set
         * @exception IllegalArgumentException If the style is not of the approriate type
         */
    public abstract void setStyle(JASHistStyle style);
        /**
         * Get the JASHistStyle associated with this DataSource
         * @return The associated style
         */
    public abstract JASHistStyle getStyle();
    
        /**
         * Sets which Y axis the data is plotted against.
         * @param axis Either YAXIS_LEFT or YAXIS_RIGHT
         */
    public void setYAxis(int i)
    {
        yAxisIndex = i;
        axisChanged();
    }
        /**
         * Get the y axis against which this data is plotted
         * @return Either YAXIS_LEFT or YAXIS_RIGHT
         */
    public int getYAxis()
    {
        return yAxisIndex;
    }
        /**
         * Equivalent to show(boolean)
         * @param value true if the corresponding DataSource should be visible
         */
    public void setShowing(boolean value)
    {
        show(value);
    }
        /**
         * Find out if this DayaSource is visible
         * @return true if the corresponding DataSource is visible
         */
    public boolean isShowing()
    {
        return isVisible;
    }
    public String toString()
    {
        return getTitle();
    }
        /**
         * Set the text for the legene entry corresponding to this data item
         */
    public void setLegendText(String text)
    {
        String newText = null;
        if (text.length() > 0 && !text.equals(getTitle())) newText = text;
        if (newText != legendText)
        {
            legendText = newText;
            parent.getPlot().getLegend().legendTextChanged();
        }
    }
    public String getLegendText()
    {
        return legendText != null ? legendText : getTitle();
    }
    boolean isLegendChanged()
    {
        return legendText != null;
    }
    Statistics getStatistics()
    {
       if (showStatistics)
       {
          DataSource dataSource = getDataSource();
          if (dataSource instanceof HasStatistics)
            return ((HasStatistics) dataSource).getStatistics();
       }
       return null;
    }
        /**
         * Writes the data and surrounding XML tags.  Subclasses must implement this sensibly.
         */
    abstract void writeAsXML(XMLPrintWriter pw, boolean snapshot);
    void deleteNormalizationObserver()
    {
        if (normalization instanceof Observable) ((Observable) normalization).deleteObserver(this);
    }
    void restoreNormalizationObserver()
    {
        if (normalization instanceof Observable) ((Observable) normalization).addObserver(this);
    }
    
    /**
     * Set the normalization for this dataset.
     * @param factor The normalization to apply, or null for no normalization
     */
    public void setNormalization(jas.hist.normalization.Normalizer factor)
    {
        if (factor != normalization)
        {
            if (normalization instanceof Observable)
            {
                ((Observable) normalization).deleteObserver(this);
            }
            normalization = factor;
            if (factor instanceof Observable)
            {
                ((Observable) normalization).addObserver(this);
            }
            normalizationChanged(true);
        }
    }
    public jas.hist.normalization.Normalizer getNormalization()
    {
        return normalization;
    }
    
    public void setXBounds(double xmin, double xmax) {
    }
    
    abstract void normalizationChanged(boolean now);
    static final long serialVersionUID = -3529869583896718619L;
    jas.hist.normalization.Normalizer normalization;
    int yAxisIndex;
    boolean isVisible = false;
    DataManager parent;
    private String legendText;
    private boolean showStatistics = true;
    private boolean showLegend = true;
    protected Overlay overlay;
        /**
         * Y Axis on the left of the data area
         */
    public final static int YAXIS_LEFT = 0;
        /**
         * Y Axis on the right of the data area
         */
    public final static int YAXIS_RIGHT = 1;
}

