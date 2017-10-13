package org.freehep.application.mdi;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;

/**
 * Handle for a graphical component (page) managed by a {@link PageManager}.
 * Keeps information about the page properties and state.
 * Allows the user to interact with a page in an abstract way.
 * Keeps a list of listeners that should be notified of changes in the page state.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: PageContext.java 16420 2016-10-03 01:10:39Z onoprien $
 */
public class PageContext {

    /**
     * Constructs an instance.
     * 
     * @param component Graphical component (page) to be managed by this {@code PageContext} instance.
     * @param title Name of the page.
     * @param icon Icon associated with the page.
     * @param type Type of the page. Can be used to apply bulk operations to all pages of a certain type.
     */
    PageContext(Component component, String title, Icon icon, String type) {
        this.component = component;
        this.title = title;
        this.icon = icon;
        this.type = type;
    }

    void setPageManager(PageManager manager) {
        this.pageManager = manager;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    /**
     * Adds a page listener to receive notifications of user initiated changes.
     *
     * @param listener The PageListener to install
     */
    public void addPageListener(PageListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(PageListener.class, listener);
    }

    /**
     * Remove a previously installed PageListener
     *
     * @param listener The PageListener to remove
     */
    public void removePageListener(PageListener listener) {
        listenerList.remove(PageListener.class, listener);
    }

    void firePageEvent(PageEvent event, int id) {
        if (listenerList != null) {
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == PageListener.class) {
                    // Lazily create the event:
                    if (event == null) {
                        event = new PageEvent(this, id);
                    }
                    ((PageListener) listeners[i + 1]).pageChanged(event);
                }
            }
        }
    }

    /**
     * Requests that the associated page be shown.
     * If the page is iconized it is deiconized, and brought to the top.
     */
    public void requestShow() {
        pageManager.show(this);
    }

    /** Closes this page. */
    public void close() {
        pageManager.close(this);
    }

    /** Returns the component associated with this page. */
    public Component getPage() {
        return component;
    }

    /** Returns the name associated with the page. */
    public String getTitle() {
        return title;
    }

    /** Returns the icon associated with the page. */
    public Icon getIcon() {
        return icon;
    }

    /** Sets the title of this page. */
    public void setTitle(String title) {
        this.title = title;
        pageManager.titleChanged(this);
    }

    /** Sets the icon associated with this page. */
    public void setIcon(Icon icon) {
        this.icon = icon;
        pageManager.iconChanged(this);
    }

    @Override
    public String toString() {
        return "PageContext: " + title;
    }

    /** Returns the type of this page. */
    public String type() {
        return type;
    }
    
    /** Sets the type of this page. */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns the value of the property with the key obtained by calling {@code toString()} method on the argument.
     * Only properties added with {@code putProperty} will return a non-null value.
     * 
     * @param key The property key.
     * @return The value of this property or {@code null}.
     */
    public Object getProperty(Object key) {
        return properties == null ? null : properties.get(key.toString());
    }
    
    /**
     * Adds a key/value property to this page. 
     * The {@code get/putProperty} methods provide access to a small per-instance hash map.
     * Objects specified as keys are converted to strings by calling their {@code toString()}
     * methods before being used as keys to the map.If value is {@code null} this method will
     * remove the property. Changes to client properties are reported with PropertyChange events.

     * @param key The property key.
     * @param value The property value.
     * @return The previous value of the property.
     */
    public Object putProperty(Object key, Object value) {
        Object oldValue;
        String sKey = key.toString();
        if (value == null) {
            if (properties == null) return null;
            oldValue = properties.remove(sKey);
            if (properties.isEmpty()) properties = null;
        } else {
            if (properties == null) properties = new HashMap<>(4);
            oldValue = properties.put(sKey, value);
        }
        if (listenerList != null && !Objects.equals(oldValue, value)) {
            PropertyChangeEvent event = null;
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == PropertyChangeListener.class) {
                    if (event == null) event = new PropertyChangeEvent(this, sKey, oldValue, value);
                    ((PropertyChangeListener) listeners[i + 1]).propertyChange(event);
                }
            }
        }
        return oldValue;
    }
    
    /**
     * Adds a listener that should be notified of changes in properties modified with {@code putProperty}.
     * @param listener The listener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(PropertyChangeListener.class, listener);
    }
    
    /**
     * Removes a property change listener.
     * @param listener The listener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }
    
    /** Graphical component handled by this PageContext. */
    private final Component component;
    
    private PageManager pageManager;
    private EventListenerList listenerList;
    
    private String type;
    private String title;
    private Icon icon;
    
    private HashMap<String,Object> properties;
}
