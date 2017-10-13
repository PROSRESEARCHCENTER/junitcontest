/*
 * plotterStyleEditor.java
 *
 * Created on September 27, 2007, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hep.aida.web.taglib;

import hep.aida.web.taglib.util.StyleUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 *
 * @author serbo
 */
public class PlotterStyleEditorTagSupport implements PlotterStyleEditorTag {
    // can set those variables from the tag
    private String name                 = null;
    private String action               = null;
    private String var                  = null;
    private String background           = null;
    private String selectorBackground   = null;
    private String selectorText         = null;
    private boolean showAlways          = false;
    private boolean includeStatistics   = true;
    private boolean includeLegend       = true;
    private boolean includeError        = true;
    private boolean includeMarker       = true;
    private boolean includeNormalization= true;
    private boolean includeComparison   = false;
    
    // can not set those through the tag attributes
    private static String no_selection = "none";
    private static String[] size = new String[] {no_selection, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static String[] norm = new String[] {no_selection, "Area", "Entries"};
    
    private boolean showPlotStyle       = false;
    private boolean showStatistics      = false;
    private boolean showLegend          = false;
    private boolean showError           = false;
    private int numberOfOverlays        = -1;
    private String markerShape          = no_selection;
    private String markerSize           = no_selection;
    private String normalization        = no_selection;
    private String statCompareAlgorithm = no_selection;
    
    public String createForm() {
        String[] algorithms = StyleUtils.statCompareAlgorithmNames();
        String selected = "";
        String display = "none";
        String tmp = "";
        tmp += "<form name=\""+name+"\" action=\""+action+"\">\n";
        tmp += "<TABLE>\n";
        tmp += "    <TR><TD>\n";
        
        if (showPlotStyle) { display="block"; selected="checked"; } else { display="none"; selected=""; }
        if (!showAlways) {
            tmp += "        <TABLE bgcolor=\""+selectorBackground+"\" border=\"1\">\n";
            tmp += "            <TR><TD>\n";
            tmp += "                <input type=\"checkbox\" name=\"showPlotStyle\" onClick=\"this.form.submit()\" "+selected+">"+selectorText+"\n";
            tmp += "            </TD></TR>\n";
            tmp += "        </TABLE>\n";
            tmp += "    </TD><TD>\n";
            tmp += "            <div id=\"stylePanel\" style=\"display: "+display+";\">\n";
        }
        tmp += "                <TABLE  bgcolor=\""+background+"\" border=\"1\">\n";
        tmp += "                    <TR>\n";
        if (includeStatistics) {
            tmp += "                        <TD title=\"Show Statistics\">\n";
            if (showStatistics) { selected="checked"; } else { selected=""; }
            tmp += "                            <input type=\"checkbox\" name=\"showStatistics\" "+selected+" onClick=\"this.form.submit()\">Statistics&nbsp;\n";
            tmp += "                        </TD>\n";
        }
        if (includeLegend) {
            tmp += "                        <TD title=\"Show Legend\">\n";
            if (showLegend) { selected="checked"; } else { selected=""; }
            tmp += "                            <input type=\"checkbox\" name=\"showLegend\" "+selected+" onClick=\"this.form.submit()\">Legend&nbsp;\n";
            tmp += "                        </TD>\n";
        }
        if (includeError) {
            tmp += "                        <TD title=\"Show Errors\">\n";
            if (showError) { selected="checked"; } else { selected=""; }
            tmp += "                            <input type=\"checkbox\" name=\"showError\" "+selected+" onClick=\"this.form.submit()\">Errors&nbsp;\n";
            tmp += "                        </TD>\n";
        }
        if (includeMarker) {
            tmp += "                        <TD title=\"Select Marker Size\">\n";
            tmp += "                            Marker Size:&nbsp;\n";
            tmp += "                            <select name=\"markerSize\" onChange=\"this.form.submit()\">\n";
            for (int i=0; i<size.length; i++) {
                if (markerSize.equals(size[i])) { selected="selected"; } else { selected=""; }
                tmp += "                                <option "+selected+">\n";
                tmp += "                                    "+size[i]+"\n";
                tmp += "                                </option>\n";
            }
            tmp += "                            </select>\n";
            tmp += "                        </TD>\n";
        }
        if (includeNormalization) {
            tmp += "                        <TD title=\"Select Normalization for the Refrence Plot\">\n";
            tmp += "                            Norm:&nbsp;\n";
            tmp += "                            <select name=\"normalization\" onChange=\"this.form.submit()\">\n";
            for (int i=0; i<norm.length; i++) {
                if (normalization.equals(norm[i])) { selected="selected"; } else { selected=""; }
                tmp += "                                <option "+selected+">\n";
                tmp += "                                    "+norm[i]+"\n";
                tmp += "                                </option>\n";
            }
            tmp += "                            </select>\n";
            tmp += "                        </TD>\n";
        }
        if (includeComparison) {
            tmp += "                        <TD title=\"Statistical Comparison of Plots\">\n";
            tmp += "                            Compare:&nbsp;\n";
            tmp += "                            <select name=\"statCompareAlgorithm\" onChange=\"this.form.submit()\">\n";
            if (statCompareAlgorithm.equals(no_selection)) { selected="selected"; } else { selected=""; }
            tmp += "                                <option "+selected+">\n";
            tmp += "                                    "+no_selection+"\n";
            tmp += "                                </option>\n";
            for (int i=0; i<algorithms.length; i++) {
                if (statCompareAlgorithm.equals(algorithms[i])) { selected="selected"; } else { selected=""; }
                tmp += "                                <option "+selected+">\n";
                tmp += "                                    "+algorithms[i]+"\n";
                tmp += "                                </option>\n";
            }
            tmp += "                            </select>\n";
            tmp += "                        </TD>\n";
        }
        tmp += "                      </TR>\n";
        tmp += "                    </TABLE>\n";
        if (!showAlways) tmp += "                </div>\n";
        tmp += "        </TD></TR>\n";
        tmp += "    </TABLE>\n";
        tmp += "    <input type=\"hidden\" name=\""+name+"Par"+"\" value=\"true\"/>\n";
        tmp += "</form>\n";
        
        return tmp;
    }
    
    
    // get/set methods
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    public String getBackground() {
        return background;
    }
    
    public void setBackground(String background) {
        this.background = background;
    }
    
    public String getSelectorBackground() {
        return selectorBackground;
    }
    
    public void setSelectorBackground(String selectorBackground) {
        this.selectorBackground = selectorBackground;
    }
    
    public String getSelectorText() {
        return selectorText;
    }
    
    public void setSelectorText(String selectorText) {
        this.selectorText = selectorText;
    }
    
    public boolean isShowAlways() {
        return showAlways;
    }
    
    public void setShowAlways(boolean showAlways) {
        this.showAlways = showAlways;
    }
    
    public boolean isShowPlotStyle() {
        return showPlotStyle;
    }
    
    public void setShowPlotStyle(boolean showPlotStyle) {
        this.showPlotStyle = showPlotStyle;
    }
    
    public boolean isShowStatistics() {
        return showStatistics;
    }
    
    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }
    
