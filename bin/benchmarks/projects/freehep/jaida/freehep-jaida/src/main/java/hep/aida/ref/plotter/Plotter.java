package hep.aida.ref.plotter;

import hep.aida.IPlotterRegion;
import hep.aida.IPlotterStyle;
import jas.util.layout.PercentLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.freehep.swing.popup.GlobalMouseListener;
import org.freehep.swing.popup.GlobalPopupListener;

/**
 *
 * @author tonyj
 * @version $Id: Plotter.java 10546 2007-02-21 23:39:01Z duns $
 */
public class Plotter extends DummyPlotter
{
   private String name;
   private boolean isShowing = false;
   private JPanel panel;
   
   protected Plotter(String name) {
       this(name, null);
   }
   
   protected Plotter(String name, String options)
   {
      super(name, options);
      this.name = name;
    }

    public void setStyle(IPlotterStyle style) {
        super.setStyle(style);
    }
    
   protected PlotterRegion createPlotterRegion()
   {
      return new PlotterRegion(this);
   }
   protected void configurePanel(JPanel p)
   {
      p.setLayout(new PercentLayout());
      p.setBackground(Color.white);
      p.setPreferredSize(new Dimension(plotterWidth(),plotterHeight()));    
   }
   
   protected JPanel getPanel()
   {
       if ( panel == null ) {
          panel = new JPanel();
          configurePanel(panel);
       }
       panel.setPreferredSize(new Dimension(plotterWidth(),plotterHeight()));
       return panel;
   }

   public boolean isShowing() {
        return isShowing;
    }
    
    public JPanel panel() {
        return getPanel();
    }

   
   protected String getTitle()
   {
      return name;
   }
   public void show()
   {
      if (!isShowing)
      {
         JFrame frame = new JFrame(name);
         
         GlobalMouseListener gml = new GlobalMouseListener(frame);
         gml.addMouseListener(new GlobalPopupListener());
         
         frame.setContentPane(panel());
         frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
         frame.pack();
         panel().setVisible(true);
         frame.setVisible(true);
         isShowing = true;
      }
   }
   public void hide()
   {
      if (isShowing)
      {
         JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class,panel());
         panel().setVisible(false);
         if (frame != null) frame.dispose();
         isShowing = false;
      }
   }
   protected IPlotterRegion justCreateRegion(double x, double y, double width, double height)
   {
      CreateRegion cr = new CreateRegion(x,y,width,height);
      invokeOnSwingThread(cr);
      return cr.getRegion();
   }
   public void destroyRegions()
   {
      invokeOnSwingThread(new DestroyRegions());
      super.destroyRegions();
   }

   public void writeToFile(String file, String type) throws IOException {
       writeToFile(file, type, System.getProperties());
   }
   
   private class CreateRegion implements Runnable
   {
      private double x;
      private double y;
      private double width;
      private double height;
      private PlotterRegion region;
      
      CreateRegion(double x, double y, double width, double height)
      {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
      public void run()
      {
         region = createPlotterRegion();
         getPanel().add(region.getPanel(),new PercentLayout.Constraint(x*100,y*100,width*100,height*100));
      }
      IPlotterRegion getRegion()
      {
         return region;
      }
   }
      
//   private class CreateRegions implements Runnable
//   {
//      private int rows;
//      private int columns;
//      CreateRegions(int columns, int rows)
//      {
//         this.rows = rows;
//         this.columns = columns;
//      }
//      public void run()
//      {
//         panel.removeAll();
//      
//         if (rows*columns != 0)
//         {
//            double pcWidth = 100/columns;
//            double pcHeight = 100/rows;
//
//            for (int r=0; r<rows; r++)
//            {
//               for (int c=0; c<columns; c++)
//               {
//                  panel.add(new Region(),new PercentLayout.Constraint(c*pcWidth,r*pcHeight,pcWidth,pcHeight));
//               }
//            }
//         }
//         panel.repaint();
//      }
//   }
   private class DestroyRegions implements Runnable
   {
      public void run()
      {
         getPanel().removeAll();
         getPanel().repaint();        
      }
   }
}