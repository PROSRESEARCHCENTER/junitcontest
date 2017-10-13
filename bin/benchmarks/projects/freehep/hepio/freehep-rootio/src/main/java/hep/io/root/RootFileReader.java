package hep.io.root;

import hep.io.root.core.DefaultClassFactory;
import hep.io.root.core.FastInputStream;
import hep.io.root.core.FileClassFactory;
import hep.io.root.core.RootClassFactory;
import hep.io.root.core.RootDaemonInputStream;
import hep.io.root.core.RootInput;
import hep.io.root.core.RootRandomAccessFile;
import hep.io.root.daemon.DaemonInputStream;
import hep.io.root.interfaces.TDatime;
import hep.io.root.interfaces.TDirectory;
import hep.io.root.interfaces.TFile;
import hep.io.root.interfaces.TKey;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


/**
 * A class for reading root files.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootFileReader.java 15983 2014-05-15 22:33:47Z tonyj $
 */
public class RootFileReader implements TFile
{
   private static boolean welcome = false;
   private static boolean debug = System.getProperty("debugRoot") != null;
   private ClassLoader classLoader;
   private java.util.Date fDatimeC;
   private java.util.Date fDatimeM;
   private RootClass fileClass;
   private RootClassFactory factory;
   private RootInput in;
   private String name;
   private String title;
   private TDirectory dir;
   private TKey streamerInfo;
   private int fCompress;
   private int fNbytesKeys;
   private int fNbytesName;
   private long fSeekDir;
   private long fSeekKeys;
   private long fSeekParent;
   private int fVersion;
   private long fSeekInfo;
 
   /** 
    * Open a file specified by URL for reading.
    * The URL may point to
    * <ul>
    * <li>a file, 
    * <li>to a rootd daemon (root: protocol)
    * <li>to any other data (not yet implemented)
    * </ul>
    * source. In the last case the file will be buffered in memory prior to 
    * reading, so this method should only be used for small files.
    * <p>
    * The options argument is used to pass options to the file reader. Supported
    * option include:
    * <ul>
    * <li>scheme -- The authorization scheme, currently supported UsrPwd, Anonymous
    * <li>user -- The user name to use
    * <li>password -- The password to use
    * </ul>
    * The shared parameter may be used to pass an already open RootFileReader. In this case
    * the files will share the same cache or loaded RootClass's. This will speed up opening 
    * many files, but will only work if the files contain the same root classes.
    */
   public RootFileReader(URL url, Map options, RootFileReader shared) throws IOException
   {
      if (url.getProtocol().equals("file")) init(new File(url.getFile()),shared);
      else
      {
         URLConnection connection = url.openConnection();
         
         if (options != null)
         {
            Object user = options.get("user");
            if (user != null) connection.setRequestProperty("user", user.toString());

            Object pass = options.get("password");
            if (pass != null) connection.setRequestProperty("password", pass.toString());         

            Object mode = options.get("scheme");
            if (mode != null) connection.setRequestProperty("scheme", mode.toString()); 
            
            Object bufferSize = options.get("bufferSize");
            if (bufferSize != null) connection.setRequestProperty("bufferSize", bufferSize.toString()); 
         }
         
         InputStream source = connection.getInputStream();
         if (source instanceof DaemonInputStream) init(new RootDaemonInputStream((DaemonInputStream) source,this),shared);
         else
         {
            throw new IOException("Unsupported protocol: "+url.getProtocol() );
//            if ((source.readByte() != 'r') || (source.readByte() != 'o') || (source.readByte() != 'o') || (source.readByte() != 't'))
//               throw new IOException("Not a root file");
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            byte[] buffer = new byte[8192];
//            for (;;)
//            {
//               int l = source.read(buffer);
//               if (l<0) break;
//               out.write(buffer,0,l);
//            }
//            source.close();
//            out.close();
//            init(new RootInputStream(new RootByteArrayInputStream(out.toByteArray(),0),this);
         }
      }
   }
   public RootFileReader(URL url, Map options) throws IOException
   {
      this(url,options,null);
   }
   /**
    * Open a file specified by URL for reading with the default options.
    */
   public RootFileReader(URL url) throws IOException
   {
      this(url,null);
   }

