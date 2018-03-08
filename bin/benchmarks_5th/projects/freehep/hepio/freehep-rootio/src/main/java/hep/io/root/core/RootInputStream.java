package hep.io.root.core;

import hep.io.root.RootClassNotFound;
import hep.io.root.RootObject;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Inflater;
import org.tukaani.xz.XZInputStream;

/**
 * An implementation of RootInput
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootInputStream.java 15211 2013-05-24 23:52:40Z bvan $
 */
class RootInputStream extends DataInputStream implements RootInput
{
   private static int kByteCountMask = 0x40000000;
   private static int kNewClassTag = 0xFFFFFFFF;
   private static int kClassMask = 0x80000000;
   private static int kMapOffset = 2;
   private static int HDRSIZE = 9;
   private Hashtable readMap = new Hashtable();
   private RootInput top;
   
   public enum ZAlgo {
       GLOBAL_SETTING,
       ZLIB,
       LZMA,
       OLD,
       UNDEFINED;
       
       public static ZAlgo getAlgo(int fCompress){
           int algo = (fCompress - (fCompress % 100) ) / 100;
           return ZAlgo.values()[algo];
       }
       
       public static int getLevel(int fCompress){
           return fCompress % 100;
       }
       
       public static ZAlgo getAlgo(byte[] header){
           if(header[0] == 'Z' && header[1] == 'L'){
               return ZLIB;
           }
           if(header[0] == 'X' && header[1] == 'Z'){
               return LZMA;
           }
           return UNDEFINED;
       }
   }
   
   public RootInputStream(RootByteArrayInputStream in, RootInput top)
   {
      super(in);
      this.top = top;
   }
   
   public RootClassFactory getFactory()
   {
      return top.getFactory();
   }
   
   public void setMap(int offset)
   {
      ((RootByteArrayInputStream) in).setOffset(offset);
   }
   
   public void setPosition(long pos)
   {
      ((RootByteArrayInputStream) in).setPosition(pos);
   }
   
   public long getPosition()
   {
      return ((RootByteArrayInputStream) in).getPosition();
   }
   
   public int getRootVersion()
   {
      return top.getRootVersion();
   }
   
   public RootInput getTop()
   {
      return top;
   }
   
   public void checkLength(AbstractRootObject obj) throws IOException
   {
      RootInputStream.checkLength(this, obj);
   }
   
   public void clearMap()
   {
      ((RootByteArrayInputStream) in).setOffset(0);
   }
   
   public int readArray(int[] data) throws IOException
   {
      return RootInputStream.readArray(this, data);
   }
   
   public int readArray(byte[] data) throws IOException
   {
      return RootInputStream.readArray(this, data);
   }
   
   public int readArray(short[] data) throws IOException
   {
      return RootInputStream.readArray(this, data);
   }
   
   public int readArray(float[] data) throws IOException
   {
      return RootInputStream.readArray(this, data);
   }
   
   public int readArray(double[] data) throws IOException
   {
      return RootInputStream.readArray(this, data);
   }
   
   public void readFixedArray(int[] data) throws IOException
   {
      RootInputStream.readFixedArray(this, data);
   }
   
   public void readFixedArray(long[] data) throws IOException
   {
      RootInputStream.readFixedArray(this, data);
   }
   
   public void readFixedArray(byte[] data) throws IOException
   {
      int l = data.length;
      for (int i = 0;i<l;)
      {
         int n = in.read(data,i,l-i);
         if (n < 0) throw new EOFException();
         i += n;
      }
   }
   
   public void readFixedArray(short[] data) throws IOException
   {
      RootInputStream.readFixedArray(this, data);
   }
   
   public void readFixedArray(float[] data) throws IOException
   {
      RootInputStream.readFixedArray(this, data);
   }
   
   public void readFixedArray(double[] data) throws IOException
   {
      RootInputStream.readFixedArray(this, data);
   }
   
