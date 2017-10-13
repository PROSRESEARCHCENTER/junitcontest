package hep.io.stdhep;

import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRDataOutput;

import java.io.IOException;


/**
 * A stdhep event. This class does not attempt to provide
 * an OO interface to the event, but simply acts as a container
 * for the packed stdhep event record.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public class StdhepEvent extends StdhepRecord
{
   private int[] idhep;
   private int[] isthep;
   private int[] jdahep;
   private int[] jmohep;
   private double[] phep;
   private double[] vhep;
   private int nevhep;
   private int nhep;

   public StdhepEvent(int nevhep, int nhep, int[] isthep, int[] idhep, int[] jmohep, int[] jdahep, double[] phep, double[] vhep)
   {
      this(MCFIO_STDHEP,nevhep,nhep,isthep,idhep,jmohep,jdahep,phep,vhep);
   }
   StdhepEvent(int id, int nevhep, int nhep, int[] isthep, int[] idhep, int[] jmohep, int[] jdahep, double[] phep, double[] vhep)
   {
      super(id);
      this.version = STDHEP_VERSION;
      this.length = 4 * (12 + (24 * nhep));
      this.nevhep = nevhep;
      this.nhep = nhep;
      this.isthep = isthep;
      this.idhep = idhep;
      this.jmohep = jmohep;
      this.jdahep = jdahep;
      this.phep = phep;
      this.vhep = vhep;
   }

   StdhepEvent()
   {
      this(MCFIO_STDHEP);
   }
   StdhepEvent(int id)
   {
      super(id);
   }

   public int getIDHEP(int index)
   {
      return idhep[index];
   }

   public int getISTHEP(int index)
   {
      return isthep[index];
   }

   public int getJDAHEP(int index, int i)
   {
      return jdahep[(index * 2) + i];
   }

   public int getJMOHEP(int index, int i)
   {
      return jmohep[(index * 2) + i];
   }

   /**
* The event number
*/
   public int getNEVHEP()
   {
      return nevhep;
   }

   /**
* The number of particles
*/
   public int getNHEP()
   {
      return nhep;
   }

   public double getPHEP(int index, int i)
   {
      return phep[(index * 5) + i];
   }

   public double getVHEP(int index, int i)
   {
      return vhep[(index * 4) + i];
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      super.read(xdr);
      nevhep = xdr.readInt();
      nhep = xdr.readInt();
      isthep = xdr.readIntArray(isthep);
      idhep = xdr.readIntArray(idhep);
      jmohep = xdr.readIntArray(jmohep);
      jdahep = xdr.readIntArray(jdahep);
      phep = xdr.readDoubleArray(phep);
      vhep = xdr.readDoubleArray(vhep);
   }

   public String toString()
   {
      return "Event " + nevhep + " particles=" + nhep;
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      super.write(xdr);
      xdr.writeInt(nevhep);
      xdr.writeInt(nhep);
      xdr.writeIntArray(isthep, 0, nhep);
      xdr.writeIntArray(idhep, 0, nhep);
      xdr.writeIntArray(jmohep, 0, 2 * nhep);
      xdr.writeIntArray(jdahep, 0, 2 * nhep);
      xdr.writeDoubleArray(phep, 0, 5 * nhep);
      xdr.writeDoubleArray(vhep, 0, 4 * nhep);
   }
}
