package org.freehep.webutil.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter saves specified request parameters in the session.
 * You need to specify "attributeList" as init-param in the
 * declaration of the filter. The value of the parameter is
 * a comma separated list of attributes that need cashing.
 */
public class RequestParameterCashFilter  implements Filter {
    private static String parameterName = "attributeList";
    private FilterConfig filterConfig = null;
    private String[] attributeList;
    
    public RequestParameterCashFilter() {
    }
    
    public String getAttributeList() {
        if (attributeList == null) return null;
        String tmp = "";
        for (int i=0; i<attributeList.length; i++)
            tmp += ", "+attributeList[i];
        
        return tmp;
    }
    
    public void setAttributeList(String list) {
        if (list == null || list.trim().equals("")) attributeList = null;
        else {
            if (list.indexOf(",") < 0) {
                attributeList = new String[] { list };
            } else {
                StringTokenizer st = new StringTokenizer(list, ",");
                ArrayList al = new ArrayList(st.countTokens());
                while (st.hasMoreTokens()) {
                    al.add(st.nextToken().trim());
                }
                attributeList = new String[al.size()];
                attributeList = (String[]) al.toArray(attributeList);
            }
        }
    }
    
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        setAttributeList(filterConfig.getInitParameter(parameterName));
    }
    
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = null;
        HttpServletResponse httpResponse = null;
        
        if (attributeList != null) {
            if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
                httpRequest = (HttpServletRequest) servletRequest;
                httpResponse = (HttpServletResponse) servletResponse;
                
                for (int i=0; i<attributeList.length; i++) {
                    String par = servletRequest.getParameter(attributeList[i]);
                    httpRequest.getSession().setAttribute(attributeList[i], par);
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
        filterConfig = null;
    }
    
}
