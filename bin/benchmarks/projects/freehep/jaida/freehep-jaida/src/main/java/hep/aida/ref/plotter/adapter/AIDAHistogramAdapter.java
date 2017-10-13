package hep.aida.ref.plotter.adapter;

import hep.aida.IHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.ref.event.ObserverAdapter;
import jas.hist.DataSource;
import jas.hist.HistogramUpdate;
/**
 * Creates a datasource from an IHistogram
 * @author  manj
 * @version $Id: AIDAHistogramAdapter.java 10738 2007-05-16 22:47:34Z serbo $
 */
abstract class AIDAHistogramAdapter extends ObserverAdapter implements DataSource {
    /**
     * Create a DataSource from a Histogram
     */
    public static DataSource create(IHistogram h) {
        AIDAHistogramAdapter result;
        
        if      (h instanceof IHistogram1D) {
            if (((IHistogram1D) h).axis().isFixedBinning())
                result = new AIDAHistogramAdapter1D((IHistogram1D)h);
            else
                result = new AIDAHistogramVariableAdapter1D((IHistogram1D)h);
        } else if (h instanceof IHistogram2D) {
            if ( ((IHistogram2D) h).xAxis().isFixedBinning() &&
                 ((IHistogram2D) h).yAxis().isFixedBinning() )
                result = new AIDAHistogramAdapter2D((IHistogram2D)h);
            else
                result = new AIDAHistogramVariableAdapter2D((IHistogram2D)h);
        }
        else throw new IllegalArgumentException("Argument is an unsupported subtype of IHistogram");
        return result;
    }
    
    protected AIDAHistogramAdapter(IHistogram h) {
        super(h);
        update = hu;
    }
    
    protected int xAxisType = DOUBLE;
    protected int yAxisType = DOUBLE;
    
    private final static jas.hist.HistogramUpdate hu = new jas.hist.HistogramUpdate(HistogramUpdate.TITLE_UPDATE+HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,false);
    static {
        hu.setAxis(hu.HORIZONTAL_AXIS);
        hu.setAxis(hu.VERTICAL_AXIS);
    }
    
}
