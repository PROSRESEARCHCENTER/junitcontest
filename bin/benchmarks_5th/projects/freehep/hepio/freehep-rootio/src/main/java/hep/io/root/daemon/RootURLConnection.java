package hep.io.root.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;


/**
 * The core class for dealing with root: protocol connections.
 * Currently only supports reading files. Currently only supports
 * plain text (insecure) authorization.
 * @author Tony Johnson
 */
public class RootURLConnection extends URLConnection
{
   private String username;
   private String password;
   private String auth; // authorization mode to use
   private int bufferSize = 0;
   
   private DaemonInputStream connection;
   private long date;
   private long fSize;
   private static Logger logger = Logger.getLogger("hep.io.root.daemon");

   RootURLConnection(URL url)
   {
      super(url);
   }
   public InputStream getInputStream() throws IOException
   {
      connect();
      return connection;
   }
   public void connect() throws IOException
   {
      if (connected) return;

      if (auth == null) auth = System.getProperty("root.scheme");
      if (auth != null && auth.equalsIgnoreCase("anonymous"))
      {
         username = "anonymous";
         try
         {
            password = System.getProperty("user.name")+"@"+InetAddress.getLocalHost().getCanonicalHostName();
         }
         catch (SecurityException x)
         {
            password = "freehep-user@freehep.org";
         }
      }
      
      if (username == null) username = System.getProperty("root.user");
      if (password == null) password = System.getProperty("root.password");
      
      // Check for username password, if not present, and if allowed, prompt the user.
      if ((password == null || username == null) && getAllowUserInteraction())
      {
         int port = url.getPort();
         if (port == -1) port = RootProtocol.defaultPort;
         PasswordAuthentication pa = Authenticator.requestPasswordAuthentication(url.getHost(),null,port,"root","Username/Password required", auth);
         if (pa != null) 
         {
            username = pa.getUserName();
            password = new String(pa.getPassword());
         }
      }

      if (password == null || username == null) throw new IOException("Authorization Required");
            
      logger.fine("Opening rootd connection to: "+url);
      RootProtocol rp = new RootProtocol(url.getHost(),url.getPort(),auth,username,password);
      if (bufferSize != 0) rp.setBufferSize(bufferSize);

      rp.open(url.getFile());
      String[] fstat = rp.fstat();
      fSize = Long.parseLong(fstat[1]);
      date = Long.parseLong(fstat[3])*1000;
      
      connection = rp.openStream(fSize);
      connected = true;
   }
   
   public int getContentLength()
   {
      if (connection == null) return -1;
      return (int) fSize;
   }
   
   public long getLastModified()
   {
      if (connection == null) return -1;
      return date;
   }
   
   public long getDate()
   {
      return getLastModified();
   }   
   
   public void setRequestProperty(String key, String value)
   {
      if      (key.equalsIgnoreCase("user"))         username = value;
      else if (key.equalsIgnoreCase("password"))     password = value;
      else if (key.equalsIgnoreCase("scheme"))           auth = value;
      else if (key.equalsIgnoreCase("bufferSize")) bufferSize = Integer.parseInt(value);
   }  
}
