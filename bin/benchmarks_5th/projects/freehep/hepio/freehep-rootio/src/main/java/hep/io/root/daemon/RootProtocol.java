package hep.io.root.daemon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  Tony Johnson
 */
public class RootProtocol
{
   public final static int defaultPort = 1094;
   
   //---- ROOTD message opcodes (2000 - 2099)
   private final static int kROOTD_USER             = 2000;  //user id follows
   private final static int kROOTD_PASS             = 2001;  //passwd follows
   private final static int kROOTD_AUTH             = 2002;  //authorization status (to client)
   private final static int kROOTD_FSTAT            = 2003;  //filename follows
   private final static int kROOTD_OPEN             = 2004;  //filename follows + mode
   private final static int kROOTD_PUT              = 2005;  //offset, number of bytes and buffer
   private final static int kROOTD_GET              = 2006;  //offset, number of bytes
   private final static int kROOTD_FLUSH            = 2007;  //flush file
   private final static int kROOTD_CLOSE            = 2008;  //close file
   private final static int kROOTD_STAT             = 2009;  //return rootd statistics
   private final static int kROOTD_ACK              = 2010;  //acknowledgement (all OK)
   private final static int kROOTD_ERR              = 2011;  //error code and message follow
   private final static int kROOTD_PROTOCOL         = 2012;  //returns rootd protocol
   private final static int kROOTD_SRPUSER          = 2013;  //user id for SRP authentication follows
   private final static int kROOTD_SRPN             = 2014;  //SRP n follows
   private final static int kROOTD_SRPG             = 2015;  //SRP g follows
   private final static int kROOTD_SRPSALT          = 2016;  //SRP salt follows
   private final static int kROOTD_SRPA             = 2017;  //SRP a follows
   private final static int kROOTD_SRPB             = 2018;  //SRP b follows
   private final static int kROOTD_SRPRESPONSE      = 2019;  //SRP final response
   private final static int kROOTD_PUTFILE          = 2020;  //store file
   private final static int kROOTD_GETFILE          = 2021;  //retrieve file
   private final static int kROOTD_CHDIR            = 2022;  //change directory
   private final static int kROOTD_MKDIR            = 2023;  //make directory
   private final static int kROOTD_RMDIR            = 2024;  //delete directory
   private final static int kROOTD_LSDIR            = 2025;  //list directory
   private final static int kROOTD_PWD              = 2026;  //pwd
   private final static int kROOTD_MV               = 2027;  //rename file
   private final static int kROOTD_RM               = 2028;  //delete file
   private final static int kROOTD_CHMOD            = 2029;  //change permission
   private final static int kROOTD_KRB5             = 2030;  //krb5 authentication follows
   private final static int kROOTD_PROTOCOL2        = 2031;  //client proto follows, returns rootd proto
   private final static int kROOTD_BYE              = 2032;  //terminate rootd
   private final static int kROOTD_GLOBUS           = 2033;  //Globus authetication follows
   private final static int kROOTD_CLEANUP          = 2034;  //Cleanup things
   private final static int kROOTD_SSH              = 2035;  //SSH-like authentication follows
   private final static int kROOTD_RFIO             = 2036;  //RFIO-like authentication follows
   private final static int kROOTD_NEGOTIA          = 2037;  //Negotiation follows
   private final static int kROOTD_RSAKEY           = 2038;  //RSA public key exchange
   private final static int kROOTD_ENCRYPT          = 2039;  //An encrypted message follows

   private int bufferSize = 8096;
   private static int MAXGETSIZE = -1;
   
   private static Logger logger = Logger.getLogger("hep.io.root.daemon");
   static
   {
      if (System.getProperty("debugRootDaemon")!= null)
      {
         logger.setLevel(Level.FINE);
         ConsoleHandler handler = new ConsoleHandler();
         handler.setLevel(Level.FINE);
         logger.addHandler(handler);
      }
   }
   private Socket socket;
   private Message message;
   private Response response;
   
   /** Creates a new instance of RootProtocol */
   public RootProtocol(String host, int port, String auth, String username, String password) throws IOException
   {
      if (port == -1) port = defaultPort;
      logger.fine("Opening rootd connection to: "+host+":"+port);
      socket = new Socket(host,port);
      socket.setSoTimeout(Integer.getInteger("root.timeout",10000).intValue());
      OutputStream out = socket.getOutputStream();
      DataOutputStream data = new DataOutputStream(out);
      
      logger.fine("Sending welcome");
      data.writeInt(0);
      data.writeInt(0);
      data.writeInt(0);
      data.flush();
      
      message = new Message(data);
      InputStream in = socket.getInputStream();
      DataInputStream dataIn = new DataInputStream(in);
      response = new Response(dataIn);
      
      message.send(kROOTD_PROTOCOL);
      int rc = response.read();
      if (response.dataAsInt() < 9) throw new IOException("Unexpected response: "+rc+" "+response.dataAsInt());
      
      message.send(kROOTD_PROTOCOL2,"9");
      if (response.dataAsInt() < 9) throw new IOException("Unexpected response: "+rc+" "+response.dataAsInt());
      rc = response.read();
      
      String user = "9999 -1 5 "+username.length()+" "+username;
      message.send(kROOTD_USER,user);
      rc = response.read();
      
      message.send(kROOTD_PASS,password,true);
      do
      {
         rc = response.read();
      }
      while (rc != kROOTD_AUTH);
   }
   
   public int getBufferSize()
   {
      return this.bufferSize;
   }
   
   public void setBufferSize(int bufferSize)
   {
      this.bufferSize = bufferSize;
   }
   
