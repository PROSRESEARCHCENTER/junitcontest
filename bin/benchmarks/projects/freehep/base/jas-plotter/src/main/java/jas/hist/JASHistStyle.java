package jas.hist;

import java.io.Serializable;
import java.util.Observable;
import java.util.TimeZone;

public abstract class JASHistStyle extends Observable implements Serializable
{
   public static final int SOLID = 0;
   public static final int DOTTED = 1;
   public static final int DASHED = 2;
   public static final int DOTDASH = 3;
   
   public void setCustomOverlay(CustomOverlay o)
   {
      overlay = o;
   }
   public CustomOverlay getCustomOverlay()
   {
      return overlay;
   }
   protected void changeNotify()
   {
      setChanged();
      if (m_batch == 0) notifyObservers();
   }
   public void startBatch()
   {
      m_batch++;
   }
   public void endBatch()
   {
      if (--m_batch == 0) notifyObservers();
   }
   public void setTimeZone(TimeZone t) {
       timeZone = t;
       changeNotify();
   }
   public TimeZone getTimeZone() {
       return timeZone;
   }
   private CustomOverlay overlay; // BUG: what if this isn't serializable??
   transient private int m_batch = 0;
   protected TimeZone timeZone = TimeZone.getDefault();
   static final long serialVersionUID = -3911970150059917139L;
}