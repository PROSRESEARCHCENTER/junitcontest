package hep.aida.ref.event;
import java.util.EventObject;

/**
 * A interface to be implemented by those wishing to observe changes
 * to AIDA objects.
 * @author tonyj
 * @version $Id: AIDAListener.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface AIDAListener
{
   void stateChanged(EventObject e);
}
