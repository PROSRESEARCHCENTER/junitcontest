package hep.io.stdhep;

import java.io.IOException;
import hep.io.xdr.XDRDataOutput;
import hep.io.xdr.XDRDataInput;

/**
 * A base class for stdhep begin/end run records.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepRunRecord.java 9132 2006-10-13 05:39:06Z tonyj $
 */
public abstract class StdhepRunRecord extends StdhepRecord
{
   private double stdseed1;
   private double stdseed2;
   private float stdecom;
   private float stdxsec;
   private int nevtgen;
   private int nevtreq;
   private int nevtwrt;

   protected StdhepRunRecord(int id, int nevtreq, int nevtgen, int nevtwrt, float stdecom, float stdxsec, double stdseed1, double stdseed2)
   {
      super(id);
      this.length = 13 * 4;
      this.version = STDHEP_VERSION;
      this.nevtreq = nevtreq;
      this.nevtgen = nevtgen;
      this.nevtwrt = nevtwrt;
      this.stdecom = stdecom;
      this.stdxsec = stdxsec;
      this.stdseed1 = stdseed1;
      this.stdseed2 = stdseed2;
   }

   StdhepRunRecord(int id)
   {
      super(id);
   }

   public int nevtgen()
   {
      return nevtgen;
   }

   public int nevtreq()
   {
      return nevtreq;
   }

   public int nevtwrt()
   {
      return nevtwrt;
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      super.read(xdr);
      nevtreq = xdr.readInt();
      nevtgen = xdr.readInt();
      nevtwrt = xdr.readInt();
      stdecom = xdr.readFloat();
      stdxsec = xdr.readFloat();
      stdseed1 = xdr.readDouble();
      stdseed2 = xdr.readDouble();
   }

   public float stdecom()
   {
      return stdecom;
   }

   public double stdseed1()
   {
      return stdseed1;
   }

   public double stdseed2()
   {
      return stdseed2;
   }

   public float stdxsec()
   {
      return stdxsec;
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      super.write(xdr);
      xdr.writeInt(nevtreq);
      xdr.writeInt(nevtgen);
      xdr.writeInt(nevtwrt);
      xdr.writeFloat(stdecom);
      xdr.writeFloat(stdxsec);
      xdr.writeDouble(stdseed1);
      xdr.writeDouble(stdseed2);
   }
}
