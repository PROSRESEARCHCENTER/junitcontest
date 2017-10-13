package hep.io.root.core;

import hep.io.root.RootFileReader;
import hep.io.root.RootObject;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.zip.Inflater;

/**
 *
 * @author tonyj
 * @version $Id: FastInputStream.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class FastInputStream implements RootInput
{
   private static long elapsed;
   private static long invocations;
   private ByteBuffer buffer;
   private FastInputStream top;
   private HashMap map = new HashMap();
   private RootFileReader rfr;
   private byte[] in;
   private byte[] out;
   private int offset;
   private RandomAccessFile raf;

   public FastInputStream(RootFileReader rfr, RandomAccessFile raf) throws IOException
   {
      this.rfr = rfr;
      this.top = this;
      this.raf = raf;

      FileChannel channel = raf.getChannel();
      this.buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int) raf.length());
      buffer.getInt(); // Skip "root"
   }

   private FastInputStream(ByteBuffer buffer, FastInputStream top)
   {
      this.buffer = buffer;
      this.top = top;
   }

   public RootClassFactory getFactory()
   {
      return top.rfr.getFactory();
   }

   public void setMap(int offset)
   {
      this.offset = offset - buffer.position();
   }

   public void setPosition(long pos) throws IOException
   {
      buffer.position((int) pos - offset);
   }

   public long getPosition() throws IOException
   {
      return buffer.position() + offset;
   }

   public int getRootVersion()
   {
      return top.rfr.getVersion();
   }

   public RootInput getTop()
   {
      return top;
   }

   public void checkLength(AbstractRootObject obj) throws IOException
   {
      obj.checkLength(buffer.position());
   }

   public void clearMap()
   {
      map.clear();
      offset = 0;
   }

   public void dump()
   {
      System.out.println("Decompressing took " + elapsed + "ms (" + invocations + ")");
      System.out.println("in size = " + in.length);
      System.out.println("out size = " + out.length);
   }

   public int readArray(short[] data) throws IOException
   {
      int n = buffer.getInt();
      buffer.asShortBuffer().get(data, 0, n);
      buffer.position(buffer.position() + (n * 2));
      return n;
   }

   public int readArray(byte[] data) throws IOException
   {
      int n = buffer.getInt();
      buffer.get(data, 0, n);
      return n;
   }

   public int readArray(double[] data) throws IOException
   {
      int n = buffer.getInt();
      buffer.asDoubleBuffer().get(data, 0, n);
      buffer.position(buffer.position() + (n * 8));
      return n;
   }

   public int readArray(float[] data) throws IOException
   {
      int n = buffer.getInt();
      buffer.asFloatBuffer().get(data, 0, n);
      buffer.position(buffer.position() + (n * 4));
      return n;
   }

   public int readArray(int[] data) throws IOException
   {
      int n = buffer.getInt();
      buffer.asIntBuffer().get(data, 0, n);
      buffer.position(buffer.position() + (n * 4));
      return n;
   }

   public boolean readBoolean() throws java.io.IOException
   {
      return buffer.get() != 0;
   }

   public byte readByte() throws java.io.IOException
   {
      return buffer.get();
   }

   public char readChar() throws java.io.IOException
   {
      return buffer.getChar();
   }

   public double readDouble() throws java.io.IOException
   {
      return buffer.getDouble();
   }
   public double readTwistedDouble() throws java.io.IOException
   {
      int i1 = buffer.getInt();
      int i2 = buffer.getInt();
      long val = i1 + (((long) i2)<<32);
      return Double.longBitsToDouble(val);
   }

   public void readFixedArray(byte[] data) throws IOException
   {
      buffer.get(data);
   }

   public void readFixedArray(double[] data) throws IOException
   {
      buffer.asDoubleBuffer().get(data);
      buffer.position(buffer.position() + (data.length * 8));
   }

   public void readFixedArray(int[] data) throws IOException
   {
      buffer.asIntBuffer().get(data);
      buffer.position(buffer.position() + (data.length * 4));
   }
   
   public void readFixedArray(long[] data) throws IOException
   {
      buffer.asLongBuffer().get(data);
      buffer.position(buffer.position() + (data.length * 8));
   }
   
   public void readFixedArray(float[] data) throws IOException
   {
      buffer.asFloatBuffer().get(data);
      buffer.position(buffer.position() + (data.length * 4));
   }

   public void readFixedArray(short[] data) throws IOException
   {
      buffer.asShortBuffer().get(data);
      buffer.position(buffer.position() + (data.length * 2));
   }

   public float readFloat() throws java.io.IOException
   {
      return buffer.getFloat();
   }

   public void readFully(byte[] values) throws java.io.IOException
   {
      buffer.get(values);
   }

   public void readFully(byte[] values, int param, int param2) throws java.io.IOException
   {
      buffer.get(values, param, param2);
   }

   public int readInt() throws java.io.IOException
   {
      return buffer.getInt();
   }

   public String readLine() throws java.io.IOException
   {
      throw new IOException("Unimplemented method: readLine");
   }

   public long readLong() throws java.io.IOException
   {
      return buffer.getLong();
   }

   public void readMultiArray(Object[] array) throws IOException
   {
      for (int i = 0; i < array.length; i++)
      {
         Object o = array[i];
         if (o instanceof double[])
            readFixedArray((double[]) o);
         else if (o instanceof float[])
            readFixedArray((float[]) o);
         else if (o instanceof short[])
            readFixedArray((short[]) o);
         else if (o instanceof byte[])
            readFixedArray((byte[]) o);
         else if (o instanceof int[])
            readFixedArray((int[]) o);
         else if (o instanceof long[])
            readFixedArray((long[]) o);
         else if (o instanceof Object[])
            readMultiArray((Object[]) o);
         else
            throw new IOException("Unknown multiarray element: "+o.getClass());
      }
   }

   public String readNullTerminatedString(int maxLength) throws IOException
   {
      int actualLength = maxLength - 1;
      byte[] data = new byte[maxLength];
      for (int i = 0; i < maxLength; i++)
      {
         data[i] = buffer.get();
         if (data[i] == 0)
         {
            actualLength = i;
            break;
         }
      }
      return new String(data, 0, actualLength);
   }

   public RootObject readObject(String type) throws IOException
   {
      return RootInputStream.readObject(this, type);
   }

   public RootObject readObjectRef() throws IOException
   {
      return RootInputStream.readObjectRef(this, map);
   }

   public short readShort() throws java.io.IOException
   {
      return buffer.getShort();
   }

   public String readString() throws IOException
   {
      int l = buffer.get();
      byte[] data = new byte[l];
      buffer.get(data);
      return new String(data);
   }

   public String readUTF() throws java.io.IOException
   {
      return DataInputStream.readUTF(this);
   }

   public int readUnsignedByte() throws java.io.IOException
   {
      int result = buffer.get();
      if (result < 0)
         result += 256;
      return result;
   }

   public int readUnsignedShort() throws java.io.IOException
   {
      int result = buffer.getShort();
      if (result < 0)
         result += 65536;
      return result;
   }

   public int readVersion() throws IOException
   {
      return readVersion(null);
   }

   public int readVersion(AbstractRootObject obj) throws IOException
   {
      int version = buffer.getShort();
      if ((version & 0x4000) == 0)
         return version;

      int byteCount = ((version & 0x3fff) << 16) + readUnsignedShort();
      if (obj != null)
         obj.setExpectedLength(buffer.position(), byteCount);
      return buffer.getShort();
   }
   
   public void skipObject() throws IOException 
   {
      RootInputStream.skipObject(this);
   }

   public int skipBytes(int param) throws java.io.IOException
   {
      buffer.position(buffer.position() + param);
      return param;
   }

   public RootInput slice(int size) throws IOException
   {
      int oldLimit = buffer.limit();
      buffer.limit(buffer.position() + size);

      ByteBuffer slice = buffer.slice();
      buffer.position(buffer.limit());
      buffer.limit(oldLimit);
      return new FastInputStream(slice, top);
   }

   public RootInput slice(int inSize, int outSize) throws IOException
   {
      long start = System.currentTimeMillis();
      if (in == null)
         in = new byte[32768];
      if (out == null)
         out = new byte[65536];

      int nout = 0;
      int nin = 0;

      ByteBuffer slice = ByteBuffer.allocateDirect(outSize);
      Inflater inf = new Inflater(true);
      try
      {
         while (nout < outSize)
         {
            inf.reset();

            // root adds 9 bytes of header which are not needed
            nin += 9;
            buffer.position(buffer.position() + 9);
outer: while (true)
            {
               int rc;
               while ((rc = inf.inflate(out)) == 0)
               {
                  if (inf.finished() || inf.needsDictionary())
                     break outer;
                  if (inf.needsInput())
                  {
                     int l = Math.min(inSize - nin, in.length);
                     if (l == 0)
                        break outer;
                     buffer.get(in, 0, l);
                     inf.setInput(in, 0, l);
                     nin += l;
                  }
               }
               slice.put(out, 0, rc);
               nout += rc;
            }

            int back = inf.getRemaining();
            nin -= back;
            buffer.position(buffer.position() - back);
         }
         slice.rewind();

         long end = System.currentTimeMillis();
         elapsed += (end - start);
         invocations++;
         return new FastInputStream(slice, top);
      }
      catch (Exception x)
      {
         IOException io = new IOException("Error during decompression");
         io.initCause(x);
         throw io;
      }
      finally
      {
         inf.end();
      }
   }
   
   public void close() throws IOException
   {
      raf.close();
   }
}
