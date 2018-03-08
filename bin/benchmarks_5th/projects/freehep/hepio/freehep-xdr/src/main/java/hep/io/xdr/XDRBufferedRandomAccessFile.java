package hep.io.xdr;

import java.io.File;
import java.io.IOException;

/**
 * The performance of XDRRandomAccessFile used directly is
 * pretty appalling. This is a buffered implementation
 * that is much faster so long as it is used mostly for reading or mostly
 * for writing.
 */
public class XDRBufferedRandomAccessFile extends XDRRandomAccessFile
{
   private byte[] buffer;
   private long offset; // Offset of buffer relative to base
   private int pos; // Current position in buffer
   private int used; // Number of bytes written or available in buffer
   private final boolean readOnly;
   private boolean readMode;

   public XDRBufferedRandomAccessFile(String name, boolean readOnly, int bufferSize) throws IOException
   {
      super(name, readOnly ? "r" : "rw");
      this.readOnly = readOnly;
      this.readMode = readOnly;
      buffer = new byte[bufferSize];
   }

   public XDRBufferedRandomAccessFile(File file, boolean readOnly, int bufferSize) throws IOException
   {
      super(file, readOnly ? "r" : "rw");
      this.readOnly = readOnly;
      this.readMode = readOnly;
      buffer = new byte[bufferSize];
   }

   private void setReadMode(boolean readMode) throws IOException
   {
      if (this.readMode != readMode)
      {
         flush();
         this.readMode = readMode;
      }
   }

   private void checkCanRead() throws IOException
   {
      setReadMode(true);
   }

   private void checkCanWrite() throws IOException
   {
      if (readOnly)
      {
         throw new IOException("Can not write to read-only file");
      }
      setReadMode(false);
   }

    @Override
   public long getFilePointer() throws IOException
   {
      return offset + pos;
   }

    @Override
   public void close() throws IOException
   {
      if (!readMode)
      {
         flush();
      }
      super.close();
      buffer = null;
   }

    @Override
   public void flush() throws IOException
   {
      if (used > 0)
      {
         if (readMode)
         {
            offset += pos;
            super.seek(offset);
         }
         else
         {
            super.write(buffer, 0, used);
            offset += used;
         }
      }
      used = 0;
      pos = 0;
   }

   private void loadBuffer() throws IOException
   {
      offset += used;
      used = super.read(buffer);
      pos = 0;
   }

   public int read() throws IOException
   {
      checkCanRead();
      if (pos >= used)
      {
         loadBuffer();
         if (used < 0)
         {
            return -1;
         }
      }
      return buffer[pos++] & 0xff;
   }

   public int read(byte[] buf) throws IOException
   {
      return read(buf, 0, buf.length);
   }

   public int read(byte[] buf, int start, int length) throws IOException
   {
      checkCanRead();
      int available = used - pos;
      if (available <= 0)
      {
         loadBuffer();
         if (used < 0)
         {
            return -1;
         }
         available = used - pos;
      }
      int bytesToCopy = Math.min(available, length);
      System.arraycopy(buffer, pos, buf, start, bytesToCopy);
      pos += bytesToCopy;
      return bytesToCopy;
   }

   public void seek(long position) throws IOException
   {
      if ((position >= offset) && (position <= (offset + used)))
      {
         pos = (int) (position - offset);
      }
      else
      {
         if (readMode)
         {
            super.seek(position);
            offset = position;
            used = 0;
            pos = 0;
         }
         else
         {
            flush();
            offset = position;
            super.seek(offset);
         }
      }
   }

   public void write(byte[] buf, int start, int len) throws IOException
   {
      checkCanWrite();
      if ((buffer.length - pos) > len)
      {
         System.arraycopy(buf, start, buffer, pos, len);
         pos += len;
         if (pos > used)
         {
            used = pos;
         }
      }
      else
      {
         used = pos;
         flush();
         super.write(buf, start, len);
         offset += len;
      }
   }

   public void write(byte[] buf) throws IOException
   {
      write(buf, 0, buf.length);
   }

   public void write(int b) throws IOException
   {
      checkCanWrite();
      if (pos == buffer.length)
      {
         flush();
      }
      buffer[pos++] = (byte) b;
      if (pos > used)
      {
         used = pos;
      }
   }
}
