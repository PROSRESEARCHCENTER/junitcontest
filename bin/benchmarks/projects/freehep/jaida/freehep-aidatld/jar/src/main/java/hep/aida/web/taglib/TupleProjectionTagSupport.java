package hep.aida.web.taglib;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.IEvaluator;
import hep.aida.IFilter;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.aida.ref.histogram.Cloud1D;
import hep.aida.ref.histogram.Cloud2D;
import hep.aida.web.taglib.util.PlotUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all TupleProjectionTag classes.
 * 
 * @author The AIDA team @ SLAC.
 *  
 */
public class TupleProjectionTagSupport implements TupleProjectionTag {

    private String var,name;

    private String scope = "page";

    private String xProj, yProj;

    private String filter;

    private ITuple tuple;

    private int xbins = 40;

    private int ybins = 40;
    
    private boolean forceConversion = false;
    
    private double xmin = Double.NaN;
    private double xmax = Double.NaN;
    private double ymin = Double.NaN;
    private double ymax = Double.NaN;
    

    private static IAnalysisFactory analysisFactory = IAnalysisFactory.create();

    private static ITreeFactory treeFactory = analysisFactory
            .createTreeFactory();

    public void doStartTag() throws JspException {
        if (var == null || var.length() == 0)
            throw new JspException("var must not be null");

        if (xProj == null || xProj.length() == 0)
            throw new JspException("xprojection must not be null");

        if (tuple == null)
            throw new JspException("tuple must not be null");
    }

    public void doEndTag(PageContext pageContext) throws JspException {

        String scopeName = getScope();
        if (scopeName == null) {
            scopeName = "page";
        }
        int scope = PlotUtils.getScope(scopeName);

        IManagedObject result = null;
        ITree tree = treeFactory.create();
        ITupleFactory tupleFactory = analysisFactory.createTupleFactory(tree);

        IEvaluator xIEvaluator = tupleFactory.createEvaluator(xProj);
        IEvaluator yIEvaluator = null;
        if (yProj != null)
            yIEvaluator = tupleFactory.createEvaluator(yProj);

        IFilter iFilter = null;
        if (filter != null)
            iFilter = tupleFactory.createFilter(filter);

        IHistogramFactory histogramFactory = analysisFactory
                .createHistogramFactory(tree);

        if (yIEvaluator == null) {
            if ( name == null )
                name = "c1";
            Cloud1D c1 = (Cloud1D)histogramFactory.createCloud1D(name);
            if (iFilter == null)
                tuple.project(c1, xIEvaluator);
            else
                tuple.project(c1, xIEvaluator, iFilter);

            double xle = Double.isNaN(getXmin()) ? c1.lowerEdgeWithMargin() : getXmin();
            double xue = Double.isNaN(getXmax()) ? c1.upperEdgeWithMargin() : getXmax();
            
            if ( forceConversion ) {
                c1.convert(xbins, xle, xue);
                result = (IManagedObject) c1.histogram();
            } else
                result = (IManagedObject) c1;

        } else {
            if ( name == null )
                name = "c2";
            Cloud2D c2 = (Cloud2D)histogramFactory.createCloud2D(name);
            if (iFilter == null)
                tuple.project(c2, xIEvaluator, yIEvaluator);
            else
                tuple.project(c2, xIEvaluator, yIEvaluator, iFilter);

            double xle = Double.isNaN(getXmin()) ? c2.lowerEdgeXWithMargin() : getXmin();
            double xue = Double.isNaN(getXmax()) ? c2.upperEdgeXWithMargin() : getXmax();
            double yle = Double.isNaN(getYmin()) ? c2.lowerEdgeYWithMargin() : getYmin();
            double yue = Double.isNaN(getYmax()) ? c2.upperEdgeYWithMargin() : getYmax();

            if ( forceConversion ) {
                c2.convert(xbins, xle, xue, ybins, yle, yue);
                result = (IManagedObject) c2.histogram();
            } else
                result = (IManagedObject) c2;                
        }

        // Store the IManagedObject in a JSP scope.
        if (result != null)
            pageContext.setAttribute(getVar(), result, scope);
        else
            throw new JspException("Could not create the output projection");
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setVar(java.lang.String)
     */
    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setScope(java.lang.String)
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setTuple(java.lang.String)
     */
    public void setTuple(ITuple tuple) {
        this.tuple = tuple;
    }

    public ITuple getTuple() {
        return tuple;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setXprojection(java.lang.String)
     */
    public void setXprojection(String xproj) {
        this.xProj = xproj;
    }

    public String getXprojection() {
        return xProj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setYprojection(java.lang.String)
     */
    public void setYprojection(String yproj) {
        this.yProj = yproj;
    }

    public String getYprojection() {
        return yProj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setFilter(java.lang.String)
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setXbins(int)
     */
    public void setXbins(int xbins) {
        this.forceConversion = true;
        this.xbins = xbins;
    }

    public int getXbins() {
        return xbins;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setYbins(int)
     */
    public void setYbins(int ybins) {
        this.forceConversion = true;
        this.ybins = ybins;
    }

    public int getYbins() {
        return ybins;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setXmin(double)
     */
    public void setXmin(double xmin) {
        this.forceConversion = true;
        this.xmin = xmin;
    }

    public double getXmin() {
        return xmin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setXmax(double)
     */
    public void setXmax(double xmax) {
        this.forceConversion = true;
        this.xmax = xmax;
    }

    public double getXmax() {
        return xmax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setYmin(double)
     */
    public void setYmin(double ymin) {
        this.forceConversion = true;
        this.ymin = ymin;
    }

    public double getYmin() {
        return ymin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleProjectionTag#setYmax(double)
     */
    public void setYmax(double ymax) {
        this.forceConversion = true;
        this.ymax = ymax;
    }

    public double getYmax() {
        return ymax;
    }
}