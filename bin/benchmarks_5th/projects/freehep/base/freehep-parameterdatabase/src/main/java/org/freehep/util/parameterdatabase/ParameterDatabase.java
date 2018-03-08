package org.freehep.util.parameterdatabase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.freehep.xml.util.XMLWriter;

public class ParameterDatabase {

    /**
     * The hashtable which contains all of the parameters.
     */
    protected Hashtable database;

    /**
     * This list contains all of the database listeners.
     */
    protected LinkedList databaseListeners = new LinkedList();

    /**
     * Constructor creates the hashtable to hold all of the parameters. The
     * default size is 10000 entries.
     */
    public ParameterDatabase() {
        this(10000);
    }

    /**
     * Constructor creates the hashtable of the given initial size to hold all
     * of the parameters.
     */
    public ParameterDatabase(int size) {
        database = new Hashtable(size);
    }

    /**
     * This method will determine if the given parameter is local. That is, that
     * the parameter is defined on the first object returned from the iterator.
     */
    public boolean isParameterLocal(String parameterName, Iterator iterator) {

        // Just check the first object to see if this is local.
        if (iterator.hasNext()) {

            // Create an appropriate ParameterKey.
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Extract the appropriate value.
            ParameterValue value = (ParameterValue) database.get(key);

            return (value != null);
        }

        // The default return is false.
        return false;
    }

    /**
     * Retrieve a parameter from the database. The iterator must produce a
     * sequence of Objects which correspond to the specific tables in which to
     * try to find the parameter. The iteration will stop when the parameter is
     * first found.
     */
    public Object getParameter(String parameterName, Iterator iterator) {

        // Loop until we find the parameter or run out of tables to check.
        // This will return as soon as the parameter is found.
        while (iterator.hasNext()) {

            // Create an appropriate ParameterKey.
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Extract the appropriate value.
            ParameterValue value = (ParameterValue) database.get(key);

            if (value != null)
                return value.getValue();
        }

        // Return null because the given parameter wasn't found.
        return null;
    }

    /**
     * Set a parameter. Use the iterator to traverse a series of tables in which
     * to find the given parameter. Only the first parameter with this name will
     * be modified. This method returns true if the parameter was found and set;
     * it returns false otherwise.
     */
    public boolean setParameter(String parameterName, Object value,
            Iterator iterator) {

        // Parameter name and value cannot be null.
        if (parameterName == null)
            throw new IllegalArgumentException("parameterName cannot be null.");
        if (value == null)
            throw new IllegalArgumentException("value cannot be null.");

        // Loop until we find the parameter or run out of tables to
        // check. This will return as soon as the parameter is
        // found.
        while (iterator.hasNext()) {

            // Create an appropriate ParameterKey.
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Extract the appropriate ParameterValue and set it.
            ParameterValue parameterValue = (ParameterValue) database.get(key);
            if (parameterValue != null) {
                parameterValue.setValue(value);

                firePropertyChange(parameterName, parameterValue);

                return true;
            }
        }

        // Indicate that the parameter was not found.
        return false;
    }

    /**
     * Return a boolean indicating whether this parameter is defined using the
     * given iterator to iterate through a set of objects.
     */
    public boolean isParameterDefined(String parameterName, Iterator iterator) {

        // Parameter name cannot be null.
        if (parameterName == null)
            throw new IllegalArgumentException("parameterName cannot be null.");

        // Loop until we find the parameter or run out of tables to
        // check. This will return as soon as the parameter is
        // found.
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (isParameterDefined(parameterName, object))
                return true;
        }

