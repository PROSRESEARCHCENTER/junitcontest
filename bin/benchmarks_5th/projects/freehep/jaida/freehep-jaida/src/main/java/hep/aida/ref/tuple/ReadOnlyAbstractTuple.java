package hep.aida.ref.tuple;

import hep.aida.ref.ReadOnlyException;

import org.freehep.util.Value;

/**
 * Base class for read only tuples.
 * This implements all methods which modify the tuple to throw exceptions.
 *
 * @author The AIDA team @ SLAC.
 *
 */

public abstract class ReadOnlyAbstractTuple extends AbstractTuple {


    public ReadOnlyAbstractTuple(String name, String options) {
        this(name, null, options);
    }
    
    public ReadOnlyAbstractTuple(String name, String title, String options) {
        super(name, title, options);
    }

    public void addRow() throws hep.aida.OutOfStorageException {
        throw new ReadOnlyException();
    }

    public void fill(int index, Value value) {
        throw new ReadOnlyException();
    }

    public void reset() {
        throw new ReadOnlyException();
    }

    public void resetRow() {
        throw new ReadOnlyException();
    }    
}
