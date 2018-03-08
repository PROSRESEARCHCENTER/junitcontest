package hep.aida.web.taglib;

import hep.aida.ITree;
import hep.aida.web.taglib.util.LogUtils;
import hep.aida.web.taglib.util.TreeUtils;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all TreeTag classes.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class TreeTagSupport implements TreeTag {

    private String storeName;
    private String storeType;
    private String options = "";


    public void doStartTag() throws JspException {
    }

    public void doEndTag(PageContext pageContext) throws JspException {
        try {
            // Now open the tree.
            long t0 = System.currentTimeMillis();
            ITree tree = TreeUtils.getTree(this, pageContext.getSession().getId());            
            long t1 = System.currentTimeMillis();
            LogUtils.log().debug(" TreeTagSupport ::  name="+getStoreName()+
                    ", *** Total Time = "+(t1-t0));
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setStoreName(java.lang.String)
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreType() {
        return storeType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setOptions(java.lang.String)
     */
    public void setOptions(String options) {
        this.options = options;
    }

    public String getOptions() {
        return options;
    }
}