/*
 * PlotterFactory.java
 *
 * Created on January 22, 2002, 3:26 PM
 */

package hep.aida.ref.plotter;
import hep.aida.IPlotter;
import hep.aida.IPlotterFactory;

/**
 * A simple plotter factory
 * @author tonyj
 * @version $Id: DummyPlotterFactory.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DummyPlotterFactory implements IPlotterFactory
{
    public DummyPlotterFactory()
   {
   }
    
    public IPlotter create(String name, String options)
   {
      return new DummyPlotter(name, options);
   }
    
   public hep.aida.IPlotter create(String title)
   {
       return create(title, null);
   }
   
   public hep.aida.IPlotter create() 
   {
      return create(null);       
   }
   
    public hep.aida.IAxisStyle createAxisStyle() {
        return new AxisStyle();
    }
   
    public hep.aida.IDataStyle createDataStyle() {
        return new DataStyle();
    }

    public hep.aida.IPlotterStyle createPlotterStyle() {
        return new PlotterStyle();
    }
       
    public hep.aida.IPlotterStyle createPlotterStyle(hep.aida.IPlotterStyle style) {
        if (style instanceof PlotterStyle) return new PlotterStyle((PlotterStyle) style);
        else return null;
    }
       
   public hep.aida.IFillStyle createFillStyle() {
       return new FillStyle();
   }
   
   public hep.aida.ILineStyle createLineStyle() {
       return new LineStyle();
   }
   
   public hep.aida.IMarkerStyle createMarkerStyle() {
       return new MarkerStyle();
   }
      
   public hep.aida.ITextStyle createTextStyle() {
       return new TextStyle();
   }
   
   public hep.aida.ITitleStyle createTitleStyle() {
       return new TitleStyle();
   }   
}
