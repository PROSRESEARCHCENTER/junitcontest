package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ref.plotter.PlotterUtilities;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import junit.framework.TestCase;


/**
 * A simple test of the AIDA Plotter
 * @author tonyj
 * @version $Id: TestPlotter.java 10546 2007-02-21 23:39:01Z duns $
 */
public class TestPlotter extends TestCase
{
   public TestPlotter(String name)
   {
      super(name);
   }

   public static void main(String[] args) throws Exception
   {
      TestPlotter test = new TestPlotter("Test Plotter");

      String name = args.length>0 ? args[0] : "test.gif";
      String type = args.length>1 ? args[1] : null;
      test.writePlotToFile(name,type);
   }
   public void testPngOutput() throws IOException
   {
      testPlotOutput("png",0,0);
      testPlotOutput("png",345,678);
   }
   public void testGifOutput() throws IOException
   {
      testPlotOutput("gif",0,0);
      testPlotOutput("gif",345,678);
   }
   public void testPlotOutput(String format, int width, int height) throws IOException
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      IPlotter plotter = createTestPlot(af,1234567);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      Properties props = new Properties();
      if (width > 0) props.setProperty("plotWidth",String.valueOf(width));
      if (height > 0) props.setProperty("plotHeight",String.valueOf(height));
      
      try {
          PlotterUtilities.writeToFile(plotter,out,format,props);
      } catch (IllegalArgumentException iae ) {
          System.out.println("*************************");
          System.out.println("Cannot print with Dummy plotter.");
          System.out.println("Probably the DISPLAY is not set.");
          System.out.println("Skipping testPlotOutput.");          
          System.out.println("*************************");
          return;
      }

      out.close();

      // Check that what was written really is PNG image!
      
      InputStream in = new ByteArrayInputStream(out.toByteArray());
      BufferedImage image = ImageIO.read(in);
      in.close();
      
      assertEquals(image.getWidth(),width > 0 ? width : 600);
      assertEquals(image.getHeight(),height > 0 ? height : 600);
   }
   private void writePlotToFile(String file, String type) throws IOException
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      IPlotter plotter = createTestPlot(af,1234567);
      plotter.writeToFile(file,type);
   }
   private IPlotter createTestPlot(IAnalysisFactory af, long seed)
   {
      ITree tree = af.createTreeFactory().create();
      IHistogramFactory hf = af.createHistogramFactory(tree);

      IHistogram1D h1 = hf.createHistogram1D("Test", 50, -4, 4);
      Random r = new Random(seed);
      for (int i = 0; i < 10000; i++)
         h1.fill(r.nextGaussian());

      IPlotter plotter = af.createPlotterFactory().create("Title");
      plotter.region(0).plot(h1);    
      return plotter;
   }
}
