package hep.io.root.core;

import java.io.ByteArrayInputStream;

/**
 * A ByteArrayInputStream that will reveal its current
 * position.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootByteArrayInputStream.java 13617 2009-04-09 22:48:46Z tonyj $
 */
class RootByteArrayInputStream extends ByteArrayInputStream
{
   private int offset;

   /**
    * @param buf The buffer from which to read
    */
   public RootByteArrayInputStream(byte[] buf, int offset)
   {
      super(buf);
      this.offset = offset;
   }

   void setOffset(int offset)
   {
      this.offset = offset;
   }

   void setPosition(long pos)
   {
      this.pos = (int) pos - offset;
   }

   long getPosition()
   {
      return pos + offset;
   }
}
