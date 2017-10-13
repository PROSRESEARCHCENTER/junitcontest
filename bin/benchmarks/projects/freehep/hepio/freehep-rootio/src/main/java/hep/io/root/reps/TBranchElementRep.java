package hep.io.root.reps;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootMember;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.GenericRootClass;
import hep.io.root.core.HollowBuilder;
import hep.io.root.core.RootClassFactory;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TLeafElement;
import hep.io.root.interfaces.TObjArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TBranchElementRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TBranchElementRep extends AbstractRootObject implements hep.io.root.interfaces.TBranchElement
{
   private Class cloneClass;
   private RootInput rin;
   private int curIndex;
   private int fWriteBasket;
   private int[] cBasketBytes;
   private long[] cBasketEntry;
   private long[] cBasketSeek;
   
   private int[] saveIntSpace(String source, int size)
   {
      try
      {
         if (size == 0) return null;
         int[] result = new int[size];
         Field field = getClass().getDeclaredField(source);
         Object array = field.get(this);
         for (int i=0; i<size; i++) result[i] = Array.getInt(array,i);
         return result;
      }
      catch (Exception x)
      {
         throw new RuntimeException("Wierd error while compressing "+source,x);
      }
   }
   private long[] saveLongSpace(String source, int size)
   {
      try
      {
         if (size == 0) return null;
         long[] result = new long[size];
         Field field = getClass().getDeclaredField(source);
         Object array = field.get(this);
         for (int i=0; i<size; i++) result[i] = Array.getLong(array,i);
         return result;
      }
      catch (Exception x)
      {
         throw new RuntimeException("Wierd error while compressing "+source,x);
      }
   }
   
   /**
    * If this branch represents a (split) TClonesArray this will return
    * the class used to represent the elements of the array.
    */
   public Class getCloneClass()
   {
      try
      {
         if (cloneClass == null)
         {
            HollowBuilder builder = new HollowBuilder(this, "fTracks.", true);
            String name = "hep.io.root.hollow." + getClonesName();
            RootClassFactory factory = rin.getFactory();
            GenericRootClass gc = (GenericRootClass) factory.create(getClonesName());
            cloneClass = factory.getLoader().loadSpecial(builder, name, gc);
            
            // Populate the leafs.
            builder.populateStatics(cloneClass, factory);
         }
         return cloneClass;
      }
      catch (RootClassNotFound x)
      {
         throw new RuntimeException("Error looking up class for TBranchClones " + x.getClassName(),x);
      }
   }
   
   public void read(RootInput in) throws IOException
   {
      super.read(in);
      rin = in.getTop();
      
      // Clean-up unnecessarily large arrays
      cBasketBytes = saveIntSpace("fBasketBytes",fWriteBasket);
      cBasketEntry = saveLongSpace("fBasketEntry",fWriteBasket+1);
      cBasketSeek = saveLongSpace("fBasketSeek",fWriteBasket);
      
      // The leaves need to know which branch they are on
      TObjArray leaves = getLeaves();
      if (leaves != null)
      {
         for (int i = 0; i < leaves.size(); i++)
         {
            TLeaf leaf = (TLeaf) leaves.get(i);
            leaf.setBranch(this);
         }
      }
      curIndex = -1;
      
      String className = getClassName();
      int fId = getID();
      
      try
      {
         RootClass rc = in.getFactory().create(className);
         RootClass[] sup = rc.getSuperClasses();
         fId -= sup.length;
         if (fId >= 0)
         {
            RootMember[] members = rc.getMembers();
            RootMember member = members[fId];
            if (leaves != null)
            {
               for (int i = 0; i < leaves.size(); i++)
               {
                  TLeafElement leaf = (TLeafElement) leaves.get(i);
                  leaf.setMember(member);
               }
            }
         }
      }
      catch (RootClassNotFound x)
      {
         IOException io = new IOException("Could not find root class " + className);
         io.initCause(x);
         throw io;
      }
   }
}
