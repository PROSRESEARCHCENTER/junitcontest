package org.freehep.webutil.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter which redirects all incoming http connections to equivalent https connections.
 * @author tonyj
 */
public class RequireSSLFilter implements Filter
{
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response; 
      
      if ("http".equals(request.getScheme()))
      {
         StringBuffer requestURL = httpRequest.getRequestURL();
         String queryString = httpRequest.getQueryString();
         requestURL.replace(0,4,"https");
         if (queryString != null)
         {
            requestURL.append('?');
            requestURL.append(queryString);
         }
         httpResponse.sendRedirect(requestURL.toString());
      }
      else
      {
         chain.doFilter(request, response);
      }
   }

   public void init(FilterConfig filterConfig) throws ServletException
   {
      // Empty
   }

   public void destroy()
   {
      // Empty
   }
}