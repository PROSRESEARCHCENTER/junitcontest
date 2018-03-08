package hep.physics.event;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple event structure. Designed to be used as a base class for experiment
 * specific event descriptions.
 * @author Tony Johnson
 */
public interface HEPEvent
{
   /**
    * @return The event number
    */
   public int getEventNumber();
   /**
    * @return The run number for this event
    */
   public int getRunNumber();
   
   /**
    * Get the current timestamp. By convention this is measured in nS since 
    * 1-jan-1970 GMT.
    * @return The timestamp.
    */
   public long getTimeStamp();
   
   /**
    * Puts an arbitrary object into the event
    * @param key The key for this object
    * @param component The object to add
    */
   public void put(String key, Object component);
   /**
    * Get an object from the event
    * @param key The key for the object
    * @throws IllegalArgumentException if the specified object does not exist in the event.
    */
   public Object get(String key) throws IllegalArgumentException;
   /**
    * An arbitrary set of tags that can be associated with the event.
    * Intended to be used for fast event selection.
    */
   public Map getTags();
   /**
    * @return The set of keys stored in the event
    */
   public Set keys();
}
