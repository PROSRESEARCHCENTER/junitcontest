package hep.aida.ref.event;
import hep.aida.IBaseHistogram;

/**
 * An event send from a Histogram to AIDAListeners
 * @author tonyj
 * @version $Id: HistogramEvent.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HistogramEvent extends java.util.EventObject
{  
   public HistogramEvent(IBaseHistogram source)
   {
      super(source);
   }
}
