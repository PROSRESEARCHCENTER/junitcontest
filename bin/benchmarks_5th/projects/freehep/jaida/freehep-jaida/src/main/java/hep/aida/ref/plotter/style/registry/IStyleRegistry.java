package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterStyle;

public interface IStyleRegistry {

    // To work with Style Stores
    
    String[] getAvailableStoreNames();
    
    IStyleStore getStore(String storeName);
    
    
    // To work with categories, this can be a separate service
    // Available category keys are filled from Rules of all available Stores
    
    String[] getAvailableCategoryKeys();
    
    String[] getAvailableCategoryValues(String categoryKey);
    
    String getCategoryCurrentValue(String categoryKey);
    
    void setCategoryCurrentValue(String categoryKey, String categoryValue);
    
    // Following methods are used to obtain cumulative IPlotterStyle
    // for particular plotter, region, object, action, and (possibly) categories
    
    IPlotterStyle getStyleForState(IPlotterState state);
}
