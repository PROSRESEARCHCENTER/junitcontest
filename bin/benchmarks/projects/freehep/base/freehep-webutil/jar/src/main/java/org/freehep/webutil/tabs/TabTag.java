/**
 *	@author The FreeHEP team @ SLAC
 *
 */
package org.freehep.webutil.tabs;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.freehep.webutil.util.writer.JspResponseWriter;
import org.freehep.webutil.util.Constants;


public class TabTag extends SimpleTagSupport {
    
    private JspResponseWriter bodyOut = new JspResponseWriter();
    private String name;
    private String href = null;
    private String target = null;
    private TabsTag tabs = null;
    private String value;
    
    public void doTag() throws JspException, IOException {
        tabs = (TabsTag) findAncestorWithClass(this,TabsTag.class);
        if ( tabs == null )
            throw new JspException("A TAB tag must be encapsulated inside a TABS tag.");
        addToContainer();
        if ( getTabs().isTabSelected(this) )
            if ( getJspBody() != null )
                printBody(bodyOut, getTabs().getUsestylesheet());
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getTarget() {
        return target;
    }
    
    private TabsTag getTabs() {
        return tabs;
    }
    
    private void addToContainer() {
        getTabs().addChild(this);
    }
    
    boolean hasBody() {
        return getJspBody() != null;
    }
    
    private void printBody(JspResponseWriter out, boolean useStyle) throws JspException, IOException {
        if ( hasBody() ) {
            out.lineBreak();
            out.startElement(Constants.DIV);
            if ( ! useStyle )
                out.attribute(Constants.STYLE,"padding:0px;border-color: "+getTabs().getSelectedColor()+"; border-style: solid; border-width: 1px; clear: both;");
            out.attribute(Constants.CLASS,Constants.TAB_BODY);
            out.text(" ");
            getJspBody().invoke(out.getWriter());
            out.lineBreak();
            out.endElement(Constants.DIV);
        }
    }
    
    JspResponseWriter getWriter() {
        return bodyOut;
    }
    
    String getSelectionValue() {
        if ( getValue() != null )
            return getValue();
        return getName();
    }
}