   /**
    * Open a root file for reading.
    * The DefaultClassFactory will be used for creating classes
    * @param file The name of the file to open
    * @throws IOException If the file cannot be opened
    */
   public RootFileReader(String file) throws IOException
   {
      init(new File(file),null);
   }

   /**
    * Open a root file for reading
    * @param file The file to open
    */
   public RootFileReader(File file) throws IOException
   {
      init(file,null);
   }
   public RootFileReader(File file, RootFileReader shared) throws IOException
   {
      init(file,shared);
   }
   private void init(File file, RootFileReader shared) throws IOException
   {
      RootRandomAccessFile raf = new RootRandomAccessFile(file, this);
      RootInput in = raf;
      if (System.getProperty("useNIO") != null) in = new FastInputStream(this, raf);
      init(in, shared);
   }
   private void init(RootInput in, RootFileReader shared) throws IOException
   {
      try
      {
         this.in = in;
         if (!welcome) welcome();
         factory = new DefaultClassFactory(this);
         fileClass = factory.create("TFile");

         fVersion = in.readInt();
         if (fVersion < 30006)
            throw new IOException("hep.io.root package cannot read files created by Root before release 3.00/6 (" + fVersion + ")");
         if (debug)
            System.out.println("version=" + fVersion);
         boolean fLargeFile = fVersion > 1000000;
         fVersion %= 1000000;

         int fBEGIN = in.readInt();
         if (fLargeFile)
         {
             long fEND = in.readLong();
             long fSeekFree = in.readLong();
             int fNbytesFree = in.readInt();
             int nfree = in.readInt();
             fNbytesName = in.readInt();

             int fUnits = in.readByte();
             fCompress = in.readInt();
             fSeekInfo = in.readLong();
             int fNBytesInfo = in.readInt();             
         }
         else
         {
             int fEND = in.readInt();
             int fSeekFree = in.readInt();
             int fNbytesFree = in.readInt();
             int nfree = in.readInt();
             fNbytesName = in.readInt();

             int fUnits = in.readByte();
             fCompress = in.readInt();
             fSeekInfo = in.readInt();
             int fNBytesInfo = in.readInt();
         }

         in.setPosition(fBEGIN);

         int Nbytes = in.readInt();
         int version = in.readShort();
         int ObjLen = in.readInt();
         fDatimeC = fDatimeM = ((TDatime) in.readObject("TDatime")).getDate();

         int KeyLen = in.readShort();
         int Cycle = in.readShort();
         long fSeekKey;
         long fSeekPdir;
         if (version > 1000)
         {
            fSeekKey = in.readLong();
            fSeekPdir = in.readLong();
         }
         else
         {
            fSeekKey = in.readInt();
            fSeekPdir = in.readInt();         
         }

         String className = in.readObject("TString").toString();
         name = in.readObject("TString").toString();
         title = in.readObject("TString").toString();
                 
         in.setPosition(fBEGIN + fNbytesName); // This should get us to the directory
         dir = (TDirectory) in.readObject("TDirectory");

         if (fSeekInfo == 0) recover(fBEGIN);
         
         if (shared != null)
         {
            this.factory = shared.factory;
         }
         else if (fSeekInfo != 0)
         {
            in.setPosition(fSeekInfo);
            streamerInfo = (TKey) in.readObject("TKey");
            this.factory = new FileClassFactory(streamerInfo, factory, this);
         }
         else throw new IOException("Could not located StreamerInfo in root file");
      }
      catch (RootClassNotFound xr)
      {
         IOException x = new IOException("Root Class Not Found: " + xr.getClassName());
         x.initCause(xr);
         throw x;
      }
   }

