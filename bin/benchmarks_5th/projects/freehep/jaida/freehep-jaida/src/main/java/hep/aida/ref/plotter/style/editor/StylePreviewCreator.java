package hep.aida.ref.plotter.style.editor;
/*
 * StylePreviewPanel.java
 *
 * Created on June 14, 2005, 1:59 PM
 */

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IPlotter;
import hep.aida.IPlotterRegion;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.DummyPlotter;
import hep.aida.ref.plotter.PlotterRegion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.EtchedBorder;
/**
 *
 * @author  serbo
 */
public class StylePreviewCreator {
    private static final Class[] possibleTypes = { IHistogram1D.class, IHistogram2D.class, IDataPointSet.class };
    
    private IPlotter plotter;
    private IHistogram1D hist1D;
    private IHistogram2D hist2D;
    private IDataPointSet dps;
    
    private int nEvents1D = 1000;
    private int nx1D = 50;
    private double xMin1D = 0;
    private double xMax1D = 100;
    
    private int nEvents2D = 1000;
    private int nx2D = 50;
    private double xMin2D = 0;
    private double xMax2D = 100;
    private int ny2D = 50;
    private double yMin2D = 0;
    private double yMax2D = 100;
    
    private int nEventsDps2D = 20;
    private double xMinDps2D = 1;
    private double xMaxDps2D = 21;
    private double yMinDps2D = 10;
    private double yMaxDps2D = 210;
    
    private JComponent previewPanel;
    private Dimension previewPanelDimension = new Dimension(-1, -1);
    private Logger styleLogger;
    
    public static Class[] getPossiblePreviewTypes() { return possibleTypes; }
    
    /** Creates a new instance of StylePreviewPanel */
    public StylePreviewCreator() {
        this(null);
    }
    
    public StylePreviewCreator(ConfigurePreviewPanel configurePanel) {
        styleLogger = Logger.getLogger("hep.aida.ref.plotter.style.editor");
        setupPreviewCreator(configurePanel);
        init();
    }    
    
    public void setupPreviewCreator(ConfigurePreviewPanel configurePanel) {
        if (configurePanel == null) configurePanel = new ConfigurePreviewPanel();       
        readConfigurePanel(configurePanel);
        init();
    }
    
    private void readConfigurePanel(ConfigurePreviewPanel configurePanel) {
        boolean fit = configurePanel.previewFitRegion;
        Dimension d = null;
        if (!fit) d = new Dimension(configurePanel.previewWidth, configurePanel.previewHeight);
        else d = new Dimension(-1, -1);
        setPreviewPanelDimension(d);

        nEvents1D = configurePanel.nEvents1D;
        nx1D = configurePanel.nx1D;
        xMin1D = configurePanel.xMin1D;
        xMax1D = configurePanel.xMax1D;
        
        nEvents2D = configurePanel.nEvents2D;
        nx2D = configurePanel.nx2D;
        ny2D = configurePanel.ny2D;
        xMin2D = configurePanel.xMin2D;
        xMax2D = configurePanel. xMax2D;     
        yMin2D = configurePanel.yMin2D;
        yMax2D = configurePanel.yMax2D;
        
        nEventsDps2D = configurePanel.nEventsDps2D;
        xMinDps2D = configurePanel.xMinDps2D;
        xMaxDps2D = configurePanel.  xMaxDps2D;  
        yMinDps2D = configurePanel.yMinDps2D;
        yMaxDps2D = configurePanel.  yMaxDps2D; 
        
    }
    
