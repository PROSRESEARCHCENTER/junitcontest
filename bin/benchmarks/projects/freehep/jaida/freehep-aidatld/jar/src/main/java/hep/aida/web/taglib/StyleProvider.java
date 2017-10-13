package hep.aida.web.taglib;

import hep.aida.IBaseStyle;

import javax.servlet.jsp.JspException;

/**
 * Interface for classes that provide AIDA styles.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface StyleProvider {

    /**
     * This style type is reserved for getting the IPlotterStyle (top-level)
     */
    public static String plotterStyle = "plotter";
    
    IBaseStyle getStyle() throws JspException;

    IBaseStyle getStyle(String type) throws JspException;
}