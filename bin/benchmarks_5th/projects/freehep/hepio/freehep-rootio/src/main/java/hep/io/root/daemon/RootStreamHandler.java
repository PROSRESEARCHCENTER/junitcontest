package hep.io.root.daemon;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * A stream handler for files opened with the root protocol.
 * This stream handler will be created automatically if RootURLStreamFactory
 * is used, or this can be created and passed as an argument to the URL 
 * constructor.
 * @author Tony Johnson
 */
public class RootStreamHandler extends URLStreamHandler
{
   
   protected URLConnection openConnection(URL uRL) throws IOException
   {
      return new RootURLConnection(uRL);
   }
   
   protected int getDefaultPort()
   {
      return RootProtocol.defaultPort;
   }
   
}
