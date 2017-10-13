package hep.io.root.reps;

import hep.io.root.*;
import hep.io.root.core.*;
import hep.io.root.interfaces.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

import java.io.*;


/**
 * @author Tony Johnson
 * @version $Id: TLeafElementRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TLeafElementRep extends AbstractRootObject implements TLeafElement, Constants
{
   private BasicMember member;
   private Class hollowClass;
   private Clones2 clones;
   private Object lastValue;
   private RootInput rin;
   private TBranchElement branch;
   private long lastValueIndex;

   public void setBranch(TBranch branch)
   {
      this.branch = (TBranchElement) branch;
      lastValueIndex = -1;
   }

   public void setMember(RootMember member)
   {
      this.member = (BasicMember) member;
   }

   public RootInput setPosition(long pos) throws IOException
   {
      return branch.setPosition(this, pos);
   }

   public Object getValue(long index) throws IOException
   {
      try
      {
         if (index == lastValueIndex)
            return lastValue;
         lastValueIndex = index;

         //boolean hollow = branch.getEntryNumber() == 0;
         boolean hollow = branch.getBranches().size() > 0;
         if (!hollow)
         {
            int fId = branch.getID();
            if (fId < 0)
            {
               RootInput in = branch.setPosition(this, index);
               String className = branch.getClassName();

               Class k = in.getFactory().getLoader().loadClass("hep.io.root.proxy2." + className);
               AbstractRootObject o = (AbstractRootObject) k.newInstance();
               in.readShort(); //???
               o.read(in);
               return lastValue = o;
            }
            else
            {
               RootInput in = branch.setPosition(this, index);
               int streamerType = branch.getStreamerType();

               // Wrong! Fixme Fixme Fixme
               return lastValue = new Integer(in.readInt());
            }
         }
         else
         {
            String clonesName = branch.getClonesName();
            if (clonesName.length() > 0)
            {
               //System.out.println("AAAAAAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGGGGGGGGGGGHHHHHHHHHHHHH!");
               RootInput in = branch.setPosition((TLeaf) branch.getLeaves().get(0), index);

               // No problem
               if (hollowClass == null)
               {
                  //System.out.println("Creating clones2 class "+branch.getClonesName());
                  Clones2Builder builder = new Clones2Builder(branch);
                  String name = "hep.io.root.clones2." + branch.getClonesName();
                  RootClassFactory factory = in.getFactory();
                  GenericRootClass gc = (GenericRootClass) factory.create(branch.getClonesName());
                  hollowClass = factory.getLoader().loadSpecial(builder, name, gc);

                  // Populate the leafs.
                  builder.populateStatics(hollowClass, factory);

                  // Not really cocher
                  clones = (Clones2) hollowClass.newInstance();
               }

               int size = in.readInt();
               clones.setData(size, index);
               return clones;
            }
            else
            {
               String className = ((TBranchElement) branch.getBranches().get(0)).getClassName();

               //if (className.length() == 0) className = ((TBranchElement) branch.getBranches().get(0)).getClassName();
               //System.out.println("ClassName="+className);
               RootClass rc = rin.getFactory().create(className);
               int fType = branch.getType();

               //if (fType == 1) // subclass
               //{
               //    int fId = branch.getID();
               //    RootClass[] sup = rc.getSuperClasses();
               //    rc = sup[fId];
               //}
               //else if (fType == 2) // embedded class
               //{
               //    int fId = branch.getID();
               //    RootClass[] sup = rc.getSuperClasses();
               //    fId -= sup.length;
               //    RootMember[] members = rc.getMembers();
               //    rc = members[fId].getType();   
               //}
               if (hollowClass == null)
               {
                  //System.out.println("Creating hollow class "+branch.getClassName());
                  HollowBuilder builder = new HollowBuilder(branch);
                  String name = "hep.io.root.hollow." + branch.getClassName();
                  RootClassFactory factory = rin.getFactory();
                  GenericRootClass gc = (GenericRootClass) factory.create(branch.getClassName());
                  hollowClass = factory.getLoader().loadSpecial(builder, name, gc);

                  // Populate the leafs.
                  builder.populateStatics(hollowClass, factory);
               }

               //System.out.println("Creating hollow object "+branch.getClassName());
               Hollow h = (Hollow) hollowClass.newInstance();
               h.setHollowIndex(index);
               return lastValue = h;

               //HollowRootObject ho = (HollowRootObject) rc.newInstance();
               //ho.setHollow((hep.io.root.interfaces.TBranch) getProxy());
               //ho.setHollowIndex(index);
               //return ho;
            }
         }
      }
      catch (IOException x)
      {
         lastValueIndex = -1;
         throw x;
      }
      catch (RootClassNotFound x)
      {
         lastValueIndex = -1;
         IOException io = new IOException("RootClassNotFound " + x.getClassName());
         io.initCause(x);
         throw io;
      }
      catch (Throwable x)
      {
         lastValueIndex = -1;
         IOException io =  new IOException("RootClassNotFound " + x);
         io.initCause(x);
         throw io;
      }
   }

   public Object getWrappedValue(long index) throws IOException
   {
      return getValue(index);
   }

   public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
   {
      String leafClassName = getClass().getName();
      int fId = branch.getID();
      int nBranches = branch.getBranches().size();

      if (fId >= 0)
      {
         if (nBranches == 0)
         {
            //boolean special = getType()<20 && getLeafCount() != null;
            //if (special)
            //{
            //    BasicRootClass varClass = (BasicRootClass) member.getType();
            //    il.append(factory.createInvoke(leafClassName,"setPosition",new ObjectType("hep.io.root.core.RootInput"), new Type[]{ Type.INT },INVOKEVIRTUAL));
            //    il.append(new PUSH(cp,10));
            //    BasicType type = (BasicType) varClass.getJavaType();
            //    il.append(new NEWARRAY(type));
            //    il.append(InstructionConstants.DUP_X1);
            //    Type[] arrayArgType = new Type[]{ new ArrayType(type,1) }; 
            //    il.append(factory.createInvoke("hep.io.root.core.RootInput","readFixedArray",Type.VOID,arrayArgType, INVOKEINTERFACE));
            //}
            //else
            //{
            il.append(factory.createInvoke(leafClassName, "setPosition", new ObjectType("hep.io.root.core.RootInput"), new Type[]
                  {
                     Type.LONG
                  }, INVOKEVIRTUAL));
            member.generateReadCode(il, factory, cp, className);

            //}
         }
         else
         {
            //System.out.println("Unhandled ---- "+member.getName());
            // Here we need to generate a HollowObject
            il.append(factory.createInvoke(leafClassName, "getValue", Type.OBJECT, new Type[]
                  {
                     Type.LONG
                  }, INVOKEVIRTUAL));
         }
      }
      else
      {
         System.out.println("***************************NEVER CALLED???????***************************");
         il.append(factory.createInvoke(leafClassName, "getValue", Type.OBJECT, new Type[]
               {
                  Type.LONG
               }, INVOKEVIRTUAL));
      }
   }

   public void read(RootInput in) throws IOException
   {
      super.read(in);
      this.rin = in;
   }
}