   public void readMultiArray(Object[] array) throws IOException
   {
      RootInputStream.readMultiArray(this, array);
   }
   
   public String readNullTerminatedString(int maxLength) throws IOException
   {
      return RootInputStream.readNullTerminatedString(this, maxLength);
   }
   
   public RootObject readObject(String type) throws IOException
   {
      return RootInputStream.readObject(this, type);
   }
   
   public RootObject readObjectRef() throws IOException
   {
      return RootInputStream.readObjectRef(this, readMap);
   }
   
   public String readString() throws IOException
   {
      return RootInputStream.readString(this);
   }
   
   public int readVersion() throws IOException
   {
      return RootInputStream.readVersion(this, null);
   }
   
   public int readVersion(AbstractRootObject obj) throws IOException
   {
      return RootInputStream.readVersion(this, obj);
   }
   
   public RootInput slice(int size) throws IOException
   {
      return RootInputStream.slice(this, size);
   }
   
   public RootInput slice(int inSize, int outSize) throws IOException
   {
      return RootInputStream.slice(this, inSize, outSize);
   }
   public double readTwistedDouble() throws IOException
   {
      return RootInputStream.readTwistedDouble(this);
   }
   public void dump() throws IOException
   {
      RootInputStream.dump(this,200);
   }
   public void skipObject() throws IOException
   {
      RootInputStream.skipObject(this);
   }
   static void checkLength(RootInput in, AbstractRootObject obj) throws IOException
   {
      obj.checkLength(in.getPosition());
   }
   
   static int readArray(RootInput in, int[] data) throws IOException
   {
      int n = in.readInt();
      for (int i = 0; i < n; i++)
         data[i] = in.readInt();
      return n;
   }
   
   static int readArray(RootInput in, byte[] data) throws IOException
   {
      int n = in.readInt();
      for (int i = 0; i < n; i++)
         data[i] = in.readByte();
      return n;
   }
   
   static int readArray(RootInput in, short[] data) throws IOException
   {
      int n = in.readInt();
      for (int i = 0; i < n; i++)
         data[i] = in.readShort();
      return n;
   }
   
   static int readArray(RootInput in, float[] data) throws IOException
   {
      int n = in.readInt();
      for (int i = 0; i < n; i++)
         data[i] = in.readFloat();
      return n;
   }
   
   static int readArray(RootInput in, double[] data) throws IOException
   {
      int n = in.readInt();
      for (int i = 0; i < n; i++)
         data[i] = in.readDouble();
      return n;
   }
   
