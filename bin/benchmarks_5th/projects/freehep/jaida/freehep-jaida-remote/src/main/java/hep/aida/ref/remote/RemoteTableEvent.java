package hep.aida.ref.remote;

import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.remote.interfaces.ITable;

/**
 * An event send from a ITable to AIDAListeners
 * @author serbo
 * @version $Id: RemoteTableEvent.java 8584 2006-08-10 23:06:37Z duns $
 */
public class RemoteTableEvent extends HistogramEvent {  
    private static transient Histogram1D histogram = new Histogram1D();
    
    public RemoteTableEvent(ITable source)
   {
      super(histogram);
      this.source = source;
   }
}
