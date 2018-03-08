package org.freehep.application.studio;

import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for handling {@link StudioListener}s.
 * 
 * @author tonyj
 */
public class EventSender {

    EventSender() {
    }

    public void broadcast(EventObject event) {
        for (ListenerEntry l : listeners) {
            if (l.getEntryClass().isAssignableFrom(event.getClass())) {
                l.getListener().handleEvent(event);
            }
        }
    }

    public boolean hasListeners(Class c) {
        // FIXME:
        return true;
    }

    public void addEventListener(StudioListener l, Class c) {
        listeners.add(new ListenerEntry(l, c));
    }

    public void removeEventListener(StudioListener l, Class c) {
        listeners.remove(new ListenerEntry(l, c));
    }
    private Set<ListenerEntry> listeners = new HashSet<ListenerEntry>();

    private class ListenerEntry {

        private StudioListener l;
        private Class c;

        ListenerEntry(StudioListener l, Class c) {
            this.l = l;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ListenerEntry) {
                ListenerEntry that = (ListenerEntry) o;
                return this.l == that.l && this.c == that.c;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return l.hashCode() + c.hashCode();
        }

        Class getEntryClass() {
            return c;
        }

        StudioListener getListener() {
            return l;
        }
    }
}