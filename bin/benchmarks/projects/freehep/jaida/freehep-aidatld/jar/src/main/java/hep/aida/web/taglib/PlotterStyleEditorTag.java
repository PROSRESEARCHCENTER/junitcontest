package hep.aida.web.taglib;

/**
 *
 * @author serbo
 */
public interface PlotterStyleEditorTag {
    void setAction(String action);

    void setBackground(String background);

    void setName(String name);

    void setSelectorBackground(String selectorBackground);

    void setSelectorText(String selectorText);

    void setShowAlways(boolean showAlways);

    void setVar(String var);
    
    void setIncludeStatistics(boolean includeStatistics);
    
    void setIncludeLegend(boolean includeLegend);
    
    void setIncludeError(boolean includeError);
    
    void setIncludeMarker(boolean includeMarker);
    
    void setIncludeNormalization(boolean includeNormalization);

    void setIncludeComparison(boolean includeComparison);

}
