/*
 * VectorGraphicsTransferable.java
 *
 * Created on March 29, 2002, 2:53 PM
 */

package jas.hist;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.RepaintManager;
/**
 *
 * @author  tonyj
 */
public class VectorGraphicsTransferable implements ClipboardOwner, Transferable
{
   private Component component;
   private static DataFlavor imageFlavor = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");
   private static Map types = new HashMap();
   /** Creates a new instance of VectorGraphicsTransferable */
   public VectorGraphicsTransferable(Component c)
   {
      this.component = c;
   }
   public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException
   {
      if (dataFlavor.match(imageFlavor))
      {
        Image img = component.createImage(component.getWidth(),component.getHeight());
        Graphics g = img.getGraphics();
         
        // TODO: It would be better to use the PrintHelper to do this??
        // TODO: Make sure we get high quality printing for GIF.
        RepaintManager pm = RepaintManager.currentManager(component);
        boolean save = pm.isDoubleBufferingEnabled();
        pm.setDoubleBufferingEnabled(false);
        component.print(g);
        g.dispose();
        pm.setDoubleBufferingEnabled(save);
        
        return img;
      }
      else
      {
         SaveAsPlugin type = (SaveAsPlugin) types.get(dataFlavor);
         if (type != null)
         {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            type.saveAs(component,out,null,component);
            out.close();
            return new ByteArrayInputStream(out.toByteArray());
         }
      }
      throw new UnsupportedFlavorException(dataFlavor);
   }
   public DataFlavor[] getTransferDataFlavors()
   {
      DataFlavor[] result = new DataFlavor[types.size() + 1];
      types.keySet().toArray(result);
      result[types.size()] = imageFlavor;
      return result;
   }
   public boolean isDataFlavorSupported(DataFlavor dataFlavor)
   {
      if (dataFlavor.match(imageFlavor)) return true;
      if (types.containsKey(dataFlavor)) return true;
      return false;
   }
   public void lostOwnership(Clipboard clipboard, Transferable transferable)
   {
   }
   public static void register(DataFlavor flavor, SaveAsPlugin type)
   {
      types.put(flavor,type);
   }
}