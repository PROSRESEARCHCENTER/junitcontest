package hep.aida.ref.event;
import hep.aida.IDataPointSet;

/**
 * An event send from a Histogram to AIDAListeners
 * @author tonyj
 * @version $Id: DataPointSetEvent.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DataPointSetEvent extends java.util.EventObject
{  
    public DataPointSetEvent(IDataPointSet source)
   {
      super(source);
   }
}
