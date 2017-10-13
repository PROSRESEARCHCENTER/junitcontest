package hep.aida.ref.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * A basic implementation of isObservable.
 * Although this class implements all the methods of IsObservable, it does
 * not explicitly implement the IsObservable interface. This is so that
 * ManagedObject can extend AIDAObservable, although not all ManagedObjects
 * implement IsObservable.
 *
 * @author tonyj
 * @version $Id: AIDAObservable.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class AIDAObservable
{
   private List listeners;
   private Map hash;
   protected boolean isConnected = true;
   protected boolean connectionDidChange = false;
   private ConnectEvent connectEvent = new ConnectEvent(this, true);
   protected boolean isValid = false;
   private boolean isValidAfterNotify = false;
   private EventObject theEvent;
   
   protected AIDAObservable()
   {
   }
   protected void setIsValidAfterNotify(boolean value)
   {
      isValidAfterNotify = value;
   }
   public synchronized void addListener(AIDAListener l)
   {
      if (listeners == null) listeners = new ArrayList();
      listeners.add(l);
      if (theEvent == null) theEvent = createEvent();
      if (hash == null) hash = new HashMap();
      hash.put(l, new Boolean(true));
      isValid = true;
   }
   public synchronized void removeListener(AIDAListener l)
   {
      if (listeners != null) listeners.remove(l);
      if (listeners != null) hash.remove(l);
      if (listeners.isEmpty()) isValid = false;
   }
   public synchronized void removeAllListeners()
   {
      if (listeners != null) listeners.clear();
      if (hash != null) hash.clear();
      isValid = false;
   }
   public void setValid(AIDAListener l) {
       boolean fireConnectionEvent = false;
       synchronized (this) {
           if (connectionDidChange) {
               fireConnectionEvent = true;
               connectionDidChange = false;
           }
           if (listeners != null) {
               hash.put(l, new Boolean(true));
               isValid = true;
           }
       }
       if (fireConnectionEvent) fireStateChanged(connectEvent, false);
   }
   public void setValidForAll() {
       boolean fireConnectionEvent = false;
       synchronized (this) {
           if (connectionDidChange) {
               fireConnectionEvent = true;
               connectionDidChange = false;
           }
           if (listeners != null) {
               for (int i = listeners.size(); i-->0; ) {
                   AIDAListener l = (AIDAListener) listeners.get(i);
                   hash.put(l, new Boolean(true));
               }
               isValid = true;
           }
       }
       if (fireConnectionEvent) fireStateChanged(connectEvent, false);
   }
   protected EventObject createEvent()
   {
      return new EventObject(this);
   }
   
   public void setConnected(boolean isConnected) {
       boolean fireConnectionEvent = false;
       synchronized (this) {
           //if (this.isConnected == isConnected) return;
           this.isConnected = isConnected;
           connectEvent.setConnected(isConnected);
           if (isValid) {
               fireConnectionEvent = true;
               connectionDidChange = false;
           } else {
               connectionDidChange = true;
           }
       }
       if (fireConnectionEvent) fireStateChanged(connectEvent, false);
   }

   public boolean isConnected() {return isConnected; }
   
   /**
    * Method to be used by subclass that only fire a single event (theEvent)
    */
   protected void fireStateChanged()
   {
      fireStateChanged(theEvent);
   }
   protected synchronized void fireStateChanged(EventObject event)
   {
       fireStateChanged(event, true);
   }
   protected synchronized void fireStateChanged(EventObject event, boolean checkValid)
   { 
      isValid = isValidAfterNotify;
      if (listeners != null)
      {
         for (int i = listeners.size(); i-->0; )
         {
            AIDAListener l = (AIDAListener) listeners.get(i);
            boolean listenerValid = ((Boolean) hash.get(l)).booleanValue();
            if (!checkValid) {
                l.stateChanged(event);
            } else if (listenerValid) {
                l.stateChanged(event);
                hash.put(l, new Boolean(isValidAfterNotify));
            }
         }
      }
   }
}