   public String[] fstat() throws IOException
   {
      message.send(kROOTD_FSTAT);
      int rc = response.read();     
      return response.dataAsString().split("\\s+");
   }
   public void open(String file) throws IOException
   {
      message.send(kROOTD_OPEN,file+" r");
      int rc = response.read();
   }
   public DaemonInputStream openStream(long size)
   {
      return new RootStream(size);
   }
   public String[] ls(String dir) throws IOException
   {
      message.send(kROOTD_LSDIR,dir);
      StringBuffer result = new StringBuffer();
      for (;;) 
      {
         int rc = response.read();
         result.append(response.dataAsString());
         if (rc == kROOTD_LSDIR) break;
      }
      return result.toString().split("\n");
   }
   public String pwd() throws IOException
   {
      message.send(kROOTD_PWD);
      int rc = response.read();
      return response.dataAsString();
   }
   private static class Message
   {
      private DataOutputStream data;
      Message(DataOutputStream data)
      {
         this.data = data;
      }
      void send(int message) throws IOException
      {
         data.writeInt(4);
         data.writeInt(message);
         data.flush();
      }
      void send(int message, String extra) throws IOException
      {
         send(message,extra,false);
      }
      void send(int message, String extra, boolean invert) throws IOException
      {
         logger.fine("->"+message+" "+extra);
         byte[] bytes = extra.getBytes();
         if (invert)
         {
            for (int i=0; i<bytes.length; i++)
            {
               bytes[i] = (byte) ~bytes[i];
            }
         }
         data.writeInt(4+bytes.length+1);
         data.writeInt(message);
         data.write(bytes);
         data.write(0);
         data.flush();
      }
   }
   private static class Response
   {
      private DataInputStream in;
      private int code;
      private int dataLength;
      private byte[] data;
      Response(DataInputStream in)
      {
         this.in = in;
      }
      int read() throws IOException
      {
         dataLength = in.readInt() - 4;
         code = in.readInt();
         logger.fine("<-"+code+" ("+dataLength+") ");
         if (data == null || dataLength>data.length) data = new byte[dataLength];
         for (int i=0; i<dataLength; i++)
         {
            data[i] = in.readByte();
         }
         if (code == kROOTD_ERR) throw new RootdException(this);
         return code;
      }
      int read(byte[] values, int offset, int size) throws IOException
      {
         read();
	 int n = size;
	 while (n>0)
	 {
	    int k = in.read(values,offset,n);
	    if (k<0) throw new IOException("Unexpected end of input");
	    n -= k;
	    offset += k;
	 }
	 return size;
      }
      int dataAsInt()
      {
         int l = data[0] << 24;
         l += data[1] << 16;
         l += data[2] << 8;
         l += data[3];
         return l;
      }
      String dataAsString()
      {
         int l = dataLength;
         for (int i=0; i<l; i++)
         {
            if (data[i] == 0) 
            {
               l = i;
               break;
            }
         }
         return new String(data,0,l);
      }
   }
   private static class RootdException extends IOException
   {
      RootdException(Response response)
      {
         super("Root Deamon exception: "+response.dataAsInt());
      }
   }
   private class RootStream extends DaemonInputStream
   {
      private byte[] buffer = new byte[bufferSize];
      private int bpos = 0;
      private int blen = 0;
      private long fsize;
      
      RootStream(long fsize)
      {
         this.fsize = fsize;
      }
      long getSize()
      {
         return fsize;
      }
      public int read() throws IOException
      {
         if (bpos >= blen) 
         {
            if (!fillBuffer()) return -1;
         }
         int i = buffer[bpos++];
         if (i < 0) i += 256;
         return i;
      }
      
      public void close() throws IOException
      {
         message.send(kROOTD_CLOSE);
         //response.read();
         socket.close();
      }
      
      public int read(byte[] values, int offset, int size) throws IOException
      {
         if (bpos >= blen)
         {
            long position = this.position+bpos;
            if (position >= fsize) return -1;
            long n = Math.min(fsize-position,size);
            if (MAXGETSIZE > 0 && n > MAXGETSIZE) n = MAXGETSIZE;
            String where = position+" "+n;
            message.send(kROOTD_GET,where);
            int l = response.read(values,offset,(int) n);
            this.position += l;
            return l;
         }
         else
         {
            int l = Math.min(size,blen-bpos);
            System.arraycopy(buffer, bpos, values, offset, l);
            bpos += l;
            return l;
         }
      }
      
      public long skip(long skip) throws IOException
      {
         setPosition(getPosition()+skip);
         return skip;
      }
      
      public void setPosition(long pos)
      {
         if (pos>position && pos<position+blen)
         {
            bpos = (int) (pos-position);
         }
         else
         {
            blen = 0;
            bpos = 0;
            super.setPosition(pos);
         }
      }
      
      public int available() throws IOException
      {
         return blen - bpos;
      }
      
      private boolean fillBuffer() throws IOException
      {
        position += bpos;
        bpos = 0;
        long n = Math.min(fsize-position,buffer.length);
        if (n <= 0) return false;
        if (MAXGETSIZE > 0 && n > MAXGETSIZE) n = MAXGETSIZE;
        String where = position+" "+n;
        message.send(kROOTD_GET,where);
        blen = response.read(buffer,0,(int) n); 
        return true;
      }
      
      public long getPosition()
      {
         return position + bpos;
      }
   }   
   public static void main(String[] args) throws IOException
   {
      RootProtocol rp = new RootProtocol("london.jaws.com",-1,"anonymous","tonyj@slac",null);
      System.out.println(rp.pwd());
      String[] result = rp.ls("/bin/ls -F");
      for (int i=0; i<result.length; i++) System.out.println(result[i]);
   }   
}
