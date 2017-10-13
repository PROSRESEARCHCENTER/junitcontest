package hep.io.root.daemon.xrootd;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * A session allows for all supported xrootd commands to be send. All the 
 * methods of this class are synchronous, i.e. they wait until the data is
 * available before they return.
 * @author tonyj
 */
public class Session {

    private static Logger logger = Logger.getLogger(Session.class.getName());
    private Dispatcher dispatcher = Dispatcher.instance();
    private Destination destination;

    public Session(String host, int port, String userName) throws IOException {
        this(new Destination(host, port, userName));
    }
    
    Session(Destination dest) throws IOException {
        this.destination = dest;
    }

    public void close() throws IOException {
        // ToDo: Should we close any open files?
    }
    
    /** 
     * This method allows asynchronous execution of an operation.
     * @param operation The operation to be performed
     * @return A future which will contain the response
     */
    public <V> FutureResponse<V> send(Operation<V> operation)
    {
        Destination actualDestination = operation.getDestination();
        if (actualDestination == null) actualDestination = destination;
        return dispatcher.send(actualDestination, operation);
    }
    
    public List<String> dirList(String path) throws IOException {
        return send(new DirListOperation(path)).getResponse();
    }

    public void ping() throws IOException {
        send(new PingOperation()).getResponse();
    }

    public void remove(final String path) throws IOException {
        send(new RemoveOperation(path)).getResponse();
    }

    public FileStatus stat(final String path) throws IOException {
        return send(new StatOperation(path)).getResponse();
    }

    public String query(final int queryType, final String path) throws IOException {
        return send(new QueryOperation(queryType,path)).getResponse();
    }

    public String prepare(String[] path, int options, int priority) throws IOException {
        return send(new PrepareOperation(path,options,priority)).getResponse();
    }
    
    public String[] locate(String path, boolean noWait, boolean refresh) throws IOException {
        return send(new LocateOperation(path,noWait,refresh)).getResponse();
    }
    
    public String protocol() throws IOException {
        return send(new ProtocolOperation()).getResponse();
    }

    public OpenFile open(final String path, final int mode, final int options) throws IOException {
        return send(new OpenOperation(path,mode,options)).getResponse();
    }

    public void close(OpenFile file) throws IOException {
        send(new CloseOperation(file)).getResponse();
    }

    public int read(OpenFile file, long fileOffset, byte[] buffer) throws IOException {
        return read(file, fileOffset, buffer, 0, buffer.length);
    }

    public int read(OpenFile file, long fileOffset, byte[] buffer, int bufOffset, int size) throws IOException {
        return send(new ReadOperation(file,fileOffset, buffer,bufOffset,size)).getResponse();
    }
    
    public void write(OpenFile file, long fileOffset, byte[] buffer, int offset, int length) throws IOException {
        send(new WriteOperation(file,fileOffset,buffer,offset,length));
    }

    @Override
    public String toString()
    {
        return destination.toString();
    }
}