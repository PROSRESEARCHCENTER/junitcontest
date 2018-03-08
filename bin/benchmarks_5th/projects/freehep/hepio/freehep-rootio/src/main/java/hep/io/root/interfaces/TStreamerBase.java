/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TStreamerBase extends hep.io.root.RootObject, TStreamerElement
{
   public final static int rootIOVersion = 3;

   /** version number of the base class */
   int getBaseVersion();
}
