package hep.aida.ref.event;

/**
 * An interface to be implemented by AIDA objects that can notify
 * observers of change to their state. The interface is designed to
 * add minimal overhead to objects which are observable, whether they
 * are observed or not. Since objects such as histograms may be updated
 * very frequently they will generally only send one notification to 
 * observers. If the observer needs to be notified again it must call 
 * setValid before another notification will be sent.
 */
public interface IsObservable
{
   public void addListener(AIDAListener o);
   public void removeListener(AIDAListener o);
   public void removeAllListeners();
   public void setValid(AIDAListener o);
}
