package jas.plot;

import jas.util.OnScreenPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupMenuBuilder extends MouseAdapter
{
   void buildMenu(MouseEvent e)
   {
      // TODO: once we switch to JDK 1.3 we can use
      // JPopupMenu.isPopupTrigger()
      boolean isPopupTrigger = e.isPopupTrigger() && e.getID() != MouseEvent.MOUSE_EXITED;
      if (!isPopupTrigger) return;
      
      Component source = (Component) e.getSource();
      JPopupMenu menu = new OnScreenPopupMenu();
      
      for (Component c=source; c != null; c = c.getParent())
      {
         
         if (c instanceof HasPopupItems)
            ((HasPopupItems) c).modifyPopupMenu(menu, source);
      }
      if (menu.getComponentCount() > 0)
      {
         menu.show(source,e.getX(),e.getY());
      }
   }
   public void mousePressed(MouseEvent e)
   {
      buildMenu(e);
      super.mousePressed(e);
   }
   public void mouseReleased(MouseEvent e)
   {
      buildMenu(e);
      super.mouseReleased(e);
   }
}
