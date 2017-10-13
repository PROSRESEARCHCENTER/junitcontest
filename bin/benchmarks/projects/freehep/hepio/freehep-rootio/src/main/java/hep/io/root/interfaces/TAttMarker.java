/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Jan 15 18:53:43 PST 2001
 */
package hep.io.root.interfaces;

public interface TAttMarker extends hep.io.root.RootObject
{
   public final static int rootIOVersion = 1;

   /** Marker color index */
   short getMarkerColor();

   /** Marker size */
   float getMarkerSize();

   /** Marker style */
   short getMarkerStyle();
}
