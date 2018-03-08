/*
 * Plotter.java
 *
 * Created on January 22, 2002, 3:08 PM
 */

package hep.aida.ref.plotter;

import hep.aida.IPlotter;
import hep.aida.IPlotterRegion;
import hep.aida.IPlotterStyle;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.freehep.application.PropertyUtilities;
import org.freehep.graphicsbase.swing.Headless;
import org.freehep.graphicsbase.util.export.ExportFileType;

/**
 * A dummy implementation of an AIDA Plotter, useful in batch jobs where no
 * graphical output is required.
 * @author tonyj
 * @version $Id: DummyPlotter.java 14104 2013-02-22 17:33:47Z turri $
 */
public class DummyPlotter implements IPlotter {
    
    private ArrayList parameters  = new ArrayList();
    private ArrayList parOptions  = new ArrayList();
    private ArrayList parValues   = new ArrayList();
    private ArrayList parDefaults = new ArrayList();

    protected IPlotterStyle plotterStyle = new PlotterStyle();
    
    private static final String[] emptyArray = new String[0];
    private List regions = new ArrayList();
    private int currentRegion = 0;
    
    private String title = "";
    
    static final int DEFAULT_WIDTH = 600;
    static final int DEFAULT_HEIGHT = 600;
    
    private boolean warned = false;
    
    protected DummyPlotter() {
        // This constructor should not invoke createRegion() or the JAS plotter will have problems
        initializeParameters();
    }
    
    DummyPlotter(String name) {
        this(name, null);
    }
    
    DummyPlotter(String name, String options) {
        initializeParameters();
        createRegion();
    }
    
    private void initializeParameters() {
        if (plotterStyle != null) plotterStyle.setParameter(Style.PLOTTER_STYLE_NAME, "PlotterStyle");
        addParameter("plotterWidth",String.valueOf(-1));
        addParameter("plotterHeight",String.valueOf(-1));
    }
    
    protected IPlotterRegion justCreateRegion(double x, double y, double width, double height) {
        return new DummyPlotterRegion(x,y,width,height);
    }
    public void show() {
        System.err.println("Warning: Dummy plotter used, no plot will appear");
    }
    public void hide() {
    }
    public void refresh() {
    }
    public void interact() {
    }
    public IPlotterRegion next() {
        return (IPlotterRegion) regions.get(++currentRegion);
    }
    public IPlotterRegion createRegion(double x) {
        return createRegion(x,0,1-x,1);
    }
    public IPlotterRegion createRegion() {
        return createRegion(0,0,1,1);
    }
    public IPlotterRegion createRegion(double x, double y) {
        return createRegion(x,y,1-x,1-y);
    }
    public IPlotterRegion createRegion(double x, double y, double w) {
        return createRegion(x,y,w,1-y);
    }
    
    public IPlotterRegion createRegion(double x, double y, double w, double h) {
        if (x<0 || x>1) throw new IllegalArgumentException("x");
        if (y<0 || y>1) throw new IllegalArgumentException("y");
        if (w<0 || x+w>1) throw new IllegalArgumentException("w");
        if (h<0 || y+h>1) throw new IllegalArgumentException("h");
        IPlotterRegion region = justCreateRegion(x,y,w,h);
        regions.add(region);
        return region;
    }
    public IPlotterRegion currentRegion() {
        return (IPlotterRegion) regions.get(currentRegion);
    }
    public void createRegions() {
        createRegions(1,1,0);
    }
    public void createRegions(int columns) {
        createRegions(columns,1,0);
    }
    public void createRegions(int columns, int rows) {
        createRegions(columns,rows,0);
    }
    public void createRegions(int columns, int rows, int start) {
        if (columns <= 0) throw new IllegalArgumentException("columns");
        if (rows <= 0) throw new IllegalArgumentException("rows");
        if (start < 0 || start >= rows*columns) throw new IllegalArgumentException("start");
        
        // Does this destroy current regions?
        destroyRegions();
        
        double width = 1./columns;
        double height = 1./rows;
        
        for (int i=0; i<columns; i++) {
            for (int j=0; j<rows; j++) {
                regions.add(justCreateRegion(i*width,j*height,width,height));
            }
        }
        
        currentRegion = start;
    }
    public void setCurrentRegionNumber(int value) {
        if (value < 0 || value >= regions.size()) throw new IllegalArgumentException();
        currentRegion = value;
    }
    public int currentRegionNumber() {
        return currentRegion;
    }
    public IPlotterRegion region(int index) {
        if (index < 0 || index >= regions.size()) throw new IllegalArgumentException();
        currentRegion = index;
        return (IPlotterRegion) regions.get(index);
    }
    
