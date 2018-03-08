package hep.aida.web.taglib;

import hep.aida.IManagedObject;
import hep.aida.ITuple;

/**
 * A top level tag which cretes a projection from an {@link ITuple}.
 * 
 * @author The AIDA team @ SLAC
 *  
 */
public interface TupleProjectionTag {

    public void setName(String name);
    
    /**
     * Set the name of the output variable for the projection; it is an
     * {@link IManagedObject}. This is a required attribute. If the scope is
     * not specified (see {@link #setScope(String)}) then the projection will
     * be stored in <code>page</code> scope.
     * 
     * @param var
     *            the name of the output variable
     * 
     * @see #setScope(String)
     */
    public void setVar(String var);

    /**
     * Set the scope of the output variable. This is an optional attribute, and
     * can be one of <code>page</code>,<code>request</code>,
     * <code>session</code> or <code>application</code>. The default is
     * <code>page</code>.
     * 
     * @param scope scope of the output variable
     * 
     * @see #setVar(String)
     */
    public void setScope(String scope);

    /**
     * Set the {@link ITuple}from which to make the projection. This is a
     * required attribute.
     * 
     * @param tuple
     *            the {@link ITuple}to project.
     */
    public void setTuple(ITuple tuple);

    /**
     * Set the x projection. This is a required attribute.
     * 
     * @param xproj
     *            the value to project.
     */
    public void setXprojection(String xproj);

    /**
     * Set the y projection.
     * 
     * @param yproj
     *            the value to project.
     */
    public void setYprojection(String yproj);

    /**
     * Set the filter.
     * 
     * @param filter
     *            the filter for the projection.
     */
    public void setFilter(String filter);

    /**
     * Set the number of bins along the x on the projection.
     * 
     * @param xbins
     *            The number of bins on the x axis.
     *  
     */
    public void setXbins(int xbins);

    /**
     * Set the number of bins along the y on the projection.
     * 
     * @param ybins
     *            The number of bins on the y axis.
     *  
     */
    public void setYbins(int ybins);
    
    /**
     * Set the lower edge along x.
     * 
     * @param xmin
     *           The lower edge along x.
     */
    public void setXmin(double xmin);

    /**
     * Set the upper edge along x.
     * 
     * @param xmax
     *           The upper edge along x.
     */
    public void setXmax(double xmax);

    /**
     * Set the lower edge along y.
     * 
     * @param ymin
     *           The lower edge along y.
     */
    public void setYmin(double ymin);

    /**
     * Set the upper edge along y.
     * 
     * @param ymax
     *           The upper edge along y.
     */
    public void setYmax(double ymax);
    
}