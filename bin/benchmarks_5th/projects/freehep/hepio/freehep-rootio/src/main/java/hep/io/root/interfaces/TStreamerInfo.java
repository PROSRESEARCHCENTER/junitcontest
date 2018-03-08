/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Wed Jan 10 15:19:15 PST 2001
 */
package hep.io.root.interfaces;

public interface TStreamerInfo extends hep.io.root.RootObject, TNamed
{
   int getCheckSum();

   int getClassVersion();

   hep.io.root.interfaces.TObjArray getElements();
}
