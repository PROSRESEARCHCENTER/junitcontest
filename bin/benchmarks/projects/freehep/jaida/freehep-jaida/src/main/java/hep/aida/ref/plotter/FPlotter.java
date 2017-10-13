package hep.aida.ref.plotter;

import java.awt.Component;

/**
 * This interface maps the Plotter interface in JAS3.
 * Eventually the two should merge.
 *
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FPlotter {
    
    public final int NORMAL = 0;
    public final int OVERLAY = 1;
    public final int ADD = 2;
    public final int STACK = 3;
    /**
     * Plot a given data set.
     * @param data The data to be plotted. Must be of the class specified when
     * the plotter was created.
     * @param options One of (NORMAL, OVERLAY, ADD, STACK)
     */
    void plot(Object data, int options);
    void plot(Object data, int options, Object style);
    
    /**
     * Remove a data item from a plot
     */
    void remove(Object data);
    /**
     * Clear all the data in the plot
     */
   void clearPlotter();

    /**
     * Tests if the plotter can plot the given datatype
     */
    //boolean canPlot(Object data, int options);
    
    /**
     * Returns a Component that can be used to display a Plotter in some other swing
     * component.
     */
    Component viewable();
    
}
