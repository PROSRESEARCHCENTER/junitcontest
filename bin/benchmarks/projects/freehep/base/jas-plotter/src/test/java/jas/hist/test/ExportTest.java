package jas.hist.test;

import jas.export.SaveAsPluginAdapter;
import jas.hist.JASHist;
import jas.hist.SaveAsPlugin;
import jas.hist.XMLHistBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.freehep.graphicsbase.util.export.ExportFileType;

class ExportTest extends JFrame implements Runnable
{
   private JASHist plot;
   public ExportTest() throws Exception
   {
      super("Export Test");
      setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
      XMLHistBuilder xhb = new XMLHistBuilder(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ExportTest.plotml"))),"ExportTest.plotml");
      plot = xhb.getSoloPlot();
      plot.setAllowUserInteraction(false);
      getContentPane().add(plot);
   }
   public static void main(String args[]) throws Exception
   {
      ExportTest xhv = new ExportTest();
      xhv.pack();
      xhv.show();
      Thread.currentThread().sleep(500);
      SwingUtilities.invokeAndWait(xhv);
      System.exit(0);
   }
   public void run()
   {
      try
      {
         BufferedReader control = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("ExportTest.ini")));
         for (;;)
         {
            String line = control.readLine();
            if (line == null) break;
            StringTokenizer st = new StringTokenizer(line,",=");
            String title = st.nextToken().trim();
            String file = st.nextToken().trim();
            String klass = st.nextToken().trim();
            Properties props = new Properties();
            while (st.hasMoreTokens())
            {
               String key = st.nextToken().trim();
               if (!st.hasMoreTokens()) break;
               String value = st.nextToken().trim();
               props.setProperty(key,value);
            }
            test(title,file,klass,props);
         }
         control.close();
      }
      catch (Exception x)
      {
         x.printStackTrace();
      }
   }
   private void test(String title,String file,String exported, Properties options) throws Exception
   {
      Class c = Class.forName(exported);
      Object exporter = c.newInstance();
      if (exporter instanceof ExportFileType) exporter = new SaveAsPluginAdapter((ExportFileType) exporter);
      SaveAsPlugin saveAs = (SaveAsPlugin) exporter;
      saveAs.restoreOptions(options);
      File f = new File(file);
      OutputStream os = new FileOutputStream(f);
      System.out.println(title+" running...");
      long start = System.currentTimeMillis();
      saveAs.saveAs(plot,os,f,this);
      long stop = System.currentTimeMillis();
      os.close();
      System.out.println(title+" done, time="+(stop-start)+"ms size="+f.length());
   }
}