    public void destroyRegions() {
        if (regions != null && regions.size() > 0) {
            clearRegions();
            regions.clear();
        }
        currentRegion = 0;
    }
    
    public void clearRegions() {
        if (regions == null || regions.size() == 0) return;
        for (Iterator i = regions.iterator(); i.hasNext(); ) {
            ((IPlotterRegion) i.next()).clear();
        }
    }
    
    public String[] availableParameterOptions(String parameterName) {
        int parIndex = parameterIndex( parameterName );
        Object obj = parOptions.get(parIndex);
        return obj == null ? emptyArray : (String[]) obj;
    }
    
    public String[] availableParameters() {
        int size = parameters.size();
        String[] pars = new String[ size ];
        for ( int i = 0; i < size; i++ )
            pars[i] = (String) parameters.get(i);
        return pars;
    }
    
    public String parameterValue(String parameterName) {
        int parIndex = parameterIndex( parameterName );
        return (String) parValues.get(parIndex);
    }
    
    private int parameterIndex( String parameterName ) {
        for ( int i = 0; i < parameters.size(); i++ )
            if ( ( (String) parameters.get(i) ).toLowerCase().equals( parameterName.toLowerCase() ) )
                return i;
        throw new IllegalArgumentException("Parameter "+parameterName+" is not a plotter parameter.");
    }
        
    public void setParameter(String name) {
        setParameter(name,null);
    }
    
    public void setParameter(String name, String value) {
        int parIndex = parameterIndex( name );
        if ( value == null )
            parValues.set(parIndex,parDefaults.get(parIndex));
        else
            parValues.set(parIndex,value);        
    }
    
    public int numberOfRegions() {
        return regions.size();
    }

    public void setTitle(String str) {
        title = str;
    }
    
    public String title() {
        return title;
    }
        
    public hep.aida.ITitleStyle titleStyle() {
        return plotterStyle.titleStyle();
    }
    
    public void setTitleStyle(hep.aida.ITitleStyle iTitleStyle) {
        plotterStyle.setTitleStyle(iTitleStyle);
    }
    
    public IPlotterStyle style() {
        return plotterStyle;
    }
    
    public void setStyle(IPlotterStyle style) {
        if (style == plotterStyle) return;
        this.plotterStyle = style;
        if (plotterStyle != null) plotterStyle.setParameter(Style.PLOTTER_STYLE_NAME, "PlotterStyle");
        if (regions != null && regions.size() > 0) {
            for (int i=0; i<regions.size(); i++) {
                Object obj = regions.get(i);
                if (obj instanceof IPlotterRegion) ((IPlotterRegion) obj).refresh();
            }
        }
    }

    public void writeToFile(String file) throws IOException {
        writeToFile(file, null);
    }
    
    public void writeToFile(String file, String type) throws IOException {
        System.err.println("Warning: Dummy plotter used, no file will be written");
    }
    
    public void writeToFile(String fileName, String fileType, Properties props) throws IOException {
        if (fileType == null) {
            int pos = fileName.lastIndexOf('.');
            if (pos > 0) fileType = fileName.substring(pos+1);
        }
        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
        writeToFile(out,fileType,props, true);
    }
    
    void writeToFile(OutputStream out, String type, Properties props) throws IOException {
        writeToFile(out, type, props, false);
    }

    void writeToFile(OutputStream out, String type, Properties props, boolean closeOut) throws IOException {
        int delay = 0;
        if ( isShowing() ) {
            delay = 1500;
            if ( ! warned ) {
                warned = true;
                System.out.println("There might be a synchronization problem when the plotter is being shown and it is written to file.\n" +
                "Please refer to the JAIDA release notes for more details: http://java.freehep.org/jaida.\n" +
                "To avoid this problem don't invoke the show() method on the Plotter when writing to file.");
            }
        }
        
        if (props == null)
            props = new Properties();

        if ( props.getProperty("plotWidth") == null )
            PropertyUtilities.setInteger(props,"plotWidth",plotterWidth());
        if ( props.getProperty("plotHeight") == null )
            PropertyUtilities.setInteger(props,"plotHeight",plotterHeight());
        WriteToFile writeToFile = new WriteToFile( panel(), isShowing(), out, type, props, closeOut );
        
        if ( isShowing() ) {
            WriteToFileActionListener actionListener = new WriteToFileActionListener(writeToFile, isShowing());
        
            Timer timer = new Timer(delay,actionListener);
            timer.setRepeats(false);
            timer.start();
        } else {
            writeToFile.run();
        }
    }
    
