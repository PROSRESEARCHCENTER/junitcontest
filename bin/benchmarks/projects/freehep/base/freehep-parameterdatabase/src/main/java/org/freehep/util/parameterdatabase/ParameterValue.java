package org.freehep.util.parameterdatabase;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ParameterValue {

    /**
     * The value associated with this parameter.
     */
    private Object value;

    /**
     * This monitors the state of this ParameterValue. If this key has been
     * cleared, then this ParameterValue is invalid and most user-visible calls
     * will return an IllegalStateException.
     */
    private boolean isValid;

    /**
     * This contains the maximum number of recycled keys.
     */
    static private int inventoryLimit = 10000;

    /**
     * This linked list contains the set of recycled keys.
     */
    static private LinkedList inventory = new LinkedList();

    /**
     * The object which keeps the list of PropertyChangeListeners.
     */
    private LinkedList propertyChangeListeners;

    /**
     * This protected constructor creates a new ParameterValue with the given
     * parameters. Users should not create ParameterValues via this constructor,
     * but should instead use the static factory method createParameterValue().
     */
    protected ParameterValue(Object value) {
        this.value = value;
        isValid = true;
        propertyChangeListeners = new LinkedList();
    }

    /**
     * This static factory method returns a ParameterValue using the given
     * arguments. This method will try to use a recycled ParameterValue if one
     * is available.
     */
    static public ParameterValue createParameterValue(Object value) {

        // Check the validity of the input parameter.
        if (value == null) {
            throw new IllegalArgumentException();
        }

        ParameterValue newValue = null;

        // First check to see if a value is already available. This
        // must be synchronized to guarantee that the same value isn't
        // given out twice by accident.
        synchronized (inventory) {
            if (!inventory.isEmpty()) {
                newValue = (ParameterValue) inventory.removeFirst();
                newValue.value = value;
                newValue.isValid = true;
            }
        }

        // If we couldn't find an existing key, then make a new one.
        if (newValue == null) {
            newValue = new ParameterValue(value);
        }

        return newValue;
    }

    /**
     * Return the value associated with this ParameterValue.
     */
    public Object getValue() {
        if (!isValid)
            throw new IllegalStateException();
        return value;
    }

    /**
     * Set the value associated with the parameter name.
     */
    public void setValue(Object value) {
        if (!isValid)
            throw new IllegalStateException();
        if (value == null)
            throw new IllegalArgumentException();
        this.value = value;
    }

    /**
     * This method will recycle this ParameterValue. Any further user method
     * calls on this object will result in an IllegalStateException being
     * thrown. This object will be available for reuse only after the JVM
     * determines that there are no more outstanding references to this
     * ParameterValue and the JVM runs the finalize() method.
     */
    protected void recycle() {
        isValid = false;
        value = null;
        propertyChangeListeners.clear();
    }

    /**
     * Equality for two ParameterValues is determined by whether the underlying
     * value objects are equal.
     */
    public boolean equals(Object otherValue) {
        if (!isValid)
            throw new IllegalStateException();
        if (otherValue instanceof ParameterValue) {
            ParameterValue other = (ParameterValue) otherValue;
            return value.equals(other.value);
        } else {
            return false;
        }
    }

    /**
     * The hashcode is simply value.hashCode().
     */
    public int hashCode() {
        if (!isValid)
            throw new IllegalStateException();
        return value.hashCode();
    }

    /**
     * We override the finalize method to allow this ParameterValue to be
     * recycled. It is added to the list of recycled values if the inventory is
     * below the given limit.
     */
    public void finalize() throws Throwable {

        // Recycle this value if we haven't exceeded the limit. A
        // normal return will allow this key to be garbage collected
        // normally.
        synchronized (inventory) {
            if (inventory.size() < inventoryLimit) {
                recycle();
                inventory.add(this);
                throw new Throwable();
            }
        }
    }

    /**
     * Return an unmodifiable list of the current listeners.
     */
    public List getPropertyChangeListeners() {
        return Collections.unmodifiableList(propertyChangeListeners);
    }

    /**
     * Add a PropertyChangeListener to this ParameterValue.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

        synchronized (propertyChangeListeners) {
            if (!propertyChangeListeners.contains(listener)) {
                propertyChangeListeners.add(listener);
            }
        }
    }

    /**
     * Remove a PropertyChangeListener from this ParameterValue.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        synchronized (propertyChangeListeners) {
            propertyChangeListeners.remove(listener);
        }
    }

}
