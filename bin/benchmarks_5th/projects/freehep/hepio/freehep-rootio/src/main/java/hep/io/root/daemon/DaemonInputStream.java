package hep.io.root.daemon;

import java.io.InputStream;

/**
 * Adds limited random access to InputStream.
 * This class is returned from RootURLConnection.getInputStream().
 * @author Tony Johnson
 */
public abstract class DaemonInputStream extends InputStream
{
   protected long position = 0;
   /**
    * Get the current file position
    */
   public long getPosition()
   {
      return position;
   }
   /**
    * Set the current file position
    */
   public void setPosition(long pos)
   {
      position = pos;
   }
}
