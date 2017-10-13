package hep.io.root.daemon.xrootd;

import java.util.Date;

/**
 *
 * @author tonyj
 */
public interface MultiplexorMBean {

    long getBytesReceived();

    long getBytesSent();

    Date getCreateDate();
    
    long getIdleTime();

    String getUserName();

    String getHostAndPort();
    
    Date getLastActive();

    int getOutstandingResponseCount();

    int getProtocolVersion();

    int getServerFlag();
}
