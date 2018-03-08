/*
 * JASGUIPlotterFactroy.java
 *
 * Created on August 28, 2002, 7:44 AM
 */

package hep.aida.ref.plotter;
import hep.aida.IPlotter;

/**
 *
 * @author  turri
 */
public class PlotterFactory extends hep.aida.ref.plotter.DummyPlotterFactory {

    public IPlotter create() {
        return create(null);
    }

    public IPlotter create(String title) {
        return create(title, null);
    }
    
    public IPlotter create(String title, String options) {
        try {
            return new Plotter(title, options);
        } catch (NoClassDefFoundError e) {
            if (e.getMessage() != null) throw e;

            // Almost certainly the display is not set
            System.err.println("*******************************************");
            System.err.println("WARNING: DISPLAY variable probably not set.");
            System.err.println("Plotter replaced by DummyPlotter.          ");
            System.err.println("*******************************************");
            return new DummyPlotter(title);
        }
    }
}
