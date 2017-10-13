package jas.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A button which allows the user to select a color.
 */

public class ColorChooser extends JButton
{
   public ColorChooser()
   {
      this(Color.black);
   }
   public ColorChooser(Color c)
   {
      m_color = c;
      setIcon(new ColorChooserIcon());
   }
   public ColorChooser(final String key)
   {
      this();
      m_prop = Application.getApplication().getUserProperties();
      m_key = key;
      setColor(m_prop.getColor(key, Color.black));
   }
   
   public void setColor(Color c)
   {
      m_color = c;
      repaint(); // Should not be necessary? (swing 0.6.1)
      Enumeration e = m_listeners.elements();
      while (e.hasMoreElements())
      {
         ColorListener l = (ColorListener) e.nextElement();
         l.colorChanged(new ColorEvent(this,c));
      }
   }
   public Color getColor()
   {
      return m_color;
   }
   public void addColorListener(ColorListener l)
   {
      m_listeners.addElement(l);
   }
   public void removeColorListener(ColorListener l)
   {
      m_listeners.removeElement(l);
   }
   protected void fireActionPerformed(ActionEvent e)
   {
      JColorChooser chooser = new JColorChooser(m_color);
      chooser.addChooserPanel(new ARGBColorChooserPanel());

      if (m_color.getAlpha() != 255) 
      {
         AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
         int l = panels.length;
         AbstractColorChooserPanel[] newpanels = new AbstractColorChooserPanel[l];
         for (int i=1; i<l; i++) newpanels[i] = panels[i-1];
         newpanels[0] = panels[l-1];
         chooser.setChooserPanels(newpanels);
      }
      int rc = JOptionPane.showOptionDialog(this,chooser,"Choose color...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,null,null);
      if (rc == JOptionPane.OK_OPTION)
      {
         Color c = chooser.getColor();
         setColor(c);
         if (m_key != null) m_prop.setColor(m_key, c);
      }
   }
   private UserProperties m_prop;
   private String m_key;
   private Color m_color;
   private Vector m_listeners = new Vector();

   public Dimension getPreferredSize()
   {
      return new Dimension(20,20);
   }
   
   private class ColorChooserIcon implements Icon
   {
      public int getIconHeight()
      {
         return 16;
      }

      public int getIconWidth()
      {
         return 16;
      }

      public void paintIcon(Component component, Graphics g, int x, int y)
      {
         g.setColor(m_color);
         g.fillRect(x,y,16,16);
      }
   }
}
