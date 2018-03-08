package hep.aida.ref.plotter.adapter;

import hep.aida.ICloud;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ref.event.ObserverAdapter;
import jas.hist.DataSource;
import jas.hist.HistogramUpdate;

/**
 *
 * @author  manj
 * @version $Id: AIDACloudAdapter.java 13402 2007-11-02 21:19:21Z serbo $
 */
abstract class AIDACloudAdapter extends ObserverAdapter implements DataSource
{
    /**
    * Create a DataSource from a Cloud
    */
   public static DataSource create(ICloud c)
   {
      AIDACloudAdapter result;
      if      (c instanceof ICloud1D) result = new AIDACloudAdapter1D((ICloud1D)c);
      else if (c instanceof ICloud2D) result = new AIDACloudAdapter2D((ICloud2D)c);
      else throw new IllegalArgumentException("Argument is an unknown subtype of ICloud");
  
      return result;
   }
   protected AIDACloudAdapter(ICloud h)
   {
       super(h);
       update = hr;
       
   }
   
   protected double getMarginValue(double lowerEdge, double upperEdge) {
        double le = lowerEdge != upperEdge ? lowerEdge : lowerEdge - 1;
        double ue = lowerEdge != upperEdge ? upperEdge : upperEdge + 1;
        double delta = ue - le;
        return margin*Math.abs(delta);
       
   }
   
   protected int xAxisType = DOUBLE;
   protected int yAxisType = DOUBLE;
   protected double margin = 0.05;
    
   private final static jas.hist.HistogramUpdate hr = new jas.hist.HistogramUpdate(HistogramUpdate.TITLE_UPDATE+HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,false);
   static
   {
      hr.setAxis(hr.HORIZONTAL_AXIS);
      hr.setAxis(hr.VERTICAL_AXIS);
   } 
}
