package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.TupleTag;
import hep.aida.web.taglib.TupleTagSupport;

import java.io.IOException;

import hep.aida.ITuple;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA team @ SLAC.
 *  
 */
public class TupleTagImpl extends SimpleTagSupport implements TupleTag {

    private TupleTagSupport tupleTagSupport = new TupleTagSupport();

    public TupleTagSupport getTupleTagSupport() {
        return tupleTagSupport;
    }

    public void doTag() throws JspException, IOException {

        JspContext jspContext = getJspContext();

        tupleTagSupport.doStartTag();

        tupleTagSupport.doEndTag((PageContext) jspContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setVar(java.lang.String)
     */
    public void setVar(String var) {
        tupleTagSupport.setVar(var);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setScope(java.lang.String)
     */
    public void setScope(String scope) {
        tupleTagSupport.setScope(scope);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setQuery(java.sql.ResultSet)
     */
    public void setQuery(Object resultSet) {
        tupleTagSupport.setQuery(resultSet);
    }
}