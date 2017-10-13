/**
 *	@author The FreeHEP team @ SLAC
 *
 */
package org.freehep.webutil.util;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;

public abstract class Constants {

    private static Map scopeMap = new HashMap();
    
    static {
        scopeMap.put("page", new Integer(PageContext.PAGE_SCOPE));
        scopeMap.put("request", new Integer(PageContext.REQUEST_SCOPE));
        scopeMap.put("session", new Integer(PageContext.SESSION_SCOPE));
        scopeMap.put("application", new Integer(PageContext.APPLICATION_SCOPE));
    }
    
    public static int getScope(String scopeName) throws JspException {
        Object scopeObj = scopeMap.get(scopeName);
        if ( scopeObj == null )
            throw new JspException("Unknown scope: "+scopeName+". It must be either \"page\",\"request\",\"session\",\"application\"");
        return ((Integer) scopeObj).intValue();
    }
    
    public static final String PARAM_SELECTED_TAB = "freehepTabSelected";
    public static final String TAB_BODY = "freehepTabBody";
            
    public static final String UL     = "ul";
    public static final String LI     = "li";
    public static final String HREF   = "href";
    public static final String NAME   = "name";
    public static final String TARGET = "target";
    public static final String A      = "a";    
    public static final String ID     = "id";
    public static final String CLASS  = "class";
    public static final String STYLE  = "style";
    public static final String DIV    = "div";
    public static final String BR     = "br";
    public static final String QUESTION_MARK = "?";
    public static final String EQUALS        = "=";
    public static final String AMPERSAND     = "&amp;";
}