    public boolean isShowing() {
        return false;
    }
    
    public JPanel panel() {
        return null;
    }
    
    protected int plotterWidth() {
        int width = Integer.parseInt(parameterValue("plotterWidth"));
        if ( width > 0 )
            return width;
        return PropertyUtilities.getInteger(System.getProperties(),"plotWidth",DEFAULT_WIDTH);
    }
    
    protected int plotterHeight() {
        int height = Integer.parseInt(parameterValue("plotterHeight"));
        if ( height > 0 )
            return height;
        return PropertyUtilities.getInteger(System.getProperties(),"plotHeight",DEFAULT_HEIGHT);
    }

    public static void invokeOnSwingThread(Runnable run) {
        try {
            if (SwingUtilities.isEventDispatchThread()) run.run();
            else SwingUtilities.invokeAndWait(run);
        }
        catch (java.lang.reflect.InvocationTargetException x) {
            x.printStackTrace();
        }
        catch (InterruptedException x) {
            x.printStackTrace();
        }
    }
    
    private class WriteToFileActionListener implements ActionListener {
        
        private WriteToFile writeToFile;
        private boolean isShowing;
        
        WriteToFileActionListener( WriteToFile writeToFile, boolean isShowing ) {
            this.writeToFile = writeToFile;
            this.isShowing = isShowing;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( isShowing )
                invokeOnSwingThread(writeToFile);
            else
                writeToFile.run();
        }
    }
    
    private class WriteToFile implements Runnable {
        
        private JPanel panel;
        private boolean isShowing;
        private OutputStream out;
        private String fileType;
        private Properties props;
        private boolean closeOut;
        
        WriteToFile(JPanel panel, boolean showing, OutputStream out, String type, Properties props, boolean closeOut) {
            this.panel = panel;
            isShowing = showing;
            this.out = out;
            fileType = type;
            this.props = props;
        }
        
        public void run() {
            Headless headLess = null;
            if (!isShowing) {
                // OK, this actually has to do something!
                headLess = new Headless(panel);
                int width = props == null ? DEFAULT_WIDTH : PropertyUtilities.getInteger(props,"plotWidth",DEFAULT_WIDTH);
                int height = props == null ? DEFAULT_HEIGHT : PropertyUtilities.getInteger(props,"plotHeight",DEFAULT_HEIGHT);
                panel.setPreferredSize(new Dimension(width,height));
                headLess.pack();
                headLess.setVisible(true);
            }
            
            try {
                List types = ExportFileType.getExportFileTypes(fileType);
                if (types == null || types.size() == 0) throw new IllegalArgumentException("Unsupported file type: "+fileType);
                ExportFileType fType = (ExportFileType) types.get(0);
                try {
                    fType.exportToFile(out, panel, null, props, "AIDA");
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } finally {
                if (headLess != null) {
                    if (!isShowing) {
                        panel.removeNotify();
                    }
                    headLess.removeAll();
                    headLess.dispose();
                }
            }
            if ( closeOut )
                try {
                    out.close();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe.getMessage());
                }
        }
    }
    
    protected void addParameter( String parameterName ) {
        addParameter( parameterName, null, null );
    }

    protected void addParameter( String parameterName, String defaultValue ) {
        addParameter( parameterName, null, defaultValue );
    }

    protected void addParameter( String parameterName, String[] options ) {
        addParameter( parameterName, options, null );
    }

    protected void addParameter( String parameterName, String[] options, String defaultValue ) {
        try {
            parameterIndex(parameterName);
            throw new IllegalArgumentException("Parameter "+parameterName+" already belongs to this BaseStyle");
        } catch (IllegalArgumentException iae) {
            parameters.add(parameterName);
            parOptions.add(options);
            parDefaults.add(defaultValue);
            parValues.add(defaultValue);
        }
    }
}