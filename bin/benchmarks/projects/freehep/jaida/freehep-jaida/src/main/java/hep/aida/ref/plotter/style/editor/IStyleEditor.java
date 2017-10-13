package hep.aida.ref.plotter.style.editor;

import hep.aida.IPlotterStyle;

public interface IStyleEditor {
    
    void edit(IPlotterStyle style, String title, boolean showPreview);

    void edit(IPlotterStyle[] style, String title, boolean showPreview);
}
