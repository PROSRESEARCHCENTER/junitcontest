package hep.aida.web.taglib.jsp20;

import hep.aida.util.comparison.StatisticalComparison;
import hep.aida.web.taglib.StatCompareTag;
import hep.aida.web.taglib.StatCompareTagSupport;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public class StatCompareTagImpl extends SimpleTagSupport implements StatCompareTag {
    
    private StatCompareTagSupport statCompareTagSupport = new StatCompareTagSupport();
    
    public void doTag() throws JspException, IOException {
        statCompareTagSupport.doStartTag();
        JspContext jspContext = getJspContext();
        statCompareTagSupport.doEndTag((PageContext) jspContext);
    }
    
    public void setAlgorithm(String var) {
        statCompareTagSupport.setAlgorithm(var);
    }
    
    public void setOptions(String var) {
        statCompareTagSupport.setOptions(var);
    }
    
    public void setVar1(Object var) {
        statCompareTagSupport.setVar1(var);
    }
    
    public void setVar2(Object var) {
        statCompareTagSupport.setVar2(var);
    }
    
    public void setVerbose(boolean verbose) {
        statCompareTagSupport.setVerbose(verbose);
    }
    
    public void setResultVar(String resultVar) {
        statCompareTagSupport.setResultVar(resultVar);
    }
    
}