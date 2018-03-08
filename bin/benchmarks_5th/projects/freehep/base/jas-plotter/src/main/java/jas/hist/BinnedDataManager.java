package jas.hist;

import jas.plot.DataArea;
import jas.plot.DateAxis;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Binned data manager is used for all 1D histograms.
 */
abstract class BinnedDataManager
extends OneDDataManager
implements SupportsFunctions
{
    private int m_defaultNumberOfBins;
    
    BinnedDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats, final int defaultNumberOfBins)
    {
        super(plot,da,l, stats);
        m_defaultNumberOfBins = defaultNumberOfBins;
    }
    void XAxisUpdated()
    {
        for (Enumeration e = data.elements(); e.hasMoreElements();)
        {
            JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
            if (dw.isShowing()) dw.setXRange(xm.getBins(),xLow,xHigh);
        }
        for (Enumeration e = funcs.elements(); e.hasMoreElements();)
        {
            JASHist1DFunctionData dw = (JASHist1DFunctionData) e.nextElement();
            if (dw.isShowing()) dw.setXRange(xLow,xHigh);
        }
        ym[0].setAttentionNeeded();
        if (ym[1] != null) ym[1].setAttentionNeeded();
    }
    public JASHist1DFunctionData addFunction(Basic1DFunction d)
    {
        JASHist1DFunctionData dw = new JASHist1DFunctionData(this,d);
        funcs.addElement(dw);
        return dw;
    }
    public void removeFunction(JASHist1DFunctionData d)
    {
        d.setShowing(false);
        d.destroy();
        funcs.removeElement(d);
        //The line below is to force the plot to invoke setChanged().
        //It should be replaced by something better.
        plot.setTitle( plot.getTitle() );
    }
    public void destroy()
    {
        super.destroy();
        removeAllFunctions();
    }
    public void removeAllFunctions()
    {
        final Enumeration e = funcs.elements();
        while (e.hasMoreElements())
        {
            JASHist1DFunctionData d = (JASHist1DFunctionData) e.nextElement();
            d.setShowing(false);
            d.destroy();
        }
        funcs.removeAllElements();
    }
    public int numberOfFunctions()
    {
        return funcs.size();
    }
    public Enumeration getFunctions()
    {
        return funcs.elements();
    }
    final public void update(final HistogramUpdate hu, final JASHistData data)
    {
        // I dont see any point to this??
        //if (hu.isFinalUpdate() || hu.isReset())
        //{
        //	if (m_defaultNumberOfBins != -1 && !xm.getIsFixed()) xm.bins = m_defaultNumberOfBins;
        //}
        super.update(hu,data);
    }
    final public void update(JASHist1DFunctionData data)
    {
        // Danger: likely to be run in a different thread
        
        SwingUtilities.invokeLater(this);
    }
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        // first get rid of excess IO volume...
        funcs.trimToSize();
        // then write out the vector
        out.defaultWriteObject();
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        data = new Vector();
        in.defaultReadObject();
    }
    protected final Vector funcs = new Vector();
    
    
    final protected class DateAxisListener extends AxisListener
    {
        DateAxisListener(final ManagedAxis axis)
        {
            super(axis);
            axisType = (DateAxis) axis.getType();
        }
        public void mouseDragged(MouseEvent me)
        {
            // Scale the axis so the correct point is under the mouse;
            int min = axis.getMinLocation();
            int max = axis.getMaxLocation();
            
            double f = ((double) me.getX()) / (max - min);
            if (grabType == 1) // move xHigh
            {
                xHigh = xLow + (grab-xLow)/f;
                axisType.setMax((long) (xHigh*1000));
            }
            else if (grabType == 0) // move xLow
            {
                xLow = xHigh - (xHigh - grab)/(1-f);
                axisType.setMin((long) (xLow*1000));
            }
            else // keep the range the same but move the axis
            {
                double off = (grab-f)*(xHigh - xLow);
                xHigh = xLowOld + off + xHigh - xLow;
                xLow = xLowOld + off;
                axisType.setMin((long) (xLow*1000));
                axisType.setMax((long) (xHigh*1000));
            }
            axis.setRangeAutomatic(false);
            XAxisUpdated();
            
            computeYAxisRange();
            //yAxis.getAxis().processRangeUpdate();
            da.repaint();
        }
        private DateAxis axisType;
    }
    final protected class DoubleAxisListener extends AxisListener
    {
        DoubleAxisListener(final ManagedAxis axis)
        {
            super(axis);
            axisType = (DoubleAxis) axis.getType();
        }
        public void mouseDragged(MouseEvent me)
        {
            // Scale the axis so the correct point is under the mouse;
            int min = axis.getMinLocation();
            int max = axis.getMaxLocation();
            
            double f = ((double) me.getX()) / (max - min);
            if (grabType == 1) // move xHigh
            {
                xHigh = xLow + (grab-xLow)/f;
                axisType.setMax(xHigh);
            }
            else if (grabType == 0) // move xLow
            {
                xLow = xHigh - (xHigh - grab)/(1-f);
                axisType.setMin(xLow);
            }
            else // keep the range the same but move the axis
            {
                double off = (grab-f)*(xHigh - xLow);
                xHigh = xLowOld + off + xHigh - xLow;
                xLow = xLowOld + off;
                axisType.setMin(xLow);
                axisType.setMax(xHigh);
            }
            axis.setRangeAutomatic(false);
            XAxisUpdated();
            
            computeYAxisRange();
            //yAxis.getAxis().processRangeUpdate();
            da.repaint();
        }
        private DoubleAxis axisType;
    }
    
    
    abstract class AxisListener extends MouseAdapter implements MouseMotionListener
    {
        AxisListener(final ManagedAxis axis)
        {
            this.axis = axis;
            axis.addMouseListener(this);
            axis.addMouseMotionListener(this);
            axis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        public void mouseEntered(MouseEvent me)
        {
        }
        public void mouseMoved(MouseEvent me)
        {
            
        }
        public void mousePressed(MouseEvent me)
        {
            int min = axis.getMinLocation();
            int max = axis.getMaxLocation();
            double rgrab =  ((double) me.getX()) / (max - min);
            oldCursor = axis.getCursor();
            
            // The type of grab depends on where on the axis the grab occurs;
            
            if      (rgrab < .2)
            {
                grabType = 0;
                grab = xLow + rgrab * (xHigh - xLow);
                axis.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            }
            else if (rgrab > .8)
            {
                grabType = 1;
                grab = xLow + rgrab * (xHigh - xLow);
                axis.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            }
            else
            {
                grabType = 2;
                grab = rgrab;
                xLowOld = xLow;
                axis.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
        public void mouseReleased(MouseEvent me)
        {
            axis.setCursor(oldCursor);
        }
        protected double xLowOld;
        protected double grab;
        protected int grabType;
        protected ManagedAxis axis;
        protected Cursor oldCursor;
    }
    void modifyPopupMenu(JPopupMenu menu, Component source)
    {
        super.modifyPopupMenu(menu,source);
        if (menu.getComponentCount() > 0) menu.addSeparator();
        menu.add(new AddFunctionMenu());
        menu.add(new RemoveFunctionMenu());
        menu.add(new FitMenu());
        menu.add(new AdvancedOptionsMenu());
    }
    public void fillFunctionMenu(JMenu menu)
    {
        menu.add(new AddFunctionMenu());
        menu.add(new RemoveFunctionMenu());
        menu.add(new FitMenu());
        menu.add(new AdvancedOptionsMenu());
    }
    final private class AddFunctionMenu extends JMenu
    {
        public AddFunctionMenu()
        {
            super("Add function");
            setMnemonic('A');
            
            // we need to build the menu next time it's selected
            m_build = FitterRegistry.instance().getDefaultFitter() != null &&
            FunctionRegistry.instance().size() > 0;
            setEnabled(m_build);
        }
        private void buildMenu()
        {
            removeAll();
            int i = 0;
            final Enumeration e = FunctionRegistry.instance().elements();
            while (e.hasMoreElements())
                this.add(new AddFunctionMenuItem((FunctionFactory) e.nextElement(), ++i));
            m_build = false; // we don't need to build the menu next time it's selected
        }
        protected void fireMenuSelected()
        {
            if (m_build) buildMenu();
            //	super.fireMenuSelected();
        }
        private boolean m_build;
        final private class AddFunctionMenuItem extends JMenuItem
                /*
                 * This class is for an item on the AddFunctionMenu.
                 * Each is a function from the FuntionRegistry
                 * that can be added to a window.
                 */
        {
            AddFunctionMenuItem(final FunctionFactory func, final int i)
            {
                super(new StringBuffer(String.valueOf(i)).append(' ').append(func.getFunctionName()).toString());
                m_func = func;
                setMnemonic('0' + (char) i);
            }
            protected void fireActionPerformed(final ActionEvent evt)
            {
                try
                {

                    //This line replaces the one below. This is so that the adding of the
                    //function goes through the JASHist. This allows to control the title and
                    //the name of the function
                    plot.addData(m_func.createFunction(plot)).show(true);
                   // BinnedDataManager.this.addFunction(m_func.createFunction(plot)).show(true);
                }
                catch (FunctionFactoryError ffe)
                {
                    JOptionPane.showMessageDialog(this,"Could not add function.");
                }
            }
            private FunctionFactory m_func;
        }
    }
    final private class RemoveFunctionMenu extends JMenu
        /*
         * Has on it all functions that are on the
         * selected hist.
         */
    {
        public RemoveFunctionMenu()
        {
            super("Remove function");
            setMnemonic('R');
            
            // use the same remove all item for all menus
            m_removeAll = new RemoveAll();
            
            m_build = numberOfFunctions() > 0; // we need to build the menu next time it's selected
            setEnabled(m_build);
        }
        private void buildMenu()
        {
            m_build = false;
            removeAll();
            int i = 0;
            final Enumeration e = getFunctions();
            while (e.hasMoreElements())
                this.add(new RemoveFunctionMenuItem((JASHist1DFunctionData) e.nextElement(), ++i));
            if (i > 1) // at least 2 menu items
            {
                addSeparator();
                this.add(m_removeAll);
            }
        }
        protected void fireMenuSelected()
        {
            if (m_build) buildMenu();
            //	super.fireMenuSelected();
        }
        private boolean m_build; // indicates whether it should be built when selected
        private RemoveAll m_removeAll;
        final private class RemoveFunctionMenuItem extends JMenuItem
        {
            RemoveFunctionMenuItem(final JASHist1DFunctionData func, final int i)
            {
                super(new StringBuffer(String.valueOf(i)).append(' ').append(func.getTitle()).toString());
                m_func = func;
                setMnemonic('0' + (char) i);
            }
            protected void fireActionPerformed(final ActionEvent evt)
            {
                BinnedDataManager.this.removeFunction(m_func);
            }
            private JASHist1DFunctionData m_func;
        }
        final private class RemoveAll extends JMenuItem
        {
            RemoveAll()
            {
                super("Remove all");
            }
            protected void fireActionPerformed(final ActionEvent evt)
            {
                removeAllFunctions();
            }
        }
    }
    final private class FitMenu extends JMenu
        /*
         * This is a dynamic menu whose contents depend
         * on what data sets are on the window, and
         * what fittable functions have already been
         * added.  It is only enabled if there is at
         * least one fittable function on the window,
         * and at least one data set displayed.
         */
    {
        public FitMenu()
        {
            super("Fit");
            setMnemonic('F');
            
            m_build = false;
            if (numberOfDataSources() > 0)
            {
                final Enumeration e = getFunctions();
                while (e.hasMoreElements())
                {
                    final Basic1DFunction func = ((JASHist1DFunctionData) e.nextElement()).getFunction();
                    if (func instanceof Fittable1DFunction && ((Fittable1DFunction) func).getFit() == null)
                    {
                        m_build = true;
                        break;
                    }
                }
            }
            setEnabled(m_build);
        }
        private void buildMenu()
        {
            removeAll();
            m_build = false;
            if (numberOfDataSources() > 1)
                // there are multiple data sets to choose from
            {
                int i = 0;
                final Enumeration e = getDataSources();
                while ( e.hasMoreElements() )
                    this.add( new FitMenuDataMenu( (JASHist1DHistogramData) e.nextElement(), ++i ) );
                // The FitMenu menu will contain a selection
                // of FitMenuDataMenu menus (one for each data
                // source).  Each menu will offer a list of
                // selectable functions.
            }
            else
                // there is only one data set so it's ovbious
                // which data set to use
            {
                m_selectedDataSet = (JASHist1DHistogramData) getDataSources().nextElement();
                // the only data set is selected by default
                final Enumeration e = getFunctions();
                int i = 0;
                while ( e.hasMoreElements() )
                {
                    final Basic1DFunction func = ((JASHist1DFunctionData) e.nextElement()).getFunction();
                    if (func instanceof Fittable1DFunction && ((Fittable1DFunction) func).getFit() == null)
                        this.add( new FitMenuFunctionItem((Fittable1DFunction) func, ++i) );
                }
                // the FitMenu will offer a choice of all of
                // the available functions to fit
            }
        }
        protected void fireMenuSelected()
        {
            if (m_build) buildMenu();
            //	super.fireMenuSelected();
        }
        private JASHist1DHistogramData m_selectedDataSet;
        // This is the data that the user has selected
        // to fit the function to.  If only one choice
        // of data is available, this is selected
        // automatically.
        
        private boolean m_build;
        final private class FitMenuFunctionItem extends JMenuItem
        {
            FitMenuFunctionItem(final Fittable1DFunction func, final int i)
            {
                super( String.valueOf(i) +" "+ func.getTitle() );
                m_func = func;
                setMnemonic('0' + (char) i);
            }
            protected void fireActionPerformed(final ActionEvent evt)
            {
                final Fitter fitter = FitterRegistry.instance().getDefaultFitter();
                fitter.setFunction(m_func);
                fitter.setData((XYDataSource) m_selectedDataSet.getFittableDataSource());
                plot.notifyFitListeners(fitter);
                fitter.start();
            }
            private Fittable1DFunction m_func;
        }
        final private class FitMenuDataMenu extends JMenu
        {
            FitMenuDataMenu(final JASHist1DHistogramData data, final int i)
            {
                super( String.valueOf(i) +" "+ data.getTitle() );
                m_data = data;
                setMnemonic('0' + (char) i);
            }
            protected void fireMenuSelected()
            {
                m_selectedDataSet = m_data;
                if (!m_built)
                {
                    m_built = true;
                    // build menu:
                    this.removeAll();
                    final Enumeration e = getFunctions();
                    int i = 0;
                    while ( e.hasMoreElements() )
                    {
                        JASHist1DFunctionData d = (JASHist1DFunctionData) e.nextElement();
                        Object f = d.getDataSource();
                        if (f instanceof Fittable1DFunction)
                            this.add( new FitMenuFunctionItem((Fittable1DFunction) f, ++i) );
                    }
                    // Users select an FitMenuDataMenu on the
                    // basis of which data set they want.  Once a
                    // FitMenuDataMenu is selected, the
                    // selectedDataSet is set.  This menu offers a
                    // list of possible functions to fit.  Once
                    // a function is selected, the method
                    // FitMenuFunctionItem.fireActionPerformed()
                    // is called, and a fit is added based on the
                    // selected function and data.
                    //	super.fireMenuSelected();
                }
            }
            private boolean m_built = false;
            private JASHist1DHistogramData m_data;
        }
    }
    final private class AdvancedOptionsMenu extends JMenu
    {
        public AdvancedOptionsMenu()
        {
            super("Advanced options...");
            setMnemonic('o');
            
            final Enumeration e = getFunctions();
            m_build = false;
            if (e != null)
                while (e.hasMoreElements())
                {
                    if (((JASHist1DFunctionData) e.nextElement()).getFunction() instanceof FunctionAdvancedOptions)
                    {
                        m_build = true;
                        break;
                    }
                }
            setEnabled(m_build);
        }
        protected void fireMenuSelected()
        {
            if (m_build)
            {
                m_build = false;
                removeAll();
                final Enumeration e = getFunctions();
                int i = 0;
                while ( e.hasMoreElements() )
                {
                    final Basic1DFunction func = ((JASHist1DFunctionData) e.nextElement()).getFunction();
                    if (func instanceof FunctionAdvancedOptions)
                        this.add( new AdvancedOptionsMenuItem( (FunctionAdvancedOptions) func, ++i ) );
                }
            }
            //	super.fireMenuSelected();
        }
        private boolean m_build;
        final private class AdvancedOptionsMenuItem extends JMenuItem
        {
            AdvancedOptionsMenuItem(final FunctionAdvancedOptions function, final int i)
            {
                super(String.valueOf(i) +" "+ ((Basic1DFunction) function).getTitle());
                m_function = function;
                setMnemonic('0' + (char) i);
            }
            protected void fireActionPerformed(final ActionEvent e)
            {
                m_function.openAdvancedDialog((Frame) SwingUtilities.getAncestorOfClass(Frame.class, plot), plot);
            }
            private FunctionAdvancedOptions m_function;
        }
    }
    
    void computeXAxisRange()
    {
        if (!xm.needsAttention()) return;
        xm.payingAttention(); // do first to avoid race conditions
        
        if (data.isEmpty()) return;
        
        // Note, when we add items which are rebinnable, we coerce them to adopt the binning preferred by
        // the x-axis. However, when we add items which are non-rebinnable, they are displayed without regard
        // to the binning preferred by the axis.
        
        if (!xm.getRangeAutomatic())
        {
            xLow = xm.getMin();
            xHigh = xm.getMax();
            return;
        }
        
        int nShowing = 0;
        xLow = 0;
        xHigh = 0;
        boolean hasRebinnables = false;
        
        for (Enumeration e = data.elements(); e.hasMoreElements();)
        {
            JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
            if (!dw.isShowing()) continue;
            if (Double.isNaN(dw.getXMin())) continue;
            
            if (nShowing++ == 0)
            {
                xLow = dw.getXMin();
                xHigh = dw.getXMax();
            }
            else
            {
                xLow = Math.min(xLow,dw.getXMin());
                xHigh = Math.max(xHigh,dw.getXMax());
            }
            if (dw.isRebinnable()) hasRebinnables = true;
        }
        if (nShowing == 0) return;
        
        xm.setBinned(hasRebinnables);
        
        if (!xm.getAllowSuppressedZero())
        {
            if (xLow > 0) xLow = 0;
            if (xHigh < 0) xHigh = 0;
        }
        if (xHigh <= xLow) xHigh = xLow + 1;
        
        calcMinMaxBins(xLow,xHigh);
    }
    
    void computeYAxisRange()
    {
        for (int i=0; i<ym.length; i++)
        {
            double ymin = 0;
            double ymax = 0;
            
            if (ym[i] == null) continue;
            if (!ym[i].needsAttention()) continue;
            ym[i].payingAttention(); // do first to avoid race conditions
            
            DoubleAxis yAxis = (DoubleAxis) ym[i].getType();
            
            if (data.isEmpty()) return;
            if (!ym[i].getRangeAutomatic())
            {
                yAxis.setUseSuggestedRange(false);
                yAxis.getAxis().invalidate();
                for (Enumeration e = data.elements(); e.hasMoreElements();)
                {
                    JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
                    if (dw.isShowing() && dw.getYAxis() == i) dw.validate();
                }
            }
            else
            {
                boolean first = true;
                
                for (Enumeration e = data.elements(); e.hasMoreElements();)
                {
                    JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
                    if (!dw.isShowing() || dw.getYAxis() != i) continue;
                    
                    if (first)
                    {
                        ymin = dw.getYMin();
                        ymax = dw.getYMax();
                        first = false;
                    }
                    else
                    {
                        ymin = Math.min(ymin,dw.getYMin());
                        ymax = Math.max(ymax,dw.getYMax());
                    }
                }
                if (!ym[i].getAllowSuppressedZero())
                {
                    if (ymin > 0) ymin = 0;
                    if (ymax < 0) ymax = 0;
                }
                if  (ymax <= ymin) ymax = ymin + 1;
                
                if (ym[i].isLogarithmic()) {
                    //Fix to JAIDA-85, JAP-59, JAP-53
                    double min = Double.NaN;
                    for (Enumeration e = data.elements(); e.hasMoreElements();) {
                        JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();                       
                        if (dw.isShowing()) {
                            XYDataSource ds = (XYDataSource)dw.getFittableDataSource();
                            for ( int j = 0; j < ds.getNPoints(); j++ ) {
                                double tmpMin = ds.getY(j) - ds.getMinusError(j);
                                if ( tmpMin > 0 ) {
                                    if ( Double.isNaN(min) )
                                        min = tmpMin;
                                    else
                                        min = Math.min( tmpMin, min );
                                }
                            }
                        }
                    }
                    if ( Double.isNaN( min ) )
                        ymin = Math.max(ymin, 0.5); 
                    else
                        ymin = 0.8*min;
                    
                    // if Y Axis is logarithmic and ymin > 0.1 and allowSuppressedZero=false, force ymin=0.1
                    if (!ym[i].getAllowSuppressedZero()) ymin = Math.min(ymin, 0.1);
                }                
                double oldYMin = yAxis.getPlotMin();
                double oldYMax = yAxis.getPlotMax();
                if (ymin < oldYMin || ymax > oldYMax || (ymax - ymin) / (oldYMax - oldYMin) < .75)
                {
                    yAxis.setUseSuggestedRange(true);
                    yAxis.setMin(ymin);
                    yAxis.setMax(ymax);
                    yAxis.getAxis().revalidate(); // Why does this have to be revalidate??
                }
            }
        }
    }
    
    abstract void calcMinMaxBins(double x1, double x2);
}
