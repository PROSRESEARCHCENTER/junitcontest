package hep.io.root.daemon.xrootd;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Represents a target host for an xrootd operation.
 * @author tonyj
 */
public class Destination {

    private String host;
    private int port;
    private String userName;
    private Destination previous;
    private InetAddress[] addresses;
    private InetAddress address;

    private Destination() {
    }

    public Destination(String host, int port, String userName) throws UnknownHostException {
        this.host = host;
        this.port = port <= 0 ? XrootdProtocol.defaultPort : port;
        this.userName = userName;
        this.addresses = InetAddress.getAllByName(host);
        if (addresses.length == 0) {
            throw new UnknownHostException("No valid IP addresses");
        }
        Collections.shuffle(Arrays.asList(addresses));
        address = addresses[0];
    }

    String getAddressAndPort() {
        return address + ":" + port;
    }

    Destination getAlternateDestination(int index) {
        if (index == 0 || addresses.length < 2) {
            return this;
        } else {
            return copy(index);
        }
    }

    private Destination copy(int index) {
        Destination dest = new Destination();
        dest.host = host;
        dest.port = port;
        dest.userName = userName;
        dest.previous = previous;
        dest.addresses = addresses;
        dest.address = addresses[index % addresses.length];
        return dest;
    }

    int getPort() {
        return port;
    }

    Destination getPrevious() {
        return previous;
    }

    Destination getRedirected(String host, int port) throws UnknownHostException {
        Destination dest = new Destination(host, port, this.userName);
        dest.previous = this;
        return dest;
    }

    InetAddress getAddress() {
        return address;
    }

    SocketAddress getSocketAddress() {
        return new InetSocketAddress(address, port);
    }

    String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Destination) {
            Destination that = (Destination) obj;
            return this.address.equals(that.address) &&
                    this.port == that.port &&
                    this.userName.equals(that.userName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return address.hashCode() + port + userName.hashCode();
    }

    @Override
    public String toString() {
        return "[" + address + ":" + port + ":" + userName + "]";
    }
}
