/*
 * TClonesArray.java
 *
 * Created on January 14, 2001, 5:28 PM
 */
package hep.io.root.reps;

import hep.io.root.RootObject;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.Clone;
import hep.io.root.core.Clones;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TObjArray;
import hep.io.root.interfaces.TObject;

import java.io.IOException;


/**
 *
 * @author  tonyj
 * @version $Id: TClonesArrayRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TClonesArrayRep extends AbstractRootObject implements TObjArray
{
   private String fClassName;
   private String fName;
   private TObject fObject;
   private RootObject[] fArray;
   private int fLowerBound;
   private int fNobjects;
   private int fSize;

   public void readMembers(RootInput in) throws IOException
   {
      int v = in.readVersion(this);
      fObject = (TObject) in.readObject("TObject");

      //System.out.println("TClonesArray Version="+v+" fBits="+Integer.toHexString(fObject.getBits()));
      fName = in.readObject("TString").toString();
      fClassName = in.readObject("TString").toString();
      fNobjects = in.readInt();
      if (fNobjects < 0)
         fNobjects = -fNobjects;
      fLowerBound = in.readInt();
      fArray = new RootObject[fNobjects];

      //for (int i=0; i<500; i++)
      //{
      //      int b = in.readByte();
      //      Character c = new Character((char) b);
      //      System.out.println("["+i+"] "+b+" "+Integer.toHexString(b)+" "+c);
      //}
      //System.exit(0);
      if (in.getRootVersion() >= 30200)
         in.skipBytes(4 * fNobjects);

      try
      {
         String className = fClassName;
         int pos = className.indexOf(';');
         if (pos > 0)
            className = className.substring(0, pos);

         Class clcl = in.getFactory().getLoader().loadClass("hep.io.root.clones." + className);
         Clones clones = (Clones) clcl.newInstance();
         clones.read(in, fNobjects);

         clcl = in.getFactory().getLoader().loadClass("hep.io.root.clone." + className);
         for (int i = 0; i < fNobjects; i++)
         {
            Clone clone = (Clone) clcl.newInstance();
            clone.setData(i, clones);
            fArray[i] = clone;
         }
         fSize = fArray.length;
         while ((fSize > 0) && (fArray[fSize - 1] == null))
            fSize--; // trim trailing nulls
      }
      catch (ClassNotFoundException x)
      {
         throw new IOException("TClonesArray class not found: " + fClassName);
      }
      catch (Throwable x)
      {
         x.printStackTrace();
         System.exit(0);
         throw new IOException("TClonesArray instantiation exception: " + fClassName);
      }
      in.checkLength(this);
   }
}
