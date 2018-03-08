package org.freehep.webutil.tree;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author The FreeHEP team @ SLAC
 * web.servlet name = "fhicon" load-on-startup = "1"
 * web.servlet-mapping url-pattern = "/servlet/FreeHEPIcon"
 */
public class IconServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        res.setHeader("Cache-Control","max-age=86400");
        
        String name = req.getParameter("name");
        name = "images/"+name+".png";
        InputStream imageStream = DefaultIconSet.class.getResourceAsStream(name);
        
        ServletOutputStream out = res.getOutputStream();
        BufferedImage image = ImageIO.read(imageStream);
        ImageIO.write(image,"png",out);
        out.close();
    }
}