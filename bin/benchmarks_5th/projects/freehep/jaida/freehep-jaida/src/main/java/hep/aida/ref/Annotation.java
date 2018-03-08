package hep.aida.ref;

/**
 * Implementation of IAnnotation.
 *
 * It keeps the exact case of added keys, but all other methods
 * are not case sensitive
 * @author The AIDA Team at SLAC.
 *
 */
import hep.aida.IAnnotation;

import java.util.Vector;

public class Annotation implements IAnnotation, Cloneable {
    
    protected Vector keyVector = new Vector();
    protected Vector valVector = new Vector();
    protected Vector stkVector = new Vector();
    protected boolean fillable = true;
    public static String titleKey = "Title";
    public static String aidaPathKey = "AidaPath"; // Path in the local AIDA ITree
    public static String fullPathKey = "FullPath"; // Path in the top-level AIDA Tree (aidaMasterTree)
    
    /**
     * Creates a new instance of Annotation.
     *
     */
    public Annotation() {
        addItem(titleKey,"",true);
    }
    
    public Annotation(IAnnotation an) {
        this();
        copyAn(an);
    }
    
    public Object clone() {
        try {
            Annotation copy = new Annotation(this);
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Error while cloning Annotation", e);
        }
    }
    
    private void copyAn(IAnnotation an) {
        int size = an.size();
        for (int i=0; i<size; i++) {
            String key = an.key(i);
            String val = an.value(key);
            boolean sticky = an.isSticky(key);
            if (hasKey(key)) {
                setValue(key, val);
                setSticky(key, sticky);
            } else {
                this.addItem(key, val, sticky);
            }
        }
    }
            
    private String getKeyForKey(String key) {
        int size = keyVector.size();
        for (int i=0; i<size; i++) {
            Object obj = keyVector.get(i);
            if (obj instanceof String) {
                String str = (String) obj;
                if (str.equalsIgnoreCase(key)) return str;
            }
        }
        return null;
    }
    /**
     * If annotation is fillable, can modified.
     * othervisw throws ReadOnlyException.
     */
    public void setFillable(boolean fillable) {
        this.fillable = fillable;
    }
    
    public boolean isFillable() {
        return fillable;
    }
    
    /**
     * Add a key/value pair with a given visibility.
     *
     */
    public void addItem( String  key, String  value ) {
        if (!fillable) throw new ReadOnlyException();
        addItem(key, value, false);
    }
    
    public void addItem( String  key, String  value, boolean sticky ) {
        if (!fillable) throw new ReadOnlyException();
        String localKey = getKeyForKey(key);
        if ( localKey != null && keyVector.contains(localKey) ) {
            setValue(localKey, value);
            setSticky(localKey, sticky);
        } else {
            keyVector.add(key);
            valVector.add(value);
            stkVector.add(new Boolean(sticky));
        }
    }
    
    /**
     * Remove the item indicated by a given key.
     *
     */
    public void removeItem( String  key ) {
        if (!fillable) throw new ReadOnlyException();
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        int index = keyVector.indexOf(localKey);
        if ( isSticky( index ) ) throw new IllegalArgumentException("Item "+key+" is sticky; it cannot be removed");
        keyVector.remove(index);
        valVector.remove(index);
        stkVector.remove(index);
    }
    
    /**
     * Retrieve the value for a given key.
     * @return The value of the corresponding item.
     *
     */
    public String  value( String  key ) {
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        int index = keyVector.indexOf(localKey);
        return (String)valVector.get(index);
    }
    
    /**
     * Set value for a given key.
     * @param key The item's key.
     * @param value The new value for the correponding item.
     *
     */
    public void setValue( String  key, String value ) {
        if (!fillable) throw new ReadOnlyException();
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        int index = keyVector.indexOf(localKey);
        valVector.set(index,value);
    }
    
    /**
     * Get the stickyness for a given key.
     * @param key The item's key.
     * @return The stickyness for the corresponding item.
     *
     */
    public boolean isSticky(String key) {
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        int index = keyVector.indexOf(localKey);
        return ((Boolean)(stkVector.get(index))).booleanValue();
    }
    
    /**
     * Set stickyness for a given key.
     * @param key The item's key.
     * @param sticky The new stickyness for the correponding item.
     *
     */
    public void setSticky( String  key, boolean sticky ) {
        if (!fillable) throw new ReadOnlyException();
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        int index = keyVector.indexOf(localKey);
        stkVector.set(index, new Boolean(sticky));
    }
    
    /**
     * Get the number of items in the Annotation.
     * @return The size of the Annotation.
     *
     */
    public int size() {
        return keyVector.size();
    }
    
    /**
     * Get the key corresponding to the item in a given position in the Annotation.
     * @param index The item's index.
     * @return The corresponding key.
     *
     */
    public String  key(int index) {
        checkIndex(index);
        return (String)(keyVector.get(index));
    }
    
    /**
     * Get the value corresponding to the item in a given position in the Annotation.
     * @param index The item's index.
     * @return The corresponding value.
     *
     */
    public String  value(int index) {
        checkIndex(index);
        return (String)(valVector.get(index));
    }
    
    /**
     * Get the stickyness corresponding to the item in a give position in the Annotation.
     * @param index The item's index.
     * @return The stickyness for the corresponding item.
     *
     */
    public boolean isSticky(int index) {
        checkIndex(index);
        return ((Boolean)(stkVector.get(index))).booleanValue();
    }
    
    /**
     * Reset the contents of the Annotation.
     *
     */
    public void reset() {
        if (!fillable) throw new ReadOnlyException();
        Object[] stkVct = stkVector.toArray();
        for ( int i = stkVct.length-1; i > -1; i-- ) {
            if ( ! ((Boolean)stkVct[i]).booleanValue() ) {
                keyVector.remove(i);
                valVector.remove(i);
                stkVector.remove(i);
            }
        }
    }
    
    
    /**
     * Utility method to check if an index is valid for the current configuration of the Annotation.
     * @param index The index to be checked.
     *
     */
    protected void checkIndex(int index) {
        if ( index + 1 > size() ) throw new IllegalArgumentException("Index "+index+" exceeds the dimension of the Annotation: "+size());
        if ( index < 0 ) throw new IllegalArgumentException("Index "+index+" cannot be negative");
    }

    public int findKey(String key) {
        String localKey = getKeyForKey(key);
        if ( localKey == null || ! keyVector.contains(localKey) ) throw new IllegalArgumentException("Item "+key+" does not exist");
        return keyVector.indexOf(localKey);
    }

    public boolean hasKey(String key) {
        String localKey = getKeyForKey(key);
        return localKey != null;
    }
    
}
