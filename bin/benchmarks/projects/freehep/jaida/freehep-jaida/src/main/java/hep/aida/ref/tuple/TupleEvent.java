package hep.aida.ref.tuple;

import java.util.EventObject;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TupleEvent extends EventObject implements FTupleEvent {

    public TupleEvent(Object source) {
        super(source);
    }

    public void setRange(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public int getFirstIndex() {
        return first;
    }

    public int getLastIndex() {
        return last;
    }
    
    private int first, last;
}
