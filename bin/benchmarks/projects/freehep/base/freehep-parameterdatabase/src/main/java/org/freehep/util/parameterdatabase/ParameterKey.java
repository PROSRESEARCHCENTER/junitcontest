package org.freehep.util.parameterdatabase;

import java.util.LinkedList;

public class ParameterKey {

    /**
     * The object to which this parameter belongs.
     */
    private Object object;

    /**
     * The name of this parameter.
     */
    private String name;

    /**
     * This monitors the state of this ParameterKey. If this key has been
     * cleared, then this ParameterKey is invalid and most user-visible calls
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
     * This protected constructor creates a new ParameterKey with the given
     * parameters. Users should not create ParameterKeys via this constructor,
     * but should instead use the static factory method createParameterKey().
     */
    protected ParameterKey(Object object, String name) {
        this.object = object;
        this.name = name;
        isValid = true;
    }

    /**
     * This static factory method returns a ParameterKey using the given
     * arguments. This method will try to use a recycled ParameterKey if one is
     * available.
     */
    static public ParameterKey createParameterKey(Object object, String name) {

        // Check the validity of the input parameters. Neither one
        // can be null.
        if (object == null || name == null) {
            throw new IllegalArgumentException();
        }

        ParameterKey newKey = null;

        // First check to see if a key is already available. This
        // must be synchronized to guarantee that the same key isn't
        // given out twice by accident.
        synchronized (inventory) {
            if (!inventory.isEmpty()) {
                newKey = (ParameterKey) inventory.removeFirst();
                newKey.object = object;
                newKey.name = name;
                newKey.isValid = true;
            }
        }

        // If we couldn't find an existing key, then make a new one.
        if (newKey == null) {
            newKey = new ParameterKey(object, name);
        }

        return newKey;
    }

    /**
     * Return the object associated with this ParameterKey. Note that no
     * operations should be performed on the returned object which will affect
     * the results of the equals() method.
     */
    public Object getObject() {
        if (!isValid)
            throw new IllegalStateException();
        return object;
    }

    /**
     * Return the name of the parameter.
     */
    public String getName() {
        if (!isValid)
            throw new IllegalStateException();
        return name;
    }

    /**
     * This method will recycle this ParameterKey. Any further user method calls
     * on this object will result in an IllegalStateException being thrown. This
     * object will be available for reuse only after the JVM determines that
     * there are no more outstanding references to this ParameterKey and the JVM
     * runs the finalize() method.
     */
    protected void recycle() {
        isValid = false;
        object = null;
        name = null;
    }

    /**
     * Two ParameterKeys are equal if and only if the corresponding objects and
     * names are equal.
     */
    public boolean equals(Object otherKey) {

        if (!isValid)
            throw new IllegalStateException();

        if (!(otherKey instanceof ParameterKey)) {
            return false;
        } else {
            ParameterKey other = (ParameterKey) otherKey;
            if (!other.isValid)
                throw new IllegalStateException();
            return (object.equals(other.object) && name.equals(other.name));
        }
    }

    /**
     * The hashcode returned is simply the exclusive-or of the hashcodes of the
     * object and the name.
     */
    public int hashCode() {
        if (!isValid)
            throw new IllegalStateException();
        return (object.hashCode() ^ name.hashCode());
    }

    /**
     * We override the finalize method to allow this ParameterKey to be
     * recycled. It is added to the list of recycled keys if the inventory is
     * below the given limit.
     */
    public void finalize() throws Throwable {

        // Recycle this key if we haven't exceeded the limit. A
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

}
