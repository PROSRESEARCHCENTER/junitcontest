/**
 *	@author The FreeHEP team @ SLAC
 *
 */
package org.freehep.webutil.tabs;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.freehep.webutil.util.properties.PropertiesLoader;
import org.freehep.webutil.util.writer.JspResponseWriter;
import org.freehep.webutil.util.Constants;


public class TabBodyTag extends SimpleTagSupport {
    
    private String color;
    private JspResponseWriter out = new JspResponseWriter();
    
    public TabBodyTag() {
        super();
        this.color = PropertiesLoader.tabsSelectedColor();
    }
    
    public void doTag() throws JspException, IOException {
        if ( getJspBody() != null ) {
            out.lineBreak();
            out.startElement(Constants.DIV);
            out.attribute(Constants.STYLE,"padding:0px;border-color: "+getColor()+"; border-style: solid; border-width: 1px; clear: both;");
            out.attribute(Constants.CLASS,Constants.TAB_BODY);
            out.text(" ");
            getJspBody().invoke(out.getWriter());
            out.lineBreak();
            out.endElement(Constants.DIV);
            getJspContext().getOut().print(out.getBuffer());
        }
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
}
