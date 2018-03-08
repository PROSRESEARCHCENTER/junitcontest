package org.freehep.util.parameterdatabase.selector;

import java.util.Iterator;

import javax.swing.JComboBox;

/**
 * This provides a class which allows the user to choose between a small number
 * of different choices. Instances of this class and subclasses should be
 * immutable. This is intended to work within a table context. For everything to
 * work correctly, the subclasses must provide a constructor which takes an
 * Object as an argument and one which takes a String as an argument. The String
 * constructor must work with the Strings produced from the getTag() method.
 * 
 * For some of the utility routines to work, the iterator over all of the
 * possible selector values must be provided. The initialization of the
 * underlying data structure and the filling with possible values should be done
 * in a static context.
 */
abstract public class Selector {

    /**
     * The internal value giving the selected state.
     */
    private Object value;

    /**
     * The internal value giving the tag of the selected state.
     */
    private String tag;

    /**
     * This protected constructor unconditionally creates a Selector with the
     * given value. This is necessary for subclasses which wish to create
     * statically a list of all possible Selectors of a particular class.
     */
    protected Selector(String tag, Object value) {
        this.tag = tag;
        this.value = value;
    }

    /**
     * This constructor will create a Selector with the given value. Subclasses
     * must provide a constructor with this signature.
     */
    public Selector(Object value) {
        Selector selector = getSelectorFromValue(value);
        if (selector != null) {
            this.value = selector.value;
            this.tag = selector.tag;
        } else {
            String message = "illegal value: " + value.toString();
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * This constructor will create a Selector from the given tag. Subclasses
     * must provide a constructor with this signature.
     */
    public Selector(String tag) {
        Selector selector = getSelectorFromTag(tag);
        if (selector != null) {
            this.value = selector.value;
            this.tag = selector.tag;
        } else {
            String message = "illegal tag: " + tag;
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Return a Selector object which corresponds to the given value.
     */
    protected Selector getSelectorFromValue(Object value) {
        for (Iterator i = iterator(); i.hasNext();) {
            Selector selector = (Selector) i.next();
            try {
                if (selector.getValue().equals(value))
                    return selector;
            } catch (ClassCastException e) {
                // If the equality throws an exception, then simply treat this
                // as being not equal.
            }
        }
        return null;
    }

    /**
     * Return a Selector object which corresponds to the given tag.
     */
    protected Selector getSelectorFromTag(String tag) {
        for (Iterator i = iterator(); i.hasNext();) {
            Selector selector = (Selector) i.next();
            if (selector.getTag().equals(tag))
                return selector;
        }
        return null;
    }

    /**
     * This returns an iterator over all of the possible Selector objects of
     * this class.
     */
    abstract public Iterator iterator();

    /**
     * This returns the associated tag for this Selector.
     */
    public String getTag() {
        return tag;
    }

    /**
     * This returns the value associated with this Selector as an Object.
     */
    public Object getValue() {
        return value;
    }

    /**
     * This returns the value associated with this Selector as an int. This will
     * throw a ClassCastException if the underlying value is not an Integer.
     */
    public int getIntValue() {
        return ((Integer) value).intValue();
    }

    /**
     * This returns the value associated with this Selector as a double. This
     * will throw a ClassCastException if the underlying value is not an Double.
     */
    public double getDoubleValue() {
        return ((Double) value).doubleValue();
    }

    /**
     * This returns the value associated with this Selector as a boolean. This
     * will throw a ClassCastException if the underlying value is not a Boolean.
     */
    public boolean getBooleanValue() {
        return ((Boolean) value).booleanValue();
    }

    /**
     * Return the tag associated with this selector's value.
     */
    public String toString() {
        return getTag();
    }

    /**
     * Initialize a JComboBox with all of the possible values.
     */
    public void initialize(JComboBox comboBox) {

        comboBox.removeAllItems();
        for (Iterator i = iterator(); i.hasNext();) {
            comboBox.addItem(i.next());
        }
        comboBox.setSelectedItem(getSelectorFromValue(value));
    }

    /**
     * For equality, the two objects must have the same class and the same
     * underlying value (compared with equals()).
     */
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Selector selector = (Selector) obj;
            return value.equals(selector.value);
        }
    }

    /**
     * The hashcode for this object is the one from the underlying value.
     */
    public int hashCode() {
        return value.hashCode();
    }

}
