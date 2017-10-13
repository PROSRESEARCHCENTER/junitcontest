package org.freehep.webutil.tabs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.JspException;
import org.freehep.webutil.util.properties.PropertiesLoader;
import org.freehep.webutil.tabs.servlet.TabCorner;
import org.freehep.webutil.util.writer.JspResponseWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspContext;
import org.freehep.webutil.util.Constants;


/**
 *  @author The FreeHEP team @ SLAC
 *
 */
public class TabsTag extends SimpleTagSupport {
    
    private String name;
    private String href = null;
    private String target = null;
    private String selectedTabValue;
    private boolean useStyleSheet;
    private String id;
    private String var;
    private int scope;
    private String scopeStr = "page";
    private String param = null;
    private String color;
    private String bkgColor;
    private String selectedColor;
    private String position;
    private String align = "left";
    private String margin;
    private boolean showLine;
    private String textStyle;
    private String selectedTextStyle;
    private String servlet = null;
    
    private boolean addAnchor = true;
    
    private int leftImageType;
    private int rightImageType;
    
    private List children;
    private JspResponseWriter out = new JspResponseWriter();
    private JspResponseWriter bodyOut = new JspResponseWriter();
    
    public TabsTag() {
        super();
        this.id = PropertiesLoader.tabsId();
        this.useStyleSheet = Boolean.valueOf(PropertiesLoader.tabsUsestylesheet()).booleanValue();
        this.color = PropertiesLoader.tabsColor();
        this.selectedColor = PropertiesLoader.tabsSelectedColor();
        this.bkgColor = PropertiesLoader.tabsBkgColor();
        this.position = PropertiesLoader.tabsPosition();
//        this.align = PropertiesLoader.tabsAlign();
        this.margin = PropertiesLoader.tabsMargin();
        this.showLine = Boolean.valueOf(PropertiesLoader.tabsShowline()).booleanValue();
        this.textStyle = PropertiesLoader.tabsTextStyle();
        this.selectedTextStyle = PropertiesLoader.tabsSelectedTextStyle();
    }
    
    
    public void doTag() throws JspException, IOException {
        
        checkAttributes();
        
        children = null;
        JspContext jspContext = getJspContext();
        
        // The selected tab MUST be found before the tags are executed, i.e. before getJspBody() is invoked.
        findSelectedTab();
        
        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(jspContext.getOut());
        }
        
        boolean skipStyle = ( getUsestylesheet() && id != null );
        
        // Add an anchor
        out.startElement(Constants.A);
        out.attribute(Constants.NAME,getName());
        out.endElement(Constants.A);

        out.startElement(Constants.DIV);        
        if ( ! skipStyle) {
            //The following is a fix for IE
            if ( getPosition().equals("top") )
                out.attribute(Constants.STYLE,"padding:0px;border-color: white; border-style: solid; border-width: 1px; clear: both;");
        }

                
        out.startElement(Constants.UL);
        
        if ( skipStyle)
            out.attribute(Constants.ID,getId());
        else {
            // UL styles
            out.attribute(Constants.STYLE,"list-style: none;padding: 0px; padding-"+getAlign()+": "+getMargin()+";clear: both;margin: 0;");
        }
        
        TabTag selectedTab = null;
        
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            TabTag tab = (TabTag)iter.next();
            out.startElement(Constants.LI);
            String tabColor;
            String tStyle = textStyle;
            if ( isTabSelected(tab) ) {
                out.attribute(Constants.CLASS,"selected");
                tabColor = getSelectedColor();
                tStyle = selectedTextStyle;
                selectedTab = tab;
            } else {
                tabColor = getColor();
            }
            
            if ( servlet == null )
                servlet = getRequest().getContextPath()+"/tabCornerServlet.jsp";
            String image = "background-image: url("+servlet+"?color="+checkColor(tabColor)+"&bkgColor="+checkColor(bkgColor);
            String leftImage = image+"&type="+leftImageType+");";
            String rightImage = image+"&type="+rightImageType+");";
            
