package jas.hist;

import jas.plot.Axis;
import jas.plot.DataArea;
import jas.plot.HasPopupItems;
import jas.plot.JASPlotMouseListener;
import jas.plot.Legend;
import jas.plot.PlotComponent;
import jas.plot.PlotPanel;
import jas.plot.PrintHelper;
import jas.plot.Title;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * JASHist is the main component used for plotting histograms, scatterplots etc. The type of
 * display produced depends on the DataSource hooked to the component. If the data source is
 * observable, then the JASHist will update as it receives notifications from the data source.
 *
 * JASHist supports overlaying of data and fitting of functions (to 1D histograms).
 */

public class JASHist
extends JComponent
implements JASPlotMouseListener
{
    
        /**
         * Create a new JASHist component with no initial data source
         */
    
    public JASHist()
    {
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200,200));
        
        Axis xAxis = new ManagedAxis(Axis.HORIZONTAL);
        Axis yAxis = new ManagedAxis(Axis.VERTICAL);
        
        legend = new Legend();
        contentPane.add(legend);
        legend.setVisible(false);
        
        stats = new StatisticsBlock();
        contentPane.add(stats);
        stats.setVisible(false);
        
        dataArea = new JASHistDataArea(xAxis,yAxis);
        dataManager = new DefaultDataManager(this,dataArea);
        contentPane.add(dataArea);
        
        changed = false;
        isInit = false;
    }
    DataManager getDataManager()
    {
        return dataManager;
    }
    // Made public to allow DataArea to be set as a DnD target.
    public DataArea getDataArea()
    {
        return dataArea;
    }
        /**
         * @return true if a call to showProperties can currently be honoured
         * @see #showProperties()
         */
    public boolean supportsProperties()
    {
        return !(dataManager instanceof DefaultDataManager);
    }
        /**
         * Pops up the Properties dialog box for this plot
         * @see #showProperties(byte)
         */
    public void showProperties()
    {
        showProperties(JASHistPropertyDialog.DEFAULT);
    }
        /**
         * Pops up the properties dialog box for this plot with the
         * specified axis tab initially open
         * @param axis The axis tab to opened (defined??)
         * @see #showProperties()
         */
    //TODO: Document argument properly
    public void showProperties(final byte axis)
    {
        new JASHistPropertyDialog((Frame) SwingUtilities.getAncestorOfClass(Frame.class, this), this, axis).show();
    }
        /**
         * Overrides addNotify in JComponent
         * Starts listening for changes in data
         */
    public void addNotify() {
        isInit = true;
        if (dataManager != null) {
            if (dataManager.numberOfDataSources() != 0) {
                Enumeration e = dataManager.getDataSources();
                while (e.hasMoreElements()) {
                    JASHistData d = (JASHistData) e.nextElement();
                    d.restoreNormalizationObserver();
                    d.show(true);
                }
            }
            if (dataManager instanceof BinnedDataManager) {
                int nFunc = ((BinnedDataManager) dataManager).numberOfFunctions();
                if (nFunc > 0) {
                    Enumeration e = ((BinnedDataManager) dataManager).getFunctions();
                    while (e.hasMoreElements()) {
                        JASHistData d = (JASHistData) e.nextElement();
                        d.show(true);
                    }                    
                }
            }
            dataManager.init();
        }
        super.addNotify();
    }
    
    /**
     * Overrides removeNotify in JComponent
     * Stops listening for changes in data
     */
    public void removeNotify() {
        isInit = false;
        if (dataManager != null) {
            dataManager.setRealized(false);
            if (dataManager.numberOfDataSources() != 0) {
                Enumeration e = dataManager.getDataSources();
                while (e.hasMoreElements()) {
                    JASHistData d = (JASHistData) e.nextElement();
                    d.deleteNormalizationObserver();
                    d.show(false);
                }
            }
            if (dataManager instanceof BinnedDataManager) {
                int nFunc = ((BinnedDataManager) dataManager).numberOfFunctions();
                if (nFunc > 0) {
                    Enumeration e = ((BinnedDataManager) dataManager).getFunctions();
                    while (e.hasMoreElements()) {
                        JASHistData d = (JASHistData) e.nextElement();
                        d.show(false);
                    }                    
                }
            }
        }
        super.removeNotify();
    }
    
    
        /**
         * Writes the histogram, scatterplot etc that we are displaying as XML.
         * @param pw A PrintWriter to send the output to
         * @param snapshot A snapshot of the current data is stored if true, otherwise a reference to the datasource
         * @see <a href="http://www-sldnt.slac.stanford.edu/jas/Documentation/howto/xml/default.shtml">XML How To</a>
         */
    public void writeXML(Writer w,boolean snapshot)
    {
        XMLPrintWriter pw =  new XMLPrintWriter(w);
        pw.print(this,snapshot);
    }
        /**
         * Test if user direct user interaction with the plot is allowed
         * @return True if user interaction is currently allowed
         * @see #setAllowUserInteraction(boolean)
         */
    public boolean getAllowUserInteraction()
    {
        return contentPane.getAllowUserInteraction();
    }
        /**
         * Controls whether end users are allowed to directly interact with
         * the plot by way of popup menus or "clicking and dragging". By
         * default user interaction is allowed. If no
         *
         * @param allow True if user interactions are to be allowed.
         */
    public void setAllowUserInteraction(boolean allow)
    {
        contentPane.setAllowUserInteraction(allow);
        setChanged();
    }
    public boolean getAllowPopupMenus()
    {
        return contentPane.getAllowPopupMenus();
    }
    public void setAllowPopupMenus(boolean allow)
    {
        contentPane.setAllowPopupMenus(allow);
        setChanged();
    }
    public void setShowStatistics(boolean show)
    {
        stats.setVisible(show);
        contentPane.revalidate();
        setChanged();
    }
    public boolean getShowStatistics()
    {
        return stats.isVisible();
    }
        /**
         * Set the color used to paint the background (but not the data area)
         * @param c The new Color to use, or <code>null</code> to set the background to be transparent
         * @see #setDataAreaColor(Color)
         * @see #setForegroundColor(Color)
         */
    public void setBackground(Color c)
    {
        contentPane.setBackground(c);
        setChanged();
    }
        /**
         * Get the background color.
         */
    public Color getBackground()
    {
        if (contentPane.isPaintingBackground()) return contentPane.getBackground();
        else return super.getBackground();
    }
        /**
         * Sets the current foreground color, used by default as the color
         * for the axis, labels, title, legend etc. By default the foreground
         * color of the plots container is used
         * @param c The new foreground color
         */
    public void setForegroundColor(Color c)
    {
        super.setForeground(c);
        setChanged();
    }
        /**
         * Return the current data area background color
         * @return The current data area background color
         * @see #setDataAreaColor(Color)
         */
    public Color getDataAreaColor()
    {
        return dataArea.getBackground();
    }
        /**
         * Set the color used to paint the background of the data area
         * @param c The new color to be used to paint the data area background, or <code>null</code>`to set the bacground back to the default value
         * @see #setBackground(Color)
         * @see #setForeground(Color)
         */
    public void setDataAreaColor(Color c)
    {
        dataArea.setBackground(c);
        setChanged();
    }
    
    public final static int NONE = PlotComponent.NONE;
    public final static int BEVEL_IN = PlotComponent.BEVEL_IN;
    public final static int BEVEL_OUT = PlotComponent.BEVEL_OUT;
    public final static int ETCHED = PlotComponent.ETCHED;
    public final static int LINE = PlotComponent.LINE;
    public final static int SHADOW = PlotComponent.SHADOW;
        /**
         * Set the border to place around the data area
         * @param type One of NONE,BEVEL_IN,BEVEL_OUT,ETCHED,LINE,SHADOW
         */
    
    public void setDataAreaBorderType(int type)
    {
        dataArea.setBorderType(type);
        dataArea.revalidate();
        setChanged();
    }
        /**
         * Get the current data area border type
         * @return One of NONE,BEVEL_IN,BEVEL_OUT,ETCHED,LINE,SHADOW,OTHER
         */
    public int getDataAreaBorderType()
    {
        return dataArea.getBorderType();
    }
    public void setDataAreaBorder(Border b)
    {
        dataArea.setBorder(b);
    }
    public Border getDataAreaBorder()
    {
        return dataArea.getBorder();
    }
        /**
         * Gets the X axis
         * @return The current X axis
         */
    public JASHistAxis getXAxis()
    {
        return dataManager.getXAxis();
    }
        /**
         * Gets the default Y Axis
         * @return The current default Y Axis
         * @see #getYAxis(int)
         */
    public JASHistAxis getYAxis()
    {
        return dataManager.getYAxis(0);
    }
        /**
         * Gets either Y Axis.
         * @param index The axis to get, 0 = left (default), 1 = right
         * @return The requested Axis
         */
    public JASHistAxis getYAxis(int index)
    {
        return dataManager.getYAxis(index);
    }
        /**
         * Get an array containing all of the Y Axes
         */
    public JASHistAxis[] getYAxes()
    {
        return dataManager.getYAxes();
    }
    final public static int LEGEND_NEVER = 0;
    final public static int LEGEND_AUTOMATIC = 1;
    final public static int LEGEND_ALWAYS = 2;
        /**
         * Set when the legend will be shown.	By default the option is
         * set to LEGEND_AUTOMATIC, which means that the legend will be shown
         * whenever there is more than one DataSource attached to the plot.
         * @param legend One of LEGEND_NEVER,LEGEND_AUTOMATIC,LEGEND_ALWAYS
         */
    public void setShowLegend(int legend)
    {
        showLegend = legend;
        dataManager.showLegend();
    }
        /**
         * Get the current setting of the showLegend property
         * @return The current settign of showLegend
         * @see #setShowLegend(int)
         */
    public int getShowLegend()
    {
        return showLegend;
    }
        /**
         * Add a DataSource to the plot.
         * @param ds The DataSource to add
         * @throws DataManagerException If the subclass of DataSource is unrecognized or if the new DataSource is incompatible with previously added DataSources.
         */
    public JASHistData addData(DataSource ds) throws DataManagerException
    {
        if (ds instanceof Rebinnable1DHistogramData) return add1DData((Rebinnable1DHistogramData) ds);
        if (ds instanceof XYDataSource             ) return addXYData((XYDataSource) ds);
        if (ds instanceof Rebinnable2DHistogramData) return add2DData((Rebinnable2DHistogramData) ds);
        if (ds instanceof ScatterPlotSource        ) return addScatterData((ScatterPlotSource) ds);
        if (ds instanceof FunctionData             ) return addFunctionData((FunctionData) ds);
        throw new DataManagerException("Unknown DataSource subclass: "+ds);
    }
        /**
         * Add a Rebinnable1DHistogramData source to the plot
         * @param ds The data source to be added
         */
    private JASHistData add1DData(Rebinnable1DHistogramData ds) throws DataManagerException
    {
        if (dataManager instanceof DefaultDataManager)
        {
            int type = ds.getAxisType();
            if      (type == ds.DOUBLE ) dataManager = new DoubleDataManager(this,dataArea,legend,stats,50);
            else if (type == ds.INTEGER) dataManager = new IntegerDataManager(this,dataArea,legend,stats,50);
            else if (type == ds.STRING ) dataManager = new StringDataManager(this,dataArea,legend,stats);
            else if (type == ds.DATE   ) dataManager = new DateDataManager(this,dataArea,legend,stats,50);
            else throw new DataManagerException("Unsupported axis type");
            
            if (isInit) dataManager.init(); // Only necessary if addNotify already called
        }
        JASHistData data = dataManager.add(ds);
        setChanged();
        return data;
    }
        /**
         * Add a Rebinnable1DHistogramData source to the plot
         * @param ds The data source to be added
         */
    private JASHistData addXYData(XYDataSource ds) throws DataManagerException
    {
        if (dataManager instanceof DefaultDataManager)
        {
            int type = ds.getAxisType();
            if      (type == ds.DOUBLE ) dataManager = new DoubleDataManager(this,dataArea,legend,stats,50);
            else if (type == ds.INTEGER) dataManager = new IntegerDataManager(this,dataArea,legend,stats,50);
            else if (type == ds.DATE   ) dataManager = new DateDataManager(this,dataArea,legend,stats,50);
            else throw new DataManagerException("Unsupported axis type");
            
            if (isInit) dataManager.init(); // Only necessary if addNotify already called
        }
        JASHistData data = dataManager.add(ds);
        setChanged();
        return data;
    }
        /**
         * Add a ScatterPlotSource to the plot
         * @param ds The data source to be added
         */
    private JASHistData addScatterData(ScatterPlotSource ds) throws DataManagerException
    {
        if (dataManager instanceof DefaultDataManager)
        {
            int typeX = ds.getXAxisType();
            int typeY = ds.getYAxisType();
            if (typeY != ds.DOUBLE) throw new DataManagerException("Scatterplot Y Axis must be of type DOUBLE");
            
            if      (typeX == ds.DOUBLE) dataManager = new DoubleScatterDataManager(this, dataArea, legend, stats);
            else if (typeX == ds.DATE  ) dataManager = new DateScatterDataManager(this,dataArea, legend, stats);
            
            if (isInit) dataManager.init(); // Only necessary if addNotify already called
        }
        final JASHistData data = dataManager.add(ds);
        setChanged();
        return data;
    }
        /**
         * Add a 2D Histogram data source to the histogram
         * @param ds The data source to be added
         */
    private JASHistData add2DData(Rebinnable2DHistogramData ds) throws DataManagerException
    {
        if (dataManager instanceof DefaultDataManager)
        {
            int typeX = ds.getXAxisType();
            int typeY = ds.getYAxisType();
            if      (typeX == ds.DOUBLE) dataManager = new DoubleScatterDataManager(this, dataArea, legend, stats);
            else if (typeX == ds.DATE  ) dataManager = new DateScatterDataManager(this,dataArea, legend, stats);
            else throw new DataManagerException("Unsupported X Axis type for ScatterPlot");
            
            if (isInit) dataManager.init(); // Only necessary if addNotify already called
        }
        final JASHistData data = dataManager.add(ds);
        setChanged();
        return data;
    }
        /**
         * Add a function to be overlayed on a 1D histogram
         * @param ds The function to be added
         */
    private JASHistData addFunctionData(FunctionData ds) throws DataManagerException
    {
        if ( dataManager instanceof SupportsFunctions )
        {
           JASHistData data = ((SupportsFunctions) dataManager).addFunction((Basic1DFunction) ds);
           setChanged();
           return data;           
        }
        else throw new UnsupportedOperationException("Cannot add function."); 
    }
    int numberOfDataSets() // used by property dialog
    {
        return dataManager.numberOfDataSources();
    }
    int numberOfFunctions()
    {
        if (dataManager instanceof SupportsFunctions)
            return ((SupportsFunctions) dataManager).numberOfFunctions();
        return 0;
    }
        /**
         * Get the set of data sources currently attached to the plot.
         * Note that despite the name of this routine it does not return
         * an Enumeration of DataSources, but rather an Enumeration of
         * JASHistData objects, from which the DataSource can be obtained,
         * for example:
         * <pre>
         * Enumeration  e = hPlot.getDataSources();
         * while (e.hasMoreElements())
         * {
         *    JASHistData data = (JASHistData) e.nextElement();
         *    DataSource source = data.getDataSource();
         * }
         * </pre>
         * @return An Enumeration of the JASHistData objects
         * @see JASHistData
         * @see JASHistData#getDataSource()
         */
    public Enumeration getDataSources()
    {
        return dataManager.getDataSources();
    }
        /**
         * Get the number of data sources attached to the plot
         */
    public int getNumberOfDataSources()
    {
        return dataManager.numberOfDataSources();
    }
        /**
         * Get the set of Functions currently attached to the plot
         * @return An Enumeration of the Functions, or null if the current DataSources do not support functions
         */
    
    public Enumeration get1DFunctions()
    {
        if (dataManager instanceof SupportsFunctions)
            return ((SupportsFunctions) dataManager).getFunctions();
        return null;
    }
        /**
         * Removes and detaches all data from the plot, but doesn't set up
         * the plot for further use.  Call this method if you aren't going to
         * be using the plot object any more.  Call <a href="#removeAllData()">removeAllData()</a>
         * to remove all data and set up the plot for further use.
         */
    public void destroy()
    {
        dataManager.destroy();
        contentPane.removeAll(); // should detach itself as a listener to all of its children
    }
        /**
         * Removes and detaches all data and sets up the plot for further use.
         * The method <a href="#destroy()">destroy()</a> is less expensive and
         * should be used if the plot will not be used any more.
         */
    public void removeAllData()
    {
        dataManager.destroy(); // this call detached data, now we set up the plot for further use
        dataManager = new DefaultDataManager(this,dataArea);
        setChanged();
    }
    private void resetAxis(JASHistAxis a)
    {
        a.setRangeAutomatic(true);
        a.setAllowSuppressedZero(true);
        a.setLogarithmic(false);
        setChanged();
    }

        /**
         * Fills the appropriate Function menu items into a user
         * provided menu. This routine can be used to make a function
         * fitting menu available to an external application without having
         * to rewrite a bunch of code already contained in JASHist
         *
         * @param menu The menu to which the items will be added
         */
    public void fillFunctionMenu(JMenu menu)
    {
        if (dataManager instanceof SupportsFunctions)
        {
            ((SupportsFunctions) dataManager).fillFunctionMenu(menu);
            menu.setEnabled(true);
        }
        else menu.setEnabled(false);
    }
        /**
         * Fills the appriate Slice/Projection menu items into a user
         * provided menu. This routine can be used to make a function
         * fitting menu available to an external application without having
         * to rewrite a bunch of code already contained in JASHist
         *
         * @param menu The menu to which the items will be added
         */
    public void fillSliceMenu(JMenu menu)
    {
        if (dataManager instanceof TwoDDataManager)
        {
            ((TwoDDataManager) dataManager).fillSliceMenu(menu);
            menu.setEnabled(true);
        }
        else menu.setEnabled(false);
    }
        /**
         * Get the text of the title.
         * @return A String containing the text of the title, or null if there is no title at present
         * @see #getTitleObject()
         */
    public String getTitle()
    {
        if (title == null) return null;
        return title.getText();
    }
        /**
         * Actually get the Title object.
         * @return The current title, or null if there is no title
         */
    public Title getTitleObject()
    {
        return title;
    }
        /**
         * Actually set the Title object.
         * @param newTitle The new title object
         */
    public void setTitleObject(Title newTitle)
    {
        if (title != null)
        {
            contentPane.remove(title);
        }
        title = newTitle;
        contentPane.add(title);
        contentPane.invalidate();
        setChanged();
    }
        /**
         * Set the text of the title
         * @param newValue pass <code>null</code> to remove the title
         */
    public void setTitle(String newValue)
    {
        if (newValue != null && newValue.length() != 0)
        {
            if (title == null)
            {
                title = new Title(newValue);
                contentPane.add(title);
                contentPane.invalidate();
                validate();
            }
            else
                title.setText(newValue);
            setChanged();
        }
        else if (title != null)
        {
            contentPane.remove(title);
            contentPane.invalidate();
            validate();
            title = null;
        }
    }
    public boolean isChanged()
    {
        return changed;
    }
    private void setChanged()
    {
        changed = true;
        validate();
        repaint(); // does this cause double repaint if the component was invalidated?
    }
        /**
         * Add a FitListener that will receive notifications about the
         * status of fits being performed by the plot
         * @param fitListener The FitListener to add
         */
    //TODO: Dont we need a removeFitListener?
    public static void addFitListener(final FitListener fitListener)
    {
        if (fitListeners == null)
            fitListeners = new Vector(1, 1);
        fitListeners.addElement(fitListener);
    }
    static void notifyFitListeners(final Fitter fitter)
    {
        if (fitListeners != null)
        {
            final Enumeration e = fitListeners.elements();
            while (e.hasMoreElements())
                ((FitListener) e.nextElement()).fitStarted(fitter);
        }
    }
    public void mouseEventNotify(final MouseEvent e)
    {
        processMouseEvent(e);
    }
    public void deselected()
    {
        contentPane.deselected();
    }
    public StatisticsBlock getStats()
    {
        return stats;
    }
    public void setStats(StatisticsBlock newStats)
    {
        stats = newStats;
    }
    public Legend getLegend()
    {
        return legend;
    }
    public void setLegend(Legend newLegend)
    {
        legend = newLegend;
    }
        /**
         * Pops up a dialog asking the user to choose a file/format to save the plot.
         */
    public void saveAs()
    {
       //types.add(new SaveAsPluginAdapter(new org.freehep.graphics2d.exportchooser.EPS2DExportFileType()));
       //types.add(new SaveAsPluginAdapter(new org.freehep.graphics2d.exportchooser.PDF2DExportFileType()));
       //types.add(new SaveAsPluginAdapter(new org.freehep.graphics2d.exportchooser.SVG2DExportFileType()));
       SaveAsDialog dlg = new SaveAsDialog(this);
       dlg.pack();
       dlg.doModal(); 
    }
    /**
     * Copies the plot to the clipboard
     */
    public void copy()
    {
       VectorGraphicsTransferable t = new VectorGraphicsTransferable(this);
       getToolkit().getSystemClipboard().setContents(t,t);
    }
    
    static private Vector fitListeners;
    private Title title; // The plot title
    private StatisticsBlock stats;
    private boolean isInit = false;
    private boolean changed = false;
    private int showLegend = JASHist.LEGEND_AUTOMATIC;
    
    private DataArea dataArea;
    private DataManager dataManager;
    private Legend legend;
    private PlotPanel contentPane = new JASHistPlotPanel();
    
    static final long serialVersionUID = 4433180397297758071L;
    
    final private class JASHistPlotPanel extends PlotPanel implements HasPopupItems
    {
        public void print(Graphics g) {
            jas.plot.PrintHelper ph = jas.plot.PrintHelper.instance();
            Thread t = ph.printingThread();
            if ( ! ph.isPrinting() )
                ph.setPrintingThread(Thread.currentThread());       
            super.print(g);
            ph.setPrintingThread(t);
        }
        public void modifyPopupMenu(final JPopupMenu menu, final Component source)
        {
            if (menu.getComponentCount() > 0) menu.addSeparator();
            JCheckBoxMenuItem dl = new JCheckBoxMenuItem("Default Plot Layout")
            {
                final protected void fireActionPerformed(final ActionEvent e)
                {
                    contentPane.restoreDefaultLayout();
                }
            };
            boolean def = contentPane.hasDefaultLayout();
            dl.setSelected(def);
            dl.setEnabled(!def);
            menu.add(dl);
            
            menu.add(new JMenuItem("Plot Properties...")
            {
                final protected void fireActionPerformed(final ActionEvent e)
                {
                    showProperties();
                }
            });
             
            menu.add(new JMenuItem("Copy Plot to Clipboard...")
            {
                final protected void fireActionPerformed(final ActionEvent e)
                {
                   copy();
                }
            });
            menu.add(new JMenuItem("Save Plot As...")
            {
                final protected void fireActionPerformed(final ActionEvent e)
                {
                   saveAs();
                }
            });
            menu.add(new JMenuItem("Print Plot...")
            {
                final protected void fireActionPerformed(final ActionEvent e)
                {
                    try
                    {
                        PrintHelper ph = PrintHelper.instance();
                        ph.printTarget(JASHistPlotPanel.this);
                    }
                    catch (Exception x)
                    {
                        x.printStackTrace(); // TODO: Something better
                    }
                }
            });
        }
    }
    
    private final class SetTitleMenuItem extends JCheckBoxMenuItem
    {
        SetTitleMenuItem()
        {
            super("Show Title");
            setSelected(title != null && title.isVisible());
        }
        protected void fireActionPerformed(final ActionEvent e)
        {
            if (title == null || title.getText().length() == 0)
            {
                setTitle("Title");
                title.edit();
            }
            else
            {
                title.setVisible(!title.isVisible());
            }
        }
    }
    private class JASHistDataArea extends DataArea
    {
        JASHistDataArea(Axis xAxis, Axis yAxis)
        {
            super(xAxis,yAxis);
        }
        public void modifyPopupMenu(final JPopupMenu menu, final Component source)
        {
            if (source == this || !(source instanceof HasPopupItems)) // Not the axis for example
            {
                // Begin the main histogram popup menu, with bunches of Show....
                /*
                menu.add(new SetTitleMenuItem());
                JCheckBoxMenuItem ss = new JCheckBoxMenuItem("Show Statistics")
                {
                    public void fireActionPerformed(ActionEvent e)
                    {
                        setShowStatistics(!getShowStatistics());
                    }
                };
                ss.setSelected(getShowStatistics());
                menu.add(ss);
                */
                dataManager.modifyPopupMenu(menu, source);
                super.modifyPopupMenu(menu,source);
            }
        }
    }
}
class JASEvent extends AWTEvent
{
    JASEvent(Component target)
    {
        super(target,AWTEvent.RESERVED_ID_MAX+5000);
    }
}
