package hep.aida.web.taglib.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Various utility functions for the tag logs.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public abstract class LogUtils {

    private static Log log = LogFactory.getLog(LogUtils.class);

    public static Log log() {
        return log;
    }

}