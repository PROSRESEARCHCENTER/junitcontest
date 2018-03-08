package hep.aida.web.taglib;

import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.ext.IComparisonResult;
import hep.aida.util.comparison.StatisticalComparison;
import hep.aida.web.taglib.util.LogUtils;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all TreeTag classes.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public class StatCompareTagSupport implements StatCompareTag {
    
    private Object var1;
    private Object var2;
    private String algorithm;
    private String options = null;
    private String resultVar = null;
    private boolean verbose = true;
    
    
    public void doStartTag() throws JspException {
    }
    
    public void doEndTag(PageContext pageContext) throws JspException {
        long t0 = System.currentTimeMillis();
        IComparisonResult result = null;
        IManagedObject obj1 = getObject(var1, pageContext);
        IManagedObject obj2 = getObject(var2, pageContext);
        
        if (obj1 == null)
            throw new JspException("nothing to compare (var1 resolves to null)");
        
        if (obj2 == null)
            throw new JspException("nothing to compare (var2 resolves null)");
        
        if (obj1 instanceof ICloud1D) {
            boolean can = StatisticalComparison.canCompare((ICloud1D) obj1, (ICloud1D) obj2, algorithm);
            if (!can)
                throw new JspException("Can not use "+algorithm+" algorithm to compare "+obj1+" and "+obj2);
            result = StatisticalComparison.compare((ICloud1D) obj1, (ICloud1D) obj2, algorithm, options);
        } else if (obj1 instanceof IHistogram1D) {
            boolean can = StatisticalComparison.canCompare((IHistogram1D) obj1, (IHistogram1D) obj2, algorithm);
            if (!can)
                throw new JspException("Can not use "+algorithm+" algorithm to compare "+obj1+" and "+obj2);
            result = StatisticalComparison.compare((IHistogram1D) obj1, (IHistogram1D) obj2, algorithm, options);
        } else {
            // Just fail quietly
            //throw new JspException("Can not compare "+obj1+" and "+obj2);
        }
        if (resultVar != null && !resultVar.trim().equals("")) {
            pageContext.setAttribute(resultVar, result, PageContext.PAGE_SCOPE);
        }
        if (verbose) {
            try {
                Writer writer = pageContext.getOut();
                if (result != null) {
                    writer.write("Quality="+result.quality()+", ndf="+result.nDof());
                } else {
                    writer.write("NULL result comparing "+obj1.name()+" and "+obj2.name());
                }
            } catch (IOException e) {
                throw new JspException(e);
            }
        }
        long t1 = System.currentTimeMillis();
        LogUtils.log().debug(" StatCompareTagSupport ::  compare: "+obj1.name()+" and "+obj2.name()+", algorithm="+getAlgorithm()+
                ", *** Total Time = "+(t1-t0));
    }
    
    public void setVar1(Object obj) {
        this.var1 = obj;
    }
    
    public void setVar2(Object obj) {
        this.var2 = obj;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setResultVar(String resultVar) {
        this.resultVar = resultVar;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setOptions(String options) {
        this.options = options;
    }
    
    /**
     * Retrieve an IManagedObject from a var.
     * var can be a reference, or an attribute name
     */
    private IManagedObject getObject(Object var, PageContext pageContext) throws JspException {
        if (var == null) {
            return null;
        }
        
        // First, see if we were passed an appropriate object.
        IManagedObject obj = null;
        if (var instanceof IManagedObject) {
            obj = (IManagedObject) var;
            return obj;
        }
        
        // If we were passed a string, then search all JSP scopes for an
        // IManagedObject with the name.
        if (var instanceof String) {
            String attributeName = (String) var;
            Object tmp = findObject(attributeName, pageContext);
            if (tmp instanceof IManagedObject) {
                obj = (IManagedObject) tmp;
            } else if (obj != null)
                throw new JspException("Can not compare - passed not an IManagedObject: "+tmp);
        }
        
        return obj;
    }
    
    /**
     * Find an Object in a JSP scope under the given attribute name. If
     * nothing is found then return null.
     *
     * @param attributeName the name of the Object in a JSP scope
     * @return the Object if it is found, otherwise null
     */
    private Object findObject(String attributeName, PageContext pageContext) {
        Object obj = null;
        
        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
        PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            obj = (Object) pageContext.getAttribute(attributeName,
                    scope[i]);
            if (obj != null) {
                break;
            }
        }
        
        return obj;
    }
    
}
