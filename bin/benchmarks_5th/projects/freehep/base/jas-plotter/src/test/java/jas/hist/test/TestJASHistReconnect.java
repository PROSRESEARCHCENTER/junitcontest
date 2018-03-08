/*
 * TestJASHistReconnect.java
 *
 * Created on May 3, 2005, 2:53 PM
 */

package jas.hist.test;

import jas.hist.JASHist;
import jas.hist.JASHistData;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;

/**
 *
 * @author  serbo
 */
public class TestJASHistReconnect extends MemoryDataSource {
    
    /** Creates a new instance of TestJASHistReconnect */
    public TestJASHistReconnect() {
        super();
    }
	public String getTitle()
	{
		return "Java Memory Usage TIMES TWO";
	}
    public double[][] rebin(int nbin, double min, double max, boolean wantErrors, boolean hurry) {
        double[][] r = super.rebin(nbin, min, max, wantErrors, hurry);
        for (int i=0; i<r.length; i++) {
            double[] tmp = r[i];
            for (int j=0; j<tmp.length; j++) {
                tmp[j] = tmp[j]*2.;
            }
        }
        return r;
    }
    
    static void printDB(JComponent comp) {
        RepaintManager currentManager = RepaintManager.currentManager(comp);
        boolean manager = currentManager.isDoubleBufferingEnabled();
        boolean component = comp.isDoubleBuffered();
        
        System.out.println("Double-Buffered:  manager="+manager+",  component="+component);
    }
    
    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) throws Exception {
       final JASHist plot = new JASHist(); 
       printDB(plot);
       JASHistData data1 = plot.addData(new MemoryDataSource());
       //JASHistData data1 = plot.addData(new Gauss());
       //JASHistData data1 = plot.addData(new LiveGauss("LiveGauss", 1000, 1, 0, 0));
       //JASHistData data2 = plot.addData(new TestJASHistReconnect());
       plot.setTitle("Java Memory Usage");
       plot.setDataAreaBorderType(plot.ETCHED);
       plot.getYAxis().setLabel("MBytes");
       plot.getXAxis().setLabel("Time (seconds)");
       plot.setAllowUserInteraction(true);
       plot.setShowLegend(JASHist.LEGEND_NEVER);
       printDB(plot);
       
       Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1010);
                    } catch (Exception e) { e.printStackTrace(); }
                    printDB(plot);
                }
            }
       });
       t.start();
       
       int reply = JOptionPane.OK_OPTION;
       while (reply == JOptionPane.OK_OPTION) {
            JOptionPane.showConfirmDialog(null, plot, "Plot Dialog", JOptionPane.DEFAULT_OPTION);
            reply = JOptionPane.showConfirmDialog(null, "Show Plot again ...", " Question Dialog", JOptionPane.OK_CANCEL_OPTION);
       }       
       System.exit(0);
   }    
    
   
}
