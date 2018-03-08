package org.freehep.webutil.tabs.servlet;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.freehep.swing.Headless;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TabCornerServlet extends HttpServlet {
    
    private static String date = "March 14 2006";
    
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        String lastModified = req.getHeader("if-modified-since");
        
        if ( lastModified != null && lastModified.equals(date) ) {
            res.setStatus(res.SC_NOT_MODIFIED);
        } else {
            res.setHeader("Last-Modified", date);
            
            String type  = req.getParameter("type");
            String color = req.getParameter("color");
            String bkgColor = req.getParameter("bkgColor");
            
            TabCorner corner = new TabCorner(Integer.valueOf(type).intValue(),15, color, bkgColor);
            
            ServletOutputStream out = res.getOutputStream();
            res.setContentType("image/png");
            
            Headless h = new Headless(corner);
            corner.setPreferredSize(new Dimension(15,15));
            BufferedImage image = new BufferedImage(15,15,BufferedImage.TYPE_INT_ARGB);
            corner.paint(image.getGraphics());
            h.pack();
            h.setVisible(true);
            ImageIO.write(image,"png",out);
            out.close();
        }
    }
}