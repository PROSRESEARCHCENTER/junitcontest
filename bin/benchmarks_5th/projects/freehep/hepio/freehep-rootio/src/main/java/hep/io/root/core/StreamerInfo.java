package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;

/**
 * Interface implemented by both StreamerInfoString and StreamerInfoNew
 * @author  tonyj
 * @version $Id: StreamerInfo.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public abstract class StreamerInfo implements org.apache.bcel.Constants
{
   protected BasicMember[] members;
   protected RootClass[] superClasses;

   abstract int getBits();

   abstract int getCheckSum();

   abstract void resolve(RootClassFactory factory) throws RootClassNotFound;

   BasicMember[] getMembers()
   {
      return members;
   }
   
   BasicMember getMember(String name)
   {
       for (int i=0; i<members.length; i++)
       {
           if (name.equals(members[i].getName())) return members[i];
       }
       return null;
   }

   RootClass[] getSuperClasses()
   {
      return superClasses;
   }

   abstract int getVersion();
}
