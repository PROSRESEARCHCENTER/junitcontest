package jas.hist.util;
import java.util.EventObject;
/**
 * An event which is fired by a SliceAdapter when a new slice is added or removed
 */
public class SliceEvent extends EventObject
{
    /**
     * Creates a new object representing a hypertext link event.
     *
     * @param source the object responsible for the event
     * @param type the event type
     * @param u the affected URL
     */
    public SliceEvent(Object source, EventType type, int index) 
	{
        super(source);
		this.type = type;
		this.index = index;
    }

    /**
     * Gets the type of event.
     *
     * @return the type
     */
    public EventType getEventType() 
	{
		return type;
    }
	/**
	 * Gets the index of the slice
	 * 
	 * @return the index
	 */
	public int getIndex()
	{
		return index;
	}
	
	private int index;
    private EventType type;
	
    /**
     * Defines the ENTERED, EXITED, and ACTIVATED event types, along
     * with their string representations, returned by toString().
     */
    public static final class EventType 
	{

        private EventType(String s) 
		{
			typeString = s;
		}

        /**
         * Entered type.
         */
		public static final EventType SLICEADDED = new EventType("SLICEADDED");

        /**
         * Exited type.
         */
		public static final EventType SLICEREMOVED = new EventType("SLICEREMOVED");

        /**
         * Converts the type to a string.
         *
         * @return the string
         */
        public String toString()
		{
			return typeString;
		}

		private String typeString;
    }
	
}
