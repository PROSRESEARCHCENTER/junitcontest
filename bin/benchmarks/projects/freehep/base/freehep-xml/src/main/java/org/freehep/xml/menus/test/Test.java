// Copyright 2000, SLAC, Stanford, California, U.S.A.
package org.freehep.xml.menus.test;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;

/**
 * A simple example of how to use the XML menu system. For xml menu
 * system can also be combined with the command dispatch system.
 *
 * @see org.freehep.util.commanddispatcher
 * @see org.freehep.util.commanddispatcher.test.Test
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: Test.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Test extends org.freehep.swing.test.TestFrame implements ActionListener
{
   private class MyXMLMenuBuilder extends XMLMenuBuilder
   {
      protected JMenuItem createMenuItem(String className, String name, String type, String command) throws SAXException
      {
         JMenuItem result = super.createMenuItem(className, name, type, command);
         result.addActionListener(Test.this);
         return result;
      }
      protected AbstractButton createToolBarItem(String className, String name, String type, String command) throws SAXException
      {
         AbstractButton result = super.createToolBarItem(className, name, type, command);
         result.addActionListener(Test.this);
         return result;
      }
   }
   
   protected JComponent createComponent()
   {
      JPanel main = new JPanel(new BorderLayout());
      try
      {
         java.net.URL xml = getClass().getResource("menus.xml");
         final XMLMenuBuilder menus = new MyXMLMenuBuilder();
         menus.build(xml);
         
         JMenuBar b = menus.getMenuBar("mainMenu");
         main.add(b,BorderLayout.NORTH);
         JPanel p = new JPanel();
         main.add(p,BorderLayout.CENTER);
         p.setLayout(new BorderLayout());
         
         JToolBar t = menus.getToolBar("applicationToolBar");
         p.add(t,BorderLayout.NORTH);
         addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent e)
            {
               if (e.isPopupTrigger()) showPopup(e);
               super.mousePressed(e);
            }
            public void mouseReleased(MouseEvent e)
            {
               if (e.isPopupTrigger()) showPopup(e);
               super.mouseReleased(e);
            }
            private void showPopup(MouseEvent e)
            {
               JPopupMenu popup = menus.getPopupMenu("toolbarPopupMenu");
               popup.show(Test.this,e.getX(),e.getY());
            }
         });
      }
      catch (Exception x)
      {
         x.printStackTrace();
         System.exit(0);
      }
      main.setPreferredSize(new Dimension(400,200));
      return main;
   }
   public static void main(String[] argv) throws Exception
   {
      new Test();
   }
   public void actionPerformed(ActionEvent e)
   {
      System.out.println("Command="+e.getActionCommand());
   }
}
