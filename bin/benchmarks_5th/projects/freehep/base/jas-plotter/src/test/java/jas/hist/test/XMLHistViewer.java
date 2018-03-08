package jas.hist.test;

import jas.hist.JASHist;
import jas.hist.XMLHistBuilder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class XMLHistViewer extends JFrame
{
   public XMLHistViewer(String str, String fileName)
   {
      super(str);
      getContentPane().add(new JLabel("Reading "+fileName+"..."));
      addWindowListener(new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {System.exit(0);}
      });
   }
   void load(String fileName)
   {
      try
      {
         xhb = new XMLHistBuilder(new BufferedReader(new FileReader(fileName)),fileName);
         final JASHist hist = xhb.getSoloPlot();
         
         if (hist == null) throw new RuntimeException("No Histogram found");
         
         final JPanel p = new JPanel(new BorderLayout());
         p.add("Center",hist);
         JButton export = new JButton("Save As...");
         export.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               hist.saveAs();
            }
         });
         p.add("South",export);
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               setContentPane(p);
               pack();
            }
         });
      }
      catch (final Throwable t)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               dispose();
               JOptionPane.showMessageDialog(null, t.getMessage(), "Error",  JOptionPane.ERROR_MESSAGE);
               System.exit(0);
            }
         });
      }
   }
   public static void main(String args[])
   {
      XMLHistViewer xhv = new XMLHistViewer("XMLHistViewer", args[0]);
      xhv.pack();
      xhv.show();
      xhv.load(args[0]);
      // Attempt to register functions/fitters
      // Will fail of Fitting.jar is not in the CLASSPATH
      try
      {
         Class c = XMLHistViewer.class.forName("jasext.hist.Register");
         java.lang.reflect.Method m = c.getMethod("init",noArgc);
         Object reg = c.newInstance();
         m.invoke(reg,noArgs);
      }
      catch (Throwable t)
      {
         System.err.println("Unable to register functions/fitters");
         t.printStackTrace();
      }
      // Attempt to register exporters
      // Will fail of freehep-*.jar is not in the CLASSPATH
      try
      {
         Class c = TestJASHist.class.forName("jas.export.Register");
         java.lang.reflect.Method m = c.getMethod("init",noArgc);
         Object reg = c.newInstance();
         m.invoke(reg,noArgs);
      }
      catch (Throwable t)
      {
         System.err.println("Unable to register exporters");
      }
   }
   public final static Class[] noArgc = {};
   public final static Object[] noArgs = {};
   private XMLHistBuilder xhb;
}
