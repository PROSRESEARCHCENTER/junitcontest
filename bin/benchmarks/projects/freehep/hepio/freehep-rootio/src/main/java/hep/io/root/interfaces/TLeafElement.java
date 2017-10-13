/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Wed Jul 18 16:24:09 PDT 2001
 */
package hep.io.root.interfaces;

import hep.io.root.*;


public interface TLeafElement extends hep.io.root.RootObject, TLeaf
{
   public final static int rootIOVersion = 1;

   /** element serial number in fInfo */
   int getID();

   void setMember(RootMember member);

   /** leaf type */
   int getType();
}
