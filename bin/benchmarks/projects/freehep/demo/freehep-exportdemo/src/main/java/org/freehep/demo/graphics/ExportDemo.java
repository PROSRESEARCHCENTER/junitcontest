package org.freehep.demo.graphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import org.freehep.application.*;
import org.freehep.graphicsbase.util.export.VectorGraphicsTransferable;
import org.freehep.graphicsio.test.*;

/**
 * A simple demo of the graphics export library
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ExportDemo.java 14938 2013-05-17 20:54:40Z turri $
 */
public class ExportDemo extends org.freehep.application.Application
{
   private static final String showHelpAtStartup = "showHelpAtStartup";
   private javax.swing.JTabbedPane frame;
   private SaveAsDialog saveAs;

   public ExportDemo()
   {
      super("ExportDemo");
   }
   public void init()
   {
      setStatusMessage("Loading test cases...");
      try
      {
         frame =  new javax.swing.JTabbedPane();
         frame.add("All", new TestAll(null));
         frame.add("Lines", new TestLineStyles(null));
         frame.add("Paint", new TestPaint(null));
         frame.add("Symbols", new TestSymbols2D(null));
         frame.add("Images", new TestImages(null));
         frame.add("Image2D", new TestImage2D(null));
         frame.add("Tagged String", new TestTaggedString(null));
         frame.add("Text", new TestText2D(null));
         frame.add("Transforms", new TestTransforms(null));
         frame.add("Fonts", new TestFonts(null));
         frame.add("Labels", new TestLabels(null));
         frame.add("Shapes", new TestShapes(null));
         frame.add("HTML", new TestHTML(null));
      }
      catch (Exception x)
      {
         throw new RuntimeException("Initilization Error",x);
      }

      add(frame);
      setStatusMessage("Loading exporters...");
      saveAs = new SaveAsDialog();
      saveAs.addAllExportFileTypes();
   }
   public static void main(String[] argv) throws Exception
   {
      final ExportDemo app = new ExportDemo();
      app.createFrame(argv).setVisible(true);
      boolean showAtStartup = PropertyUtilities.getBoolean(app.getUserProperties(),showHelpAtStartup,true);
      if (showAtStartup)
      {
         Runnable run = new Runnable()
         {
            public void run()
            {
               try
               {
                  app.onHelp();
               }
               catch (java.io.IOException x) { x.printStackTrace(); }
            }
         };
         SwingUtilities.invokeLater(run);
      }
   }
   public void onExport()
   {
      saveAs.showExportDialog(this,"Export...",frame.getSelectedComponent(),"export");
   }
   public void onCopy()
   {
      if (System.getProperty("java.version").compareTo("1.4")>=0)
      {
         VectorGraphicsTransferable t = new VectorGraphicsTransferable(frame.getSelectedComponent());
         getServiceManager().setClipboardContents(t);
      }
      else error("Copy to clipboard only available under Java 1.4 or later");
   }
   public void onHelp() throws java.io.IOException
   {
      JPanel message = new JPanel(new BorderLayout());
      JEditorPane pane = new JEditorPane();
      pane.setContentType("text/html");
      pane.setEditable(false);
      java.io.InputStream in = getClass().getResourceAsStream("help.html");
      pane.read(in,"Title");
      in.close();
      JScrollPane scroll = new JScrollPane(pane);
      scroll.setPreferredSize(new Dimension(400,400));
      message.add(scroll,BorderLayout.CENTER);
      final JCheckBox checkBox = new JCheckBox("Show At Startup");
      boolean showAtStartup = PropertyUtilities.getBoolean(getUserProperties(),showHelpAtStartup,true);
      checkBox.setSelected(showAtStartup);
      ActionListener al = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            getUserProperties().setProperty(showHelpAtStartup,checkBox.isSelected() ? "true" : "false" );
         }
      };
      checkBox.addActionListener(al);
      message.add(checkBox,BorderLayout.SOUTH);
      JOptionPane.showMessageDialog(this,message,"Help...",JOptionPane.PLAIN_MESSAGE);
   }
}