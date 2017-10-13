package hep.io.xdr;

import java.io.IOException;

/**
 * An interface to be implemented by objects that
 * can be read and written using XDR
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: XDRSerializable.java 9148 2006-10-16 19:41:28Z tonyj $
 */
public interface XDRSerializable
{
   public void read(XDRDataInput in) throws IOException;

   public void write(XDRDataOutput out) throws IOException;
}
