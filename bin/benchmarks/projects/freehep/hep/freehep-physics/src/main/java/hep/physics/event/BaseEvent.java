package hep.physics.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple implementation of HEPEvent
 * @author Tony Johnson
 */
public class BaseEvent implements HEPEvent
{
   private int run;
   private int event;
   private long timestamp;
   private Map keyMap = new HashMap();
   private Map tagMap;
   
   /**
    * 
    * @param run The run number
    * @param event Create a new event. The timestamp will be set 
    * to the current time.
    */
   public BaseEvent(int run, int event)
   {
      this(run,event,System.currentTimeMillis()*1000000);
   }
   
   /**
    * Create a new event with an explicit timestamp
    * @param run Teh run number
    * @param event The event number
    * @param timestamp The timestamp. By convention this is measured in nS since 
    * 1-jan-1970 GMT.
    */
   public BaseEvent(int run, int event, long timestamp)
   {
      this.run = run;
      this.event = event;
      this.timestamp = timestamp;
   }
   
   public Object get(String key)
   {
      Object result = keyMap.get(key);
      if (result == null) throw new IllegalArgumentException("Unknown event component "+key);
      return result;
   }   
   
   public int getEventNumber()
   {
      return event;
   }
   
   public int getRunNumber()
   {
      return run;
   }
   
   public Map getTags()
   {
      return tagMap == null ? Collections.EMPTY_MAP : tagMap;
   }
   
   public Set keys()
   {
      return keyMap.keySet();
   }
   
   public void put(String key, Object component)
   {
      keyMap.put(key,component);
   }
   
   public long getTimeStamp()
   {
      return timestamp;
   }
   
}