   static void readFixedArray(RootInput in, int[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readInt();
   }
   
   static void readFixedArray(RootInput in, long[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readLong();
   }
   
   static void readFixedArray(RootInput in, byte[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readByte();
   }
   
   static void readFixedArray(RootInput in, short[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readShort();
   }
   
   static void readFixedArray(RootInput in, float[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readFloat();
   }
   
   static void readFixedArray(RootInput in, double[] data) throws IOException
   {
      int n = data.length;
      for (int i = 0; i < n; i++)
         data[i] = in.readDouble();
   }
   
   static void readMultiArray(RootInput in, Object[] array) throws IOException
   {
      for (int i = 0; i < array.length; i++)
      {
         Object o = array[i];
         if (o instanceof double[])
            readFixedArray(in, (double[]) o);
         else if (o instanceof float[])
            readFixedArray(in, (float[]) o);
         else if (o instanceof short[])
            readFixedArray(in, (short[]) o);
         else if (o instanceof byte[])
            readFixedArray(in, (byte[]) o);
         else if (o instanceof int[])
            readFixedArray(in, (int[]) o);
         else if (o instanceof long[])
            readFixedArray(in, (long[]) o);
         else if (o instanceof Object[])
            readMultiArray(in, (Object[]) o);
         else
            throw new IOException("Unknown multiarray element: "+o.getClass());
      }
   }
   
   static String readNullTerminatedString(DataInput in, int maxLength) throws IOException
   {
      int actualLength = maxLength - 1;
      byte[] data = new byte[maxLength];
      ;
      for (int i = 0; i < maxLength; i++)
      {
         data[i] = in.readByte();
         if (data[i] == 0)
         {
            actualLength = i;
            break;
         }
      }
      return new String(data, 0, actualLength);
   }
   
   static RootObject readObject(RootInput in, String type) throws IOException
   {
      try
      {
         AbstractRootObject obj = ((GenericRootClass) in.getFactory().create(type)).newInstance();
         obj.read(in);
         return obj;
      }
      catch (RootClassNotFound x)
      {
         throw new IOException("Could not find class " + x.getClassName());
      }
   }
   
   static RootObject readObjectRef(RootInput in, Map map) throws IOException
   {
      long objStartPos = in.getPosition();
      int tag;
      int fVersion = 0;
      long startpos = 0;
      int bcnt = in.readInt();
      if (((bcnt & kByteCountMask) == 0) || (bcnt == kNewClassTag))
      {
         tag = bcnt;
         bcnt = 0;
      }
      else
      {
         fVersion = 1;
         startpos = in.getPosition();
         tag = in.readInt();
      }
      
      // in case tag is object tag return object
      //if (tag != 0) System.out.println("ReadObject tag="+tag+" ("+Integer.toHexString(tag)+")");
      
      if ((tag & kClassMask) == 0)
      {
         if (tag == 0) return null;
         // FixMe: tag == 1 means "self", but don't currently have self available.
         if (tag == 1) return null;
         
         Object obj = map.get(new Long(tag));
         if ((obj == null) || !(obj instanceof RootObject))
            throw new IOException("Invalid tag found " + tag);
         return (RootObject) obj;
      }
      if (tag == kNewClassTag)
      {
         try
         {
            String className = in.readNullTerminatedString(80);
            GenericRootClass rootClass = (GenericRootClass) in.getFactory().create(className);
            
            // Add this class to the map
            if (fVersion > 0)
               map.put(new Long(startpos + kMapOffset), rootClass);
            else
               map.put(new Long(map.size() + 1), rootClass);
            
            AbstractRootObject obj = rootClass.newInstance();
            
            // Add this class to the map
            if (fVersion > 0)
               map.put(new Long(objStartPos + kMapOffset), obj);
            else
               map.put(new Long(map.size() + 1), obj);
            obj.read(in);
            return obj;
         }
         catch (RootClassNotFound x)
         {
            throw new IOException("Class not found during object read: " + x.getClassName());
         }
      }
      else
      {
         tag &= ~kClassMask;
         
         Object cls = map.get(new Long(tag));
         if ((cls == null) || !(cls instanceof BasicRootClass))
         {
            System.out.println("Map Dump");
            
            Iterator i = map.entrySet().iterator();
            while (i.hasNext())
               System.out.println(i.next());
            throw new IOException("Invalid object tag " + tag);
         }
         
         GenericRootClass rootClass = (GenericRootClass) cls;
         AbstractRootObject obj = rootClass.newInstance();
         if (fVersion > 0)
         {
            Long offset = new Long(objStartPos + kMapOffset);
            map.put(offset, obj);
            
            //System.out.println("Added map entry at "+offset);
         }
         else
            map.put(new Long(map.size() + 1), obj);
         obj.read(in);
         return obj;
      }
   }
   
   static String readString(DataInput in) throws IOException
   {
      int l = in.readByte();
      if (l==-1) l = in.readInt();
      byte[] data = new byte[l];
      for (int i = 0; i < l; i++)
         data[i] = in.readByte();
      return new String(data);
   }
   
   static int readVersion(RootInput in, AbstractRootObject obj) throws IOException
   {
      int version = in.readShort();
      if ((version & 0x4000) == 0)
         return version;
      
      int byteCount = ((version & 0x3fff) << 16) + in.readUnsignedShort();
      if (obj != null)
         obj.setExpectedLength(in.getPosition(), byteCount);
      return in.readShort();
   }
   static void skipObject(RootInput in) throws IOException
   {
      int version = in.readShort();
      if ((version & 0x4000) == 0) throw new IOException("Cannot skip object with no length");
      
      int byteCount = ((version & 0x3fff) << 16) + in.readUnsignedShort();
      System.err.println("skipping "+byteCount);
      in.skipBytes(byteCount);
   }
   
   static RootInput slice(RootInput in, int size) throws IOException
   {
      // Is it really necessary to buffer here, can't we reflect requests
      // on the slice back to the underlying parent?
      byte[] buf = new byte[size];
      in.readFixedArray(buf);
      return new RootInputStream(new RootByteArrayInputStream(buf, 0), in.getTop());
   }
   
   static RootInput slice(RootInput in, int size, int decompressedSize) throws IOException
   {
      // Currently we read the whole buffer before starting to decompress.
      // It would be better to decompress each component as we read it, but perhaps
      // not possible if we need to support random access into the unpacked array.
      try
      {
         byte[] buf = new byte[size];
         in.readFixedArray(buf);
         byte[] out = new byte[decompressedSize];
      
         int nout = 0;
         ZAlgo algo = ZAlgo.getAlgo( buf );

         switch (algo) {
             case ZLIB:
             case UNDEFINED:
                 boolean hasHeader = algo != ZAlgo.UNDEFINED;
                 Inflater inf = new Inflater( !hasHeader );
                 try {
                     // Skip the header when we have to restart
                     for(int nin = HDRSIZE; nout < decompressedSize; nin += HDRSIZE){
                         inf.setInput( buf, nin, buf.length - nin );
                         int rc = inf.inflate( out, nout, out.length - nout );
                         if ( rc == 0 ) {
                             throw new IOException( "Inflate unexpectedly returned 0 (perhaps OutOfMemory?)" );
                         }
                         nout += rc;
                         nin += inf.getTotalIn();
                         inf.reset();
                     }
                 } finally {
                     inf.end();
                 }
                 break;
             case LZMA:
                 ByteArrayInputStream bufStr = new ByteArrayInputStream( buf );
                 bufStr.skip( HDRSIZE );
                 XZInputStream unc = new XZInputStream( bufStr );
                 try{
                     int rc = unc.read( out, nout, out.length - nout );
                     nout += rc;
                     // Library recommendation for integrity check
                     if( unc.read() != -1 || nout != decompressedSize){
                         throw new IOException( "Failed to decompress all LZMA bytes." );
                     }
                 }
                 finally {
                     unc.close(); 
                     bufStr.close();
                 }
                 break;
             default:
                 throw new IOException( "Unable to determine compression algorithm" );
         }
         return new RootInputStream(new RootByteArrayInputStream(out, 0), in.getTop());
      }
      catch (Exception x)
      {
         IOException xx = new IOException("Error during decompression (size="+size+"/"+decompressedSize+")");
         xx.initCause(x);
         throw xx;
      }
      catch (OutOfMemoryError x)
      {
         IOException xx = new IOException("Error during decompression (size="+size+"/"+decompressedSize+")");
         xx.initCause(x);
         throw xx;         
      }
   }
   
   static double readTwistedDouble(RootInput in) throws IOException
   {
      int i1 = in.readInt();
      int i2 = in.readInt();
      long val = i1 + (((long) i2)<<32);
      return Double.longBitsToDouble(val);
   }
   static void dump(RootInput in, int n) throws IOException
   {
      for (int i=0; i<n; i++)
      {
         int b = in.readByte();
         char c = b<32 ? ' ' : (char) b;
         System.out.println("dump["+i+"]: "+b+" "+c);
      }
      throw new IOException("dump");
   }
   
}
