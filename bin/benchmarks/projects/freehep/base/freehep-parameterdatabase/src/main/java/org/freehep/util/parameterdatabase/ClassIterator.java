package org.freehep.util.parameterdatabase;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator iterates up the class hierarchy. The first element returned is
 * always the initial defining object (which can also be a class). Each
 * subsequent element returned is the superclass of the previous one; if the
 * previous element was not a Class, then that object's Class is returned.
 */
public class ClassIterator implements Iterator {

    /**
     * This is the initial root object which starts the iteration.
     */
    protected Object root;

    /**
     * The reference to the object which will be returned by the next call to
     * next().
     */
    protected Object current;

    /**
     * Constructor requires an object on which to start the iteration. The
     * starting object may be a Class.
     */
    public ClassIterator(Object start) {
        reset(start);
    }

    /**
     * This resets the iterator to the given starting object. This allows the
     * iterator to be reused. Note that all references to external classes can
     * be cleared by passing in null here.
     */
    public void reset(Object start) {
        this.root = start;
        this.current = start;
    }

    /**
     * This resets the iterator to the saved starting object.
     */
    public void reset() {
        this.current = root;
    }

    public boolean hasNext() {
        return (current != null);
    }

    public Object next() {

        // Have we run out of elements?
        if (current == null)
            throw new NoSuchElementException();

        // Save the current value and move to the next one.
        Object currentObject = current;
        if (current instanceof Class) {
            Class c = (Class) current;
            current = c.getSuperclass();
        } else {
            current = current.getClass();
        }

        // Return the value.
        return currentObject;
    }

    /**
     * The remove() method is not supported by this iterator.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