    public boolean isShowLegend() {
        return showLegend;
    }
    
    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }
    
    public boolean getShowError() {
        return showError;
    }
    
    public void setShowError(boolean showError) {
        this.showError = showError;
    }
    
    public String getMarkerShape() {
        return markerShape;
    }
    
    public void setMarkerShape(String markerShape) {
        this.markerShape = markerShape;
    }
    
    public int getNumberOfOverlays() {
        return numberOfOverlays;
    }
    
    public void setNumberOfOverlays(int numberOfOverlays) {
        this.numberOfOverlays = numberOfOverlays;
    }
    
    public String getMarkerSize() {
        return markerSize;
    }
    
    public void setMarkerSize(String markerSize) {
        this.markerSize = markerSize;
    }
    
    public String getNormalization() {
        return normalization;
    }
    
    public void setNormalization(String normalization) {
        this.normalization = normalization;
    }
    
    public String getStatCompareAlgorithm() {
        return statCompareAlgorithm;
    }
    
    public void setStatCompareAlgorithm(String statCompareAlgorithm) {
        this.statCompareAlgorithm = statCompareAlgorithm;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setIncludeStatistics(boolean includeStatistics) {
        this.includeStatistics = includeStatistics;
    }
    
    public boolean getIncludeStatistics() {
        return includeStatistics;
    }
    
    public void setIncludeLegend(boolean includeLegend) {
        this.includeLegend = includeLegend;
    }
    
    public boolean getIncludeLegend() {
        return includeLegend;
    }
    
    public void setIncludeError(boolean includeError) {
        this.includeError = includeError;
    }
    
    public boolean getIncludeError() {
        return includeError;
    }
    
    public void setIncludeMarker(boolean includeMarker) {
        this.includeMarker = includeMarker;
    }
    
    public boolean getIncludeMarker() {
        return includeMarker;
    }
    
    public void setIncludeNormalization(boolean includeNormalization) {
        this.includeNormalization = includeNormalization;
    }
    
    public boolean getIncludeNormalization() {
        return includeNormalization;
    }
    
    public void setIncludeComparison(boolean includeComparison) {
        this.includeComparison = includeComparison;
    }
    
    public boolean getIncludeComparison() {
        return includeComparison;
    }
    
}