        // Indicate that the parameter was not found.
        return false;
    }

    /**
     * Return a boolean indicating whether this parameter is defined for the
     * given object.
     */
    public boolean isParameterDefined(String parameterName, Object object) {

        // Parameter name and object cannot be null.
        if (parameterName == null)
            throw new IllegalArgumentException("parameterName cannot be null.");
        if (object == null)
            throw new IllegalArgumentException("object cannot be null.");

        // Create an appropriate ParameterKey.
        ParameterKey key = ParameterKey.createParameterKey(object,
                parameterName);

        // Extract the appropriate ParameterValue and set it.
        ParameterValue parameterValue = (ParameterValue) database.get(key);
        return (parameterValue != null);
    }

    /**
     * Add a parameter to the database. This will only add the parameter to the
     * first object returned by the given iterator. After the parameter is
     * added, the listener will be added to the value. This same listener will
     * be removed from the next visible parameter of the same name. If the
     * parameter already exists, the listener is added but the value is not
     * modified.
     */
    public boolean addParameter(String parameterName, Object value,
            Iterator iterator, PropertyChangeListener listener) {

        return addParameter(parameterName, value, iterator, listener, false);
    }

    /**
     * Add a parameter to the database. This will only add the parameter to the
     * first object returned by the given iterator. After the parameter is
     * added, the listener will be added to the value. This same listener will
     * be removed from the next visible parameter of the same name. If the
     * parameter already exists, the listener is added but the value is not
     * modified unless overwrite is true.
     */
    public boolean addParameter(String parameterName, Object value,
            Iterator iterator, PropertyChangeListener listener,
            boolean overwrite) {

        // Parameter name and value cannot be null.
        if (parameterName == null)
            throw new IllegalArgumentException("parameterName cannot be null.");
        if (value == null)
            throw new IllegalArgumentException("value cannot be null.");

        // We must process the first object specially. Here we add or
        // modify the given parameter.
        if (iterator.hasNext()) {
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Create a new entry if it doesn't already exist.
            ParameterValue parameterValue = (ParameterValue) database.get(key);
            if (parameterValue != null) {

                // Add the listener.
                if (listener != null)
                    parameterValue.addPropertyChangeListener(listener);

                // Only overwrite the existing value if this has been
                // requested.
                if (overwrite) {
                    parameterValue.setValue(value);

                    firePropertyChange(parameterName, parameterValue);
                }
            } else {

                parameterValue = ParameterValue.createParameterValue(value);

                if (listener != null)
                    parameterValue.addPropertyChangeListener(listener);

                database.put(key, parameterValue);
            }

        } else {
            return false;
        }

        // Loop over the rest of the objects and remove this listener from the
        // first entry found.
        while (iterator.hasNext()) {

            // Create an appropriate ParameterKey.
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Extract the appropriate ParameterValue and set it.
            ParameterValue parameterValue = (ParameterValue) database.get(key);
            if (parameterValue != null) {
                parameterValue.removePropertyChangeListener(listener);
                return true;
            }
        }

        // Return indicating that the parameter has been added.
        return true;
    }

    /**
     * Remove a parameter from the database. This will only remove the parameter
     * from the first object returned by the given iterator. After the parameter
     * is removed, the listeners from this entry will be added to the next
     * visible value.
     */
    public boolean removeParameter(String parameterName, Iterator iterator) {

        // Parameter name and value cannot be null.
        if (parameterName == null)
            throw new IllegalArgumentException("parameterName cannot be null.");

        // We must process the first object specially. We only
        // actually remove the parameter associated with the first
        // object.
        List movedListeners = null;
        if (iterator.hasNext()) {
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);

            // Get the associated value.
            ParameterValue parameterValue = (ParameterValue) database
                    .remove(key);

            if (parameterValue != null) {
                movedListeners = parameterValue.getPropertyChangeListeners();
            } else {
                return false;
            }

        } else {
            return false;
        }

        // Loop over the rest of the objects and see if we find another
        // version of this parameter. If so, add the listeners to this
        // version of the parameter.
        if (movedListeners != null && !movedListeners.isEmpty()) {
            while (iterator.hasNext()) {

                // Create an appropriate ParameterKey.
                Object object = iterator.next();
                ParameterKey key = ParameterKey.createParameterKey(object,
                        parameterName);

                // Extract the appropriate ParameterValue and set it.
                ParameterValue parameterValue = (ParameterValue) database
                        .get(key);
                if (parameterValue != null) {

                    Iterator i = movedListeners.iterator();
                    while (i.hasNext()) {
                        PropertyChangeListener listener = (PropertyChangeListener) i
                                .next();
                        parameterValue.addPropertyChangeListener(listener);
                    }

                    firePropertyChange(parameterName, parameterValue);

                    return true;
                }
            }
        }

        // Return indicating that the parameter has been added.
        return true;
    }

    /**
     * Purge a parameter from the list of objects defined by the iterator.
     */
    public void purgeParameter(String parameterName, Iterator iterator) {

        while (iterator.hasNext()) {
            Object object = iterator.next();
            ParameterKey key = ParameterKey.createParameterKey(object,
                    parameterName);
            database.remove(key);
        }
    }

    /**
     * Purge all entries based on a particular object.
     */
    public void purgeEntries(Object object) {

        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ParameterKey key = (ParameterKey) i.next();
            database.remove(key);
        }
    }

    /**
     * Purge all references to a particular listener.
     */
    public void purgePropertyChangeListener(PropertyChangeListener listener) {

        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ParameterKey key = (ParameterKey) i.next();
            ParameterValue value = (ParameterValue) database.get(key);
            if (value != null) {
                value.removePropertyChangeListener(listener);
            }
        }
    }

    /**
     * Purge all references to a particular listener.
     */
    public void purgePropertyChangeListener(Iterator iterator,
            PropertyChangeListener listener) {

        while (iterator.hasNext()) {
            ParameterKey key = (ParameterKey) iterator.next();
            ParameterValue value = (ParameterValue) database.get(key);
            if (value != null) {
                value.removePropertyChangeListener(listener);
            }
        }
    }

    /**
     * Completely clear this parameter database.
     */
    public void clear() {
        database.clear();
    }

    /**
     * Write out the entire database as an XML file. Returns true if there were
     * no errors generated; returns false otherwise. Note that this simply
     * appends a parameters element to the given XMLWriter. The instanceMap must
     * map instances of non-Class objects to a unique Integer. The integer value
     * is used to tag "local" copies of parameters. If null is passed in as the
     * instanceMap none of the local copies of parameters will be saved.
     */
    public boolean writeAsXML(XMLWriter xmlWriter, Hashtable instanceMap) {

        // Begin the list of parameters.
        xmlWriter.openTag("Parameters");

        // Get all of the keys in the database and iterate over them.
        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {

            ParameterKey key = (ParameterKey) i.next();
            ParameterValue value = (ParameterValue) database.get(key);

            if (value != null) {

                String type = "";
                int id = 0;

                boolean classParameter;
                boolean valid;

                Object obj = key.getObject();
                if (obj instanceof Class) {
                    classParameter = true;
                    valid = true;
                    type = ((Class) obj).getName();

                } else {
                    classParameter = false;

                    if (instanceMap != null) {
                        Integer instanceId = (Integer) instanceMap.get(obj);
                        if (instanceId != null) {
                            valid = true;
                            id = instanceId.intValue();
                        } else {
                            valid = false;
                            id = 0;
                        }
                    } else {
                        valid = false;
                    }
                }

                String val = value.getValue().toString();
                String valType = value.getValue().getClass().getName();

                // Add the parameter itself.
                if (valid) {
                    if (classParameter) {
                        xmlWriter.setAttribute("class", type);
                        xmlWriter.setAttribute("name", key.getName());
                        xmlWriter.setAttribute("value", val);
                        xmlWriter.setAttribute("type", valType);
                        xmlWriter.printTag("ClassParameter");
                    } else {
                        xmlWriter.setAttribute("id", id);
                        xmlWriter.setAttribute("name", key.getName());
                        xmlWriter.setAttribute("value", val);
                        xmlWriter.setAttribute("type", valType);
                        xmlWriter.printTag("InstanceParameter");
                    }
                }
            }
        }

        // Write out the trailing information.
        xmlWriter.closeTag();

        return true;
    }

    /**
     * Send off the property change event.
     */
    public void firePropertyChange(String parameterName,
            ParameterValue parameterValue) {

        // Now create the property change event.
        PropertyChangeEvent pcEvent = new PropertyChangeEvent(this,
                parameterName, null, parameterValue.getValue());

        // Send off the notification of the new value.
        Iterator i = parameterValue.getPropertyChangeListeners().iterator();
        while (i.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(pcEvent);
        }

        // Send off notification to the database listeners.
        i = databaseListeners.iterator();
        while (i.hasNext()) {
            DatabaseListener listener = (DatabaseListener) i.next();
            listener.databaseUpdated();
        }
    }

    /**
     * Add a database listener.
     */
    public void addDatabaseListener(DatabaseListener listener) {
        if (listener != null)
            databaseListeners.add(listener);
    }

    /**
     * Remove a database listener
     */
    public void removeDatabaseListener(DatabaseListener listener) {
        if (listener != null)
            databaseListeners.remove(listener);
    }

    /**
     * Get a string array which contains the names of all parameters defined by
     * this iterator. Note that all parameters in the database are checked to
     * see if they match the given objects.
     */
    public String[] getCurrentParameterSet(Iterator iterator) {

        TreeSet parameterNames = new TreeSet();

        // Make a set of all of the objects in the iterator, so that the
        // database itself only needs to be searched once.
        HashSet objects = new HashSet();
        while (iterator.hasNext()) {
            objects.add(iterator.next());
        }

        // Now loop over all of the keys in the database.
        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ParameterKey key = (ParameterKey) i.next();
            if (objects.contains(key.getObject())) {
                parameterNames.add(key.getName());
            }
        }

        // Make an array out of the tree set.
        String[] names = new String[parameterNames.size()];
        names = (String[]) parameterNames.toArray(names);

        return names;
    }

    /**
     * Get a string array which gives all of the parameters which are defined
     * locally on the given object.
     */
    public String[] getLocalParameterSet(Object object) {

        TreeSet parameterNames = new TreeSet();

        // Iterate over all of the keys in the database.
        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ParameterKey key = (ParameterKey) i.next();
            if (object.equals(key.getObject())) {
                parameterNames.add(key.getName());
            }
        }

        // Make an array out of the tree set.
        String[] names = new String[parameterNames.size()];
        names = (String[]) parameterNames.toArray(names);

        return names;
    }

    /**
     * Return a hash table in which the keys are the parameter names and the
     * values are clones of those values in the database.
     */
    public Hashtable cloneLocalParameters(Object object) {

        Hashtable clonedParameters = new Hashtable();

        // Iterate over all of the keys in the database.
        Set keys = database.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            ParameterKey key = (ParameterKey) i.next();
            if (object.equals(key.getObject())) {
                String parameterName = key.getName();
                ParameterValue value = (ParameterValue) database.get(key);
                Object parameterValue = value.getValue();

                try {
                    Object clonedValue = cloneObject(parameterValue);
                    clonedParameters.put(parameterName, clonedValue);
                } catch (Exception e) {
                }
            }
        }

        return clonedParameters;
    }

    /**
     * This will "clone" the given object. This method makes the assumption that
     * the objects stored in the database produce a result from the toString()
     * method which will then produce an identical object when the single-string
     * constructor is called. (Note: this is a general assumption of the
     * database as a whole.)
     */
    protected Object cloneObject(Object originalObject)
            throws NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        // Setup the call parameters for a constructor which takes a String as
        // the only argument.
        Object[] parameters = new Object[1];
        parameters[0] = originalObject.toString();
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = String.class;

        // Construct the new value Object based on the given String value.
        Class valueClass = originalObject.getClass();
        Constructor constructor = valueClass.getConstructor(parameterTypes);
        Object newValue = constructor.newInstance(parameters);

        return newValue;
    }

}
