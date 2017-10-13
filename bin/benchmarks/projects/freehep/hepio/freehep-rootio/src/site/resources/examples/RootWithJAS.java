import hep.io.root.*;
import hep.io.root.interfaces.*;
import java.io.IOException;
import jas.hist.*;
import jasext.root.RootHistogramAdapter;
import javax.swing.*;
import java.awt.BorderLayout;

/**
 * An example of using the JASHist widget with Root Histograms
 * @author tonyj@slac.stanford.edu
 */
public class RootWithJAS extends JPanel
{
   /** Creates new RootWithJas */
    private RootWithJAS() throws IOException
    {
       RootFileReader rfr = new RootFileReader("Example.root");
       TH1 main = (TH1) rfr.getKey("mainHistogram").getObject();
       TH1 total = (TH1) rfr.getKey("totalHistogram").getObject();
       TH1 s1 = (TH1) rfr.getKey("s1Histogram").getObject();
       TH1 s2 = (TH1) rfr.getKey("s2Histogram").getObject();
              
       JASHist plot = new JASHist();
       plot.addData(RootHistogramAdapter.create(total)).show(true);
       plot.addData(RootHistogramAdapter.create(main)).show(true);
       plot.addData(RootHistogramAdapter.create(s1)).show(true);
       plot.addData(RootHistogramAdapter.create(s2)).show(true);
       
       setLayout(new BorderLayout());
       add(plot);       
    }
    public static void main (String args[]) throws IOException
    {
       JFrame f = new JFrame();
       f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
       f.setContentPane(new RootWithJAS());
       f.setSize(500,300);
       f.setVisible(true);
    }
}
