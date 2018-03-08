// Copyright 2000, SLAC, Stanford, California, U.S.A.
package org.freehep.util.commanddispatcher.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.freehep.util.commanddispatcher.BooleanCommandState;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.commanddispatcher.CommandTargetManager;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.xml.sax.SAXException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: Test.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Test extends org.freehep.swing.test.TestFrame
{
   private final static Class base = org.freehep.xml.menus.test.Test.class;
   private CommandTargetManager cmdManager;

   public static void main(String[] argv) throws Exception
   {
      new Test();
   }

   protected JComponent createComponent()
   {
      JPanel main = new JPanel(new BorderLayout());
      try
      {
         cmdManager = new CommandTargetManager();

         URL xml = base.getResource("menus.xml");
         final XMLMenuBuilder menus = new MyXMLMenuBuilder(xml);

         cmdManager.add(new MyCommandProcessor());
         cmdManager.start();

         JMenuBar b = menus.getMenuBar("mainMenu");
         main.add(b, BorderLayout.NORTH);

         JPanel p = new JPanel();
         main.add(p, BorderLayout.CENTER);
         p.setLayout(new BorderLayout());

         JToolBar t = menus.getToolBar("applicationToolBar");
         p.add(t, BorderLayout.NORTH);
         addMouseListener(new MouseAdapter()
            {
               public void mousePressed(MouseEvent e)
               {
                  if (e.isPopupTrigger())
                     showPopup(e);
                  super.mousePressed(e);
               }

               public void mouseReleased(MouseEvent e)
               {
                  if (e.isPopupTrigger())
                     showPopup(e);
                  super.mouseReleased(e);
               }

               private void showPopup(MouseEvent e)
               {
                  JPopupMenu popup = menus.getPopupMenu("toolbarPopupMenu");
                  popup.show(Test.this, e.getX(), e.getY());
               }
            });
      }
      catch (Exception x)
      {
         x.printStackTrace();
         System.exit(0);
      }
      main.setPreferredSize(new Dimension(400, 200));
      return main;
   }

   public class MyCommandProcessor extends CommandProcessor
   {
      private boolean enablePrint = true;

      public void enableEnablePrint(BooleanCommandState state)
      {
         state.setEnabled(true);
         state.setSelected(enablePrint);
      }

      public void enablePrint(CommandState state)
      {
         state.setEnabled(enablePrint);
      }

      public void onEnablePrint(boolean state)
      {
         enablePrint = state;
         setChanged();
      }

      public void onExit()
      {
         quit();
      }

      public void onPrint()
      {
         System.out.println("Print");
      }
   }

   private class MyXMLMenuBuilder extends XMLMenuBuilder
   {
      // MD: Jikes had a problem compiling an anonymous inner class with a throw clause in 
      // the constructor, therefore I redefined it as an inner class
      MyXMLMenuBuilder(URL xml) throws SAXException, IOException
      {
         build(xml);
      }

      protected JMenuItem createMenuItem(String className, String name, String type, String command) throws SAXException
      {
         JMenuItem result = super.createMenuItem(className, name, type, command);
         cmdManager.add(new CommandSourceAdapter(result));
         return result;
      }

      protected AbstractButton createToolBarItem(String className, String name, String type, String command) throws SAXException
      {
         AbstractButton result = super.createToolBarItem(className, name, type, command);
         cmdManager.add(new CommandSourceAdapter(result));
         return result;
      }
   }
}