   private void recover(int begin) throws IOException
   {
       if (debug) System.err.println("File recovery in process");
       long idcur  = begin;
       int nread = 1024;
       for (;;)
       {
           in.setPosition(idcur);
           int nbytes = in.readInt();
           if (nbytes < 0) // freespace
           {
               idcur -= nbytes;  
           }
           else
           {
               int v = in.readVersion();
               int fObjlen = in.readInt();
               java.util.Date fDatime = ((hep.io.root.interfaces.TDatime) in.readObject("TDatime")).getDate();
               int fKeylen = in.readShort();
               int fCycle = in.readShort();
               if (v > 1000)
               {
                  long fSeekKey = in.readLong();
                  long fSeekPdir = in.readLong();
               }
               else
               {
                  int fSeekKey = in.readInt();
                  int fSeekPdir = in.readInt();         
               }
               String fClassName = in.readObject("TString").toString();
               String fName = in.readObject("TString").toString();
               String fTitle = in.readObject("TString").toString();
               if ("StreamerInfo".equals(fName)) fSeekInfo = idcur;
               idcur += nbytes; 
           }
       }
   }
   public int getBits()
   {
      return 0;
   }

   public RootClassFactory getClassFactory()
   {
      return factory;
   }

   /**
    * Set the classloader to use for checking for interfaces and loading proxies.
    */
   public void setClassLoader(ClassLoader loader)
   {
      classLoader = loader;
   }

   public ClassLoader getClassLoader()
   {
      return classLoader;
   }

   public java.util.Date getDatimeC()
   {
      return fDatimeC;
   }

   public java.util.Date getDatimeM()
   {
      return fDatimeM;
   }

   public RootClassFactory getFactory()
   {
      return factory;
   }

   public TKey getKey(int index)
   {
      return dir.getKey(index);
   }

   public TKey getKey(String name)
   {
      return dir.getKey(name);
   }

   public TKey getKey(String name, int cycle)
   {
      return dir.getKey(name, cycle);
   }

   public TKey getKeyForTitle(String name)
   {
      return dir.getKeyForTitle(name);
   }
   
      public boolean hasKey(String name, int cycle)
   {
      return dir.hasKey(name,cycle);
   }

   public boolean hasKey(String name)
   {
      return dir.hasKey(name);
   }

   public String getName()
   {
      return name;
   }

   public int getCompress()
   {
      return fCompress;
   }
   
   public int getNbytesKeys()
   {
      return fNbytesKeys;
   }

   public int getNbytesName()
   {
      return fNbytesName;
   }

   /**
    * Get the class of this object
    * @return The RootClass for this object
    */
   public RootClass getRootClass()
   {
      return fileClass;
   }

   public long getSeekDir()
   {
      return fSeekDir;
   }

   public long getSeekKeys()
   {
      return fSeekKeys;
   }

   public long getSeekParent()
   {
      return fSeekParent;
   }

   public String getTitle()
   {
      return title;
   }

   public int getUniqueID()
   {
      return 0;
   }

   public int getVersion()
   {
      return fVersion;
   }

   public void close() throws IOException
   {
      in.close();
   }

   /**
    * Get the object associated with a particular key
    */
   public Object get(String name) throws IOException, RootClassNotFound
   {
      TKey key = getKey(name);
      return key.getObject();
   }

   public int nKeys()
   {
      return dir.nKeys();
   }

   /**
    * Get the StreamerInfo
    */
   public List streamerInfo() throws IOException
   {
      try
      {
         return (List) streamerInfo.getObject();
      }
      catch (RootClassNotFound x)
      {
         throw new IOException("Root Class Not Found during IO: " + x);
      }
   }

   public TKey streamerInfoKey()
   {
      return streamerInfo;
   }

   private static void welcome()
   {
      System.out.println("Root IO for Java, part of the FreeHEP library: http://java.freehep.org");
      System.out.println("Please report all bugs/problems to tonyj@slac.stanford.edu");
      System.out.println("Version $Id: RootFileReader.java 15983 2014-05-15 22:33:47Z tonyj $");
      welcome = true;
   }
}