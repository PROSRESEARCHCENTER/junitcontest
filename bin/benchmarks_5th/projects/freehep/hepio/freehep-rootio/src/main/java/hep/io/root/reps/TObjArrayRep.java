package hep.io.root.reps;

import hep.io.root.RootObject;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TObject;

import java.io.IOException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TObjArrayRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TObjArrayRep extends AbstractRootObject implements hep.io.root.interfaces.TObjArray
{
   private String fName;
   private TObject fObject;
   private RootObject[] fArray;
   private int fLowerBound;
   private int fNobjects;
   private int fSize;

   public int getLowerBound()
   {
      return fLowerBound;
   }

   public int getUpperBound()
   {
      return fLowerBound + fNobjects;
   }

   public void readMembers(RootInput in) throws IOException
   {
      int v = in.readVersion(this);
      fObject = (TObject) in.readObject("TObject");
      fName = in.readObject("TString").toString();
      fNobjects = in.readInt();
      fLowerBound = in.readInt();
      fArray = new RootObject[fNobjects];
      fSize = 0;
      for (int i = 0; i < fNobjects; i++)
      {
         fArray[i] = in.readObjectRef();
         if (fArray[i] != null)
            fSize = i + 1;
      }
      in.checkLength(this);
   }
}
