package org.freehep.webutil.filter;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.prefs.Preferences;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.freehep.webutil.util.properties.PropertiesLoader;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */

public class NotAvailableFilter implements Filter {
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;
    private boolean isAvailable;
    private String reason = null;
    private String defaultReason = null;
    private String filterPath = null;
    private String nonAvailablePage = null;
    private String adminPassword = null;
    private Preferences prefs;
    
    private String authhead;
    
    public NotAvailableFilter() {
    }
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = null;
        HttpServletResponse httpResponse = null;
        
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            httpRequest = (HttpServletRequest) request;
            httpResponse = (HttpServletResponse) response;
            
            boolean byPass = ( Boolean.valueOf( (String) httpRequest.getSession().getAttribute("isByPassed")).booleanValue() );
            boolean isRedirectPage = ! (nonAvailablePage == null || ( nonAvailablePage != null && !  httpRequest.getRequestURI().endsWith(nonAvailablePage) ) );
            
            if ( reason != null )
                httpRequest.getSession().setAttribute("nonAvailableReason", reason);            
            
            if ( httpRequest != null && httpRequest.getServletPath().startsWith(filterPath)) {
                
                if ( adminPassword == null || authenticate(httpRequest) ) {                    
                    String submit = httpRequest.getParameter("submit");
                    if (httpRequest.getParameter("submit")!=null) {
                        isAvailable = httpRequest.getParameter("disable") == null;
                        reason = httpRequest.getParameter("reason");
                        
                        if ( submit.equals("Bypass") )
                            httpRequest.getSession().setAttribute("isByPassed", "true");
                        if ( submit.equals("Stop Bypass") )
                            httpRequest.getSession().setAttribute("isByPassed", "false");
                    }
                    
                    if ( isAvailable ) {
                        httpRequest.getSession().setAttribute("isByPassed", "false");
                        reason = null;
                    }
                    
                    if ( reason == null || reason.equals("") )
                        reason = defaultReason;                    
                    
                    byPass = ( Boolean.valueOf( (String) httpRequest.getSession().getAttribute("isByPassed")).booleanValue() );
                    
                    PrintWriter writer = response.getWriter();
                    writer.println("<html>");
                    writer.println("<head><title>Not Available Filter</title></head>");
                    writer.println("<body><form>");
                    writer.println("Disable Application<input type=\"checkbox\" name=\"disable\" value=\"true\" "+(isAvailable ? "" : "checked")+"><p>");
                    writer.println("Reason: <textarea width=\"100\" height=\"10\" name=\"reason\">");
                    if ( reason != null ) writer.println(reason);
                    writer.println("</textarea></p>");
                    writer.println("<input type=\"submit\" value=\"Apply\" name=\"submit\">");
                    if ( ! byPass )
                        writer.println("<input type=\"submit\" value=\"Bypass\" name=\"submit\">");
                    else
                        writer.println("<input type=\"submit\" value=\"Stop Bypass\" name=\"submit\">");
                    writer.println("</form>");
                    
                    writer.println("</body>");
                    writer.println("<html>");
                } else {
                    httpResponse.setHeader("WWW-Authenticate","Basic realm=\"Please enter administrator password\"");
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "");
                }
            } else if (!isAvailable &&  ! byPass &&  ! isRedirectPage ) {
                printNonAvailablePage(httpRequest.getContextPath(), nonAvailablePage, response);
            } else {
                if ( !isAvailable && ! isRedirectPage ) {
                    PrintWriter writer = response.getWriter();
                    writer.println("<h2 align=\"center\"><font color=\"red\">The application is currently unavailable to outside users</font></h2>");
                }
                chain.doFilter(httpRequest, response);
            }
        } else
            chain.doFilter(request, response);
    }
    
    /**
     * Destroy method for this filter
     *
     */
    public void destroy() {
        prefs.put("isAvailable", String.valueOf(isAvailable));
        if ( reason == null || reason.equals(defaultReason) )
            prefs.remove("reason");
        else
            prefs.put("reason",reason);
    }
    
    
    /**
     * Init method for this filter
     *
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        loadProperties();
        prefs = Preferences.userNodeForPackage(NotAvailableFilter.class);
        isAvailable = Boolean.valueOf(prefs.get("isAvailable", "true")).booleanValue();
        reason = prefs.get("reason", defaultReason);
    }
    
    
    
    private void printNonAvailablePage(String contextPath, String page, ServletResponse response) throws IOException {
        if ( page == null ) {
            PrintWriter writer = response.getWriter();
            writer.println("<html>");
            writer.println("<head><title>Not Available</title></head>");
            writer.println("<body>");
            writer.println("<h1>Not currently available</h1>");
            if ( reason != null ) writer.println(reason);
            writer.println("</body>");
            writer.println("<html>");
        } else {
            if ( response instanceof HttpServletResponse )
                ((HttpServletResponse) response).sendRedirect(contextPath+page);
            else
                printNonAvailablePage(contextPath, null, response);
        }
    }
    
    private void loadProperties() {
        filterPath = PropertiesLoader.filterPath();
        defaultReason = PropertiesLoader.defaultReason();
        nonAvailablePage = PropertiesLoader.nonAvailablePage();
        adminPassword = PropertiesLoader.adminPassword();
    }
    
    private boolean authenticate(HttpServletRequest req) {
        authhead=req.getHeader("Authorization");
        
        if ( authhead != null )
            return true;
        
        
        if(authhead!=null) {
            if ( req.getAuthType() != null ) {
                //*****Decode the authorisation String*****
                String usernpass = new String(org.apache.commons.codec.binary.Base64.decodeBase64(authhead.substring(6).getBytes()));
                //*****Split the username from the password*****
                String user=usernpass.substring(0,usernpass.indexOf(":"));
                String password=usernpass.substring(usernpass.indexOf(":")+1);
            
                if (password.equals(adminPassword))
                    return true;
            } else 
                return true;
        }
        return false;
    }
    
    
    
}
