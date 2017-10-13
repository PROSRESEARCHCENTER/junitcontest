package org.freehep.application;

import java.util.EventListener;

/**
 * Listen for ApplicationEvents
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ApplicationListener.java 14082 2012-12-12 16:16:53Z tonyj $
 */
public interface ApplicationListener extends EventListener {

    void initializationComplete(ApplicationEvent e);
    //TODO: Maybe add this method back in at some future date (breaks backwards compatibility)
    //void applicationVisible(ApplicationEvent e);

    void aboutToExit(ApplicationEvent e);
}
