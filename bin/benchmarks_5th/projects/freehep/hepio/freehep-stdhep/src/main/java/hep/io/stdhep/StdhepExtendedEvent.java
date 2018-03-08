package hep.io.stdhep;

import hep.io.xdr.XDRDataInput;
import hep.io.xdr.XDRDataOutput;

import java.io.IOException;


/**
 * An extended stdhep record containing extra content corresponding to the HEPEV4 common block. 
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StdhepExtendedEvent.java 13289 2007-09-06 00:21:14Z tonyj $
 */
public class StdhepExtendedEvent extends StdhepEvent
{
   private double eventweight;
   private double alphaqed;
   private double alphaqcd;
   private double[] scale;
   private double[] spin;
   private int[] colorflow; 
   private int idrup;

   public StdhepExtendedEvent(int nevhep, int nhep, int[] isthep, int[] idhep, int[] jmohep, int[] jdahep, double[] phep, double[] vhep,
                    double eventweight, double alphaqed, double alphaqcd, double[] scale, double[] spin, int[] colorflow, int idrup)
   {
      super(MCFIO_STDHEPEV4,nevhep,nhep,isthep,idhep,jmohep,jdahep,phep,vhep);
      this.eventweight = eventweight;
      this.alphaqed = alphaqed;
      this.alphaqcd = alphaqcd;
      this.scale = scale;
      this.spin = spin;
      this.colorflow = colorflow;
      this.idrup = idrup;
   }
   
   StdhepExtendedEvent()
   {
      super(MCFIO_STDHEPEV4);
   }
   
   public double getEventWeight()
   {
      return eventweight;
   }

   public double getAlphaQED()
   {
      return alphaqed;
   }

   public double getAlphaQCD()
   {
      return alphaqcd;
   }

   public double getScale(int i)
   {
      return scale[i];
   }
   
   public double getSpin(int index, int i)
   {
      return spin[(index * 3) + i];
   }
     
   public int getColorFlow(int index, int i)
   {
      return colorflow[(index * 2) + i];
   }
   public int getIDRUP()
   {
      return idrup;
   }

   public void read(XDRDataInput xdr) throws IOException
   {
      super.read(xdr);
      
      eventweight = xdr.readDouble();
      alphaqed = xdr.readDouble();
      alphaqcd = xdr.readDouble();
      scale = xdr.readDoubleArray(scale);
      spin = xdr.readDoubleArray(spin);
      colorflow = xdr.readIntArray(colorflow);
      idrup = xdr.readInt();
   }

   public void write(XDRDataOutput xdr) throws IOException
   {
      super.write(xdr);
      
      int nhep = super.getNHEP();
      
      xdr.writeDouble(eventweight);
      xdr.writeDouble(alphaqed);
      xdr.writeDouble(alphaqcd);
      xdr.writeDoubleArray(scale,0,5);
      xdr.writeDoubleArray(spin,0,3*nhep);
      xdr.writeIntArray(colorflow,0,2*nhep);
      xdr.writeInt(idrup);
   }
}
