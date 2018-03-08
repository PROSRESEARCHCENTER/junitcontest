package hep.io.mcfio;

import java.io.IOException;
import hep.io.xdr.XDRSerializable;
import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRDataOutput;

/**
 * The block class is used as the super class of all classes
 * that read blocks. It deals with reading the block header.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MCFIOBlock.java 16295 2015-04-15 23:48:43Z tonyj $
 */
public abstract class MCFIOBlock implements XDRSerializable
{
   protected String version;
   protected double fVersion;
   protected int id;
   protected int length;

   protected MCFIOBlock(int id)
   {
      this.id = id;
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      int temp = xdr.readInt();
      if (id != temp)
         throw new MCFIOException("Block error, expected " + id + " got " + temp);
      length = xdr.readInt();
      version = xdr.readString();
      try
      {
         fVersion = version.isEmpty() ? Double.NaN :  Double.parseDouble(version);
      }
      catch (NumberFormatException x)
      {
         throw new IOException("Invalid version " + version);
      }
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      xdr.writeInt(id);
      xdr.writeInt(getLength());
      xdr.writeString(version);
   }

   /**
    * Call during write to get the length of the block.
    * By default returns the value of the length variable,
    * but can be overriden for a different algorithm.
    */
   protected int getLength()
   {
      return length;
   }
}
