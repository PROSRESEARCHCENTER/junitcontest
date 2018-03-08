package hep.aida.web.taglib.jsp20;

import hep.aida.ITuple;
import hep.aida.IManagedObject;
import hep.aida.web.taglib.TupleProjectionTag;
import hep.aida.web.taglib.TupleProjectionTagSupport;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA team @ SLAC.
 *
 */
public class TupleProjectionTagImpl extends SimpleTagSupport implements TupleProjectionTag {
    
    private TupleProjectionTagSupport tupleProjectionTagSupport = new TupleProjectionTagSupport();
    
    public TupleProjectionTagSupport getTupleProjectionTagSupport() {
        return tupleProjectionTagSupport;
    }
    
    public void doTag() throws JspException {
        
        JspContext jspContext = getJspContext();
        
        tupleProjectionTagSupport.doStartTag();
        
        tupleProjectionTagSupport.doEndTag((PageContext) jspContext);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setVar(java.lang.String)
     */
    public void setVar(String var) {
        tupleProjectionTagSupport.setVar(var);
    }
    public void setName(String var) {
        tupleProjectionTagSupport.setName(var);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setScope(java.lang.String)
     */
    public void setScope(String scope) {
        tupleProjectionTagSupport.setScope(scope);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setXbins(int)
     */
    public void setXbins(int xbins) {
        tupleProjectionTagSupport.setXbins(xbins);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setYbins(int)
     */
    public void setYbins(int ybins) {
        tupleProjectionTagSupport.setYbins(ybins);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setTuple(java.lang.String)
     */
    public void setTuple(ITuple tuple) {
        tupleProjectionTagSupport.setTuple(tuple);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setXprojection(java.lang.String)
     */
    public void setXprojection(String xproj) {
        tupleProjectionTagSupport.setXprojection(xproj);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setYprojection(java.lang.String)
     */
    public void setYprojection(String yproj) {
        tupleProjectionTagSupport.setYprojection(yproj);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setFilter(java.lang.String)
     */
    public void setFilter(String filter) {
        tupleProjectionTagSupport.setFilter(filter);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setXmin(double)
     */
    public void setXmin(double xmin) {
        tupleProjectionTagSupport.setXmin(xmin);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setXmax(double)
     */
    public void setXmax(double xmax) {
        tupleProjectionTagSupport.setXmax(xmax);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setYmin(double)
     */
    public void setYmin(double ymin) {
        tupleProjectionTagSupport.setYmin(ymin);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.TupleProjectionTag#setYmax(double)
     */
    public void setYmax(double ymax) {
        tupleProjectionTagSupport.setYmax(ymax);
    }
}