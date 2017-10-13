package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.PlotterStyleEditorTag;
import hep.aida.web.taglib.PlotterStyleEditorTagSupport;
import hep.aida.web.taglib.util.AidaTLDUtils;
import hep.aida.web.taglib.util.StyleUtils;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotterStyleEditorTagImpl extends SimpleTagSupport implements PlotterStyleEditorTag {
    
    private PlotterStyleEditorTagSupport pe;
    
    // can set those variables from the tag
    private String name                 = "plotterStyleEditorForm";
    private String action               = null;
    private String var                  = "plotterStyleEditorObject";
    private String background           = "orange";
    private String selectorBackground   = "D0D0D0";
    private String selectorText         = "Change Plot Style";
    private boolean showAlways          = false;
    private boolean includeStatistics   = true;
    private boolean includeLegend       = true;
    private boolean includeError        = true;
    private boolean includeMarker       = true;
    private boolean includeNormalization= true;
    private boolean includeComparison   = false;
    
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        ServletRequest req = pageContext.getRequest();
        
        boolean save = false;
        if (!AidaTLDUtils.isEmpty(var)) {
            pe = (PlotterStyleEditorTagSupport) pageContext.getSession().getAttribute(var);
        }
        if (pe == null) {
            pe  = new PlotterStyleEditorTagSupport();
            save = true;
        }
        
        // set pe with attributes from the tag
        if (!AidaTLDUtils.isEmpty(action)) pe.setAction(action);
        if (!AidaTLDUtils.isEmpty(background)) pe.setBackground(background);
        if (!AidaTLDUtils.isEmpty(name)) pe.setName(name);
        if (!AidaTLDUtils.isEmpty(selectorBackground)) pe.setSelectorBackground(selectorBackground);
        if (!AidaTLDUtils.isEmpty(selectorText)) pe.setSelectorText(selectorText);
        if (!AidaTLDUtils.isEmpty(var)) pe.setVar(var);
        
        if (showAlways) pe.setShowAlways(showAlways);
        if (!includeStatistics) pe.setIncludeStatistics(includeStatistics);
        if (!includeLegend) pe.setIncludeLegend(includeLegend);
        if (!includeError) pe.setIncludeError(includeError);
        if (!includeMarker) pe.setIncludeMarker(includeMarker);
        if (!includeNormalization) pe.setIncludeNormalization(includeNormalization);
        if (includeComparison) pe.setIncludeComparison(includeComparison);
        
        String form = null;
        if (!AidaTLDUtils.isEmpty(req.getParameter(name+"Par"))) {
            
            // the form has been submitted, now set possible parameters
            if (!AidaTLDUtils.isEmpty(req.getParameter("markerSize"))) pe.setMarkerSize(req.getParameter("markerSize"));
            if (!AidaTLDUtils.isEmpty(req.getParameter("markerShape"))) pe.setMarkerShape(req.getParameter("markerShape"));
            if (!AidaTLDUtils.isEmpty(req.getParameter("normalization"))) pe.setNormalization(req.getParameter("normalization"));
            if (!AidaTLDUtils.isEmpty(req.getParameter("statCompareAlgorithm"))) pe.setStatCompareAlgorithm(req.getParameter("statCompareAlgorithm"));
            
            if (!AidaTLDUtils.isEmpty(req.getParameter("showPlotStyle")))
                pe.setShowPlotStyle(true);
            else
                pe.setShowPlotStyle(false);
            
            if (!AidaTLDUtils.isEmpty(req.getParameter("showStatistics")))
                pe.setShowStatistics(true);
            else
                pe.setShowStatistics(false);
            
            if (!AidaTLDUtils.isEmpty(req.getParameter("showLegend")))
                pe.setShowLegend(true);
            else
                pe.setShowLegend(false);
            
            if (!AidaTLDUtils.isEmpty(req.getParameter("showError")))
                pe.setShowError(true);
            else
                pe.setShowError(false);
        }
        try {
            form = pe.createForm();
            Writer out = pageContext.getOut();
            out.write(form);
        } catch (IOException e) {
            throw new JspException(e);
        }
        
        if (!save || AidaTLDUtils.isEmpty(var)) return;
        pageContext.getSession().setAttribute(var, pe);
    }
    
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setBackground(String background) {
        this.background = background;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSelectorBackground(String selectorBackground) {
        this.selectorBackground = selectorBackground;
    }
    
    public void setSelectorText(String selectorText) {
        this.selectorText = selectorText;
    }
    
    public void setShowAlways(boolean showAlways) {
        this.showAlways = showAlways;
    }
    
    public void setVar(String var) {
        this.var = var;
    }

    public void setIncludeStatistics(boolean includeStatistics) {
        this.includeStatistics = includeStatistics;
    }

    public void setIncludeLegend(boolean includeLegend) {
        this.includeLegend = includeLegend;
    }

    public void setIncludeError(boolean includeError) {
        this.includeError = includeError;
    }

    public void setIncludeMarker(boolean includeMarker) {
        this.includeMarker = includeMarker;
    }
    
    public void setIncludeNormalization(boolean includeNormalization) {
        this.includeNormalization = includeNormalization;
    }

    public void setIncludeComparison(boolean includeComparison) {
        this.includeComparison = includeComparison;
    }
    
    
}