package hep.aida.web.taglib;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IEvaluator;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.aida.ref.AidaUtils;
import hep.aida.web.taglib.util.PlotUtils;
import hep.aida.web.taglib.util.TreeUtils;
import java.util.ArrayList;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all CloseTreeTag classes.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class ManagedObjectsTagSupport implements ManagedObjectsTag {

    private String storeName;
    private String path;
    private String var;
    private String scope = "page";

    public void doStartTag() throws JspException {
        if (storeName == null || storeName.length() == 0) {
            throw new JspException("storeName must not be null");
        }
        if (path == null || path.length() == 0) {
            throw new JspException("path must not be null");
        }
        if (var == null || var.length() == 0) {
            throw new JspException("var must not be null");
        }
    }
    
    
    public void doEndTag(PageContext pageContext) throws JspException {
        
        String scopeName = getScope();
        if (scopeName == null) {
            scopeName = "page";
        }
        int scope = PlotUtils.getScope(scopeName);
        
        ITree itree = TreeUtils.getTree(getStoreName(), pageContext.getSession().getId());
        if ( itree == null )
            throw new JspException( "Cannot find ITree with name: "+getStoreName() );
        
        ArrayList list = new ArrayList();
        IManagedObject obj = null;
        try {
            obj = itree.find(path);
            list.add(obj);
        } catch (IllegalArgumentException iae) {
            String[] names = null;
            String[] types = null;
            try {
                names = itree.listObjectNames(path);
                for ( int i = 0; i < names.length; i++ ) {
                    String name = names[i];
                    if ( ! name.endsWith("/") )
                        list.add( itree.find(name) );
                }
            } catch (IllegalArgumentException iae2) {
                // Everything else failed, so try to find ITuple in the path 
                // and make a projection

                // Here we assume that column is only top-level, can not plot sub-tuple columns
                String columnPath = AidaUtils.parseName(path);
                String tuplePath = AidaUtils.parseDirName(path);
                obj = itree.find(tuplePath);
                
                /*
                int i = 0;
                String treePath = "";
                boolean isTuple = false;
                String[] p = AidaUtils.stringToArray(path);
                for (i=0; i<p.length; i++) {
                    treePath += "/" + p[i];
                    names = itree.listObjectNames(treePath, false);
                    types = itree.listObjectNames(treePath, false);
                    if (names.length == 1 && types[0].toLowerCase().endsWith("ituple")) {
                        obj = itree.find(names[0]);
                        isTuple = true;
                        break;
                    }
                }
                if (!isTuple) throw iae2;
                
                String columnPath = p[i+i];
                for (int j=i+2; j<p.length; j++) columnPath += "." + p[j];
                System.out.println("columnPath="+columnPath);
                */
                
                IHistogramFactory hf = IAnalysisFactory.create().createHistogramFactory(null);
                ICloud1D c1 = hf.createCloud1D(columnPath);
                
                ITupleFactory tf = IAnalysisFactory.create().createTupleFactory(null);
                IEvaluator ev = tf.createEvaluator(columnPath);
                
                ((ITuple) obj).project(c1, ev);
                list.add(c1);
            }
        }

        pageContext.setAttribute(getVar(), list, scope);
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}