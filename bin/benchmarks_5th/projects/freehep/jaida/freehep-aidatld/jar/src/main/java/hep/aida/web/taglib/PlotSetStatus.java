package hep.aida.web.taglib;

import hep.aida.IManagedObject;
import jas.hist.DataSource;

/*
 * PlotSetStatus.java
 *
 * Created on October 17, 2007, 10:52 AM
 *
 * This class keeps the information about the current status of
 * the PlotSetTag processing
 *
 * @author The AIDA Team @ SLAC
 */

public class PlotSetStatus {
    private int nplots;
    private int maxplots;
    private int plotsinpage;
    private int index = 0;
    private int offset = 0;
    private String offsetvariable = null;
    private Object[] data;
    
    /**
     * Total number of plots to browse
     */
    public int getNplots() {
        return nplots;
    }
    public void setNplots(int nplots) {
        this.nplots = nplots;
    }
    
    /**
     * Max number of plots per page
     */
    public int getMaxplots() {
        return maxplots;
    }
    public void setMaxplots(int maxplots) {
        this.maxplots = maxplots;
    }
    
    /**
     * Total number of plots on the current page
     */
    public int getPlotsinpage() {
        return plotsinpage;
    }
    public void setPlotsinpage(int plotsinpage) {
        this.plotsinpage = plotsinpage;
    }
    
    /**
     * Index of the plot that is currently processed
     */
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * Starting index for the current page
     */
    public int getStartindex() {
        return offset;
    }
    public void setStratindex(int offset) {
        this.offset = offset;
    }
    
    /**
     * Actual data
     */
    public Object[] getPlots() {
        return data;
    }
    public void setPlots(Object[] data) {
        this.data = data;
    }
    
    public void setOffsetvariable(String offsetvariable) {
        this.offsetvariable = offsetvariable;
    }
    
    public String getOffsetvariable() {
        return offsetvariable;
    }
    
}

