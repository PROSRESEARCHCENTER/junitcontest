package org.freehep.application.studio;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Listener class for events fired by {@link Studio} applications.
 * @author tonyj
 */
public interface StudioListener extends EventListener {

    void handleEvent(EventObject event);
}
