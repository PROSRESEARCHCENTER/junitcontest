
package hep.aida.ref.plotter;

import hep.aida.IBaseStyle;
import hep.aida.ILegendBoxStyle;
import hep.aida.ILineStyle;


public interface ITickStyle extends IBaseStyle {
    // Number of ticks is determined automatically
    static int DEFAULT_TICKS = 0;
    
    // Define Tick Type
    static int INSIDE_PLOT  = 0;
    static int OUTSIDE_PLOT = 1;
    static int INSIDE_AND_OUTSIDE_PLOT = 2;
    
    // Number of Ticks
    // If nTicks=DEFAULT_TICKS, it is defined automatically
    int numberOfTicks();
    void setNumberOfTicks(int nTicks);
    
    ILineStyle tickLineStyle();
    void setTickLineStyle(ILineStyle style);
    
    // Tick Type: how to draw tick from the axis - inside plot,
    // outside of plot, or both
    int tickType();
    void setTickType(int type);
    
    // Tick length, this number can be interpreted as absolute length,
    // or fraction of Plot size
    double tickLength();
    void setTickLength(double length);
    
    // If scalable=true, tick length is interpreted as a
    // fraction of Plot size
    boolean isTickLengthScalable();
    void setTickLengthScalable(boolean scalable);
    
    ILegendBoxStyle tickLabelStyle();
    void setTickLabelStyle(ILegendBoxStyle style);
    
    // String expression that is used to format the Tick Label
    // Should we use C, Java, etc. conventions?
    // Example: "3.2f", "dd-MMM-yyyy HH:mm:ss"
    String tickLabelFormat();
    void setTickLabelFormat(String format);
    
    // If scalable=true, font size and positionning of the Tick Label
    // will be scaled automatically when Plot size changes (tickLabelStyle)
    boolean isTickLabelScalable();
    void setTickLabelScalable(boolean scalable);
    
    // Multiplier is common part of all Tick Labels on the Axis.
    // It can be extracted from labels and displayed separately
    // Example: (1000, 2000, 3000) -->  1000 * (1, 2, 3)
    ILegendBoxStyle multiplierStyle();
    void setMultiplierStyle(ILegendBoxStyle style);
    
    // String expression that is used to format the Multiplier
    String multiplierFormat();
    void setMultiplierFormat(String format);
    
    // If scalable=true, font size and positionning of the Multiplier
    // will be scaled automatically when Plot size changes (multiplierStyle)
    boolean isMultiplierScalable();
    void setMultiplierScalable(boolean scalable);    
}


