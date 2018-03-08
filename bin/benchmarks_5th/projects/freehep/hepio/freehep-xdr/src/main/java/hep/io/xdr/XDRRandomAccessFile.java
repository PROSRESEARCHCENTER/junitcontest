package hep.io.xdr;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A random access file for use with XDR.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: XDRRandomAccessFile.java 13668 2009-10-15 23:09:08Z tonyj $
 */
public class XDRRandomAccessFile extends RandomAccessFile implements XDRDataInput, XDRDataOutput
{
   private final static int SANITY_CHECK = Integer.getInteger("hep.io.xdr.sanityCheck",100000).intValue();

   public XDRRandomAccessFile(String name, String mode) throws IOException
   {
      super(name, mode);
   }
   public XDRRandomAccessFile(File file, String mode) throws IOException
   {
      super(file, mode);
   }

   public void pad() throws IOException
   {
      long pos = getFilePointer();
      int offset = (int) (pos % 4);
      if (offset != 0) seek(pos+4-offset);
   }

   public double[] readDoubleArray(double[] buffer) throws IOException
   {
      int l = readInt();
      if (l > SANITY_CHECK)
         throw new IOException("Array length failed sanity check: " + l);

      double[] result = buffer;
      if ((buffer == null) || (l > buffer.length))
         result = new double[l];
      for (int i = 0; i < l; i++)
         result[i] = readDouble();
      return result;
   }

   public float[] readFloatArray(float[] buffer) throws IOException
   {
      int l = readInt();
      if (l > SANITY_CHECK)
         throw new IOException("Array length failed sanity check: " + l);

      float[] result = buffer;
      if ((buffer == null) || (l > buffer.length))
         result = new float[l];
      for (int i = 0; i < l; i++)
         result[i] = readFloat();
      return result;
   }

   public int[] readIntArray(int[] buffer) throws IOException
   {
      int l = readInt();
      if (l > SANITY_CHECK)
         throw new IOException("Array length failed sanity check: " + l);

      int[] result = buffer;
      if ((buffer == null) || (l > buffer.length))
         result = new int[l];
      for (int i = 0; i < l; i++)
         result[i] = readInt();
      return result;
   }

   public String readString(int l) throws IOException
   {
      byte[] ascii = new byte[l];
      readFully(ascii);
      pad();
      return new String(ascii,"US-ASCII");
   }

   public String readString() throws IOException
   {
      int l = readInt();
      if (l > SANITY_CHECK)
         throw new IOException("String length failed sanity check: " + l);
      return readString(l);
   }

   public void writeDoubleArray(double[] array) throws IOException
   {
      writeInt(array.length);
      for (int i = 0; i < array.length; i++)
         writeDouble(array[i]);
   }

   public void writeDoubleArray(double[] array, int start, int n) throws IOException
   {
      writeInt(n);
      for (int i = start; i < n; i++)
         writeDouble(array[i]);
   }

   public void writeFloatArray(float[] array) throws IOException
   {
      writeInt(array.length);
      for (int i = 0; i < array.length; i++)
         writeFloat(array[i]);
   }

   public void writeFloatArray(float[] array, int start, int n) throws IOException
   {
      writeInt(n);
      for (int i = start; i < n; i++)
         writeFloat(array[i]);
   }

   public void writeIntArray(int[] array) throws IOException
   {
      writeInt(array.length);
      for (int i = 0; i < array.length; i++)
         writeInt(array[i]);
   }

   public void writeIntArray(int[] array, int start, int n) throws IOException
   {
      writeInt(n);
      for (int i = start; i < n; i++)
         writeInt(array[i]);
   }

   public void writeString(String s) throws IOException
   {
      writeInt(s.length());

      byte[] ascii = s.getBytes();
      write(ascii);
      pad();
   }

   public void writeStringChars(String s) throws IOException
   {
      byte[] ascii = s.getBytes();
      write(ascii);
      pad();
   }

   public void flush() throws IOException {
      // Nothing to do, random access file is not buffered
   }
}