    private void init() {
        if (plotter != null) plotter.clearRegions();
        IAnalysisFactory af = IAnalysisFactory.create();
        plotter = af.createPlotterFactory().create();
        plotter.createRegions(1);
        
        // Create histograms
        try {
            hist1D = af.createHistogramFactory(null).createHistogram1D("Histogram 1D", nx1D, xMin1D, xMax1D);
            hist2D = af.createHistogramFactory(null).createHistogram2D("Histogram 2D", nx2D, xMin2D, xMax2D, ny2D, yMin2D, yMax2D);
            dps    = af.createDataPointSetFactory(null).create("DataPointSet", 2);
            
            Random rand = new Random();
            double x = 0;
            double y = 0;
            
            // fill 1D
            double xMean = (xMax1D + xMin1D)/2.;
            double xk = (xMax1D - xMin1D)/6.;
            for (int i=0; i<nEvents1D; i++) {
                x = xMean + xk*rand.nextGaussian();
                hist1D.fill(x);
            }
            
            // fill 2D
            xMean = (xMax2D + xMin2D)/2.;
            xk = (xMax2D - xMin2D)/6.;
            double yMean = (yMax2D + yMin2D)/2.;
            double yk = (yMax2D - yMin2D)/6.;
            for (int i=0; i<nEvents2D; i++) {
                x = xMean + xk*rand.nextGaussian();
                y = yMean + yk*rand.nextGaussian();
                hist2D.fill(x, y);
            }
            
            // fill dps 2D
            xk = (xMaxDps2D - xMinDps2D)/nEventsDps2D;
            yk = (yMaxDps2D - yMinDps2D)/nEventsDps2D;
            for (int i=0; i<nEventsDps2D; i++) {
                x = xk*((double) i) + xMinDps2D;
                y = yk*((double) i) + yMinDps2D;
                if (i < (nEventsDps2D-1)) y = y + 0.5*rand.nextGaussian();
                else y = y - 0.5*rand.nextGaussian();
                
                IDataPoint point = dps.addPoint();
                point.coordinate(0).setValue(x);
                point.coordinate(0).setErrorMinus(Math.sqrt(x));
                point.coordinate(0).setErrorPlus(Math.sqrt(x));
                point.coordinate(1).setValue(y);
                point.coordinate(1).setErrorMinus(Math.sqrt(y));
                point.coordinate(1).setErrorPlus(Math.sqrt(y));
            }
            
            plotter.region(0).plot(hist1D);
        } catch (Exception e) {
            styleLogger.info("Can not create sample histograms for the preview panel");
            styleLogger.log(Level.FINE, null, e);
        }
        
    }
    
    public JComponent getPreviewPanel(IPlotterStyle style, Class type) {
        // Create histograms
        String options = PlotterRegion.USE_EXACT_STYLE;
        try {
            plotter.region(0).clear();
            if (type == IHistogram1D.class) {
                plotter.region(0).plot(hist1D, style, options);
            } else if (type == IHistogram2D.class) {
                plotter.region(0).plot(hist2D, style, options);
            } else if (type == IDataPointSet.class) {
                plotter.region(0).plot(dps, style, options);
            }
            
            //(new UpdateThread(hist, style)).start();
            
        } catch (Exception e) {
            e.printStackTrace();
            styleLogger.info("Can not create preview panel for type: "+type);
            styleLogger.log(Level.FINE, "", e);
        }
        
        // Create JComponent for this plotter
        if (plotter instanceof hep.aida.ref.plotter.DummyPlotter) {
            IPlotterRegion region = plotter.region(0);
            if (region instanceof hep.aida.ref.plotter.PlotterRegion) {
                ((hep.aida.ref.plotter.PlotterRegion) region).getPlot().setAllowUserInteraction(true);
            }
            
            previewPanel = ((DummyPlotter) plotter).panel();
        }
        
        if (previewPanel != null) {
            if (previewPanelDimension.width > 0 && previewPanelDimension.height > 0) {
                previewPanel.setPreferredSize(previewPanelDimension);
                //previewPanel.setMaximumSize(previewPanelDimension);
                //previewPanel.setMinimumSize(previewPanelDimension);
            }
            previewPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        return previewPanel;
    }
    
    public Dimension getPreviewPanelDimensiont() { return previewPanelDimension; }
    public void setPreviewPanelDimension(Dimension d) { previewPanelDimension = d; }
    
    
    // Updating thread for tests
    class UpdateThread extends Thread {
        IHistogram1D h;
        IPlotterStyle ps;
        Random rand = new Random();
        String scaling = "lin";
        
        public UpdateThread(IHistogram1D hist, IPlotterStyle ps) {
            this.h = hist;
            this.ps = ps;
        }
        public void run() {
            while (true) {
                try {
                    Thread.sleep(4000);
                    //for (int i=0; i<100; i++) h.fill(rand.nextGaussian());
                    
                    if (scaling.equals("lin")) scaling = "log";
                    else scaling = "lin";
                    ps.yAxisStyle().setScaling(scaling);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    
    
    
    public static void main(String[] args) throws Exception {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //LookAndFeelTweaks.tweak();
        
        JFrame frame = new JFrame("PropertySheet");
        frame.getContentPane().setLayout(new BorderLayout());
        
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();
        
        StylePreviewCreator spc = new StylePreviewCreator();
        frame.getContentPane().add("Center", spc.getPreviewPanel(style,possibleTypes[0]));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 500);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( (d.width-frame.getSize().width )/2, (d.height-frame.getSize().height )/2 );
        frame.setVisible(true);
    }
    
}
