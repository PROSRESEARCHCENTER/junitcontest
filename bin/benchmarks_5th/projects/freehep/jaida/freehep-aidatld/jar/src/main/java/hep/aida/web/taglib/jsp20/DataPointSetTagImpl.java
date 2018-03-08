package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.DataPointSetTag;
import hep.aida.web.taglib.DataPointSetTagSupport;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import hep.aida.ITuple;
import hep.aida.IDataPointSet;

/**
 * @author The AIDA team @ SLAC.
 *  
 */
public class DataPointSetTagImpl extends SimpleTagSupport implements DataPointSetTag {

    private DataPointSetTagSupport dataPointSetTagSupport = new DataPointSetTagSupport();

    public DataPointSetTagSupport getDataPointSetTagSupport() {
        return dataPointSetTagSupport;
    }

    public void doTag() throws JspException {

        JspContext jspContext = getJspContext();

        dataPointSetTagSupport.doStartTag();

        dataPointSetTagSupport.doEndTag((PageContext) jspContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setVar(java.lang.String)
     */
    public void setVar(String var) {
        dataPointSetTagSupport.setVar(var);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setScope(java.lang.String)
     */
    public void setScope(String scope) {
        dataPointSetTagSupport.setScope(scope);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setTuple(java.lang.Object)
     */
    public void setTuple(Object tuple) {
        dataPointSetTagSupport.setTuple(tuple);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setXaxisColumns(java.lang.String)
     */
    public void setXaxisColumn(String xAxisColumn) {
        dataPointSetTagSupport.setXaxisColumn(xAxisColumn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setYaxisColumns(java.lang.String)
     */
    public void setYaxisColumn(String yAxisColumn) {
        dataPointSetTagSupport.setYaxisColumn(yAxisColumn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        dataPointSetTagSupport.setTitle(title);
    }

}