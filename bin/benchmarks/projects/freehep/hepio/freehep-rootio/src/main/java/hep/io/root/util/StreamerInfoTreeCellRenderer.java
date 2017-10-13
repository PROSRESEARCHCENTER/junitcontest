package hep.io.root.util;

import hep.io.root.interfaces.TNamed;
import hep.io.root.interfaces.TStreamerBase;
import hep.io.root.interfaces.TStreamerElement;
import hep.io.root.interfaces.TStreamerInfo;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A TreeCellRenderer for StreamerInfo objects.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StreamerInfoTreeCellRenderer.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class StreamerInfoTreeCellRenderer extends DefaultTreeCellRenderer
{
   private final static Icon classIcon = new ImageIcon(RootDirectoryTreeCellRenderer.class.getResource("images/class_t.gif"));
   private final static Icon memberIcon = new ImageIcon(RootDirectoryTreeCellRenderer.class.getResource("images/member_t.gif"));

   public java.awt.Component getTreeCellRendererComponent(JTree p1, Object p2, boolean p3, boolean p4, boolean p5, int p6, boolean p7)
   {
      super.getTreeCellRendererComponent(p1, p2, p3, p4, p5, p6, p7);
      if (p2 instanceof TNamed)
      {
         TNamed named = (TNamed) p2;
         String title = named.getTitle();
         if ((title != null) && (title.length() > 0))
            setText(named.getName() + " \"" + title + "\"");
         else
            setText(named.getName());
      }
      if (p2 instanceof TStreamerInfo)
         setIcon(classIcon);
      else if (p2 instanceof TStreamerBase)
         setIcon(classIcon);
      else if (p2 instanceof TStreamerElement)
      {
         TStreamerElement e = (TStreamerElement) p2;
         StringBuffer text = new StringBuffer(getText());
         text.insert(0, " ");
         for (int i = 0; i < e.getArrayDim(); i++)
            text.insert(0, "[]");
         text.insert(0, e.getTypeName());
         setText(text.toString());
         setIcon(memberIcon);
      }
      return this;
   }
}
