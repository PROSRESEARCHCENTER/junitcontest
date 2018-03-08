package hep.aida.ref.function;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FunctionChangedEvent {
    
    
    public static int PARAMETER_VALUE_CHANGED = 0;
    public static int PARAMETER_NAME_CHANGED = 1;
    public static int TITLE_CHANGED = 2;
    public static int RANGE_CHANGED = 3;
    
    private int eventId;
    
    public FunctionChangedEvent(int eventId) {
        this.eventId = eventId;
    }
    
    public int eventId() {
        return eventId;
    }
}