            if ( ! skipStyle ) {
                // LI styles
                out.attribute(Constants.STYLE,"background-color: "+tabColor+"; margin: 0 3px 0 0;padding: 0;background-repeat: no-repeat;float: "+getAlign()+";background-position: "+getPosition()+" right;"+rightImage);
            }
            
            out.startElement(Constants.A);
            String href = getTabHref(tab);
            if ( href.indexOf(Constants.QUESTION_MARK) != -1 )
                href +=  Constants.AMPERSAND;
            else
                href +=  Constants.QUESTION_MARK;
            href += getParamTabSelected() + Constants.EQUALS + tab.getSelectionValue();
            
            // Add anchor to href
            if ( addAnchor )
                href += "#"+getName();            
            out.attribute(Constants.HREF,href);
            
            String target = getTabTarget(tab);
            if ( target != null )
                out.attribute(Constants.TARGET,target);
            
            if ( ! skipStyle ) {
                // A styles
                out.attribute(Constants.STYLE,"width: auto; text-decoration: none;white-space: nowrap;display: block;background-repeat: no-repeat;padding: 5px 15px 5px;float: "+getAlign()+";"+tStyle+";background-position: "+getPosition()+" left;"+leftImage);
            }
            
            out.text(tab.getName());
            out.endElement(Constants.A);
            out.endElement(Constants.LI);
        }
        
        out.endElement(Constants.UL);
        out.endElement(Constants.DIV);
        
        if ( selectedTab != null && selectedTab.hasBody() )
            bodyOut = selectedTab.getWriter();
        else {
            bodyOut.lineBreak();
            bodyOut.startElement(Constants.DIV);
            if ( showLine ) {
                bodyOut.attribute(Constants.STYLE,"padding:0px;border-"+getOtherPosition()+"-color: "+getSelectedColor()+"; border-"+getOtherPosition()+"-style: solid; border-"+getOtherPosition()+"-width: 1px; clear: both;margin: 0px;");
            } else {
                bodyOut.attribute(Constants.STYLE,"clear: both;");
            }
            bodyOut.attribute(Constants.CLASS,Constants.TAB_BODY);
            bodyOut.text(" ");
            bodyOut.lineBreak();
            bodyOut.endElement(Constants.DIV);
        }
        
        if ( getPosition().equals("top") ) {
            getJspContext().getOut().print(out.getBuffer());
            getJspContext().getOut().print(bodyOut.getBuffer());
        } else {
            getJspContext().getOut().print(bodyOut.getBuffer());
            getJspContext().getOut().print(out.getBuffer());
        }
    }
    
    
    
    /**
     *  Tag attribute methods
     *
     */
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setSelectedTabValue(String selectedTabValue) {
        this.selectedTabValue = selectedTabValue;
        if ( getVar() != null && selectedTabValue != null )
            getJspContext().setAttribute(getVar(), selectedTabValue, scope);
    }
    
    public String getSelectedTabValue() {
        return selectedTabValue;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setUsestylesheet(boolean useStyleSheet) {
        this.useStyleSheet = useStyleSheet;
    }
    
    public boolean getUsestylesheet() {
        return useStyleSheet;
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
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setScope(String scope){
        this.scopeStr = scope;
    }
    
    public String getScope() {
        return scopeStr;
    }
    
    public void setParam(String param) {
        this.param = param;
    }
    
    public String getParam() {
        return param;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setMargin(String margin) {
        this.margin = margin;
    }
    
    public String getMargin() {
        return margin;
    }
    
    public void setBkgColor(String bkgColor) {
        this.bkgColor = bkgColor;
    }
    
    public String getBkgColor() {
        return bkgColor;
    }
    
    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }
    
    public String getSelectedColor() {
        return selectedColor;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public void setAddanchor(boolean addAnchor) {
        this.addAnchor = addAnchor;
    }

    public String getPosition() {
        return position;
    }
    /*
    public void setAlign(String align) {
        this.align = align;
    }
*/    
    public String getAlign() {
        return align;
    }
    
    public void setTextstyle(String textStyle) {
        this.textStyle = textStyle;
    }
    
    public String getTextstyle() {
        return textStyle;
    }
    
    public void setSelectedtextstyle(String selectedTextStyle) {
        this.selectedTextStyle = selectedTextStyle;
    }
    
    public String getSelectedtextstyle() {
        return selectedTextStyle;
    }
    
    public void setShowline(boolean showLine) {
        this.showLine = showLine;
    }
    
    public boolean getShowline() {
        return showLine;
    }
    
    public void setServlet(String servlet) {
        this.servlet = servlet;
    }
    
    /**
     * Private/Protected methods.
     *
     */
    
    List getChildren() {
        if (null == children) {
            children = new ArrayList();
        }
        return children;
    }
    
    int getChildCount() {
        return getChildren().size();
    }
    
    void addChild(final TabTag child) {
        if ( getChildCount() == 0 && getSelectedTabValue() == null )
            setSelectedTabValue(child.getSelectionValue());
        getChildren().add(child);
    }
    
    private void findSelectedTab() {
        String selTabName = getRequest().getParameter(getParamTabSelected());
        if ( selTabName == null ) {
            selTabName = (String)getJspContext().getAttribute(getParamTabSelected(), PageContext.SESSION_SCOPE);
            if ( selTabName == null )
                selTabName = getSelectedTabValue();
        } else {
            getJspContext().setAttribute(getParamTabSelected(), selTabName, PageContext.SESSION_SCOPE);
        }
        
        setSelectedTabValue(selTabName);        
    }
    
    private String getParamTabSelected() {
        if ( getParam() != null )
            return getParam();
        return getName()+"-"+Constants.PARAM_SELECTED_TAB;
    }
    
    private String getTabHref(TabTag tab) {
        String href = tab.getHref();
        if ( href != null )
            return href;
        href = getHref();
        if ( href != null )
            return href;
        return getRequest().getRequestURL().toString();
    }
    
    private String getTabTarget(TabTag tab) {
        String target = tab.getTarget();
        if ( target != null )
            return target;
        return getTarget();
    }
    
    private HttpServletRequest getRequest() {
        return (HttpServletRequest)((PageContext)getJspContext()).getRequest();
    }
    
    private void checkAttributes() throws JspException {
        if ( getUsestylesheet() && getId() == null )
            throw new JspException("When using attribute \"usestylesheet\" attribute \"id\" must be set.");
        if ( ! getUsestylesheet() && getId() != null )
            throw new JspException("When using attribute \"id\" attribute \"usestylesheet\" must be set to true.");
        
        this.scope = Constants.getScope(scopeStr);
        
        String position = getPosition();
        if ( ! position.equals("top") && ! position.equals("bottom") )
            throw new JspException("Illegal value "+position+" for attribute \"position\". It must be either \"top\" or \"bottom\".");
        
        if ( position.equals("top") ) {
            leftImageType = TabCorner.UPPER_LEFT;
            rightImageType = TabCorner.UPPER_RIGHT;
        } else {
            leftImageType = TabCorner.LOWER_LEFT;
            rightImageType = TabCorner.LOWER_RIGHT;
        }
        
        String align = getAlign();
        if ( ! align.equals("left") && ! align.equals("right") )
            throw new JspException("Illegal value "+align+" for attribute \"align\". It must be either \"left\" or \"right\".");
        
    }
    
    boolean isTabSelected(TabTag tab) {
        return tab.getSelectionValue().equals( getSelectedTabValue() );
    }
    
    private String checkColor(String color) {
        return color.replaceAll("#", "0x");
    }
    
    private String getOtherPosition() {
        if ( getPosition().equals("top") )
            return "bottom";
        return "top";
    }
}
