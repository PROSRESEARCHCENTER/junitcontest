package org.freehep.application.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.freehep.application.mdi.MDIApplication;
import org.freehep.application.mdi.ManagedPage;
import org.freehep.application.mdi.PageContext;
import org.freehep.util.images.ImageHandler;

/**
 *
 * @author  Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MDITest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MDITest extends MDIApplication
{
   /** Creates new MDITest */
   public MDITest()
   {
      super("MDITest");
   }
   public static void main(String[] args)
   {
      new MDITest().createFrame(args).setVisible(true);
   }
   public void onNewPage()
   {
      getPageManager().openPage(new CloseablePanel(),"Page "+(page++),icon);
   }
   public void onNewConsole()
   {
      getConsoleManager().openPage(new CloseablePanel(),"Console "+(console++),icon);
   }
   public void onNewControl()
   {
      getControlManager().openPage(new CloseablePanel(),"Control "+(control++),icon);
   }
   
   protected void init()
   {
      onNewPage();
      onNewPage();
      onNewConsole();
      onNewControl();
   }
   
   private int page;
   private int console;
   private int control;
   private Icon icon = ImageHandler.getIcon("/toolbarButtonGraphics/development/Bean16.gif",MDITest.class);
   private class CloseablePanel extends JPanel implements ManagedPage, ActionListener
   {
      CloseablePanel()
      {
         JButton close = new JButton("Close");
         add(close);
         close.addActionListener(this);
      }
      public void setPageContext(PageContext context)
      {
         this.context = context;
      }
      public boolean close()
      {
         return true;
      }
      public void actionPerformed(ActionEvent e)
      {
         context.close();
      }
      public void pageSelected(){}
      public void pageDeselected(){}
      public void pageIconized(){}
      public void pageDeiconized(){}
      public void pageClosed(){}
      
      private PageContext context;
   }
}