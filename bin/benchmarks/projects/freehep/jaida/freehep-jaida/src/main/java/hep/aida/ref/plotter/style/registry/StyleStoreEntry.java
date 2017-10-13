package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterStyle;

public class StyleStoreEntry {
    public static String DEFAULT_ENTRY_TYPE = "hep.aida.IHistogram1D";
    private Class previewType = hep.aida.IHistogram1D.class;
    private String name;
    private IPlotterStyle style;
    private IStyleRule rule;
    
    public StyleStoreEntry(String name, IPlotterStyle style, IStyleRule rule) {
        this.name = name;
        this.style = style;
        this.rule = rule;
    }
    
    public String getName() { return name; }
    public IPlotterStyle getStyle() { return style; }
    
    public IStyleRule getRule() { return rule; }
    public void setRule(IStyleRule rule) { this.rule = rule; }
    
    public Class getPreviewType() { return previewType; }
    public void setPreviewType(Class previewType) { this.previewType = previewType; }
    
    public String toString() { return name; }
}