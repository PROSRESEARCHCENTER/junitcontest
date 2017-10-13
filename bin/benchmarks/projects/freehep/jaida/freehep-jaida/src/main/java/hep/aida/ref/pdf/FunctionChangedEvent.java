package hep.aida.ref.pdf;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FunctionChangedEvent {
    
    public static int FUNCTION_VALUE_CHANGED = 0;
    public static int FUNCTION_CHANGED = 1;
    public static int PARAMETER_VALUE_CHANGED = 2;
    public static int PARAMETER_NAME_CHANGED = 3;
    public static int TITLE_CHANGED = 4;
    public static int RANGE_CHANGED = 5;
    
    private int eventId;
    
    public FunctionChangedEvent(int eventId) {
        this.eventId = eventId;
    }
    
    public int eventId() {
        return eventId;
    }
}
