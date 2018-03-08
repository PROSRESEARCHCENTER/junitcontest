package hep.aida.web.taglib;

import hep.aida.IBaseStyle;
import hep.aida.IPlotterStyle;
import hep.aida.web.taglib.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;

/**
 * The implementation class for all StyleTag classes.
 * 
 * @author The AIDA Team @ SLAC.
 *
 */
public class StyleTagSupport implements StyleTag {

    private String type;

    private StyleProvider styleProvider;

    public void doStartTag(StyleProvider styleProvider) throws JspException {
        // Reset per-invocation state.

        this.styleProvider = styleProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.StyleTag#setType(java.lang.String)
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Called by subordinate <styleAttribute>tags.
     * 
     * @param name
     *            The name of the AIDA style attibute
     * @param value
     *            The value of the AIDA style attibute
     */
    public void addAttribute(String name, String value) throws JspException {
        if (LogUtils.log().isDebugEnabled()) {
            String message = "Add attribute: name = "+name+", value = "+value;
            LogUtils.log().debug(message);
        }
        getMyStyle().setParameter(name, value);
    }

    public IBaseStyle getStyle() throws JspException {
        throw new JspException("If you see this you have a logic error");
    }

    public IBaseStyle getStyle(String type) throws JspException {
        if (LogUtils.log().isDebugEnabled()) {
            LogUtils.log().debug("type = " + type);
        }

        IBaseStyle myStyle = getMyStyle();
        try {
            Method method = myStyle.getClass().getMethod(type + "Style",(Class[]) null);                 
            return (IBaseStyle) method.invoke(myStyle, (Object[]) null);
        } catch (NoSuchMethodException e) {
            throw new JspException("Invalid type: " + type);
        } catch (IllegalAccessException e) {
            throw new JspException(e);
        } catch (InvocationTargetException e) {
            throw new JspException(e.getTargetException());
        }
    }

    private IBaseStyle getMyStyle() throws JspException {
        if (type == null) {
            return styleProvider.getStyle();
        } else {
            return styleProvider.getStyle(type);
        }
    }
}